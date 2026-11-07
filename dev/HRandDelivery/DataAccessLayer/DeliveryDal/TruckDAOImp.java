package HRandDelivery.DataAccessLayer.DeliveryDal;

import HRandDelivery.DTO.TruckDTO;
import HRandDelivery.DataAccessLayer.Database;
import HRandDelivery.DomainLayer.Delivery.enums.License;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TruckDAOImp implements TruckDAO{

    @Override
    public TruckDTO findById(String id) throws SQLException {
        String sql = """
            SELECT truck_number,
                   model,
                   net_weight,
                   max_weight,
                   license
              FROM trucks
             WHERE truck_number = ?
        """;
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new TruckDTO(
                        rs.getString("truck_number"),
                        rs.getString("model"),
                        rs.getInt("net_weight"),
                        rs.getInt("max_weight"),
                        License.valueOf(rs.getString("license"))
                );
            }
        }
    }

    @Override
    public List<TruckDTO> findAll() throws SQLException {
        String sql = """
            SELECT truck_number,
                   model,
                   net_weight,
                   max_weight,
                   license
              FROM trucks
        """;
        List<TruckDTO> list = new ArrayList<>();
        try (Statement st = Database.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new TruckDTO(
                        rs.getString("truck_number"),
                        rs.getString("model"),
                        rs.getInt("net_weight"),
                        rs.getInt("max_weight"),
                        License.valueOf(rs.getString("license"))
                ));
            }
        }
        return list;
    }

    @Override
    public void save(TruckDTO truck) throws SQLException {
        String sql = """
            INSERT OR REPLACE INTO trucks(
              truck_number,
              model,
              net_weight,
              max_weight,
              license
            ) VALUES(?,?,?,?,?)
        """;
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, truck.getTruckNumber());
            ps.setString(2, truck.getModel());
            ps.setInt(3, truck.getNetWeight());
            ps.setInt(4, truck.getMaxWeight());
            ps.setString(5, truck.getLicense().name());
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
                st.executeUpdate("DELETE FROM trucks");
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(oldAuto);
        }
    }
}
