package HRandDelivery.DomainLayer;

import HRandDelivery.DataAccessLayer.Database;
import HRandDelivery.DomainLayer.Delivery.DeliveryDomainLayer.Delivery;
import HRandDelivery.DomainLayer.Delivery.DeliveryDomainLayer.DeliveryController;
import HRandDelivery.DomainLayer.Delivery.DeliveryDomainLayer.Site;
import HRandDelivery.DomainLayer.Delivery.DeliveryDomainLayer.Truck;
import HRandDelivery.DomainLayer.Delivery.enums.License;
import HRandDelivery.DomainLayer.HR.HRDomainLayer.Employee;
import HRandDelivery.DomainLayer.HR.HRDomainLayer.EmployeeController;
import HRandDelivery.DomainLayer.HR.HRDomainLayer.Shift;
import HRandDelivery.DomainLayer.HR.enums.Role;
import HRandDelivery.DomainLayer.HR.enums.ShiftType;
import HRandDelivery.Utils.Response;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HRandDeliveryDomainService {

    private static HRandDeliveryDomainService instance;

    private EmployeeController employeeController;
    private DeliveryController deliveryController;

    private final Connection conn;

    private HRandDeliveryDomainService() {
        this.employeeController = new EmployeeController();
        this.deliveryController = new DeliveryController();
        Connection tmp;
        try {
            tmp = Database.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Cannot open DB connection", e);
        }
        this.conn = tmp;
    }

    public static HRandDeliveryDomainService getInstance() {
        if (instance == null) {
            instance = new HRandDeliveryDomainService();
        }
        return instance;
    }

    public Response addEmployee(String name, String id, String bank, String workCon, String roles, String branch, String licenses) throws Exception {
        return employeeController.addEmployee(name, id, bank, workCon, roles, branch, licenses);
    }

    public Response setShiftRequirements(String branchId, String shiftDate, String timeSlot, String startTime, String endTime, Map<Role, Integer> rolesQuantities) throws Exception {
        return employeeController.setShiftRequirements(branchId, shiftDate, timeSlot, startTime, endTime, rolesQuantities);
    }

    public Response setShiftSubmissionDeadline(String deadline) throws Exception {
        return employeeController.setShiftSubmissionDeadline(deadline);
    }

    public Response validateDet(String id) throws Exception {
        return employeeController.validateDet(id);
    }

    public Response displayEmployeesAndRole() throws Exception {
        return employeeController.displayEmployeesAndRole();
    }

    public Response removeEmployee(String id) throws Exception {
        return employeeController.removeEmployee(id);
    }

    public Response displayEmployeeDetails(String id) throws Exception {
        return employeeController.displayEmployeeDetails(id);
    }

    public Response updateEmployeeName(String id, String newDetails) throws Exception {
        return employeeController.updateEmployeeName(id, newDetails);
    }

    public Response updateEmployeeBankAccount(String id, String newDetails) throws Exception {
        return employeeController.updateEmployeeBankAccount(id, newDetails);
    }

    public Response updateEmployeeWorkConditions(String id, String newDetails) throws Exception {
        return employeeController.updateEmployeeWorkConditions(id, newDetails);
    }

    public Response updateEmployeeRoles(String id, String newDetails, String license) throws Exception {
        return employeeController.updateEmployeeRoles(id, newDetails, license);
    }

    public Response displayShiftDetails(String branchId, String shiftDate, String timeSlot) throws Exception {
        return employeeController.displayShiftDetails(branchId, shiftDate, timeSlot);
    }

    public Response handleRemoveEmployeeFromShift(String branchId, String shiftDate, String timeSlot, String employeeId, Role role) throws Exception {
        return employeeController.handleRemoveEmployeeFromShift(branchId, shiftDate, timeSlot, employeeId, role);
    }

    public Response handleAddEmployeeToShift(String branchId, String shiftDate, String timeSlot, String employeeId, Role role) throws Exception {
        return employeeController.handleAddEmployeeToShift(branchId, shiftDate, timeSlot, employeeId, role);
    }

    public Response editShiftTimeSlot(String branchId, String shiftDate, String timeSlot, String start, String end) {
        return employeeController.editShiftTimeSlot(branchId, shiftDate, timeSlot, start, end);
    }

    public Response alertNotEnoughEmployeeAvailability(String branchId, String shiftDate, String timeSlot) throws Exception {
        return employeeController.alertNotEnoughEmployeeAvailability(branchId, shiftDate, timeSlot);
    }

    public Response employeeShiftAssignment(String branchId, String shiftDate, String timeSlot, Map<Role, String> rolesAndEmployees) throws Exception {
        return employeeController.employeeShiftAssignment(branchId, shiftDate, timeSlot, rolesAndEmployees);
    }

    public Response checkValidTimeToSubmit() throws Exception {
        return employeeController.checkValidTimeToSubmit();
    }

    public Response employeeShiftAvailability(String userId, Map<LocalDate, List<String>> availability) throws Exception {
        return employeeController.employeeShiftAvailability(userId, availability);
    }

    public Response employeeFutureShifts(String userId) throws Exception {
        return employeeController.employeeFutureShifts(userId);
    }

    public Response displayBranches() throws Exception {
        return employeeController.displayBranches();
    }

    public Response addBranch(String name, String id, String address, String contactName, String contactNumber) throws Exception {
        boolean success = employeeController.addBranch(name, id, address, contactName, contactNumber).isSuccess() &
                            deliveryController.addSite(address, contactName, contactNumber).isSuccess();
        return new Response(success,
                        success ? "Branch added successfully."
                        : "Failed to add branch. Please check the details and try again.");
    }

    public Response addTruck(String truckNumber, String model, int netWeight, int maxWeight, String license) throws Exception {
        return deliveryController.addTruck(truckNumber, model, netWeight, maxWeight, license);
    }

    public Response addSite(String address, String contactName, String contactNumber) throws Exception {
        return deliveryController.addSite(address, contactName, contactNumber);
    }

    public Response addDelivery(String shiftDate, String departure, String timeSlot, String truckId, String driverId, String s, String d) throws Exception {

        Employee driver = employeeController.getEmployeeById(driverId);
        Truck truck = deliveryController.getTruckByNum(truckId);
        ShiftType shiftType = ShiftType.fromCode(timeSlot);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = LocalDate.parse(shiftDate, formatter);

        if (!driver.getLicenses().contains(truck.getLicense())) {
            return new Response(false, "The truck license does not match the driver's license. Please try again.");
        }

        Set<Branch> branches = new HashSet<>();

        Boolean driverAssign = false;

        Site source = deliveryController.getSiteByAddress(s);
        if (source instanceof Branch) {
            branches.add((Branch) source);
            driverAssign = employeeController.IsAssignToShift((Branch) source, date, shiftType, driver);
        }

        Set<Site> destinations = new HashSet<>();

        String[] splitAddresses = d.split(",");
        for (String addr : splitAddresses) {
            String trimmed = addr.trim();
            if (!trimmed.isEmpty()) {
                destinations.add(deliveryController.getSiteByAddress(addr));
            }
        }

        for (Site site : destinations) {
            if (site instanceof Branch) {
                branches.add((Branch) site);
            }
        }

        //Verify that a storekeeper is assigned in all branches during the shift time.
        for (Branch b : branches) {
            if (!employeeController.isSetShift(b, date, shiftType)) {
                return new Response(false, "Shift not set yet at branch " + b.getBranchName() + " . Please try again.");
            }
            if (!employeeController.hasStoreKeepInShift(date, shiftType, b)) {
                return new Response(false, "Branch id " + b.getId() + " must have a storekeeper assigned during the shift. Please try again.");
            }

            driverAssign = employeeController.IsAssignToShift(b, date, shiftType, driver);
        }

        //Verify that the driver is assigned during the shift time for one branch.
        if (!driverAssign) {
            return new Response(false, driver.getName() + " not assigned to any branch source/departure during the shift. Please try again.");
        }

        return deliveryController.addDelivery(date, shiftType, departure, driver, truck, source, destinations);
    }

    public Response displayDrivers() throws Exception {
        return employeeController.displayDrivers();
    }

    public Response displaySites() throws Exception {
        return deliveryController.displaySites();
    }

    public Response displayTrucks() throws Exception {
        return deliveryController.displayTrucks();
    }

    public void reset() throws Exception {
        employeeController.resetData();
        deliveryController.resetData();
    }

    public void resetAll() throws Exception {
        reset();
        employeeController.resetAdmin();
    }

    public void removeSite(String address) throws Exception {
        deliveryController.removeSite(address);
    }

    public void updateSiteAddress(String currAddress, String newAddress) throws Exception {
        deliveryController.updateSiteAddress(currAddress, newAddress);
    }

    public Response addOrderToDelivery(String supplierAddress, int supplyDay) throws Exception {
        LocalDate today = LocalDate.now();
        // In a case where the supply day is 0, we will add the delivery for the next day.
        Site supplierSite = deliveryController.getSiteByAddress(supplierAddress);
        if(supplyDay == 0) {
            Delivery delivery = deliveryController.getFirstDelivery();
            if (delivery == null) {
                Shift shift = employeeController.getShiftWithEmpByDateAndType(today.plusDays(1), "M");
                if (shift == null) {
                    throw new IllegalStateException("Need to set a shift for the next day before adding a delivery.");
                }

                Employee driver = employeeController.getRoleInShift(shift,Role.DRIVER);
                if (driver == null) {
                    throw new IllegalStateException("Need to assign a driver to the shift before adding a delivery.");
                }

                License license = driver.getLicenses().iterator().next();

                Truck truck = deliveryController.getTruckByLicense(license);

                return deliveryController.addDelivery(today.plusDays(1), ShiftType.MORNING,"08:00",driver,truck, shift.getBranch(), Set.of(supplierSite));
            }
            else {
                return delivery.setDestination(supplierSite);
            }
        }

        // Find the next delivery date that matches the target day of the week

        DayOfWeek targetDay = supplyDay == 1 ? DayOfWeek.of(7) : DayOfWeek.of(supplyDay - 1); // Adjust for Sunday being 7
        LocalDate targetDate = null;

        for (int i = 0; i < 7; i++) {
            LocalDate candidate = today.plusDays(i);
            if (candidate.getDayOfWeek() == targetDay) {
                targetDate = candidate;
                break;
            }
        }

        Delivery delivery = deliveryController.getDelivery(targetDate);
        if (delivery == null) {
            Shift shift = employeeController.getShiftWithEmpByDateAndType(targetDate, "M");
            if (shift == null) {
                throw new IllegalStateException("Need to set a shift for the date " + targetDate.toString() + " before adding a delivery.");
            }

            Employee driver = employeeController.getRoleInShift(shift,Role.DRIVER);
            if (driver == null) {
                throw new IllegalStateException("Need to assign a driver to the shift before adding a delivery.");
            }

            License license = driver.getLicenses().iterator().next();

            Truck truck = deliveryController.getTruckByLicense(license);

            return deliveryController.addDelivery(targetDate, ShiftType.MORNING,"08:00",driver,truck, shift.getBranch(), Set.of(supplierSite));
        } else {
            return delivery.setDestination(supplierSite);
        }
    }

    public String getCurrentDeadline() throws Exception {
        return employeeController.getCurrDeadLine();
    }

    public Response findEmployeesByRoleInBranch(Role role,String branchId) throws Exception {
        List<Employee> employees = employeeController.findEmployeesByRole(role);
        if (employees.isEmpty()) {
            return new Response(false, "No employees found with the role: " + role);
        }

        StringBuilder sb = new StringBuilder();
        for (Employee e : employees) {
            if (e != null && e.getBranch() != null && e.getBranch().getId().equals(branchId)) {
                sb.append("  ")
                        .append(e.getName())
                        .append(" (").append(e.getId()).append(")");
                sb.append("\n");
            }
        }
        return new Response(true, sb.toString());
    }
}
