package HRandDelivery.DomainLayer.Repository;

import HRandDelivery.DTO.EmployeeDTO;
import HRandDelivery.DomainLayer.Branch;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface BranchRepository {
    void createBranch(String branchName,String branchId, String address, String contactName, String contactNumber) throws Exception;
    Branch findBranch(String branchId) throws Exception;
    List<Branch> findAllBranches() throws Exception;
    void resetData() throws Exception;
    void resetAdmin() throws Exception;
}
