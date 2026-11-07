package HRandDelivery.DataAccessLayer.DeliveryDal;

import HRandDelivery.DTO.DeliveryDTO;
import HRandDelivery.DTO.SiteDTO;
import HRandDelivery.DataAccessLayer.Database;
import HRandDelivery.DomainLayer.HR.enums.ShiftType;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class DeliveryDAOImp implements DeliveryDAO {

    private final SiteDAO siteDAO = new SiteDAOImp();

    @Override
    public DeliveryDTO findById(LocalDate date, String driverId, String departureTime) throws SQLException {
        // 1) Query the main delivery record
        String sql = """
            SELECT shift_type,
                   truck_num,
                   source_adss
              FROM deliveries
             WHERE date = ?
               AND driver_id = ?
               AND departure_time = ?
        """;
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, date.toString());
            ps.setString(2, driverId);
            ps.setString(3, departureTime);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                // Map core fields
                String shiftTypeCode = rs.getString("shift_type");
                String truckNum    = rs.getString("truck_num");
                SiteDTO source     = siteDAO.findByAddress(rs.getString("source_adss"));

                // 2) Query the destinations for this delivery
                String destSql = """
                    SELECT destination_adss
                      FROM delivery_destinations
                     WHERE date = ?
                       AND driver_id = ?
                       AND departure_time = ?
                """;
                Set<SiteDTO> destinations = new HashSet<>();
                try (PreparedStatement ps2 = Database.getConnection().prepareStatement(destSql)) {
                    ps2.setString(1, date.toString());
                    ps2.setString(2, driverId);
                    ps2.setString(3, departureTime);
                    try (ResultSet rs2 = ps2.executeQuery()) {
                        while (rs2.next()) {
                            destinations.add(
                                    siteDAO.findByAddress(rs2.getString("destination_adss"))
                            );
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                // 3) Build and return the DTO
                return new DeliveryDTO(
                        date,
                        shiftTypeCode,
                        departureTime,
                        driverId,
                        truckNum,
                        source,
                        destinations
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public List<DeliveryDTO> findAll() throws SQLException {
        // 1) Get all primary keys from deliveries
        String sql = """
            SELECT date, driver_id, departure_time
              FROM deliveries
        """;
        List<DeliveryDTO> list = new ArrayList<>();
        try (Statement st = Database.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                LocalDate date       = LocalDate.parse(rs.getString("date"));
                String driverId      = rs.getString("driver_id");
                String departureTime = rs.getString("departure_time");

                // 2) Reuse findById to populate full DTO
                DeliveryDTO dto = findById(date, driverId, departureTime);
                if (dto != null) list.add(dto);
            }
        }
        return list;
    }

    @Override
    public void save(DeliveryDTO delivery) throws SQLException {
        Connection conn = Database.getConnection();
        boolean oldAuto = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try {
            // 1) Upsert the deliveries table
            String upsertDel = """
                INSERT OR REPLACE INTO deliveries(
                  date, shift_type, driver_id,
                  departure_time, truck_num, source_adss
                ) VALUES(?,?,?,?,?,?)
            """;
            try (PreparedStatement ps = conn.prepareStatement(upsertDel)) {
                ps.setString(1, delivery.getDate().toString());
                ps.setString(2, delivery.getShiftType());
                ps.setString(3, delivery.getDriver());
                ps.setString(4, delivery.getDepartureTime());
                ps.setString(5, delivery.getTruck());
                ps.setString(6, delivery.getSource().getAddress());
                ps.executeUpdate();
            }

            // 2) Delete existing destinations for this delivery
            String delDest = """
                DELETE FROM delivery_destinations
                 WHERE date = ?
                   AND driver_id = ?
                   AND departure_time = ?
            """;
            try (PreparedStatement ps = conn.prepareStatement(delDest)) {
                ps.setString(1, delivery.getDate().toString());
                ps.setString(2, delivery.getDriver());
                ps.setString(3, delivery.getDepartureTime());
                ps.executeUpdate();
            }

            // 3) Insert new destinations
            String insDest = """
                INSERT INTO delivery_destinations(
                  date, driver_id, departure_time, destination_adss
                ) VALUES(?,?,?,?)
            """;
            try (PreparedStatement ps = conn.prepareStatement(insDest)) {
                for (SiteDTO dest : delivery.getDestination()) {
                    ps.setString(1, delivery.getDate().toString());
                    ps.setString(2, delivery.getDriver());
                    ps.setString(3, delivery.getDepartureTime());
                    ps.setString(4, dest.getAddress());
                    ps.addBatch();
                }
                ps.executeBatch();
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
    public void reset() throws SQLException {
        Connection conn = Database.getConnection();
        boolean oldAuto = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try {
            // 1) Remove all delivery destinations first to satisfy FKs
            try (Statement st = conn.createStatement()) {
                st.executeUpdate("DELETE FROM delivery_destinations");
                // 2) Remove all deliveries
                st.executeUpdate("DELETE FROM deliveries");
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
    public boolean setDeliveryDestination(LocalDate date, String driverId, String departureTime, String newDestination) {
        String insDest = """
        INSERT INTO delivery_destinations(
            date, driver_id, departure_time, destination_adss
        ) VALUES (?, ?, ?, ?)
    """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(insDest)) {

            ps.setString(1, date.toString());
            ps.setString(2, driverId);
            ps.setString(3, departureTime);
            ps.setString(4, newDestination);

            int inserted = ps.executeUpdate();
            return inserted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
