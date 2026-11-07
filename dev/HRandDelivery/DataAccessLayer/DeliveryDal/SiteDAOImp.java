package HRandDelivery.DataAccessLayer.DeliveryDal;

import HRandDelivery.DTO.SiteDTO;
import HRandDelivery.DataAccessLayer.Database;

import java.sql.SQLException;
import java.util.List;
import java.sql.*;
import java.util.ArrayList;



public class SiteDAOImp implements SiteDAO{

    @Override
    public SiteDTO findByAddress(String address) throws SQLException {
        String sql = """
            SELECT address,
                   contact_name,
                   contact_number
              FROM sites
             WHERE address = ?
        """;
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, address);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new SiteDTO(
                        rs.getString("address"),
                        rs.getString("contact_name"),
                        rs.getString("contact_number")
                );
            }
        }
    }

    @Override
    public List<SiteDTO> findAll() throws SQLException {
        String sql = """
            SELECT address,
                   contact_name,
                   contact_number
              FROM sites
        """;
        List<SiteDTO> list = new ArrayList<>();
        try (Statement st = Database.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new SiteDTO(
                        rs.getString("address"),
                        rs.getString("contact_name"),
                        rs.getString("contact_number")
                ));
            }
        }
        return list;
    }

    @Override
    public void save(SiteDTO site) throws SQLException {
        String sql = """
            INSERT OR REPLACE INTO sites(
              address,
              contact_name,
              contact_number
            ) VALUES(?,?,?)
        """;
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, site.getAddress());
            ps.setString(2, site.getContactName());
            ps.setString(3, site.getContactNumber());
            ps.executeUpdate();
        }
    }

    @Override
    public void reset() throws SQLException {
        Connection conn = Database.getConnection();
        boolean oldAuto = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try {
            try (Statement st = conn.createStatement()) {
                st.executeUpdate("DELETE FROM sites WHERE address <> 'Gilat 81'");
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
    public void delete(String address) throws SQLException {
        String sql = """
        DELETE FROM sites WHERE address = ?
    """;
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, address);
            ps.executeUpdate();
        }
    }

    @Override
    public void updateAddress(String currAddress, String newAddress) throws SQLException {
        String sql = """
        UPDATE sites SET address = ? WHERE address = ?
    """;
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, newAddress);
            ps.setString(2, currAddress);
            ps.executeUpdate();
        }
    }
}
