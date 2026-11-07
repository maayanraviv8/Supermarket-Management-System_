package HRandDelivery.DataAccessLayer.DeliveryDal;

import HRandDelivery.DTO.DeliveryDTO;
import HRandDelivery.DTO.SiteDTO;

import java.sql.SQLException;
import java.util.List;

public interface SiteDAO {
    SiteDTO findByAddress(String address) throws Exception;
    List<SiteDTO> findAll() throws Exception;
    void save(SiteDTO site) throws Exception;
    void reset() throws Exception;
    void delete(String address) throws Exception;
    void updateAddress(String currAddress, String newAddress) throws Exception;
}

