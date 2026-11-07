package StorageTest;

import Storage.Backend.ServiceLayer.Response;
import Storage.Backend.ServiceLayer.StorageService;
import Storage.Frontend.PresentationLayer.Factory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class StorageFacadeTest {

    static Factory factory;
    static StorageService storage;

    @BeforeAll
    static void setUp() {
        factory = new Factory();
        factory.loadData();
        storage = factory.getStorageService();
    }

    @AfterEach
    void tearDown() {
        factory.DeleteData();
    }

    @Test
    void addNewProductToWareHouse() {
        try {
            String productDetails = "Milk Dairy DairyA Shelf1 4.5 5.0 1001 Tnuva 20 5";
            Response res = storage.AddNewProduct(productDetails);
            assertNull(res.getErrorMessage());

            Response move = storage.MoveProduct("1001 1");
            assertNull(move.getErrorMessage());

            Response fail = storage.MoveProduct("9999 1");
            assertNotNull(fail.getErrorMessage());
        } catch (Exception e) {
            factory.DeleteData();
            throw e;
        }
    }

    @Test
    void deleteProduct() {
        try {
            storage.AddNewProduct("Milk Dairy DairyA Shelf1 4.5 5.0 1002 Tnuva 7 10");
            storage.MoveProduct("1002 5");

            Response res = storage.DeleteProduct("1002 3");
            assertNull(res.getErrorMessage());

            Response fail1 = storage.DeleteProduct("9999 1");
            assertNotNull(fail1.getErrorMessage());

            Response fail2 = storage.DeleteProduct("1002 10");
            assertNotNull(fail2.getErrorMessage());

            Response ok = storage.DeleteProduct("1002 2");
            assertNull(ok.getErrorMessage());
        } catch (Exception e) {
            factory.DeleteData();
            throw e;
        }
    }

    @Test
    void moveProduct() {
        try {
            storage.AddNewProduct("Milk Dairy DairyA Shelf1 4.5 5.0 1003 Tnuva 20 7");

            Response success = storage.MoveProduct("1003 4");
            assertNull(success.getErrorMessage());

            Response tooMuch = storage.MoveProduct("1003 100");
            assertNotNull(tooMuch.getErrorMessage());

            Response notFound = storage.MoveProduct("9999 1");
            assertNotNull(notFound.getErrorMessage());

            Response remaining = storage.MoveProduct("1003 3");
            assertNull(remaining.getErrorMessage());
        } catch (Exception e) {
            factory.DeleteData();
            throw e;
        }
    }

    @Test
    void reportAboutDamaged() {
        try {
            storage.AddNewProduct("Bread Bakery White Regular 4.0 10 5.0 1010 Berman 6 9");
            storage.AddDamagedProduct("1010");
            Response damaged = storage.ReportAboutDamaged();
            assertNull(damaged.getErrorMessage());
            storage.DeleteProduct("1010 6");
            damaged = storage.ReportAboutDamaged();
            assertNull(damaged.getErrorMessage());
        } catch (Exception e) {
            factory.DeleteData();
            throw e;
        }
    }

    @Test
    void reportAboutExpired() {
        try {
            storage.AddNewProduct("Bread Bakery White Regular 4.0 10 5.0 1010 Berman 6 3");
            Response expired = storage.ReportAboutDamaged();
            assertNull(expired.getErrorMessage());
            storage.DeleteProduct("1010 6");
            expired = storage.ReportAboutDamaged();
            assertNull(expired.getErrorMessage());
        } catch (Exception e) {
            factory.DeleteData();
            throw e;
        }
    }

    @Test
    void applySaleByCategory() {
        try {
            storage.AddNewProduct("Milk Dairy DairyA Shelf1 4.5 5.0 1001 Tnuva 20 5");

            Response res = storage.ApplySaleByCategory("Dairy 15");
            assertNull(res.getErrorMessage());

            Response fail = storage.ApplySaleByCategory("UnknownCategory 10");
            assertNotNull(fail.getErrorMessage());
        } catch (Exception e) {
            factory.DeleteData();
            throw e;
        }
    }

    @Test
    void applySaleByProductId() {
        try {
            storage.AddNewProduct("Milk Dairy DairyA Shelf1 4.5 5.0 1005 Tnuva 20 10");

            Response res = storage.ApplySaleByProductId("1005 25");
            assertNull(res.getErrorMessage());

            Response fail = storage.ApplySaleByProductId("9999 5");
            assertNotNull(fail.getErrorMessage());

            Response again = storage.ApplySaleByProductId("1005 30");
            assertNull(again.getErrorMessage());
        } catch (Exception e) {
            factory.DeleteData();
            throw e;
        }
    }

    @Test
    void addDamagedProduct() {
        try {
            storage.AddNewProduct("Cheese Dairy Hard Mature 6.0 8.0 1011 Tnuva 10 3");
            storage.MoveProduct("1011 5");

            Response res = storage.AddDamagedProduct("1011 3");
            assertNull(res.getErrorMessage());

            Response fail = storage.AddDamagedProduct("1011 100");
            assertNotNull(fail.getErrorMessage());
            assertTrue(fail.getErrorMessage().contains("Product not found or insufficient quantity"));
        } catch (Exception e) {
            factory.DeleteData();
            throw e;
        }
    }

    @Test
    void addExpiredProduct() {
        try {
            storage.AddNewProduct("Yogurt Dairy Creamy LowFat 3.5 4.5 1012 Strauss 2 1");
            storage.MoveProduct("1012 2");

            for (int i = 0; i < 2; i++) {
                storage.NewDay();
            }

            Response expired = storage.AddExpiredProduct();
            assertNull(expired.getErrorMessage());

            Response report = storage.ReportAboutExpired();
            assertNull(report.getErrorMessage());
        } catch (Exception e) {
            factory.DeleteData();
            throw e;
        }
    }

    @Test
    void reportAboutCategory() {
        try {
            storage.AddNewProduct("Juice Beverages Fruit Orange 3.0 4.0 1013 Prigat 6 4");
            storage.MoveProduct("1013 6");

            Response report = storage.ReportAboutCategory("Beverages");
            assertNull(report.getErrorMessage());

            Response empty = storage.ReportAboutCategory("NonExisting");
            assertNull(empty.getErrorMessage());
        } catch (Exception e) {
            factory.DeleteData();
            throw e;
        }
    }

    @Test
    void reportAboutSubCategory() {
        try {
            storage.AddNewProduct("Soda Beverages Sparkling Cola 2.0 2.5 1014 CocaCola 10 5");
            storage.MoveProduct("1014 10");

            Response report = storage.ReportAboutSubCategory("Sparkling");
            assertNull(report.getErrorMessage());

            Response empty = storage.ReportAboutSubCategory("StillWater");
            assertNull(empty.getErrorMessage());
        } catch (Exception e) {
            factory.DeleteData();
            throw e;
        }
    }

    @Test
    void reportAboutSubSubCategory() {
        try {
            storage.AddNewProduct("Tea Beverages Herbal Chamomile 2.0 2.5 1015 Wissotzky 8 4");
            storage.MoveProduct("1015 8");

            Response report = storage.ReportAboutSubSubCategory("Chamomile");
            assertNull(report.getErrorMessage());

            Response empty = storage.ReportAboutSubSubCategory("GreenMint");
            assertNull(empty.getErrorMessage());
        } catch (Exception e) {
            factory.DeleteData();
            throw e;
        }
    }

    @Test
    void newDay() {
        try {
            storage.AddNewProduct("Butter Dairy Fat Creamy 5.0 6.0 1017 Tara 2 2");

            Response res = storage.NewDay();
            assertNull(res.getErrorMessage());

            for (int i = 0; i < 5; i++) {
                assertNull(storage.NewDay().getErrorMessage());
            }
        } catch (Exception e) {
            factory.DeleteData();
            throw e;
        }
    }
}
