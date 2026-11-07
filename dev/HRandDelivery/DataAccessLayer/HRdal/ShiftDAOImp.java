package HRandDelivery.DataAccessLayer.HRdal;

import HRandDelivery.DTO.BranchDTO;
import HRandDelivery.DTO.ShiftDTO;
import HRandDelivery.DataAccessLayer.BranchDAOImp;
import HRandDelivery.DataAccessLayer.Database;
import HRandDelivery.DomainLayer.HR.enums.Role;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ShiftDAOImp implements ShiftDAO {

    @Override
    public void save(ShiftDTO shift) throws SQLException {
        String sql = """
        INSERT OR REPLACE INTO shifts(
            branch_num,
            date,
            time_slot,
            start_time,
            end_time
        ) VALUES(?,?,?,?,?)
    """;
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, shift.getBranch().getId());
            ps.setString(2, shift.getDate().toString());
            ps.setString(3, shift.getTimeSlotName());
            ps.setString(4, shift.getStartTime());
            ps.setString(5, shift.getEndTime());
            ps.executeUpdate();
        }
    }


    @Override
    public ShiftDTO findShift(BranchDTO branch, LocalDate date, String timeSlotName) throws SQLException {
        String sql = """
        SELECT start_time,
               end_time
          FROM shifts
         WHERE branch_num = ?
           AND date       = ?
           AND time_slot  = ?
    """;
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, branch.getId());
            ps.setString(2, date.toString());
            ps.setString(3, timeSlotName);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                ShiftDTO dto = new ShiftDTO();
                dto.setBranch(branch);
                dto.setDate(date);
                dto.setTimeSlotName(timeSlotName);
                dto.setStartTime(rs.getString("start_time"));
                dto.setEndTime(rs.getString("end_time"));
                return dto;
            }
        }
    }

    @Override
    public Boolean isSet(BranchDTO branch, LocalDate date, String timeSlotName) throws SQLException {
        String sql = """
        SELECT 1
          FROM shifts
         WHERE branch_num = ?
           AND date       = ?
           AND time_slot  = ?
        LIMIT 1
    """;
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, branch.getId());
            ps.setString(2, date.toString());
            ps.setString(3, timeSlotName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public void setRolesQuantities(ShiftDTO shiftDTO, Map<Role, Integer> rolesQuantities) throws SQLException {
        String sql = """
        UPDATE shifts
           SET needed_admin         = ?,
               needed_shift_manager = ?,
               needed_driver        = ?,
               needed_cashier       = ?,
               needed_store_keeper  = ?
         WHERE branch_num  = ?
           AND date        = ?
           AND time_slot   = ?
    """;
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, rolesQuantities.getOrDefault(Role.ADMIN, 0));
            ps.setInt(2, rolesQuantities.getOrDefault(Role.SHIFT_MANAGER, 0));
            ps.setInt(3, rolesQuantities.getOrDefault(Role.DRIVER, 0));
            ps.setInt(4, rolesQuantities.getOrDefault(Role.CASHIER, 0));
            ps.setInt(5, rolesQuantities.getOrDefault(Role.STORE_KEEPER, 0));
            ps.setString(6, shiftDTO.getBranch().getId());
            ps.setString(7, shiftDTO.getDate().toString());
            ps.setString(8, shiftDTO.getTimeSlotName());
            ps.executeUpdate();
        }
    }

    @Override
    public void reset() throws Exception {
        Connection conn = Database.getConnection();
        boolean oldAuto = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try {
            try (Statement st = conn.createStatement()) {
                st.executeUpdate("DELETE FROM shifts");
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(oldAuto);
        }
    }

    @Override
    public List<ShiftDTO> findAllByDateAndTimeSlot(LocalDate date, String timeSlotName) throws SQLException {
        String sql = """
        SELECT branch_num, start_time, end_time
          FROM shifts
         WHERE date = ?
           AND time_slot = ?
    """;

        List<ShiftDTO> result = new ArrayList<>();

        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, date.toString());
            ps.setString(2, timeSlotName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String branchId = rs.getString("branch_num");
                    String startTime = rs.getString("start_time");
                    String endTime = rs.getString("end_time");

                    BranchDTO branch = new BranchDAOImp().findById(branchId);
                    ShiftDTO dto = new ShiftDTO(branch, date, timeSlotName, startTime, endTime);
                    result.add(dto);
                }
            }
        }

        return result;
    }


}
