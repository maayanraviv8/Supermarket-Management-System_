package HRandDelivery.DataAccessLayer;

import HRandDelivery.DTO.BranchDTO;

import java.sql.SQLException;
import java.util.List;

public interface BranchDAO {
    BranchDTO findById(String id) throws Exception;
    List<BranchDTO> findAll() throws Exception;
    void save(BranchDTO branch) throws Exception;
    BranchDTO findByAddress(String address) throws Exception;
    void reset() throws Exception;
    void resetAdmin() throws Exception;
}
