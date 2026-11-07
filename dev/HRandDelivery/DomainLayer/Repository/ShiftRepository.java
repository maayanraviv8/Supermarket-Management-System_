package HRandDelivery.DomainLayer.Repository;

import HRandDelivery.DTO.BranchDTO;
import HRandDelivery.DTO.ShiftDTO;
import HRandDelivery.DomainLayer.HR.HRDomainLayer.*;
import HRandDelivery.DomainLayer.HR.enums.Role;
import HRandDelivery.DomainLayer.HR.enums.ShiftType;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

public interface ShiftRepository {

    void saveShift(BranchDTO branch, LocalDate date, String timeSlotName, String startTime, String endTime) throws Exception;
    Shift findShift(BranchDTO branch, LocalDate date, String timeSlotName) throws Exception;
    Set<Shift> findAllBranchShift(LocalDate date, String timeSlotName) throws Exception;
    Boolean isSet(BranchDTO branch, LocalDate date, String timeSlotName) throws Exception;
    Map<LocalDate, Map<ShiftType, Shift>> findShiftsForEmployee(String userId) throws Exception;
    void setRolesQuantities(ShiftDTO shiftDTO, Map<Role, Integer> rolesQuantities) throws Exception;
    Set<Employee> getEmployeesAssignByRole(ShiftDTO shiftDTO, Role role) throws Exception;
    Set<Employee> getEmployeesAssign(ShiftDTO shiftDTO) throws Exception;
    void removeEmployee(ShiftDTO shiftDTO, Role role, String id) throws Exception;
    void assigneEmployee(ShiftDTO shiftDTO, Role role, String id) throws Exception;
    void setEmployeesForShift(ShiftDTO shiftDTO, Map<Role, Set<Employee>> assignedRoles) throws Exception;
    Set<Employee> findAllAvailableEmployees(ShiftDTO shiftDTO) throws Exception;
    Map<Role, Set<Employee>> findAllEmployeePerRole(ShiftDTO shiftDTO) throws Exception;
    Map<Role, Integer> findAllRolesNeeded(ShiftDTO shiftDTO) throws Exception;
    void resetData() throws Exception;
}
