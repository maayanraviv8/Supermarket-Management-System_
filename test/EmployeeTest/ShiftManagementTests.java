package EmployeeTest;

import HRandDelivery.DomainLayer.HR.enums.Role;
import HRandDelivery.DomainLayer.HRandDeliveryDomainService;
import HRandDelivery.Utils.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ShiftManagementTests {

    private HRandDeliveryDomainService domain;

    @BeforeEach
    void setUp() throws Exception {
        domain = HRandDeliveryDomainService.getInstance();
        domain.resetAll();
        domain.addBranch("Shefa", "01", "Tel Aviv 11", "Shani", "050-1111111");
        domain.addEmployee("Alice", "1", "123 45 67", "5000 01/01/2024 0 0 Keren", "SHIFT_MANAGER CASHIER", "01" , "");
    }

    @AfterAll
    void tearDown() throws Exception {
        domain.resetAll();
    }

    @Test
    void setShiftRequirements_success() throws Exception {
        Map<Role, Integer> roles = new HashMap<>();
        for (Role role : Role.values()) {
            roles.put(role, 0);
        }
        roles.put(Role.SHIFT_MANAGER, 1);
        roles.put(Role.CASHIER, 2);

        Response response = domain.setShiftRequirements("01", "18/04/2025", "M", "08:00", "16:00", roles);

        assertEquals(true, response.isSuccess());
        assertEquals("Shift requirements updated successfully.", response.getMessage());
    }

    @Test
    void setShiftRequirements_noManagerFails() throws Exception {
        Map<Role, Integer> roles = new HashMap<>();
        for (Role role : Role.values()) {
            roles.put(role, 0);
        }
        roles.put(Role.CASHIER, 2);

        Response response = domain.setShiftRequirements("01", "18/04/2025", "M", "08:00", "16:00", roles);

        assertEquals(false, response.isSuccess());
        assertEquals("A shift cannot be created without assigning a shift manager, Please try again.", response.getMessage());
    }

    @Test
    void addEmployeeToShift_success() throws Exception {
        Map<Role, Integer> roles = new HashMap<>();
        for (Role role : Role.values()) {
            roles.put(role, 0);
        }
        roles.put(Role.SHIFT_MANAGER, 1);
        domain.setShiftRequirements("01", "18/04/2025", "M", "08:00", "16:00", roles);

        Response response = domain.handleAddEmployeeToShift("01", "18/04/2025", "M", "1", Role.SHIFT_MANAGER);

        assertEquals(true, response.isSuccess());
        assertEquals("Employee added to shift successfully.", response.getMessage());
    }

    @Test
    void addEmployeeToShift_wrongBranch_fails() throws Exception {
        domain.addBranch("Beer Sheva", "02", "Ringelblum 8", "Liat", "050-9999999");

        domain.addEmployee("Tom", "20", "123 45 67", "4500 01/01/2023 0 0 Pension", "SHIFT_MANAGER", "01", "");

        Map<Role, Integer> roles = new HashMap<>();
        for (Role role : Role.values()) {
            roles.put(role, 0);
        }
        roles.put(Role.SHIFT_MANAGER, 1);
        domain.setShiftRequirements("02", "01/06/2025", "M", "08:00", "16:00", roles);

        Response response = domain.handleAddEmployeeToShift("02", "01/06/2025", "M", "20", Role.SHIFT_MANAGER);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().toLowerCase().contains("not assigned to the branch"));
    }


    @Test
    void removeEmployeeFromShift_success() throws Exception {
        Map<Role, Integer> roles = new HashMap<>();
        for (Role role : Role.values()) {
            roles.put(role, 0);
        }
        roles.put(Role.CASHIER, 1);
        roles.put(Role.SHIFT_MANAGER, 1);

        domain.setShiftRequirements("01","18/04/2025", "M", "08:00", "16:00", roles);
        domain.handleAddEmployeeToShift("01","18/04/2025", "M", "1", Role.CASHIER);

        Response response = domain.handleRemoveEmployeeFromShift("01", "18/04/2025", "M", "1", Role.CASHIER);

        assertEquals(true, response.isSuccess());
        assertEquals("Employee removed from shift successfully.", response.getMessage());
    }

    @Test
    void removeEmployeeFromShift_notInShiftFails() throws Exception {
        Map<Role, Integer> roles = new HashMap<>();
        for (Role role : Role.values()) {
            roles.put(role, 0);
        }
        roles.put(Role.SHIFT_MANAGER, 1);
        domain.setShiftRequirements("01","18/04/2025", "M", "08:00", "16:00", roles);

        Response response = domain.handleRemoveEmployeeFromShift("01","18/04/2025", "M", "1", Role.SHIFT_MANAGER);

        assertEquals(false, response.isSuccess());
        assertEquals("Employee not found in shift, please try again.", response.getMessage());
    }

    @Test
    void employeeFutureShifts_success() throws Exception {
        domain.addEmployee("Alice", "1", "123 45 67", "5000 01/01/2024 0 0 Pension", "SHIFT_MANAGER", "01" , "");
        Map<Role, Integer> roles = new HashMap<>();
        roles.put(Role.SHIFT_MANAGER, 1);
        String formattedDate = LocalDate.of(2025, 4, 30).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        domain.setShiftRequirements("01", formattedDate, "M", "08:00", "16:00", roles);
        domain.handleAddEmployeeToShift("01",formattedDate, "M", "1", Role.SHIFT_MANAGER);
        Response response = domain.employeeFutureShifts("1");
        assertTrue(response.isSuccess());
        System.out.println(response.getMessage());
        assertTrue(response.getMessage().contains("Shift Date"));
    }

    @Test
    void employeeFutureShifts_notAssign_fails() throws Exception {
        domain.addEmployee("Alice", "1", "123 45 67", "5000 01/01/2024 0 0 Pension", "SHIFT_MANAGER", "01" , "");
        Response response = domain.employeeFutureShifts("1");
        assertTrue(response.isSuccess());
        assertEquals("You haven't been assigned to a shift yet.", response.getMessage());
    }

    @Test
    void employeeShiftAvailability_success() throws Exception {
        domain.addEmployee("Alice", "1", "123 45 67", "5000 01/01/2024 0 0 Pension", "SHIFT_MANAGER", "01" , "");
        String date = "01/05/2025";
        domain.setShiftRequirements("01", date, "M", "08:00", "16:00", Map.of(Role.SHIFT_MANAGER, 1));
        Map<LocalDate, List<String>> availability = Map.of(LocalDate.of(2025, 5, 1), List.of("M"));
        Response response = domain.employeeShiftAvailability("1", availability);
        assertTrue(response.isSuccess());
    }

    @Test
    void employeeShiftAvailability_invalidUser_fails() throws Exception {
        String date = "03/05/2025";
        domain.setShiftRequirements("01", date, "M", "08:00", "16:00", Map.of(Role.SHIFT_MANAGER, 1));
        Map<LocalDate, List<String>> availability = Map.of(LocalDate.of(2025, 5, 3), List.of("M"));
        Response response = domain.employeeShiftAvailability("999", availability);
        assertFalse(response.isSuccess());
        assertEquals("Employee not found, please try again.", response.getMessage());
    }

    @Test
    void employeeShiftAvailability_inactiveUser_fails() throws Exception {
        domain.addEmployee("Alice", "1", "123 45 67", "5000 01/01/2024 0 0 Pension", "SHIFT_MANAGER", "01" , "");
        domain.removeEmployee("1");
        String date = "04/05/2025";
        domain.setShiftRequirements("01",date, "M", "08:00", "16:00", Map.of(Role.SHIFT_MANAGER, 1));
        Map<LocalDate, List<String>> availability = Map.of(LocalDate.of(2025, 5, 4), List.of("M"));
        Response response = domain.employeeShiftAvailability("1", availability);
        assertFalse(response.isSuccess());
        assertEquals("Your account is not Active.", response.getMessage());
    }


    @Test
    void setShiftSubmissionDeadline_success() throws Exception {
        Response response = domain.setShiftSubmissionDeadline("14:00");
        assertTrue(response.isSuccess());
    }

    @Test
    void checkValidTimeToSubmit_afterDeadline_fails() throws Exception {
        domain.setShiftSubmissionDeadline(LocalTime.now().minusMinutes(10).format(DateTimeFormatter.ofPattern("HH:mm")));
        Response response = domain.checkValidTimeToSubmit();
        assertFalse(response.isSuccess());
    }

    @Test
    void alertNotEnoughEmployeeAvailability_detectsMissing() throws Exception {
        domain.addEmployee("Alice", "1", "123 45 67", "5000 01/01/2024 0 0 Pension", "SHIFT_MANAGER", "01" , "");
        String date = "02/05/2025";
        domain.setShiftRequirements("01", date, "M", "08:00", "16:00", Map.of(Role.SHIFT_MANAGER, 1, Role.CASHIER, 2));
        Map<LocalDate, List<String>> availability = Map.of(LocalDate.of(2025, 5, 2), List.of("M"));
        domain.employeeShiftAvailability("1", availability);
        Response response = domain.alertNotEnoughEmployeeAvailability("01", date, "M");
        assertTrue(response.isSuccess());
        assertTrue(response.getMessage().contains("Not enough available employees"));
    }

    @Test
    void setShiftRequirements_negativeRoleCount_fails() throws Exception {
        Map<Role, Integer> roles = new HashMap<>();
        roles.put(Role.SHIFT_MANAGER, -1);
        Response response = domain.setShiftRequirements("01","05/05/2025", "M", "08:00", "16:00", roles);
        assertFalse(response.isSuccess());
    }

    @Test
    void addEmployeeToShift_userNotFound_fails() throws Exception {
        Map<Role, Integer> roles = Map.of(Role.SHIFT_MANAGER, 1);
        domain.setShiftRequirements("01", "06/05/2025", "M", "08:00", "16:00", roles);
        Response response = domain.handleAddEmployeeToShift("01", "06/05/2025", "M", "999", Role.SHIFT_MANAGER);
        assertFalse(response.isSuccess());
        assertEquals("Employee not found, please try again.", response.getMessage());
    }

    @Test
    void viewShiftDetails_shiftNotFound_fails() throws Exception {
        Response response = domain.displayShiftDetails("01","09/05/2025", "M");
        assertFalse(response.isSuccess());
        assertEquals("No shift found for the given date. Please set a shift before proceeding.", response.getMessage());
    }

    @Test
    void addEmployeeToShift_duplicateAssignment_fails() throws Exception {
        domain.addEmployee("Alice", "1", "123 689 2345", "5000 01/01/2024 0 0 Keren", "SHIFT_MANAGER", "01" , "");
        Map<Role, Integer> roles = Map.of(Role.SHIFT_MANAGER, 1);
        domain.setShiftRequirements("01", "10/05/2025", "M", "08:00", "16:00", roles);
        domain.handleAddEmployeeToShift( "01", "10/05/2025", "M", "1", Role.SHIFT_MANAGER);
        Response response = domain.handleAddEmployeeToShift("01", "10/05/2025", "M", "1", Role.SHIFT_MANAGER);
        assertFalse(response.isSuccess());
        assertEquals("Employee is already assigned to this shift.", response.getMessage());
    }

    @Test
    void addEmployeeAsTwoDiffRoleInShift_fails() throws Exception {
        domain.addEmployee("Alice", "1", "123 689 2345", "5000 01/01/2024 0 0 Keren", "SHIFT_MANAGER CASHIER", "01" , "");
        Map<Role, Integer> rolesQuantities = new HashMap<>();
        rolesQuantities.put(Role.SHIFT_MANAGER, 1);
        rolesQuantities.put(Role.CASHIER, 1);
        rolesQuantities.put(Role.DRIVER, 0);
        rolesQuantities.put(Role.ADMIN, 0);
        rolesQuantities.put(Role.STORE_KEEPER, 0);
        domain.setShiftRequirements("01", "10/05/2025", "M", "08:00", "16:00", rolesQuantities);

        Map<Role, String> roles = new HashMap<>();
        roles.put(Role.SHIFT_MANAGER, "1");
        roles.put(Role.CASHIER, "1");
        roles.put(Role.DRIVER, "");
        roles.put(Role.ADMIN, "");
        roles.put(Role.STORE_KEEPER, "");
        Response response = domain.employeeShiftAssignment("01", "10/05/2025", "M", roles);
        assertFalse(response.isSuccess());
        assertEquals("Employee 1 already assigned to another role in the same shift. Please try again.", response.getMessage());
    }

}
