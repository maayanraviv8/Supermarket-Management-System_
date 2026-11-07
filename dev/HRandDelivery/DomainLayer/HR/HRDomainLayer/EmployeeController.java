package HRandDelivery.DomainLayer.HR.HRDomainLayer;

import HRandDelivery.DTO.BranchDTO;
import HRandDelivery.DTO.ShiftDTO;
import HRandDelivery.DomainLayer.Branch;
import HRandDelivery.DomainLayer.Delivery.enums.License;
import HRandDelivery.DomainLayer.HR.enums.Role;
import HRandDelivery.DomainLayer.HR.enums.ShiftType;
import HRandDelivery.DomainLayer.Repository.*;
import HRandDelivery.Utils.Response;


import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EmployeeController {


    private ShiftRepository shifts;
    private EmployeeRepository employees;
    private BranchRepository branches;

    public EmployeeController(){
        this.employees = new EmployeeRepImpl();
        this.branches = new BranchRepositoryImpl();
        this.shifts = new ShiftRepositoryImpl();
    }

    public Response validateDet(String id) throws Exception {

        Employee e = employees.findEmployee(id);

        if(e == null){
            return new Response(false, "Employee not found, please try again.");
        }

        if(!e.getActive()){
            return new Response(false, "Your account is not Active.");
        }

        Role role = e.getHighestPermission();

        return new Response(true,e.getName() + "/" + role.toString());
    }


    public Response addEmployee(String name, String id, String bank, String workCon, String roles, String branchId, String licenses) throws Exception {

        // checking if its new employee
        if(employees.isValidEmployee(id)){
            return new Response(false, "Employee is already exist.");
        }

        // bank account details
        BankAccount bankAccount = new BankAccount(bank);

        // work conditions details
        WorkConditions workConditions = new WorkConditions(workCon);

        // roles details
        String[] roleNames = roles.split(" ");
        Set<Role> role = new HashSet<>();
        for (String roleName : roleNames) {
            role.add(Role.valueOf(roleName.trim().toUpperCase()));
        }

        // branch details
        Branch branch = branches.findBranch(branchId);

        // license details
        Set<License> licensesSet = new HashSet<>();
        if(role.contains(Role.DRIVER)) {
            if(licenses.isEmpty()){
                return new Response(false, "Driver Has to be at least one License.");
            }

            String[] licensesName = licenses.split(" ");
            for (String l : licensesName) {
                licensesSet.add(License.valueOf(l.trim().toUpperCase()));
            }
        }

        Employee employee = new Employee(name, id, bankAccount, workConditions, role, branches.findBranch(branchId), licensesSet);
        employees.saveEmployee(name, id, bankAccount, workConditions, role, getBranchDTO(branch), licensesSet);

        return new Response(true, "Employee added successfully." );

    }

    public Response removeEmployee(String id) throws Exception {

        if(!employees.isValidEmployee(id)){
            return new Response(false, "Employee not found, please try again.");
        }
        Employee e = employees.findEmployee(id);
        e.setActive(false);
        return new Response(true, "Employee has removed.");

    }

    public Response displayEmployeeDetails(String id) throws Exception {

        if(!employees.isValidEmployee(id)){
            return new Response(false, "Employee not found, please try again." );
        }
        Employee e = employees.findEmployee(id);
        String employeeDits = e.toString();
        return new Response(true, employeeDits);

    }


    public Response displayShiftDetails(String branchId, String shiftDate, String timeSlot) throws Exception {

        Shift shift;
        try {
            shift = getShift(branchId, shiftDate, timeSlot);
        } catch (Exception e) {
            return new Response(false, e.getMessage());
        }

        String details = shift.toString();

        return new Response(true, details);

    }

    public Response updateEmployeeName(String id,String newName) throws Exception {
        if(!employees.isValidEmployee(id)){
            return new Response(false, "Employee not found, please try again." );
        }
        Employee employee = employees.findEmployee(id);
        employee.setName(newName);

        return new Response(true, "Name updated successfully.");
    }


    public Response updateEmployeeBankAccount(String id,String bankDetails) throws Exception {
        if(!employees.isValidEmployee(id)){
            return new Response(false, "Employee not found, please try again." );
        }

        // bank account details
        BankAccount bankAccount = new BankAccount(bankDetails);

        employees.findEmployee(id).setBank(bankAccount);

        return new Response(true, "Bank updated successfully.");

    }

    public Response updateEmployeeWorkConditions(String id,String workConditionsDetails) throws Exception {
        if(!employees.isValidEmployee(id)){
            return new Response(false, "Employee not found, please try again." );
        }

        // work conditions details
        WorkConditions workConditions = new WorkConditions(workConditionsDetails);

        employees.findEmployee(id).setWorkConditions(workConditions);

        return new Response(true, "Work Conditions updated successfully.");

    }

    public Response updateEmployeeRoles(String id,String rolesDetails, String license) throws Exception {
        if(!employees.isValidEmployee(id)){
            return new Response(false, "Employee not found, please try again." );
        }

        // roles details
        String[] roleNames = rolesDetails.split(" ");
        Set<Role> roles = new HashSet<>();
        for (String roleName : roleNames) {
            roles.add(Role.valueOf(roleName.trim().toUpperCase()));
        }

        // license details
        Set<License> licensesSet = new HashSet<>();
        if(roles.contains(Role.DRIVER)) {
            if(license.isEmpty()){
                return new Response(false, "Driver Has to be at least one License.");
            }

            String[] licensesName = license.split(" ");
            for (String l : licensesName) {
                licensesSet.add(License.valueOf(l.trim().toUpperCase()));
            }
        }
        employees.findEmployee(id).setRoles(roles);
        employees.findEmployee(id).setLicenses(licensesSet);

        return new Response(true, "Roles updated successfully.");
    }

    public Response setShiftRequirements(String branchId, String shiftDate, String timeSlot, String startTime, String endTime, Map<Role, Integer> rolesQuantities) throws Exception {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = LocalDate.parse(shiftDate, formatter);
        Branch branch = branches.findBranch(branchId);

        Shift shift = new Shift(branch, date, new TimeSlot(ShiftType.fromCode(timeSlot), startTime, endTime));
        shifts.saveShift(getBranchDTO(branch),date, timeSlot, startTime, endTime);

        Integer smCount = rolesQuantities != null ? rolesQuantities.get(Role.SHIFT_MANAGER) : null;
        if (smCount == null || smCount < 1) {
            return new Response(false, "A shift cannot be created without assigning a shift manager, Please try again.");
        }
        shift.setRolesNeeded(rolesQuantities);

        return new Response(true, "Shift requirements updated successfully.");
    }

    public Response handleRemoveEmployeeFromShift(String branchId,String shiftDate, String timeSlot, String employeeId, Role role) throws Exception {
        Shift shift;
        try {
            shift = getShift(branchId, shiftDate, timeSlot);
        } catch (IllegalArgumentException e) {
            return new Response(false, e.getMessage());
        }

        if(!employees.isValidEmployee(employeeId)){
            return new Response(false, "Employee not found, please try again.");
        }

        Set<Employee> assignedEmployees = shift.getEmployeesAssignByRole(role);
        if (assignedEmployees == null || !ContainId(assignedEmployees,employees.findEmployee(employeeId))) {
            return new Response(false, "Employee not found in shift, please try again.");
        }

        if(role.equals(Role.SHIFT_MANAGER) && shift.getEmployeesAssignByRole(role).size() == 1){
            return new Response(false, "You cannot remove the only shift manager from the shift. Removal denied.");
        }

        shift.removeEmployee(role, employees.findEmployee(employeeId));

        return new Response(true, "Employee removed from shift successfully.");

    }

    public Response handleAddEmployeeToShift(String branchId, String shiftDate, String timeSlot, String employeeId, Role role) throws Exception {
        Shift shift;
        try {
            shift = getShift(branchId, shiftDate, timeSlot);
        } catch (IllegalArgumentException e) {
            return new Response(false, e.getMessage());
        }

        if (!employees.isValidEmployee(employeeId)) {
            return new Response(false, "Employee not found, please try again.");
        }

        Employee employee = employees.findEmployee(employeeId);

        if (ContainId(shift.rolesAssigned(), employee)) {
            return new Response(false, "Employee is already assigned to this shift.");
        }

        if (!employee.getRoles().contains(role)) {
            return new Response(false, "Cannot assign employee " + employee.getName() +" to role " +role.toString() +".");
        }

        if(!Objects.equals(employee.getBranch().getId(), shift.getBranch().getId())){ //Verifying if the employee is from the shift branch
            return new Response(false, "Employee " + employeeId + " is not assigned to the branch of the shift (Branch ID: " + shift.getBranch().getId() + "). Please try again.");
        }

        shift.addEmployee(role, employee);
        return new Response(true, "Employee added to shift successfully.");
    }


    public Response displayEmployeesAndRole() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("List of all Employees and Their Roles:\n");

        for (Employee e : employees.findAllEmployees()) {
            if (e == null || !e.getActive()) continue;

            sb.append("- ")
                    .append(e.getName())
                    .append(" (ID: ").append(e.getId()).append(")")
                    .append(" Branch Id: ").append(e.getBranch().getId());

            Set<Role> roles = e.getRoles();
            if (roles != null && !roles.isEmpty()) {
                sb.append(" | Roles: ");
                for (Role role : roles) {
                    sb.append(role.name()).append(", ");
                }
                // Remove last comma and space
                sb.setLength(sb.length() - 2);
            } else {
                sb.append(" | No roles assigned.");
            }

            sb.append("\n");
        }

        return new Response(true, sb.toString());
    }

    public Response displayBranches() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("List of all Branches:\n");

        for (Branch b : branches.findAllBranches()) {
            sb.append("- ")
                    .append(b.getBranchName())
                    .append(" ID: ").append(b.getId());

            sb.append("\n");
        }

        return new Response(true, sb.toString());
    }

    public Response employeeShiftAssignment(String branchId, String shiftDate, String timeSlot, Map<Role, String> rolesAndEmployees ) throws Exception {
        Shift shift;
        try {
            shift = getShift(branchId, shiftDate, timeSlot);
        } catch (IllegalArgumentException e) {
            return new Response(false, e.getMessage());
        }

        Map<Role, Set<Employee>> assignedRoles = new HashMap<>();

        for (Role r: rolesAndEmployees.keySet()){

            List<String> employeesIds = Arrays.stream(rolesAndEmployees.get(r).split(" ")).toList();
            Set<Employee> employeeSet = new HashSet<>();
            if(!rolesAndEmployees.get(r).isEmpty()) {
                for (String id : employeesIds) {
                    Employee e = employees.findEmployee(id);

                    if (e == null || !e.getActive()) { //Verifying that the employee exists in the system and the employee is active
                        return new Response(false, "Employee " + id + " is illegal, Please try again.");
                    }

                    if (!e.getRoles().contains(r)) { //Verifying if the employee is eligible for this role
                        return new Response(false, "Employee " + id + " cannot be assigned to the role '" + r + "'. Please try again.");
                    }

                    if(isEmployeeAlreadyAssigned(e,assignedRoles)){
                        return new Response(false, "Employee " + id + " already assigned to another role in the same shift. Please try again.");
                    }

                    if(!Objects.equals(e.getBranch().getId(), shift.getBranch().getId())){ //Verifying if the employee is from the shift branch
                        return new Response(false, "Employee " + id + " is not assigned to the branch of the shift (Branch ID: " + shift.getBranch().getId() + "). Please try again.");
                    }

                    employeeSet.add(employees.findEmployee(id));
                }
            }
            assignedRoles.put(r,employeeSet);
        }

        if(assignedRoles.get(Role.SHIFT_MANAGER).isEmpty()){
            return new Response(false, "A shift cannot be created without assigning a shift manager, Please try again..");
        }

        shift.assignRoles(assignedRoles);

        return alertIfEmployeeNotAvailabile(shift);
    }


    public Response setShiftSubmissionDeadline(String deadline) throws Exception {
        String[] time = deadline.split(":");
        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[1]);
        employees.setDeadLine(LocalTime.of(hour, minute));

        return new Response(true,"Deadline updated successfully.");
    }

    public Response employeeShiftAvailability(String employeeId, Map<LocalDate, List<String>> availability) throws Exception {

        Employee employee = employees.findEmployee(employeeId);

        if(employee == null){
            return new Response(false, "Employee not found, please try again.");
        }

        if (!employee.getActive()) {
            return new Response(false, "Your account is not Active.");
        }

        Branch branch = branches.findBranch(employee.getBranch().getId());

        for (LocalDate date : availability.keySet()){
            List<String> slots = availability.get(date);
            for(String slot: slots) {
                if(shifts.isSet(getBranchDTO(branch), date, slot)) {
                    shifts.findShift(getBranchDTO(branch), date, slot).submitAvailability(employee);
                }
                else {
                    return new Response(false, "Shift " + date + " not set yet." );
                }
            }
        }
        return new Response(true, "Submitted availability successfully.");
    }


    public Response editShiftTimeSlot(String branchId, String shiftDate, String timeSlot, String start, String end) {
        Shift shift;
        try {
            shift = getShift(branchId, shiftDate, timeSlot);
        } catch (Exception e) {
            return new Response(false, e.getMessage());
        }
        shift.getTimeSlot().setStartTime(start);
        shift.getTimeSlot().setEndTime(end);

        return new Response(true,"Time slot updated successfully.");
    }

    public Response checkValidTimeToSubmit() throws Exception {
        if(LocalTime.now().isAfter(employees.getDeadLine())){
            return new Response(false, "You can't submit availability at this time. The submission period has already passed.");
        }

        return new Response(true,"");

    }

    public Response alertNotEnoughEmployeeAvailability(String branchId, String shiftDate, String timeSlot) throws Exception {

        Shift shift = getShift(branchId, shiftDate, timeSlot);
        Set<Employee> availableEmployees = shift.getAvailableEmployees();
        Map<Role,Integer> rolesNeeded = shift.getRolesNeeded();

        Map<Role, Integer> roleAvailabilityCount = new HashMap<>();
        for (Employee employee : availableEmployees) {
            for (Role role : employee.getRoles()) {
                roleAvailabilityCount.put(role, roleAvailabilityCount.getOrDefault(role, 0) + 1);
            }
        }

        StringBuilder msg = new StringBuilder();

        for (Map.Entry<Role, Integer> entry : rolesNeeded.entrySet()) {
            Role role = entry.getKey();
            int needed = entry.getValue();
            int available = roleAvailabilityCount.getOrDefault(role, 0);

            if (available < needed) {
                String message = "Not enough available employees for role '" + role +
                        "'. Needed: " + needed + ", Available: " + available;
                msg.append("\n").append(message);
            }
        }

        return new Response(true, msg.toString());
    }

    public Response employeeFutureShifts(String userId) throws Exception {

        StringBuilder ret = new StringBuilder();

        Map<LocalDate, Map<ShiftType, Shift>> allShifts = shifts.findShiftsForEmployee(userId);

        for (Map.Entry<LocalDate, Map<ShiftType, Shift>> dateEntry : allShifts.entrySet()) {
            LocalDate date = dateEntry.getKey();
            Map<ShiftType, Shift> typeShiftMap = dateEntry.getValue();

            for (Map.Entry<ShiftType, Shift> shiftEntry : typeShiftMap.entrySet()) {
                ShiftType shiftType = shiftEntry.getKey();
                Shift shift = shiftEntry.getValue();

                if (ContainId(shift.rolesAssigned(), employees.findEmployee(userId))) {
                    String formattedDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    ret.append(displayShiftDetails(shift.getBranch().getId(),formattedDate, shiftType == ShiftType.MORNING ? "M" : "E" ).getMessage());
                }
            }
        }
        if (ret.isEmpty()) {
            ret = new StringBuilder("You haven't been assigned to a shift yet.");
        }
        return new Response(true, ret.toString());

    }

    private Shift getShift(String branchId, String shiftDate, String timeSlot) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = LocalDate.parse(shiftDate, formatter);

        if (date.isBefore(LocalDate.now().minusYears(7))) {
            throw new IllegalArgumentException("This date is too far in the past. The system only supports dates within the last 7 years.");
        }

        if (!shifts.isSet(getBranchDTO(branches.findBranch(branchId)), date, timeSlot)) {
            throw new IllegalArgumentException("No shift found for the given date. Please set a shift before proceeding.");
        }

        Shift shift = shifts.findShift(getBranchDTO(branches.findBranch(branchId)), date, timeSlot);

        if (shift == null) {
            throw new IllegalArgumentException("Shift not found for the given time slot.");
        }

        return shift;
    }

    private Response alertIfEmployeeNotAvailabile(Shift shift) throws Exception {

       Map<Role, Set<Employee>> rolesAssigned = shift.getRolesAssigned();
       Set<Employee> availableEmployees = shift.getAvailableEmployees();

       StringBuilder msg = new StringBuilder();

        for (Map.Entry<Role, Set<Employee>> entry : rolesAssigned.entrySet()) {
            Role role = entry.getKey();
            Set<Employee> assignedEmployees = entry.getValue();

            for (Employee employee : assignedEmployees) {
                if (!ContainId(availableEmployees, employee)) {
                    String message = "Employee " + employee.getId() +
                            " is assigned to role '" + role +
                            "' but did not submit availability.";
                    msg.append("\n").append(message);
                }
            }
        }
        return new Response(true, msg.toString());
    }

    private boolean isEmployeeAlreadyAssigned(Employee e, Map<Role, Set<Employee>> assignedRoles) {
        for (Set<Employee> employeesInRole : assignedRoles.values()) {
            if (ContainId(employeesInRole, e)) {
                return true;
            }
        }
        return false;
    }

    private BranchDTO getBranchDTO(Branch branch){
        return new BranchDTO(branch.getBranchName(),branch.getId(),branch.getAddress(),branch.getContactName(),branch.getContactNumber());
    }

    public Response addBranch(String name, String id, String address, String contactName, String contactNumber) throws Exception {
        if(branches.findBranch(id) != null){
            return new Response(false, "Branch is already exist.");
        }
        branches.createBranch(name,id,address,contactName,contactNumber);
        return new Response(true, "Branch added successfully." );
    }

    public Response displayDrivers() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("List of all Drivers and Their Id's:\n");

        for (Employee e : employees.findAllByRole(Role.DRIVER)) {
            if (e == null || !e.getActive()) continue;

            sb.append("- ")
                    .append(e.getName())
                    .append(" (ID: ").append(e.getId()).append(")")
                    .append(" License: ").append(e.getLicenses());

            sb.append("\n");
        }

        return new Response(true, sb.toString());

    }

    public Employee getEmployeeById(String employeeId) throws Exception {
        return employees.findEmployee(employeeId);
    }

    public Boolean IsAssignToShift(Branch b, LocalDate date, ShiftType shiftType, Employee employee) throws Exception {
        return ContainId(shifts.getEmployeesAssign(new ShiftDTO(getBranchDTO(b), date, shiftType.toString())), employee);
    }

    public boolean isSetShift(Branch b, LocalDate date, ShiftType shiftType) throws Exception {
        return shifts.isSet(getBranchDTO(b), date, shiftType.toString());
    }

    public boolean hasStoreKeepInShift(LocalDate date, ShiftType shiftType, Branch b) throws Exception {
        Shift shift = shifts.findShift(getBranchDTO(b), date, shiftType.toString());
        Set<Employee> storekeepers= shift.getEmployeesAssignByRole(Role.STORE_KEEPER);
        return !storekeepers.isEmpty();
    }

    private boolean ContainId(Set<Employee> assignedEmployees, Employee employee) {

        for(Employee e : assignedEmployees){
            if(e.getId().equals(employee.getId())){
                return true;
            }
        }

        return false;
    }

    public void resetData() throws Exception {
        employees.resetData();
        shifts.resetData();
        branches.resetData();
    }

    public void resetAdmin() throws Exception {
        employees.resetAdmin();
        branches.resetAdmin();
    }


    public Shift getShiftWithEmpByDateAndType(LocalDate localDate, String shiftType) throws Exception {
        Set<Shift> shiftsSet = shifts.findAllBranchShift(localDate, shiftType);
        if(shiftsSet.isEmpty()) {
            return null; // No shifts found for the given date and type with employees
        }

        for (Shift shift : shiftsSet) {
            if (!shift.getEmployeesAssignByRole(Role.SHIFT_MANAGER).isEmpty()) {
                return shift; // Return the first shift that has assigned employees
            }
        }

        return null; // No shifts found with assigned employees
    }

    public Employee getRoleInShift(Shift shift, Role role) throws Exception {
        Set<Employee> employees = shift.getEmployeesAssignByRole(role);
        if (employees.isEmpty()) {
            return null; // No employee assigned to this role
        }
        return employees.iterator().next(); // Return the first employee assigned to this role
    }

    public String getCurrDeadLine() throws Exception {
        return employees.getDeadLine().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public List<Employee> findEmployeesByRole(Role role) throws Exception {
        return employees.findAllByRole(role);
    }

    public Branch getBranchById(String id) throws Exception {
        return branches.findBranch(id);
    }
}
