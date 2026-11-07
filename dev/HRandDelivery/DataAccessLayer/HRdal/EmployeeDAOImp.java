package HRandDelivery.DataAccessLayer.HRdal;

import HRandDelivery.DTO.BankAccountDTO;
import HRandDelivery.DTO.BranchDTO;
import HRandDelivery.DTO.EmployeeDTO;
import HRandDelivery.DTO.WorkConditionsDTO;
import HRandDelivery.DataAccessLayer.Database;
import HRandDelivery.DomainLayer.Delivery.enums.License;
import HRandDelivery.DomainLayer.HR.enums.Role;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class EmployeeDAOImp implements EmployeeDAO {
    @Override
    public EmployeeDTO findById(String id) throws SQLException {
        String sql = """
            SELECT e.id,
                   e.full_name,
                   e.is_active,
                   e.branch_num,
                   br.branch_name,
                   br.address,
                   br.contact_name,
                   br.contact_number,
                   b.account_id,
                   b.bank_id,
                   b.bank_branch_id,
                   w.salary,
                   w.start_date,
                   w.vacation_days_used,
                   w.sick_days_used,
                   w.funds
            FROM employees e
            LEFT JOIN branches br
              ON e.branch_num = br.id
            LEFT JOIN bank_accounts b
              ON e.id = b.employee_id
            LEFT JOIN work_conditions w
              ON e.id = w.employee_id
            WHERE e.id = ?
        """;

        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                // main DTO
                EmployeeDTO dto = new EmployeeDTO();
                dto.setId(rs.getString("id"));
                dto.setFullName(rs.getString("full_name"));
                dto.setActive(rs.getInt("is_active") == 1);

                // branch
                BranchDTO branch = new BranchDTO(
                        rs.getString("branch_name"),
                        rs.getString("branch_num"),
                        rs.getString("address"),
                        rs.getString("contact_name"),
                        rs.getString("contact_number")
                );
                dto.setBranch(branch);

                // bank account
                BankAccountDTO ba = new BankAccountDTO(
                        rs.getString("account_id"),
                        rs.getString("bank_id"),
                        rs.getString("bank_branch_id")
                );
                dto.setBankAccount(ba);

                // work conditions
                WorkConditionsDTO wc = new WorkConditionsDTO(
                        rs.getString("salary"),
                        LocalDate.parse(rs.getString("start_date")),
                        rs.getInt("vacation_days_used"),
                        rs.getInt("sick_days_used"),
                        rs.getString("funds")
                );
                dto.setWorkConditions(wc);

                // roles
                Set<Role> roles = new HashSet<>();
                String rolesSql = "SELECT role_name FROM employee_roles WHERE employee_id = ?";
                try (PreparedStatement ps2 = Database.getConnection().prepareStatement(rolesSql)) {
                    ps2.setString(1, id);
                    try (ResultSet rs2 = ps2.executeQuery()) {
                        while (rs2.next()) {
                            roles.add(Role.valueOf(rs2.getString("role_name")));
                        }
                    }
                }
                dto.setRoles(roles);

                // licenses (from summary table)
                Set<String> licenses = new HashSet<>();
                String licSql = """
                        SELECT A, B
                          FROM employee_license
                         WHERE employee_id = ?
                    """;
                try (PreparedStatement ps3 = Database.getConnection().prepareStatement(licSql)) {
                    ps3.setString(1, id);
                    try (ResultSet rs3 = ps3.executeQuery()) {
                        if (rs3.next()) {
                            if (rs3.getInt("A") == 1) licenses.add("A");
                            if (rs3.getInt("B") == 1) licenses.add("B");
                        }
                    }
                }
                dto.setLicenses(licenses);


                return dto;
            }
        }
    }

    @Override
    public List<EmployeeDTO> findAll() throws SQLException {
        String sql = "SELECT id FROM employees";
        List<EmployeeDTO> list = new ArrayList<>();

        try (Statement st = Database.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                EmployeeDTO dto = findById(rs.getString("id"));
                if (dto != null) {
                    list.add(dto);
                }
            }
        }
        return list;
    }

    @Override
    public void save(EmployeeDTO dto) throws SQLException {
        Connection conn = Database.getConnection();
        boolean oldAuto = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try {
            // 0) Branch (upsert)
            String upsertBranch = """
            INSERT OR REPLACE INTO branches(
              id, branch_name, address, contact_name, contact_number
            ) VALUES(?,?,?,?,?)
        """;
            try (PreparedStatement ps = conn.prepareStatement(upsertBranch)) {
                BranchDTO br = dto.getBranch();
                ps.setString(1, br.getId());
                ps.setString(2, br.getBranchName());
                ps.setString(3, br.getAddress());
                ps.setString(4, br.getContactName());
                ps.setString(5, br.getContactNumber());
                ps.executeUpdate();
            }

            // 1) Employees (upsert)
            String upsertEmp = """
            INSERT OR REPLACE INTO employees(
              id, full_name, is_active, branch_num
            ) VALUES(?,?,?,?)
        """;
            try (PreparedStatement ps = conn.prepareStatement(upsertEmp)) {
                ps.setString(1, dto.getId());
                ps.setString(2, dto.getFullName());
                ps.setInt(3, dto.isActive() ? 1 : 0);
                ps.setString(4, dto.getBranch().getId());
                ps.executeUpdate();
            }

            // 2) BankAccount (upsert)
            String upsertBA = """
            INSERT OR REPLACE INTO bank_accounts(
              employee_id, account_id, bank_id, bank_branch_id
            ) VALUES(?,?,?,?)
        """;
            try (PreparedStatement ps = conn.prepareStatement(upsertBA)) {
                ps.setString(1, dto.getId());
                ps.setString(2, dto.getBankAccount().getAccountId());
                ps.setString(3, dto.getBankAccount().getBankId());
                ps.setString(4, dto.getBankAccount().getBankBranchId());
                ps.executeUpdate();
            }

            // 3) WorkConditions (upsert)
            String upsertWC = """
            INSERT OR REPLACE INTO work_conditions(
              employee_id, salary, start_date, vacation_days_used,
              sick_days_used, funds
            ) VALUES(?,?,?,?,?,?)
        """;
            try (PreparedStatement ps = conn.prepareStatement(upsertWC)) {
                ps.setString(1, dto.getId());
                ps.setString(2, dto.getWorkConditions().getSalary());
                ps.setString(3, dto.getWorkConditions().getStartDate().toString());
                ps.setInt(4, dto.getWorkConditions().getVacationDaysUsed());
                ps.setInt(5, dto.getWorkConditions().getSickDaysUsed());
                ps.setString(6, dto.getWorkConditions().getFunds());
                ps.executeUpdate();
            }

            // 4) Roles
            // 4a) delete first
            try (PreparedStatement del = conn.prepareStatement(
                    "DELETE FROM employee_roles WHERE employee_id = ?")) {
                del.setString(1, dto.getId());
                del.executeUpdate();
            }
            // 4b) add again
            try (PreparedStatement ins = conn.prepareStatement(
                    "INSERT INTO employee_roles(employee_id, role_name) VALUES(?,?)")) {
                for (Role r : dto.getRoles()) {
                    ins.setString(1, dto.getId());
                    ins.setString(2, r.name());
                    ins.addBatch();
                }
                ins.executeBatch();
            }

            // 5) Licenses (upsert summary)
            String upsertLic = """
                INSERT OR REPLACE INTO employee_license(
                  employee_id, A, B
                ) VALUES(?,?,?)
            """;
            try (PreparedStatement ps = conn.prepareStatement(upsertLic)) {
                Set<License> lic = dto.getLicenses();
                ps.setString(1, dto.getId());
                ps.setInt(2, lic.contains(License.A) ? 1 : 0);
                ps.setInt(3, lic.contains(License.B) ? 1 : 0);
                ps.executeUpdate();
            }

            conn.commit();
        } catch (SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(oldAuto);
        }
    }


    @Override
    public Boolean validEmployee(String id)  throws SQLException {
        String sql = "SELECT COUNT(1) FROM employees WHERE id = ? AND is_active = 1";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error validating employee " + id, e);
        }
    }

    @Override
    public void setBank(String id, BankAccountDTO bankAccountDTO)  throws SQLException {
        String sql = """
        INSERT OR REPLACE INTO bank_accounts(
          employee_id, account_id, bank_id, bank_branch_id
        ) VALUES(?,?,?,?)
    """;
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, bankAccountDTO.getAccountId());
            ps.setString(3, bankAccountDTO.getBankId());
            ps.setString(4, bankAccountDTO.getBankBranchId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error setting bank account for " + id, e);
        }
    }

    @Override
    public void setWorkCon(String id, WorkConditionsDTO wc)  throws SQLException {
        String sql = """
        INSERT OR REPLACE INTO work_conditions(
          employee_id, salary, start_date, vacation_days_used,
          sick_days_used, funds
        ) VALUES(?,?,?,?,?,?)
    """;
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, wc.getSalary());
            ps.setString(3, wc.getStartDate().toString());
            ps.setInt(4, wc.getVacationDaysUsed());
            ps.setInt(5, wc.getSickDaysUsed());
            ps.setString(6, wc.getFunds());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error setting work conditions for " + id, e);
        }
    }

    @Override
    public void setRoles(String id, Set<Role> roles)  throws SQLException {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement del = conn.prepareStatement(
                    "DELETE FROM employee_roles WHERE employee_id = ?")) {
                del.setString(1, id);
                del.executeUpdate();
            }
            try (PreparedStatement ins = conn.prepareStatement(
                    "INSERT INTO employee_roles(employee_id, role_name) VALUES(?,?)")) {
                for (Role r : roles) {
                    ins.setString(1, id);
                    ins.setString(2, r.name());
                    ins.addBatch();
                }
                ins.executeBatch();
            }
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Error setting roles for " + id, e);
        }
    }


    @Override
    public void setActive(String id, boolean isActive)  throws SQLException {
        String sql = "UPDATE employees SET is_active = ? WHERE id = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, isActive ? 1 : 0);
            ps.setString(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error setting active flag for " + id, e);
        }
    }

    @Override
    public void setName(String id, String name)  throws SQLException {
        String sql = "UPDATE employees SET full_name = ? WHERE id = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error setting name for " + id, e);
        }
    }

    @Override
    public List<EmployeeDTO> findAllByRole(Role role) throws SQLException {
        String sql = """
        SELECT e.id
          FROM employee_roles r
          JOIN employees e
            ON r.employee_id = e.id
         WHERE r.role_name = ?
    """;

        List<EmployeeDTO> result = new ArrayList<>();
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, role.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String empId = rs.getString("id");
                    EmployeeDTO dto = findById(empId);
                    if (dto != null) {
                        result.add(dto);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void reset() throws SQLException {
        Connection conn = Database.getConnection();
        boolean oldAuto = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try (Statement st = conn.createStatement()) {

            // 1) Delete bank accounts of non-admins
            st.executeUpdate("""
            DELETE FROM bank_accounts
             WHERE employee_id NOT IN (
                 SELECT employee_id
                   FROM employee_roles
                  WHERE role_name = 'ADMIN'
             )
        """);

            // 2) Delete work conditions of non-admins
            st.executeUpdate("""
            DELETE FROM work_conditions
             WHERE employee_id NOT IN (
                 SELECT employee_id
                   FROM employee_roles
                  WHERE role_name = 'ADMIN'
             )
        """);

            // 3) Delete licenses of non-admins
            st.executeUpdate("""
            DELETE FROM employee_license
             WHERE employee_id NOT IN (
                 SELECT employee_id
                   FROM employee_roles
                  WHERE role_name = 'ADMIN'
             )
        """);

            // 4) Delete role assignments of non-admins (only admin assignments remain)
            st.executeUpdate("""
            DELETE FROM employee_roles
             WHERE employee_id NOT IN (
                 SELECT employee_id
                   FROM employee_roles
                  WHERE role_name = 'ADMIN'
             )
        """);

            // 5) Finally, delete the non-admin employees themselves
            st.executeUpdate("""
            DELETE FROM employees
             WHERE id NOT IN (
                 SELECT employee_id
                   FROM employee_roles
                  WHERE role_name = 'ADMIN'
             )
        """);

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(oldAuto);
        }
    }

    public void resetAdmin() throws SQLException {
        Connection conn = Database.getConnection();
        boolean oldAuto = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try (Statement st = conn.createStatement()) {

            // 1) Delete bank accounts of non-admins
            st.executeUpdate("""
            DELETE FROM bank_accounts
             WHERE employee_id IN (
                 SELECT employee_id
                   FROM employee_roles
                  WHERE role_name = 'ADMIN'
             )
        """);

            // 2) Delete work conditions of non-admins
            st.executeUpdate("""
            DELETE FROM work_conditions
             WHERE employee_id IN (
                 SELECT employee_id
                   FROM employee_roles
                  WHERE role_name = 'ADMIN'
             )
        """);

            // 3) Delete licenses of non-admins
            st.executeUpdate("""
            DELETE FROM employee_license
             WHERE employee_id IN (
                 SELECT employee_id
                   FROM employee_roles
                  WHERE role_name = 'ADMIN'
             )
        """);

            // 4) Delete role assignments of non-admins (only admin assignments remain)
            st.executeUpdate("""
            DELETE FROM employee_roles
             WHERE employee_id IN (
                 SELECT employee_id
                   FROM employee_roles
                  WHERE role_name = 'ADMIN'
             )
        """);

            // 5) Finally, delete the non-admin employees themselves
            st.executeUpdate("""
            DELETE FROM employees
             WHERE id IN (
                 SELECT employee_id
                   FROM employee_roles
                  WHERE role_name = 'ADMIN'
             )
        """);

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(oldAuto);
        }
    }

    @Override
    public void setLicense(String id, Set<License> licenses) throws SQLException {

        int hasA = licenses.contains(License.A) ? 1 : 0;
        int hasB = licenses.contains(License.B) ? 1 : 0;

        String sql = """
        INSERT OR REPLACE INTO employee_license(
          employee_id, A, B
        ) VALUES(?, ?, ?)
    """;

        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setInt(2, hasA);
            ps.setInt(3, hasB);
            ps.executeUpdate();
        }
    }

    @Override
    public void setDeadLine(LocalTime deadline) throws SQLException {
        String deleteSql = "DELETE FROM deadline";
        String insertSql = "INSERT INTO deadline(deadline_date) VALUES(?)";

        Connection conn = Database.getConnection();
        boolean oldAuto = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try (Statement deleteStmt = conn.createStatement();
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

            deleteStmt.executeUpdate(deleteSql);

            insertStmt.setString(1, deadline.toString());
            insertStmt.executeUpdate();

            conn.commit();
        } catch (SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(oldAuto);
        }
    }

    @Override
    public LocalTime getDeadLine() throws SQLException {
        String querySql = "SELECT deadline_date FROM deadline LIMIT 1";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(querySql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String text = rs.getString("deadline_date");
                return LocalTime.parse(text);
            }
            return null;
        }
    }

}
