package HRandDelivery.DataAccessLayer.HRdal;

import HRandDelivery.DTO.EmployeeDTO;
import HRandDelivery.DTO.ShiftDTO;
import HRandDelivery.DomainLayer.HR.enums.Role;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

public interface ShiftEmployeeDAO {
    Map<LocalDate, ShiftDTO> findShiftsForEmployee(String userId) throws Exception;
    Set<EmployeeDTO> getEmployeesAssignByRole(ShiftDTO shiftDTO, Role role) throws Exception;
    Set<EmployeeDTO> getEmployeesAssign(ShiftDTO shiftDTO) throws Exception;
    void removeEmployee(ShiftDTO shiftDTO, Role role, String id) throws Exception;
    void assigneEmployee(ShiftDTO shiftDTO, Role role, String id) throws Exception;
    void setEmployeesForShift(ShiftDTO shiftDTO, Map<Role, Set<String>> employeesIdsPerRole) throws Exception;
    Set<EmployeeDTO> findAllAvailableEmployees(ShiftDTO shiftDTO) throws Exception;
    Map<Role, Set<EmployeeDTO>> findAllEmployeePerRole(ShiftDTO shiftDTO) throws Exception;
    Map<Role, Integer> findAllRolesNeeded(ShiftDTO shiftDTO) throws Exception;
    void reset() throws Exception;
}
