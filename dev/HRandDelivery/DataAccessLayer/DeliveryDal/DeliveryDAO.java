package HRandDelivery.DataAccessLayer.DeliveryDal;

import HRandDelivery.DTO.DeliveryDTO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DeliveryDAO {
    // key - date, departure, truck
    DeliveryDTO findById(LocalDate date, String driverId, String departureTime) throws Exception;
    List<DeliveryDTO> findAll() throws Exception;
    void save(DeliveryDTO employee) throws Exception;
    void reset() throws Exception;
    boolean setDeliveryDestination(LocalDate date, String driverId, String departureTime, String newDestination);
}
