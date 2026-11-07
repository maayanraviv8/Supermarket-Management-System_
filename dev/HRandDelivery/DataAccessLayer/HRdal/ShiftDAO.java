package HRandDelivery.DataAccessLayer.HRdal;

import HRandDelivery.DTO.BranchDTO;
import HRandDelivery.DTO.ShiftDTO;
import HRandDelivery.DomainLayer.HR.enums.Role;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ShiftDAO {
    // key - date, time slot, branch
    void save(ShiftDTO shift) throws SQLException;
    ShiftDTO findShift(BranchDTO branch, LocalDate date, String timeSlotName) throws Exception;
    Boolean isSet(BranchDTO branch, LocalDate date, String timeSlotName) throws Exception;
    void setRolesQuantities(ShiftDTO shiftDTO, Map<Role, Integer> rolesQuantities) throws Exception;
    void reset() throws Exception;
    List<ShiftDTO> findAllByDateAndTimeSlot(LocalDate date, String timeSlotName) throws Exception;

}
