package HRandDelivery.DomainLayer.HR.HRDomainLayer;

import HRandDelivery.DTO.BranchDTO;
import HRandDelivery.DTO.ShiftDTO;
import HRandDelivery.DomainLayer.Branch;
import HRandDelivery.DomainLayer.HR.enums.Role;
import HRandDelivery.DomainLayer.HR.enums.ShiftType;
import HRandDelivery.DomainLayer.Repository.BranchRepositoryImpl;
import HRandDelivery.DomainLayer.Repository.ShiftRepository;
import HRandDelivery.DomainLayer.Repository.ShiftRepositoryImpl;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Shift {

    private LocalDate date;
    private TimeSlot timeSlot;
    private Branch branch;

    private ShiftRepository repo;


    public Shift(Branch branch, LocalDate date, TimeSlot timeSlot) {
        this.branch = branch;
        this.date = date;
        this.timeSlot = timeSlot;

        this.repo = new ShiftRepositoryImpl();
    }

    public Shift(ShiftDTO shiftDTO) throws Exception {
        this.branch = new BranchRepositoryImpl().findBranch(shiftDTO.getBranch().getId());
        this.date = shiftDTO.getDate();
        this.timeSlot = new TimeSlot(ShiftType.fromCode(shiftDTO.getTimeSlotName()),shiftDTO.getStartTime(),shiftDTO.getEndTime());

        this.repo = new ShiftRepositoryImpl();
    }

    public void setRolesNeeded(Map<Role, Integer> rolesQuantities) throws Exception {
        repo.setRolesQuantities(getShiftDTO(), rolesQuantities);
    }

    public Set<Employee> getEmployeesAssignByRole(Role role) throws Exception {
        return repo.getEmployeesAssignByRole(getShiftDTO(), role);
    }

    public Set<Employee> rolesAssigned() throws Exception {
        return repo.getEmployeesAssign(getShiftDTO());
    }

    public void removeEmployee(Role role, Employee employee) throws Exception {
        repo.removeEmployee(getShiftDTO(), role, employee.getId());
    }

    public void addEmployee(Role role, Employee employee) throws Exception {
        repo.assigneEmployee(getShiftDTO(), role, employee.getId());
    }

    public void assignRoles(Map<Role, Set<Employee>> assignedRoles) throws Exception {
        repo.setEmployeesForShift(getShiftDTO(), assignedRoles);
    }

    public Map<Role, Set<Employee>> getRolesAssigned() throws Exception {
        return repo.findAllEmployeePerRole(getShiftDTO());
    }

    public Set<Employee> getAvailableEmployees() throws Exception {
        return repo.findAllAvailableEmployees(getShiftDTO());
    }

    public Map<Role, Integer> getRolesNeeded() throws Exception {
        return repo.findAllRolesNeeded(getShiftDTO());
    }

    public TimeSlot getTimeSlot(){
        return timeSlot;
    }

    public Branch getBranch(){
        return branch;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Branch: ").append(branch != null ? branch.getBranchName() + " " + branch.getId() : "N/A").append("\n");
        sb.append("Shift Date: ").append(date != null ? date : "N/A").append("\n");
        sb.append("Time Slot: ").append(timeSlot != null ? timeSlot.getName() : "N/A").append(" " + timeSlot.getStartTime() +  " - " + timeSlot.getEndTime()).append("\n\n");

        sb.append("Roles Needed:\n");
        try {
            if (getRolesNeeded() != null && !getRolesNeeded().isEmpty()) {
                for (Map.Entry<Role, Integer> entry : getRolesNeeded().entrySet()) {
                    sb.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                }
            } else {
                sb.append("  No roles needed\n");
            }

            sb.append("\nRoles Assigned:\n");

            if (getRolesAssigned() != null && !getRolesAssigned().isEmpty()) {
                for (Map.Entry<Role, Set<Employee>> entry : getRolesAssigned().entrySet()) {
                    sb.append("  ").append(entry.getKey()).append(": ");
                    if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                        String names = entry.getValue().stream()
                                .filter(Objects::nonNull)
                                .map(e -> e.getName() + "(" + e.getId() + ")")
                                .collect(Collectors.joining(", "));
                        sb.append(names).append("\n");
                    } else {
                        sb.append("None\n");
                    }
                }
            } else {
                sb.append("  No roles assigned\n");
            }

            sb.append("\nAvailable Employees:\n");

            if (getAvailableEmployees() != null && !getAvailableEmployees().isEmpty()) {
                for (Employee e : getAvailableEmployees()) {
                    if (e != null) {
                        sb.append("  ")
                                .append(e.getName())
                                .append(" (").append(e.getId()).append(")");

                        Set<Role> roles = e.getRoles();
                        if (roles != null && !roles.isEmpty()) {
                            sb.append(" - Roles: ");
                            for (Role r : roles) {
                                sb.append(r.name()).append(", ");
                            }

                            sb.setLength(sb.length() - 2);
                        }

                        sb.append("\n");
                    }
                }
            } else {
                sb.append("  No available employees\n");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return sb.toString();
    }

    public void submitAvailability(Employee employee) throws Exception {
        getAvailableEmployees().add(employee);
    }

    private ShiftDTO getShiftDTO(){
        return new ShiftDTO(new BranchDTO(branch.getBranchName(),branch.getId(),branch.getAddress(),branch.getContactName(),branch.getContactNumber()), date, timeSlot.getName().name(), timeSlot.getStartTime(), timeSlot.getEndTime());
    }
}
