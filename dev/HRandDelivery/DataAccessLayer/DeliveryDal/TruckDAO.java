package HRandDelivery.DataAccessLayer.DeliveryDal;

import HRandDelivery.DTO.SiteDTO;
import HRandDelivery.DTO.TruckDTO;

import java.util.List;

public interface TruckDAO {
    TruckDTO findById(String id) throws Exception;
    List<TruckDTO> findAll() throws Exception;
    void save(TruckDTO truck) throws Exception;
    void reset() throws Exception;
}
