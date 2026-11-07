package HRandDelivery.PresentationLayer;


import HRandDelivery.DomainLayer.HR.enums.Role;
import HRandDelivery.DomainLayer.HRandDeliveryDomainService;
import HRandDelivery.Utils.Response;
import HRandDelivery.Utils.UserInput;
import HRandDelivery.Utils.UserOutput;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HRandDeliveryPres {

    // This class is responsible for controlling the flow of the presentation layer
    // It will handle user userInput and output, and interact with the business logic layer


    private UserInput userInput;
    private UserOutput userOutput;

    private HRandDeliveryDomainService domainService;

    public HRandDeliveryPres() throws Exception {
        // Initialize the presentation layer components
        this.userInput = UserInput.getInstance();
        this.userOutput = UserOutput.getInstance();

        // Initialize the domain services
        this.domainService = HRandDeliveryDomainService.getInstance();

        //Init Admin
        domainService.addBranch("Gilat", "01", "Gilat 81","Gaya Aviv","050-88899");
        domainService.addEmployee("Shani Kroitoro", "123456789", "4545 789 67868","545 23/09/2014 56 76 4567890","ADMIN", "01", "");
    }

    public Response setup() throws Exception {

        //Branches
        domainService.addBranch("Oren", "02", "Oren Center","Shani Kro","050-111222");
        domainService.addBranch("Hen", "03", "Hen Center","Dudi Cohen","050-666555");

        //Branch 01
        domainService.addEmployee("Gaya Aviv", "000111222", "4545 789 67868","545 23/09/2014 56 76 4567890","SHIFT_MANAGER CASHIER", "01", "");
        domainService.addEmployee("Dani Kushmaro", "999999990", "4545 789 67868","545 23/09/2014 56 76 4567890","DRIVER", "01", "A");
        domainService.addEmployee("Justin Biber", "999999990", "4545 789 67868","545 23/09/2014 56 76 4567890","DRIVER", "01", "A B");
        domainService.addEmployee("Kobi Aflalo", "999999991", "4545 789 67868","545 23/09/2014 56 76 4567890","CASHIER", "01", "");
        domainService.addEmployee("Beyonce", "999999992", "4545 789 67868","545 23/09/2014 56 76 pension:4567890","STORE_KEEPER", "01", "");

        //Branch 02
        domainService.addEmployee("Lior Azulay", "111222333", "4545 789 67868", "545 23/09/2014 56 76 1234567", "SHIFT_MANAGER CASHIER", "02", "");
        domainService.addEmployee("Ron Cohen", "345678912", "4545 789 67868", "545 23/09/2014 56 76 3456789", "DRIVER", "02", "B");
        domainService.addEmployee("Ella Friedman", "456789123", "4545 789 67868", "545 23/09/2014 56 76 4567891", "DRIVER", "02", "A B");
        domainService.addEmployee("Yossi Peretz", "567891234", "4545 789 67868", "545 23/09/2014 56 76 5678912", "CASHIER", "02", "");
        domainService.addEmployee("Neta Barzilai", "678912345", "4545 789 67868", "545 23/09/2014 56 76 pension:6789123", "STORE_KEEPER", "02", "");

        //Branch 03
        domainService.addEmployee("Noa Tishby", "789123456", "4545 789 67868", "545 23/09/2014 56 76 7891234", "SHIFT_MANAGER CASHIER", "03", "");
        domainService.addEmployee("Dana Abramovich", "912345678", "4545 789 67868", "545 23/09/2014 56 76 9123456", "DRIVER", "03", "A");
        domainService.addEmployee("Amit Gross", "102345679", "4545 789 67868", "545 23/09/2014 56 76 1023456", "DRIVER", "03", "A B");
        domainService.addEmployee("Shira Hadad", "112345680", "4545 789 67868", "545 23/09/2014 56 76 1123456", "CASHIER", "03", "");
        domainService.addEmployee("Maya Cohen", "122345681", "4545 789 67868", "545 23/09/2014 56 76 pension:1223456", "STORE_KEEPER", "03", "");

        //Submission Deadline
        domainService.setShiftSubmissionDeadline("16:00");

        //Shifts for the Upcoming Week
        Map<Role, Integer> rolesQuantities = new HashMap<>();
        rolesQuantities.put(Role.SHIFT_MANAGER, 1);
        rolesQuantities.put(Role.CASHIER, 2);
        rolesQuantities.put(Role.DRIVER, 1);
        rolesQuantities.put(Role.ADMIN, 0);
        rolesQuantities.put(Role.STORE_KEEPER, 1);
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for(int i = 1; i < 8; i++) {
            LocalDate shiftDate = today.plusDays(i);
            String dateStr = shiftDate.format(formatter);
            domainService.setShiftRequirements("01", dateStr, "M", "08:00", "16:00", rolesQuantities);
            domainService.setShiftRequirements("01" ,dateStr, "E", "16:00", "22:00", rolesQuantities);
            domainService.setShiftRequirements("02", dateStr, "M", "08:00", "16:00", rolesQuantities);
            domainService.setShiftRequirements("02" ,dateStr, "E", "16:00", "22:00", rolesQuantities);
            domainService.setShiftRequirements("03", dateStr, "M", "08:00", "16:00", rolesQuantities);
            domainService.setShiftRequirements("03" ,dateStr, "E", "16:00", "22:00", rolesQuantities);

        }

        //Trucks
        domainService.addTruck("111", "Toyota", 100, 200, "A");
        domainService.addTruck("112", "Volvo", 120, 220, "B");
        domainService.addTruck("113", "Mercedes", 90, 180, "A");
        domainService.addTruck("114", "Isuzu", 110, 210, "B");
        domainService.addTruck("115", "Scania", 130, 250, "A");

        //Sites
        domainService.addSite("Hashmonaim 3", "Gal", "052-444555");
        domainService.addSite("Herzliya 5", "Dana", "053-111222");
        domainService.addSite("Ashdod 1", "Eli", "054-333444");
        domainService.addSite("Haifa 4", "Maya", "055-555666");
        domainService.addSite("Be'er Sheva 5", "Itay", "056-777888");
        domainService.addSite("Jerusalem 7", "Roni", "057-999000");

        //Supplier Manager
        domainService.addEmployee("supplierMan", "12345", "4545 789 67868","545 23/09/2014 56 76 pension:4567890","SUPPLIER_MANAGER", "01", "");

        return new Response(true,  "Data loaded successfully.");
    }


    public Response reset() throws Exception {
        domainService.reset();

        return new Response(true, "Data reset successfully.");
    }


    public void handleResponse(Response response) {
        userOutput.print(response.getMessage());
    }


    public Response handleAddEmployee() throws Exception {

        userOutput.print("Please enter the employee's full name: ");
        String name = userInput.getUserInput();

        userOutput.print("Please enter the employee's ID: ");
        String id = userInput.getUserInput();

        userOutput.print("Please enter the employee's roles: ");
        String roles = userInput.getUserInput();

        userOutput.print("Please enter the employee's Bank Account [account Id, bank Id, bank branch]: ");
        String bank = userInput.getUserInput();

        userOutput.print("Please enter the employee's Work conditions [salary, start date (DD/MM/YYYY), vacation days used, sick days used, funds] : ");
        String workConditions = userInput.getUserInput();

        userOutput.print("Please enter employee's Branch ID from the list: ");
        handleResponse(domainService.displayBranches());
        String branchId = userInput.getUserInput();

        userOutput.print("Please enter the employee's Licenses if their role is DRIVER: ");
        String licenses = userInput.getUserInput();

        return domainService.addEmployee(name, id, bank, workConditions, roles, branchId, licenses);

    }
    public Response handleRemoveEmployee() throws Exception {
        handleResponse(domainService.displayEmployeesAndRole());
        String id = getEmployeeId();
        return domainService.removeEmployee(id);
    }

    public Response handleViewEmployeeDetails() throws Exception {
        handleResponse(domainService.displayEmployeesAndRole());
        String id = getEmployeeId();
        return domainService.displayEmployeeDetails(id);
    }

    public Response handleUpdateEmployeeDetails() throws Exception {
        handleResponse(domainService.displayEmployeesAndRole());
        String id = getEmployeeId();

        userOutput.print("""
                Please enter the employee's categories you want to edit:
                1.Name
                2.Bank Account
                3.Work Conditions
                4.Roles""");
        String categories = userInput.getUserInput();

        String categoryName = switch (categories) {
            case "1" -> "Name";
            case "2" -> "Bank Account";
            case "3" -> "Work Conditions";
            case "4" -> "Roles";
            default -> null;
        };

        if (categoryName == null) {
            return new Response(false, "Invalid option. Please try again.");
        }

        userOutput.print("Please enter the employee's new " + categoryName + ": ");
        String newDetails = userInput.getUserInput();

        String licenses = "";
        if(categories.equals("4")){
            userOutput.print("Please enter the employee's Licenses if their role is DRIVER: ");
            licenses = userInput.getUserInput();
        }

        return switch (categories) {
            case "1" -> domainService.updateEmployeeName(id, newDetails);
            case "2" -> domainService.updateEmployeeBankAccount(id, newDetails);
            case "3" -> domainService.updateEmployeeWorkConditions(id, newDetails);
            case "4" -> domainService.updateEmployeeRoles(id, newDetails, licenses);
            default -> new Response(false, "Invalid option. Please try again.");
        };
    }

    public Response handleSetShiftRequirements() throws Exception {
        String[] shiftDetails = getShiftdet();
        String[] shiftTimeSlot = getShiftTimeSlot();
        Map<Role, Integer> rolesQuantities = getRolesAndQuantities();

        return domainService.setShiftRequirements(shiftDetails[0], shiftDetails[1], shiftDetails[2], shiftTimeSlot[0], shiftTimeSlot[1], rolesQuantities);
    }

    public Response handleEditShift() throws Exception {
        String[] shiftDetails = getShiftdet();

        handleResponse(domainService.displayShiftDetails(shiftDetails[0], shiftDetails[1], shiftDetails[2]));

        userOutput.print("""
                Please choose an option:
                1. Remove an employee from the shift
                2. Add an employee to the shift
                3. Edit shift time-slot""");
        String option = userInput.getUserInput();
        return switch (option) {
            case "1" -> {
                Role role = getRoleOfEmployee();
                yield domainService.handleRemoveEmployeeFromShift(shiftDetails[0], shiftDetails[1], shiftDetails[2], getEmployeeId(), role);
            }
            case "2" -> {
                Role role = getRoleOfEmployee();
                yield domainService.handleAddEmployeeToShift(shiftDetails[0], shiftDetails[1], shiftDetails[2], getEmployeeId(), role);
            }
            case "3" -> {
                String[] newTimeSlot = getShiftTimeSlot();
                yield domainService.editShiftTimeSlot(shiftDetails[0], shiftDetails[1], shiftDetails[2], newTimeSlot[0], newTimeSlot[1]);
            }
            default -> new Response(false, "Invalid option. Please try again.");
        };
    }

    public Response handleEmployeeShiftAssignment() throws Exception {

        String[] shiftDetails = getShiftdet();

        Response r = domainService.displayShiftDetails(shiftDetails[0], shiftDetails[1], shiftDetails[2]);
        if(!r.isSuccess()){
            return r;
        }

        handleResponse(r);
        handleResponse(domainService.displayEmployeesAndRole());
        handleResponse(domainService.alertNotEnoughEmployeeAvailability(shiftDetails[0], shiftDetails[1], shiftDetails[2]));

        Map<Role,String> assignmentPerRole = getAssignmentPerRole();

        return domainService.employeeShiftAssignment(shiftDetails[0], shiftDetails[1], shiftDetails[2], assignmentPerRole);
    }

    public Response handleSetShiftSubmissionDedline() throws Exception {
        userOutput.print("Please enter the new deadline [HH:MM]: ");
        String deadline = userInput.getUserInput();

        return domainService.setShiftSubmissionDeadline(deadline);
    }

    public Response handleViewShiftDetails() throws Exception {
        String[] shiftDetails = getShiftdet();
        return domainService.displayShiftDetails(shiftDetails[0], shiftDetails[1], shiftDetails[2]);
    }


    public Response handleSubmitAvailability(String userId) throws Exception {
        Response checkValidTimeToSubmit = domainService.checkValidTimeToSubmit();

        if(checkValidTimeToSubmit.isSuccess()) {
            Map<LocalDate, List<String>> availability = new HashMap<>();
            LocalDate today = LocalDate.now();

            userOutput.print("Please enter your preferred shifts for the upcoming week:");
            userOutput.print("For each day, type M (Morning), E (Evening) or ME (both).");

            for (int i = 1; i < 8; i++) {
                LocalDate date = today.plusDays(i);
                userOutput.print(date + " - Enter your preference: ");
                String input = userInput.getUserInput().toUpperCase();

                List<String> shifts = new ArrayList<>();
                switch (input.toUpperCase()) {
                    case "ME":
                    case "EM":
                        shifts.add("M");
                        shifts.add("E");
                        break;
                    case "M":
                    case "E":
                        shifts.add(input);
                        break;
                    default:
                        shifts.add("M");
                        shifts.add("E");
                }

                availability.put(date, shifts);
            }

            return domainService.employeeShiftAvailability(userId, availability);
        }

        return checkValidTimeToSubmit;
    }

    public Response handleViewMyShifts(String userId) throws Exception {
        return domainService.employeeFutureShifts(userId);
    }

    public Response handleAddDelivery() throws Exception {
        userOutput.print("Please enter delivery date (DD/MM/YYYY): ");
        String date = userInput.getUserInput();

        userOutput.print("Please enter delivery departure time (HH:MM): ");
        String departure = userInput.getUserInput();

        userOutput.print("Please enter shift time slot M(morning)/E(evening): ");
        String timeSlot = userInput.getUserInput();

        handleResponse(domainService.displayTrucks());
        userOutput.print("Please enter delivery truck number: ");
        String truckId = userInput.getUserInput();

        userOutput.print("Please enter the delivery driver's ID below: ");
        handleResponse(domainService.displayDrivers());
        String driverId = userInput.getUserInput();

        handleResponse(domainService.displaySites());
        userOutput.print("Please enter the delivery source site [address]: ");
        String source = userInput.getUserInput();

        userOutput.print("Please enter the delivery destination sites [address1, address2 ..]: ");
        String destinations = userInput.getUserInput();

        return domainService.addDelivery(date, departure, timeSlot, truckId, driverId, source, destinations);

    }

    public Response handleAddBranch() throws Exception {

        userOutput.print("Please enter the branch's name: ");
        String name = userInput.getUserInput();

        userOutput.print("Please enter the branch's ID: ");
        String id = userInput.getUserInput();

        userOutput.print("Please enter the branch's address: ");
        String address = userInput.getUserInput();

        userOutput.print("Please enter the branch's contact name: ");
        String contactName = userInput.getUserInput();

        userOutput.print("Please enter the branch's contact number : ");
        String contactNumber = userInput.getUserInput();

        return domainService.addBranch(name, id, address, contactName, contactNumber);

    }


    public Response handleAddTruck() throws Exception {

        userOutput.print("Please enter Truck number: ");
        String truckNumber = userInput.getUserInput();

        userOutput.print("Please enter Truck model: ");
        String model = userInput.getUserInput();

        userOutput.print("Please enter Truck net weight: ");
        String netWeight = userInput.getUserInput();

        userOutput.print("Please enter Truck max weight: ");
        String maxWeight = userInput.getUserInput();

        userOutput.print("Please enter Truck license: ");
        String license = userInput.getUserInput();

        return domainService.addTruck(truckNumber, model, Integer.parseInt(netWeight), Integer.parseInt(maxWeight), license);

    }


    //Utility methods

    private Map<Role, Integer> getRolesAndQuantities() {
        Map<Role, Integer> rolesQuantities = new HashMap<>();

        userOutput.print("Please enter the required quantity for each role in the shift:");

        for (Role role : Role.values()) {
            if(role == Role.ADMIN || role == Role.SUPPLIER_MANAGER || role == Role.SUPPLIER) {
                continue;
            }
            userOutput.print("Enter quantity for " + role + " (or 0 to skip): ");
            int quantity = Integer.parseInt(userInput.getUserInput());
            if (quantity > 0) {
                rolesQuantities.put(role, quantity);
            }
        }

        return rolesQuantities;
    }

    private Map<Role, String> getAssignmentPerRole() {
        Map<Role, String> rolesAndIds = new HashMap<>();

        userOutput.print("Please enter the employees' IDs for each role in the shift [Id1 Id2 Id3 ...]\n");

        for (Role role : Role.values()) {
            userOutput.print("Enter IDs for " + role + " (or press Enter to skip):");
            String ids = userInput.getUserInput().trim();

            if (!ids.isEmpty()) {
                rolesAndIds.put(role, ids);
            }
        }
        return rolesAndIds;
    }

    private String[] getShiftdet() throws Exception {

        userOutput.print("Please enter Branch ID from the list: ");
        handleResponse(domainService.displayBranches());
        String branchId = userInput.getUserInput();

        userOutput.print("Please enter shift date (DD/MM/YYYY): ");
        String shiftDay = userInput.getUserInput();

        userOutput.print("Please enter shift time slot M(morning)/E(evening): ");
        String timeSlot = userInput.getUserInput();

        return new String[]{branchId,shiftDay, timeSlot};
    }

    private String[] getShiftTimeSlot() {
        userOutput.print("Please enter shift start time (HH:MM) ");
        String startTime = userInput.getUserInput();

        userOutput.print("Please enter shift end time (HH:MM) ");
        String endTime = userInput.getUserInput();

        return new String[]{startTime, endTime};
    }

    private String getEmployeeId() {
        userOutput.print("Please enter the employee's ID: ");
        return userInput.getUserInput();
    }

    private Role getRoleOfEmployee() {

        Role[] roles = Role.values();

        userOutput.print("Please choose the employee's role: ");

        int i = 1;
        for (Role role : roles) {
            userOutput.print(i + "." + role);
            i++;
        }

        return roles[Integer.parseInt(userInput.getUserInput()) - 1];
    }


    public Response validateDet(String id) throws Exception {
        return domainService.validateDet(id);
    }
}
