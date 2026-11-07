package DeliveryTest;

import HRandDelivery.DomainLayer.HR.enums.Role;
import HRandDelivery.DomainLayer.HRandDeliveryDomainService;
import HRandDelivery.Utils.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeliveryManagementTests {

    private HRandDeliveryDomainService domain;

    @BeforeEach
    void setUp() throws Exception {
        domain = HRandDeliveryDomainService.getInstance();
        domain.resetAll();
    }

    @AfterAll
    void tearDown() throws Exception {
        domain.resetAll();
    }

    @Test
    void testAddTruck_Success() throws Exception {
        Response response = domain.addTruck("123", "Volvo", 1000, 5000, "A");
        assertTrue(response.isSuccess());
    }

    @Test
    void testAddTruck_Failure_DuplicateTruckNumber() throws Exception {
        domain.addTruck("TRK123", "Mercedes", 2000, 7000, "A");
        Response response = domain.addTruck("TRK123", "Volvo", 1800, 6000, "A");
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().toLowerCase().contains("exist"));
    }

    @Test
    void testAddSite_Success() throws Exception {
        Response response = domain.addSite("Herzl 12", "Dana", "052-8888888");
        assertTrue(response.isSuccess());
    }

    @Test
    void testAddSite_Failure_DuplicateAddress() throws Exception {
        domain.addSite("Herzl 12", "Dana", "052-8888888");
        Response response = domain.addSite("Herzl 12", "Avi", "050-7777777");
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().toLowerCase().contains("exist"));
    }

    @Test
    void testAddDeliveryOneSiteDes_Success() throws Exception {

        domain.addTruck("TRK001", "Volvo", 1500, 6000, "A");
        domain.addSite("Site A", "Gal", "052-1111111");
        domain.addSite("Site B", "Tal", "052-2222222");
        domain.addBranch("Shefa", "01", "Tel Aviv 11", "Shani", "050-1111111");
        domain.addEmployee("Dani", "1", "123 45 67", "4000 01/01/2023 0 0 Pension", "DRIVER", "01", "A");
        domain.addEmployee("Shan", "2", "123 45 67", "4000 01/01/2023 0 0 Pension", "SHIFT_MANAGER", "01", "");
        domain.addEmployee("Machsan", "3", "123 45 67", "4000 01/01/2023 0 0 Pension", "STORE_KEEPER", "01", "");

        Map<Role, Integer> roles = new HashMap<>();
        roles.put(Role.SHIFT_MANAGER, 1);
        roles.put(Role.DRIVER, 1);
        roles.put(Role.STORE_KEEPER, 1);
        String formattedDate = LocalDate.of(2025, 4, 30).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        domain.setShiftRequirements("01", formattedDate, "M", "08:00", "16:00",roles);

        domain.handleAddEmployeeToShift("01",formattedDate, "M", "1", Role.DRIVER);
        domain.handleAddEmployeeToShift("01",formattedDate, "M", "2", Role.SHIFT_MANAGER);
        domain.handleAddEmployeeToShift("01",formattedDate, "M", "3", Role.STORE_KEEPER);

        Response response = domain.addDelivery(
                formattedDate,
                "08:00",
                "M",
                "TRK001",
                "1",
                "Tel Aviv 11",
                "Site A,Site B"
        );

        assertTrue(response.isSuccess());
    }

    @Test
    void testAddDeliveryFails_DriverNotInShift() throws Exception {
        domain.addTruck("TRK002", "Mercedes", 1800, 8000, "A");
        domain.addSite("Site C", "Dana", "052-3333333");
        domain.addBranch("Haifa", "02", "Haifa 22", "Avi", "050-2222222");

        domain.addEmployee("Guy", "10", "222 33 44", "4000 01/01/2023 0 0 Pension", "DRIVER", "02", "A");
        domain.addEmployee("Ella", "11", "222 33 44", "4000 01/01/2023 0 0 Pension", "SHIFT_MANAGER", "02", "");
        domain.addEmployee("Tom", "12", "222 33 44", "4000 01/01/2023 0 0 Pension", "STORE_KEEPER", "02", "");

        Map<Role, Integer> roles = new HashMap<>();
        roles.put(Role.SHIFT_MANAGER, 1);
        roles.put(Role.DRIVER, 1);
        roles.put(Role.STORE_KEEPER, 1);

        String date = "31/05/2025";
        domain.setShiftRequirements("02", date, "M", "08:00", "16:00", roles);

        domain.handleAddEmployeeToShift("02", date, "M", "11", Role.SHIFT_MANAGER);
        domain.handleAddEmployeeToShift("02", date, "M", "12", Role.STORE_KEEPER);

        Response response = domain.addDelivery(date, "08:00", "M", "TRK002", "10", "Haifa 22", "Site C");
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("not assigned"));
    }

    @Test
    void testAddDeliveryFails_LicenseMismatch() throws Exception {
        domain.addTruck("TRK003", "Ford", 1600, 5000, "B");
        domain.addSite("Site D", "Rami", "052-4444444");
        domain.addBranch("Jerusalem", "03", "Jerusalem 33", "Tamar", "050-3333333");

        domain.addEmployee("Liran", "20", "111 22 33", "4000 01/01/2023 0 0 Pension", "DRIVER", "03", "A");
        domain.addEmployee("Oren", "21", "111 22 33", "4000 01/01/2023 0 0 Pension", "SHIFT_MANAGER", "03", "");
        domain.addEmployee("Noa", "22", "111 22 33", "4000 01/01/2023 0 0 Pension", "STORE_KEEPER", "03", "");

        Map<Role, Integer> roles = new HashMap<>();
        roles.put(Role.SHIFT_MANAGER, 1);
        roles.put(Role.DRIVER, 1);
        roles.put(Role.STORE_KEEPER, 1);

        String date = "31/05/2025";
        domain.setShiftRequirements("03", date, "M", "08:00", "16:00", roles);

        domain.handleAddEmployeeToShift("03", date, "M", "20", Role.DRIVER);
        domain.handleAddEmployeeToShift("03", date, "M", "21", Role.SHIFT_MANAGER);
        domain.handleAddEmployeeToShift("03", date, "M", "22", Role.STORE_KEEPER);

        Response response = domain.addDelivery(date, "08:00", "M", "TRK003", "20", "Jerusalem 33", "Site D");
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().toLowerCase().contains("not match"));
    }

    @Test
    void testAddDeliveryFails_NoStorekeeperAtBranchSou() throws Exception {
        domain.addTruck("TRK004", "MAN", 2000, 9000, "A");
        domain.addSite("Site E", "Sarit", "052-5555555");
        domain.addBranch("Ashdod", "04", "Ashdod 44", "Yoni", "050-4444444");

        domain.addEmployee("Bar", "30", "555 66 77", "4000 01/01/2023 0 0 Pension", "DRIVER", "04", "A");
        domain.addEmployee("Nir", "31", "555 66 77", "4000 01/01/2023 0 0 Pension", "SHIFT_MANAGER", "04", "");

        Map<Role, Integer> roles = new HashMap<>();
        roles.put(Role.SHIFT_MANAGER, 1);
        roles.put(Role.DRIVER, 1);

        String date = "31/05/2025";
        domain.setShiftRequirements("04", date, "M", "08:00", "16:00", roles);

        domain.handleAddEmployeeToShift("04", date, "M", "30", Role.DRIVER);
        domain.handleAddEmployeeToShift("04", date, "M", "31", Role.SHIFT_MANAGER);

        Response response = domain.addDelivery(date, "08:00", "M", "TRK004", "30", "Ashdod 44", "Site E");
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().toLowerCase().contains("storekeeper assigned"));
    }

    @Test
    void testAddDeliveryFails_NoStorekeeperAtBranchDestination() throws Exception {
        domain.addTruck("TRK005", "DAF", 1800, 7000, "B");

        domain.addSite("Site F", "Noam", "052-7777777");
        domain.addBranch("Beersheba", "05", "Beersheba 55", "Lior", "050-5555555");
        domain.addBranch("Eilat", "06", "Eilat 66", "Ronen", "050-6666666");

        domain.addEmployee("Avi", "40", "666 77 88", "4000 01/01/2023 0 0 Pension", "DRIVER", "05", "B");
        domain.addEmployee("Lina", "41", "666 77 88", "4000 01/01/2023 0 0 Pension", "SHIFT_MANAGER", "05", "");
        domain.addEmployee("Dana", "42", "666 77 88", "4000 01/01/2023 0 0 Pension", "STORE_KEEPER", "05", "");

        Map<Role, Integer> roles = new HashMap<>();
        roles.put(Role.SHIFT_MANAGER, 1);
        roles.put(Role.DRIVER, 1);
        roles.put(Role.STORE_KEEPER, 1);

        String date = "31/05/2025";
        domain.setShiftRequirements("05", date, "M", "08:00", "16:00", roles);
        domain.setShiftRequirements("06", date, "M", "08:00", "16:00", roles);

        domain.handleAddEmployeeToShift("05", date, "M", "40", Role.DRIVER);
        domain.handleAddEmployeeToShift("05", date, "M", "41", Role.SHIFT_MANAGER);
        domain.handleAddEmployeeToShift("05", date, "M", "42", Role.STORE_KEEPER);

        Response response = domain.addDelivery(
                date,
                "08:00",
                "M",
                "TRK005",
                "40",
                "Beersheba 55",
                "Site F,Eilat 66"
        );

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("storekeeper assigned"));
    }

    @Test
    void testAddDelivery_SourceIsSite_DestinationHasBranchAndSite_Success() throws Exception {
        domain.addTruck("TRK999", "Scania", 1800, 7000, "B");

        domain.addSite("Source Site", "Avi", "050-1111111");

        domain.addSite("Destination Site", "Dana", "050-2222222");
        domain.addBranch("North Branch", "99", "Haifa 99", "Gila", "050-9999999");

        domain.addEmployee("Yossi", "999", "321 65 98", "5500 01/01/2023 0 0 Pension", "DRIVER", "99", "B");
        domain.addEmployee("Lina", "998", "321 65 99", "5500 01/01/2023 0 0 Pension", "SHIFT_MANAGER", "99", "");
        domain.addEmployee("Itay", "997", "321 65 88", "5500 01/01/2023 0 0 Pension", "STORE_KEEPER", "99", "");

        Map<Role, Integer> roles = new HashMap<>();
        roles.put(Role.SHIFT_MANAGER, 1);
        roles.put(Role.DRIVER, 1);
        roles.put(Role.STORE_KEEPER, 1);
        String date = "01/06/2025";
        domain.setShiftRequirements("99", date, "M", "08:00", "16:00", roles);

        domain.handleAddEmployeeToShift("99", date, "M", "999", Role.DRIVER);
        domain.handleAddEmployeeToShift("99", date, "M", "998", Role.SHIFT_MANAGER);
        domain.handleAddEmployeeToShift("99", date, "M", "997", Role.STORE_KEEPER);

        Response response = domain.addDelivery(
                date,
                "08:00",
                "M",
                "TRK999",
                "999",
                "Source Site",
                "Haifa 99,Destination Site"
        );

        assertTrue(response.isSuccess());
    }

}
