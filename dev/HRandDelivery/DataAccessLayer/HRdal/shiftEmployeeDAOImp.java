package HRandDelivery.DataAccessLayer.HRdal;

import HRandDelivery.DTO.BranchDTO;
import HRandDelivery.DTO.EmployeeDTO;
import HRandDelivery.DTO.ShiftDTO;
import HRandDelivery.DataAccessLayer.BranchDAOImp;
import HRandDelivery.DataAccessLayer.Database;
import HRandDelivery.DomainLayer.HR.enums.Role;
import HRandDelivery.DataAccessLayer.BranchDAO;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class shiftEmployeeDAOImp implements ShiftEmployeeDAO{

    private final EmployeeDAO employeeDAO = new EmployeeDAOImp();
    private final BranchDAO branchDAO   = new BranchDAOImp();

    @Override
    public Map<LocalDate, ShiftDTO> findShiftsForEmployee(String userId) throws SQLException {
        String sql = """
            SELECT s.branch_num,
                   s.date,
                   s.time_slot,
                   s.start_time,
                   s.end_time,
                   br.branch_name,
                   br.address,
                   br.contact_name,
                   br.contact_number
              FROM shift_assignments sa
              JOIN shifts s
                ON sa.branch_num = s.branch_num
               AND sa.date       = s.date
               AND sa.time_slot  = s.time_slot
              JOIN branches br
                ON s.branch_num = br.id
             WHERE sa.employee_id = ?
        """;
        Map<LocalDate, ShiftDTO> result = new HashMap<>();
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BranchDTO br = new BranchDTO(
                            rs.getString("branch_name"),
                            rs.getString("branch_num"),
                            rs.getString("address"),
                            rs.getString("contact_name"),
                            rs.getString("contact_number")
                    );

                    ShiftDTO dto = new ShiftDTO();
                    dto.setBranch(br);
                    dto.setDate(LocalDate.parse(rs.getString("date")));
                    dto.setTimeSlotName(rs.getString("time_slot"));
                    dto.setStartTime(rs.getString("start_time"));
                    dto.setEndTime(rs.getString("end_time"));

                    result.put(dto.getDate(), dto);
                }
            }
        }
        return result;
    }

    @Override
    public Set<EmployeeDTO> getEmployeesAssignByRole(ShiftDTO shiftDTO, Role role) throws SQLException {
        String sql = """
            SELECT employee_id
              FROM shift_assignments
             WHERE branch_num = ?
               AND date       = ?
               AND time_slot  = ?
               AND role_name  = ?
        """;
        Set<EmployeeDTO> set = new HashSet<>();
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, shiftDTO.getBranch().getId());
            ps.setString(2, shiftDTO.getDate().toString());
            ps.setString(3, shiftDTO.getTimeSlotName());
            ps.setString(4, role.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EmployeeDTO e = employeeDAO.findById(rs.getString("employee_id"));
                    if (e != null) set.add(e);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return set;
    }

    @Override
    public Set<EmployeeDTO> getEmployeesAssign(ShiftDTO shiftDTO) throws SQLException {
        String sql = """
            SELECT DISTINCT employee_id
              FROM shift_assignments
             WHERE branch_num = ?
               AND date       = ?
               AND time_slot  = ?
        """;
        Set<EmployeeDTO> set = new HashSet<>();
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, shiftDTO.getBranch().getId());
            ps.setString(2, shiftDTO.getDate().toString());
            ps.setString(3, shiftDTO.getTimeSlotName());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EmployeeDTO e = employeeDAO.findById(rs.getString("employee_id"));
                    if (e != null) set.add(e);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return set;
    }

    @Override
    public void removeEmployee(ShiftDTO shiftDTO, Role role, String id) throws SQLException {
        String sql = """
            DELETE
              FROM shift_assignments
             WHERE branch_num  = ?
               AND date        = ?
               AND time_slot   = ?
               AND role_name   = ?
               AND employee_id = ?
        """;
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, shiftDTO.getBranch().getId());
            ps.setString(2, shiftDTO.getDate().toString());
            ps.setString(3, shiftDTO.getTimeSlotName());
            ps.setString(4, role.name());
            ps.setString(5, id);
            ps.executeUpdate();
        }
    }

    @Override
    public void assigneEmployee(ShiftDTO shiftDTO, Role role, String id) throws SQLException {
        String sql = """
            INSERT INTO shift_assignments(
              branch_num, date, time_slot, role_name, employee_id
            ) VALUES(?,?,?,?,?)
        """;
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, shiftDTO.getBranch().getId());
            ps.setString(2, shiftDTO.getDate().toString());
            ps.setString(3, shiftDTO.getTimeSlotName());
            ps.setString(4, role.name());
            ps.setString(5, id);
            ps.executeUpdate();
        }
    }

    @Override
    public void setEmployeesForShift(ShiftDTO shiftDTO, Map<Role, Set<String>> employeesIdsPerRole) throws SQLException {
        Connection conn = Database.getConnection();
        boolean old = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try {
            //Delete
            try (PreparedStatement del = conn.prepareStatement(
                    "DELETE FROM shift_assignments WHERE branch_num=? AND date=? AND time_slot=?")) {
                del.setString(1, shiftDTO.getBranch().getId());
                del.setString(2, shiftDTO.getDate().toString());
                del.setString(3, shiftDTO.getTimeSlotName());
                del.executeUpdate();
            }
            //Add
            try (PreparedStatement ins = conn.prepareStatement(
                    "INSERT INTO shift_assignments(branch_num,date,time_slot,role_name,employee_id) VALUES(?,?,?,?,?)")) {
                for (var entry : employeesIdsPerRole.entrySet()) {
                    String r = entry.getKey().name();
                    for (String eid : entry.getValue()) {
                        ins.setString(1, shiftDTO.getBranch().getId());
                        ins.setString(2, shiftDTO.getDate().toString());
                        ins.setString(3, shiftDTO.getTimeSlotName());
                        ins.setString(4, r);
                        ins.setString(5, eid);
                        ins.addBatch();
                    }
                }
                ins.executeBatch();
            }
            conn.commit();
        } catch (SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(old);
        }
    }

    @Override
    public Set<EmployeeDTO> findAllAvailableEmployees(ShiftDTO shiftDTO) throws SQLException {
        String sql = """
            SELECT employee_id
              FROM shift_availability
             WHERE branch_num = ?
               AND date       = ?
               AND time_slot  = ?
        """;
        Set<EmployeeDTO> set = new HashSet<>();
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, shiftDTO.getBranch().getId());
            ps.setString(2, shiftDTO.getDate().toString());
            ps.setString(3, shiftDTO.getTimeSlotName());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EmployeeDTO e = employeeDAO.findById(rs.getString("employee_id"));
                    if (e != null) set.add(e);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return set;
    }

    @Override
    public Map<Role, Set<EmployeeDTO>> findAllEmployeePerRole(ShiftDTO shiftDTO) throws SQLException {
        String sql = """
            SELECT role_name, employee_id
              FROM shift_assignments
             WHERE branch_num = ?
               AND date       = ?
               AND time_slot  = ?
        """;
        Map<Role, Set<EmployeeDTO>> map = new EnumMap<>(Role.class);
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, shiftDTO.getBranch().getId());
            ps.setString(2, shiftDTO.getDate().toString());
            ps.setString(3, shiftDTO.getTimeSlotName());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Role r = Role.valueOf(rs.getString("role_name"));
                    EmployeeDTO e = employeeDAO.findById(rs.getString("employee_id"));
                    map.computeIfAbsent(r, k -> new HashSet<>()).add(e);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return map;
    }

    @Override
    public Map<Role, Integer> findAllRolesNeeded(ShiftDTO shiftDTO) throws SQLException {
        String sql = """
            SELECT needed_admin,
                   needed_shift_manager,
                   needed_driver,
                   needed_cashier,
                   needed_store_keeper
              FROM shifts
             WHERE branch_num = ?
               AND date       = ?
               AND time_slot  = ?
        """;
        Map<Role, Integer> needed = new EnumMap<>(Role.class);
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, shiftDTO.getBranch().getId());
            ps.setString(2, shiftDTO.getDate().toString());
            ps.setString(3, shiftDTO.getTimeSlotName());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    needed.put(Role.ADMIN, rs.getInt("needed_admin"));
                    needed.put(Role.SHIFT_MANAGER, rs.getInt("needed_shift_manager"));
                    needed.put(Role.DRIVER, rs.getInt("needed_driver"));
                    needed.put(Role.CASHIER, rs.getInt("needed_cashier"));
                    needed.put(Role.STORE_KEEPER, rs.getInt("needed_store_keeper"));
                }
            }
        }
        return needed;
    }

    @Override
    public void reset() throws SQLException {
        Connection conn = Database.getConnection();
        boolean oldAuto = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try (Statement st = conn.createStatement()) {
            // 1) Clear all availability entries
            st.executeUpdate("DELETE FROM shift_availability");
            // 2) Clear all assignment entries
            st.executeUpdate("DELETE FROM shift_assignments");
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(oldAuto);
        }
    }

}
