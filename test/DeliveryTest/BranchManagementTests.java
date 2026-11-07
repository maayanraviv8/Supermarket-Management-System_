package DeliveryTest;

import HRandDelivery.DomainLayer.HRandDeliveryDomainService;
import HRandDelivery.Utils.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BranchManagementTests {

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
    void addBranch_success() throws Exception {
        Response response = domain.addBranch("Tel Aviv", "01", "Hashalom 10", "Gal", "050-1111111");
        assertTrue(response.isSuccess());
        assertTrue(response.getMessage().toLowerCase().contains("branch"));
    }

    @Test
    void addBranch_duplicateId_fails() throws Exception {
        domain.addBranch("Tel Aviv", "01", "Hashalom 10", "Gal", "050-1111111");
        Response response = domain.addBranch("Jerusalem", "01", "Herzl 5", "Dana", "050-2222222");

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().toLowerCase().contains("exist"));
    }

}
