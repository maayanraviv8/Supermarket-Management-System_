package EmployeeTest;

import HRandDelivery.DomainLayer.HRandDeliveryDomainService;
import HRandDelivery.Utils.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EmployeeManagementTests {

    private HRandDeliveryDomainService domain;

    @BeforeEach
    void setUp() throws Exception {
        domain = HRandDeliveryDomainService.getInstance();
        domain.resetAll();
        domain.addBranch("Shefa", "01", "Tel Aviv 11", "Shani", "050-1111111");
    }

    @AfterAll
    void tearDown() throws Exception {
        domain.resetAll();
    }


    @Test
    void validateDet_success() throws Exception {
        domain.addEmployee("Alice", "1", "123 45 67", "5000 01/01/2024 0 0 Pension", "SHIFT_MANAGER CASHIER","01" , "");
        Response response = domain.validateDet("1");
        assertTrue(response.isSuccess());
        assertTrue(response.getMessage().contains("Alice"));
    }

    @Test
    void validateDet_inactiveEmployee_fails() throws Exception {
        domain.addEmployee("Alice", "1", "123 45 67", "5000 01/01/2024 0 0 Pension", "SHIFT_MANAGER CASHIER", "01" , "");
        domain.removeEmployee("1");
        Response response = domain.validateDet("1");
        assertEquals(false, response.isSuccess());
        assertEquals("Your account is not Active.", response.getMessage());
    }


    @Test
    void addEmployee_success() throws Exception {
        Response response = domain.addEmployee("Alice", "1", "123 45 67", "5000 01/01/2024 0 0 KerenHishtalmut 11", "CASHIER", "01" , "");
        assertEquals(true, response.isSuccess());
        assertEquals("Employee added successfully.", response.getMessage());
    }

    @Test
    void addEmployee_duplicateId_fails() throws Exception {
        domain.addEmployee("Alice", "1", "123 45 67", "5000 01/01/2024 0 0 KerenHishtalmut 11", "CASHIER", "01" , "");
        Response response = domain.addEmployee("Bob", "1", "111 22 33", "6000 01/02/2024 1 0 KerenHishtalmut", "STOCK_KEEPER", "01" , "");
        assertEquals(false, response.isSuccess());
        assertEquals("Employee is already exist.", response.getMessage());
    }

    @Test
    void removeEmployee_success() throws Exception {
        domain.addEmployee("Alice", "1", "123 45 67", "5000 01/01/2024 0 0 KerenHishtalmut 11", "CASHIER", "01" , "");
        Response response = domain.removeEmployee("1");
        assertEquals(true, response.isSuccess());
        assertEquals("Employee has removed.", response.getMessage());
    }

    @Test
    void removeEmployee_notFound() throws Exception {
        Response response = domain.removeEmployee("999");
        assertEquals(false, response.isSuccess());
        assertEquals("Employee not found, please try again.", response.getMessage());
    }

    @Test
    void updateEmployeeName_success() throws Exception {
        domain.addEmployee("Alice", "1", "123 45 67", "5000 01/01/2024 0 0 KerenHishtalmut 11", "CASHIER", "01" , "");
        Response response = domain.updateEmployeeName("1", "Alicia");
        assertEquals(true, response.isSuccess());
        assertEquals("Name updated successfully.", response.getMessage());
    }

    @Test
    void viewEmployeeDetails_notFound() throws Exception {
        Response response = domain.displayEmployeeDetails("999");
        assertEquals(false, response.isSuccess());
        assertEquals("Employee not found, please try again.", response.getMessage());
    }

    @Test
    void updateEmployeeBankAccount_success() throws Exception {
        domain.addEmployee("Alice", "1", "123 45 67", "5000 01/01/2024 0 0 Pension", "SHIFT_MANAGER CASHIER", "01" , "");
        Response response = domain.updateEmployeeBankAccount("1", "987 11 22");
        assertTrue(response.isSuccess());
    }


    @Test
    void updateEmployeeWorkConditions_success() throws Exception {
        domain.addEmployee("Alice", "1", "123 45 67", "5000 01/01/2024 0 0 Pension", "SHIFT_MANAGER CASHIER", "01" , "");
        Response response = domain.updateEmployeeWorkConditions("1", "6000 01/02/2024 1 2 PensionPlus");
        assertTrue(response.isSuccess());
    }


    @Test
    void updateEmployeeRoles_success() throws Exception {
        domain.addEmployee("Alice", "1", "123 45 67", "5000 01/01/2024 0 0 Pension", "SHIFT_MANAGER CASHIER", "01" , "");
        Response response = domain.updateEmployeeRoles("1", "CASHIER DRIVER", "A");
        assertTrue(response.isSuccess());
    }


    @Test
    void viewEmployeesAndRole_success() throws Exception {
        domain.addEmployee("Alice", "1", "123 45 67", "5000 01/01/2024 0 0 Pension", "SHIFT_MANAGER CASHIER", "01" , "");
        Response response = domain.displayEmployeesAndRole();
        assertTrue(response.isSuccess());
        assertTrue(response.getMessage().contains("Alice"));
    }

    @Test
    void driverWith1License_success() throws Exception {
        Response response = domain.addEmployee("Alice", "1", "123 45 67", "5000 01/01/2024 0 0 Pension", "DRIVER", "01" , "A");
        assertTrue(response.isSuccess());
        assertEquals("Employee added successfully.", response.getMessage());
    }

    @Test
    void driverWith2License_success() throws Exception {
        Response response = domain.addEmployee("Alice", "1", "123 45 67", "5000 01/01/2024 0 0 Pension", "DRIVER", "01" , "A B");
        assertTrue(response.isSuccess());
        assertEquals("Employee added successfully.", response.getMessage());
    }


    @Test
    void driverWithoutLicense_fails() throws Exception {
        Response response = domain.addEmployee("Alice", "1", "123 45 67", "5000 01/01/2024 0 0 Pension", "DRIVER", "01" , "");
        assertEquals(false, response.isSuccess());
        assertEquals("Driver Has to be at least one License.", response.getMessage());
    }


}