package HRandDelivery.DomainLayer.Repository;

import HRandDelivery.DTO.*;
import HRandDelivery.DataAccessLayer.HRdal.ShiftDAO;
import HRandDelivery.DataAccessLayer.HRdal.ShiftDAOImp;
import HRandDelivery.DataAccessLayer.HRdal.ShiftEmployeeDAO;
import HRandDelivery.DataAccessLayer.HRdal.shiftEmployeeDAOImp;
import HRandDelivery.DomainLayer.Branch;
import HRandDelivery.DomainLayer.HR.HRDomainLayer.Employee;
import HRandDelivery.DomainLayer.HR.HRDomainLayer.Shift;
import HRandDelivery.DomainLayer.HR.HRDomainLayer.TimeSlot;
import HRandDelivery.DomainLayer.HR.enums.Role;
import HRandDelivery.DomainLayer.HR.enums.ShiftType;


import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ShiftRepositoryImpl implements ShiftRepository {

    private final ShiftDAO shiftDAO;
    private final ShiftEmployeeDAO shiftEmployeeDAO;
    private  Map<String, Shift> loadedShifts;



    public ShiftRepositoryImpl() {
        this.shiftDAO = new ShiftDAOImp();
        this.shiftEmployeeDAO = new shiftEmployeeDAOImp();
        this.loadedShifts = new HashMap<>();
    }


    @Override
    public void saveShift(BranchDTO branch, LocalDate date, String timeSlotName, String startTime, String endTime) throws Exception {
        if(timeSlotName == "MORNING" || timeSlotName == "EVENING"){
            timeSlotName = timeSlotName == "MORNING" ? "M" : "E";
        }

        ShiftDTO dto = new ShiftDTO(branch, date, timeSlotName, startTime, endTime);
        shiftDAO.save(dto);


        String key = generateShiftKey(branch, date, timeSlotName);
        loadedShifts.put(key, new Shift(dto));

    }

    @Override
    public Shift findShift(BranchDTO branch, LocalDate date, String timeSlotName) throws Exception {
        String key = generateShiftKey(branch, date, timeSlotName);

        if (loadedShifts.containsKey(key)) {
            return loadedShifts.get(key);
        }

        if(timeSlotName == "MORNING" || timeSlotName == "EVENING"){
            timeSlotName = timeSlotName == "MORNING" ? "M" : "E";
        }
        ShiftDTO dto = shiftDAO.findShift(branch, date, timeSlotName);
        if (dto != null) {
            Shift shift = new Shift(dto);
            loadedShifts.put(key, shift);
            return shift;
        }

        return null;
    }

    @Override
    public Set<Shift> findAllBranchShift(LocalDate date, String timeSlotName) throws Exception {
        Set<Shift> result = new HashSet<>();
        List<ShiftDTO> allShifts = shiftDAO.findAllByDateAndTimeSlot(date, timeSlotName);

        // Normalize timeSlotName
        if ("MORNING".equals(timeSlotName)) timeSlotName = "M";
        else if ("EVENING".equals(timeSlotName)) timeSlotName = "E";

        for (ShiftDTO dto : allShifts) {
            if (dto.getDate().equals(date) && dto.getTimeSlotName().equals(timeSlotName)) {
                Shift shift = new Shift(dto);
                String key = generateShiftKey(dto.getBranch(), dto.getDate(), dto.getTimeSlotName());
                loadedShifts.putIfAbsent(key, shift);
                result.add(shift);
            }
        }

        return result;
    }


    @Override
    public Boolean isSet(BranchDTO branch, LocalDate date, String timeSlotName) throws Exception {

        if(timeSlotName == "MORNING" || timeSlotName == "EVENING"){
            timeSlotName = timeSlotName == "MORNING" ? "M" : "E";
        }

        String key = generateShiftKey(branch, date, timeSlotName);

        if (loadedShifts.containsKey(key)) {
            return true;
        }

        return shiftDAO.isSet(branch, date, timeSlotName);
    }

    @Override
    public Map<LocalDate, Map<ShiftType, Shift>> findShiftsForEmployee(String userId) throws Exception {
        Map<LocalDate, ShiftDTO> dtoMap = shiftEmployeeDAO.findShiftsForEmployee(userId);

        Map<LocalDate, Map<ShiftType, Shift>> result = new HashMap<>();

        for (Map.Entry<LocalDate, ShiftDTO> entry : dtoMap.entrySet()) {
            LocalDate date = entry.getKey();
            ShiftDTO dto = entry.getValue();

            ShiftType type = ShiftType.fromCode(dto.getTimeSlotName());
            Branch branch = new BranchRepositoryImpl().findBranch(dto.getBranch().getId());

            Shift shift = new Shift(branch, date, new TimeSlot(type, dto.getStartTime(), dto.getEndTime()));

            result.computeIfAbsent(date, d -> new HashMap<>()).put(type, shift);
        }

        return result;
    }

    @Override
    public void setRolesQuantities(ShiftDTO shiftDTO, Map<Role, Integer> rolesQuantities) throws Exception {
        shiftDAO.setRolesQuantities(shiftDTO, rolesQuantities);

        String key = generateShiftKey(shiftDTO.getBranch(), shiftDTO.getDate(), shiftDTO.getTimeSlotName());
        loadedShifts.remove(key);
    }

    @Override
    public Set<Employee> getEmployeesAssignByRole(ShiftDTO shiftDTO, Role role) throws Exception {
        Set<EmployeeDTO> dtoSet = shiftEmployeeDAO.getEmployeesAssignByRole(shiftDTO, role);

        Set<Employee> result = new HashSet<>();

        for(EmployeeDTO e : dtoSet){
            result.add(new Employee(e));
        }

        return result;
    }

    @Override
    public Set<Employee> getEmployeesAssign(ShiftDTO shiftDTO) throws Exception {
        Set<EmployeeDTO> dtoSet = shiftEmployeeDAO.getEmployeesAssign(shiftDTO);

        Set<Employee> result = new HashSet<>();

        for(EmployeeDTO e : dtoSet){
            result.add(new Employee(e));
        }

        return result;
    }

    @Override
    public void removeEmployee(ShiftDTO shiftDTO, Role role, String id) throws Exception {
        shiftEmployeeDAO.removeEmployee(shiftDTO,role,id);
    }

    @Override
    public void assigneEmployee(ShiftDTO shiftDTO, Role role, String id) throws Exception {
        shiftEmployeeDAO.assigneEmployee(shiftDTO,role,id);
    }

    @Override
    public void setEmployeesForShift(ShiftDTO shiftDTO, Map<Role, Set<Employee>> assignedRoles) throws Exception {
        Map<Role, Set<String>> employeesIdsPerRole = new HashMap<>();
        assignedRoles.forEach((role, emps) ->
                employeesIdsPerRole.put(role,
                        emps.stream()
                                .map(Employee::getId)
                                .collect(Collectors.toSet())
                )
        );

        shiftEmployeeDAO.setEmployeesForShift(shiftDTO,employeesIdsPerRole);
    }

    @Override
    public Set<Employee> findAllAvailableEmployees(ShiftDTO shiftDTO) throws Exception {
        Set<EmployeeDTO> dtoSet = shiftEmployeeDAO.findAllAvailableEmployees(shiftDTO);

        Set<Employee> result = new HashSet<>();

        for(EmployeeDTO e : dtoSet){
            result.add(new Employee(e));
        }

        return result;
    }

    @Override
    public Map<Role, Set<Employee>> findAllEmployeePerRole(ShiftDTO shiftDTO) throws Exception {
        Map<Role, Set<EmployeeDTO>> dtoSet = shiftEmployeeDAO.findAllEmployeePerRole(shiftDTO);
        Map<Role, Set<Employee>> result = new HashMap<>();

        for (Map.Entry<Role, Set<EmployeeDTO>> entry : dtoSet.entrySet()) {
            Role role = entry.getKey();
            Set<EmployeeDTO> dtoEmployees = entry.getValue();

            Set<Employee> employees = new HashSet<>();

            for(EmployeeDTO e : dtoEmployees){
                employees.add(new Employee(e));
            }

            result.put(role, employees);
        }

        return result;
    }

    @Override
    public Map<Role, Integer> findAllRolesNeeded(ShiftDTO shiftDTO) throws Exception {
        return shiftEmployeeDAO.findAllRolesNeeded(shiftDTO);
    }

    @Override
    public void resetData() throws Exception {
        shiftDAO.reset();
        shiftEmployeeDAO.reset();
        loadedShifts.clear();
    }

    private String generateShiftKey(BranchDTO branch, LocalDate date, String timeSlotName) {
        if ("MORNING".equals(timeSlotName)) timeSlotName = "M";
        if ("EVENING".equals(timeSlotName)) timeSlotName = "E";
        return branch.getId() + "_" + date.toString() + "_" + timeSlotName;
    }

}
