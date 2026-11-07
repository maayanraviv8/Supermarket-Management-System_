package HRandDelivery.DataAccessLayer;

import HRandDelivery.DTO.BranchDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BranchDAOImp implements BranchDAO {
    @Override
    public BranchDTO findById(String id) throws SQLException {
        String sql = """
            SELECT id,
                   branch_name,
                   address,
                   contact_name,
                   contact_number
              FROM branches
             WHERE id = ?
        """;
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new BranchDTO(
                        rs.getString("branch_name"),
                        rs.getString("id"),
                        rs.getString("address"),
                        rs.getString("contact_name"),
                        rs.getString("contact_number")
                );
            }
        }
    }

    @Override
    public List<BranchDTO> findAll() throws SQLException {
        String sql = """
            SELECT id,
                   branch_name,
                   address,
                   contact_name,
                   contact_number
              FROM branches
        """;
        List<BranchDTO> list = new ArrayList<>();
        try (Statement st = Database.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new BranchDTO(
                        rs.getString("branch_name"),
                        rs.getString("id"),
                        rs.getString("address"),
                        rs.getString("contact_name"),
                        rs.getString("contact_number")
                ));
            }
        }
        return list;
    }

    @Override
    public void save(BranchDTO branch) throws SQLException {
        String sql = """
            INSERT OR REPLACE INTO branches(
              id,
              branch_name,
              address,
              contact_name,
              contact_number
            ) VALUES(?,?,?,?,?)
        """;
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, branch.getId());
            ps.setString(2, branch.getBranchName());
            ps.setString(3, branch.getAddress());
            ps.setString(4, branch.getContactName());
            ps.setString(5, branch.getContactNumber());
            ps.executeUpdate();
        }
    }

    @Override
    public BranchDTO findByAddress(String address) throws SQLException {
        String sql = """
            SELECT id,
                   branch_name,
                   address,
                   contact_name,
                   contact_number
              FROM branches
             WHERE address = ?
        """;
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, address);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new BranchDTO(
                        rs.getString("branch_name"),
                        rs.getString("id"),
                        rs.getString("address"),
                        rs.getString("contact_name"),
                        rs.getString("contact_number")
                );
            }
        }
    }

    @Override
    public void reset() throws Exception {
        Connection conn = Database.getConnection();
        boolean oldAuto = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("""
            DELETE FROM branches
            WHERE id <> (
              SELECT id
              FROM branches
              ORDER BY CAST(id AS INTEGER) ASC
              LIMIT 1
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
    public void resetAdmin() throws Exception {
        Connection conn = Database.getConnection();
        boolean oldAuto = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("""
            DELETE FROM branches
            """);
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(oldAuto);
        }
    }

}
