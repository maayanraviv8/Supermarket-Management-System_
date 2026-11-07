package HRandDelivery.DomainLayer.Repository;


import HRandDelivery.DTO.BranchDTO;
import HRandDelivery.DomainLayer.Delivery.enums.License;
import HRandDelivery.DomainLayer.HR.HRDomainLayer.BankAccount;
import HRandDelivery.DomainLayer.HR.HRDomainLayer.Employee;
import HRandDelivery.DomainLayer.HR.HRDomainLayer.WorkConditions;
import HRandDelivery.DomainLayer.HR.enums.Role;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public interface EmployeeRepository {

    void saveEmployee(String fullName, String id, BankAccount bankAccount, WorkConditions workConditions, Set<Role> roles, BranchDTO branch, Set<License> licenses) throws Exception;
    Employee findEmployee(String id) throws Exception;
    List<Employee> findAllEmployees() throws Exception;
    Boolean isValidEmployee(String id) throws Exception;
    void setBank(String id, BankAccount bankAccount) throws Exception;
    void setWorkConditions(String id, WorkConditions workConditions) throws Exception;
    void setRoles(String id, Set<Role> roles) throws Exception;
    void setActive(String id, boolean isActive) throws Exception;
    void setName(String id, String name) throws Exception;
    void setLicense(String id, Set<License> licenses) throws Exception;
    List<Employee> findAllByRole(Role role) throws Exception;
    void resetData() throws Exception;
    void resetAdmin() throws Exception;
    LocalTime getDeadLine() throws Exception;
    void setDeadLine(LocalTime localTime) throws Exception;
}
