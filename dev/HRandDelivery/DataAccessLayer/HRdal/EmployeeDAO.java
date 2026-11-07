package HRandDelivery.DataAccessLayer.HRdal;

import HRandDelivery.DTO.BankAccountDTO;
import HRandDelivery.DTO.EmployeeDTO;
import HRandDelivery.DTO.WorkConditionsDTO;
import HRandDelivery.DomainLayer.Delivery.enums.License;
import HRandDelivery.DomainLayer.HR.enums.Role;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EmployeeDAO {
    EmployeeDTO findById(String id) throws Exception;
    List<EmployeeDTO> findAll() throws Exception;
    void save(EmployeeDTO employee) throws Exception;
    Boolean validEmployee(String id) throws Exception;
    void setBank(String id, BankAccountDTO bankAccountDTO) throws Exception;
    void setWorkCon(String id, WorkConditionsDTO workConditionsDTO) throws Exception;
    void setRoles(String id, Set<Role> roles) throws Exception;
    void setActive(String id, boolean isActive) throws Exception;
    void setName(String id, String name) throws Exception;
    List<EmployeeDTO> findAllByRole(Role role) throws Exception;
    void reset() throws Exception;
    void resetAdmin() throws Exception;
    void setLicense(String id, Set<License> licenses) throws Exception;
    LocalTime getDeadLine() throws Exception;
    void setDeadLine(LocalTime deadline) throws Exception;
}
