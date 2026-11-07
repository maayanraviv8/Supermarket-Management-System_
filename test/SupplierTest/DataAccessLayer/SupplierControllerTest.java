package SupplierTest.DataAccessLayer;

import Supplier.Backend.DataAccessLayer.FactoryDALTest;
import Supplier.Backend.DataAccessLayer.SupplierController;
import Supplier.Backend.domainLayer.Payment;
import Supplier.Backend.DTO.AgreementDTO;
import Supplier.Backend.DTO.OrderDTO;
import Supplier.Backend.DTO.ProductDTO;
import Supplier.Backend.DTO.SupplierDTO;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SupplierControllerTest {
    private static SupplierController controller;
    private static final String dbUrl = FactoryDALTest.getUrl();

    @BeforeAll
    static void setup() throws Exception {
        new FactoryDALTest().DeleteData();
        controller = new SupplierController(FactoryDALTest.getUrl());
        insertInitialData();
    }



    private static void insertInitialData() throws SQLException {
        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            PreparedStatement stmt = conn.prepareStatement("""
                INSERT OR IGNORE INTO Suppliers (supplierId, name, supplyDay, address, bankAccount, isDelivery, paymentMethod, paymentTerm)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """);
            stmt.setLong(1, 8);
            stmt.setString(2, "supp1");
            stmt.setInt(3, 4);
            stmt.setString(4, "address1");
            stmt.setString(5, "123456");
            stmt.setBoolean(6, true);
            stmt.setString(7, "CASH");
            stmt.setString(8, "IMMEDIATE");
            stmt.executeUpdate();
        }
    }


    @Test
    void testAddAndUploadSupplier() throws SQLException {
        SupplierDTO s2 = new SupplierDTO("supp2", 9, "55555", 2, Payment.PaymentMethod.CASH, Payment.PaymentTerm.BEGINNING_OF_MONTH, "address2", true
        );
        s2.addContact("contact2", "123456789");
        controller.addSupplierDB(s2);

        SupplierDTO s2DB = controller.uploadSupplier(9);
        assertNotNull(s2DB);

        assertEquals("supp2", s2DB.getName());
        assertEquals(9, s2DB.getSupplierId());
        assertEquals("55555", s2DB.getBankAccount());
        assertEquals(2, s2DB.getSupplyDay());
        assertEquals("address2", s2DB.getAddress());
        assertEquals(Payment.PaymentMethod.CASH, s2DB.getPayment().getPaymentMethod());
        assertEquals(Payment.PaymentTerm.BEGINNING_OF_MONTH, s2DB.getPayment().getPaymentTerm());
        assertEquals(1, s2DB.getContacts().size());
        assertEquals("contact2", s2DB.getContacts().get(0).getName());
        assertEquals("123456789", s2DB.getContacts().get(0).getPhone());

        assertTrue(s2DB.isDelivery());
    }

    @Test
    void testDeleteSupplier() throws SQLException {
        SupplierDTO s3 = new SupplierDTO(
                "supp3",
                10,
                "99999",
                5,
                Payment.PaymentMethod.CASH,
                Payment.PaymentTerm.BEGINNING_OF_MONTH,
                "address3",
                false
        );
        s3.addContact("contact3", "11111");

        controller.addSupplierDB(s3);
        SupplierDTO s3BeforeDelete = controller.uploadSupplier(10);
        assertNotNull(s3BeforeDelete);
        assertEquals("supp3", s3BeforeDelete.getName());

        controller.deleteSupplier(10);

        SupplierDTO s3AfterDelete = controller.uploadSupplier(10);
        assertNull(s3AfterDelete);
    }

    @Test
    void testUpdateSupplierAddress() throws SQLException {
        SupplierDTO s4 = new SupplierDTO(
                "supp4",
                11,
                "44444",
                3,
                Payment.PaymentMethod.CASH,
                Payment.PaymentTerm.END_OF_MONTH,
                "oldAddress",
                true
        );
        controller.addSupplierDB(s4);

        SupplierDTO beforeUpdate = controller.uploadSupplier(11);
        assertEquals("oldAddress", beforeUpdate.getAddress());

        controller.updateSupplierAddress(11, "newAddress");

        SupplierDTO afterUpdate = controller.uploadSupplier(11);
        assertEquals("newAddress", afterUpdate.getAddress());
    }

    @Test
    void testUpdateSupplierBankAccount() throws SQLException {
        SupplierDTO s5 = new SupplierDTO(
                "supp5",
                12,
                "11111",
                4,
                Payment.PaymentMethod.CASH,
                Payment.PaymentTerm.UPON_DELIVERY,
                "address5",
                false
        );
        s5.addContact("contact5", "22222");
        controller.addSupplierDB(s5);

        SupplierDTO beforeUpdate = controller.uploadSupplier(12);
        assertEquals("11111", beforeUpdate.getBankAccount());

        controller.updateSupplierBankAccount(12, "22222");

        SupplierDTO afterUpdate = controller.uploadSupplier(12);
        assertEquals("22222", afterUpdate.getBankAccount());
    }

    @Test
    void testUpdateSupplierPaymentMethod() throws SQLException {
        SupplierDTO s6 = new SupplierDTO(
                "supp6",
                13,
                "33333",
                1,
                Payment.PaymentMethod.CASH,
                Payment.PaymentTerm.UPON_DELIVERY,
                "address6",
                true
        );
        s6.addContact("contact6", "444444");

        controller.addSupplierDB(s6);

        SupplierDTO beforeUpdate = controller.uploadSupplier(13);
        assertEquals(Payment.PaymentMethod.CASH, beforeUpdate.getPayment().getPaymentMethod());

        controller.updateSupplierPaymentMethod(13, "CHECK");

        SupplierDTO afterUpdate = controller.uploadSupplier(13);
        assertEquals(Payment.PaymentMethod.CHECK, afterUpdate.getPayment().getPaymentMethod());
    }

    @Test
    void testUpdateSupplierPaymentTerm() throws SQLException {
        SupplierDTO s7 = new SupplierDTO(
                "supp7",
                14,
                "77777",
                6,
                Payment.PaymentMethod.CASH,
                Payment.PaymentTerm.UPON_DELIVERY,
                "address7",
                false
        );
        s7.addContact("contact7", "777777");

        controller.addSupplierDB(s7);

        SupplierDTO beforeUpdate = controller.uploadSupplier(14);
        assertEquals(Payment.PaymentTerm.UPON_DELIVERY, beforeUpdate.getPayment().getPaymentTerm());

        controller.updateSupplierPaymentTerm(14, "BEGINNING_OF_MONTH");

        SupplierDTO afterUpdate = controller.uploadSupplier(14);
        assertEquals(Payment.PaymentTerm.BEGINNING_OF_MONTH, afterUpdate.getPayment().getPaymentTerm());
    }

    @Test
    void testUpdateSupplierDelivery() throws SQLException {
        SupplierDTO s8 = new SupplierDTO(
                "supp8",
                15,
                "88888",
                3,
                Payment.PaymentMethod.CASH,
                Payment.PaymentTerm.END_OF_MONTH,
                "address8",
                false
        );
        s8.addContact("contact8", "888888");

        controller.addSupplierDB(s8);

        SupplierDTO beforeUpdate = controller.uploadSupplier(15);
        assertFalse(beforeUpdate.isDelivery());

        controller.updateSupplierDelivery(15, true);

        SupplierDTO afterUpdate = controller.uploadSupplier(15);
        assertTrue(afterUpdate.isDelivery());
    }

    @Test
    void testUpdateSupplierSupplyDay() throws SQLException {
        SupplierDTO s9 = new SupplierDTO(
                "supp9",
                16,
                "99999",
                1,
                Payment.PaymentMethod.CASH,
                Payment.PaymentTerm.END_OF_MONTH,
                "address9",
                true
        );
        s9.addContact("contact9", "999999999");

        controller.addSupplierDB(s9);

        SupplierDTO beforeUpdate = controller.uploadSupplier(16);
        assertEquals(1, beforeUpdate.getSupplyDay());

        controller.updateSupplierSupplyDay(16, 4);

        SupplierDTO afterUpdate = controller.uploadSupplier(16);
        assertEquals(4, afterUpdate.getSupplyDay());
    }


    @Test
    void testAddAndUploadAgreement() throws SQLException {
        SupplierDTO s10 = new SupplierDTO(
                "supp10",
                17,
                "101010",
                2,
                Payment.PaymentMethod.CASH,
                Payment.PaymentTerm.BEGINNING_OF_MONTH,
                "address10",
                true
        );
        s10.addContact("contact10", "101010101");

        controller.addSupplierDB(s10);

        Map<String, ProductDTO> products = new HashMap<>();
        ProductDTO p1 = new ProductDTO("prod1", "p1", "manu1", 10.5, 5, 20, "PER_UNIT", "cat0", "cat1", "cat2", 7);
        products.put("p1", p1);
        AgreementDTO agreement = new AgreementDTO(products, "0509999999", 1);

        controller.addAgreementDB(agreement, 17);

        List<AgreementDTO> agreements = controller.uploadAgreements(17);
        assertEquals(1, agreements.size());
        AgreementDTO fromDB = agreements.get(0);

        assertEquals(1, fromDB.getAgreementId());
        assertEquals("0509999999", fromDB.getPhoneNumber());
        assertEquals(1, fromDB.getProducts().size());
        assertTrue(fromDB.getProducts().containsKey("p1"));
        assertEquals("prod1", fromDB.getProducts().get("p1").getName());
    }

    @Test
    void testDeleteAgreement() throws SQLException {
        SupplierDTO s11 = new SupplierDTO(
                "supp11",
                18,
                "111111",
                3,
                Payment.PaymentMethod.CASH,
                Payment.PaymentTerm.BEGINNING_OF_MONTH,
                "address11",
                false
        );
        s11.addContact("contact11", "111111111");

        controller.addSupplierDB(s11);

        Map<String, ProductDTO> products = new HashMap<>();
        ProductDTO p = new ProductDTO("prod2", "p2", "manu2", 20.0, 10, 50, "PER_UNIT", "cat0", "cat1", "cat2", 10);
        products.put("p2", p);
        AgreementDTO agreement = new AgreementDTO(products, "0520000000", 2);

        controller.addAgreementDB(agreement, 18);

        List<AgreementDTO> agreementsBefore = controller.uploadAgreements(18);
        assertEquals(1, agreementsBefore.size());

        controller.deleteAgreement(18, 2);

        List<AgreementDTO> agreementsAfter = controller.uploadAgreements(18);
        assertEquals(0, agreementsAfter.size());
    }

    @Test
    void testAddAndUploadProducts() throws SQLException {
        SupplierDTO s12 = new SupplierDTO(
                "supp12",
                19,
                "191919",
                4,
                Payment.PaymentMethod.BANK_TRANSFER,
                Payment.PaymentTerm.UPON_DELIVERY,
                "address12",
                true
        );
        s12.addContact("contact12", "121212121");

        controller.addSupplierDB(s12);

        Map<String, ProductDTO> products = new HashMap<>();
        ProductDTO p1 = new ProductDTO(
                "prod12",
                "P12",
                "manu12",
                99.90,
                10,
                100,
                "PER_UNIT",
                "cat0",
                "cat1",
                "cat2",
                20271212
        );
        products.put("P12", p1);

        AgreementDTO agreement = new AgreementDTO(products, "0541212121", 12);
        controller.addAgreementDB(agreement, 19);

        Map<String, ProductDTO> fromDB = controller.uploadProducts(19, 12);
        assertEquals(1, fromDB.size());
        assertTrue(fromDB.containsKey("P12"));

        ProductDTO retrieved = fromDB.get("P12");
        assertEquals("prod12", retrieved.getName());
        assertEquals("manu12", retrieved.getManufacturer());
        assertEquals(99.90, retrieved.getCost());
        assertEquals(10, retrieved.getDiscount());
        assertEquals(100, retrieved.getQuantityForDiscount());
        assertEquals("PER_UNIT", retrieved.getMeasurementType().toString());
    }

    @Test
    void testDeleteProduct() throws SQLException {
        SupplierDTO s13 = new SupplierDTO(
                "supp13",
                20,
                "202020",
                5,
                Payment.PaymentMethod.CHECK,
                Payment.PaymentTerm.BEGINNING_OF_MONTH,
                "address13",
                true
        );
        s13.addContact("contact13", "131313131");

        controller.addSupplierDB(s13);

        Map<String, ProductDTO> products = new HashMap<>();
        ProductDTO p = new ProductDTO(
                "prod13",
                "P13",
                "manu13",
                88.8,
                15,
                30,
                "PER_UNIT",
                "cat0", "cat1", "cat2", 20261231
        );
        products.put("P13", p);
        AgreementDTO agreement = new AgreementDTO(products, "0531313131", 13);
        controller.addAgreementDB(agreement, 20);

        Map<String, ProductDTO> beforeDelete = controller.uploadProducts(20, 13);
        assertTrue(beforeDelete.containsKey("P13"));

        controller.deleteProduct(20, "P13");

        Map<String, ProductDTO> afterDelete = controller.uploadProducts(20, 13);
        assertFalse(afterDelete.containsKey("P13"));
    }

    @Test
    void testAddAndUploadOrder() throws SQLException {
        SupplierDTO s14 = new SupplierDTO(
                "supp14",
                21,
                "212121",
                1,
                Payment.PaymentMethod.CASH,
                Payment.PaymentTerm.UPON_DELIVERY,
                "address14",
                true
        );
        s14.addContact("contact14", "141414141");

        controller.addSupplierDB(s14);

        Map<String, OrderDTO.ProductEntry> products = new HashMap<>();
        products.put("P14", new OrderDTO.ProductEntry(50, 10));

        OrderDTO order = new OrderDTO(
                LocalDate.of(2025, 5, 26),
                products,
                "0521414141",
                1001,
                "IN_PROGRESS"
        );

        controller.addOrderDB(order, 21);

        List<OrderDTO> orders = controller.uploadOrders(21);
        assertEquals(1, orders.size());

        OrderDTO fromDB = orders.get(0);
        assertEquals(1001, fromDB.getOrderId());
        assertEquals("0521414141", fromDB.getPhoneNumber());
        assertEquals("IN_PROGRESS", fromDB.getStatus());
        assertEquals(LocalDate.of(2025, 5, 26), fromDB.getOrderDate());
        assertEquals(1, fromDB.getProducts().size());

        OrderDTO.ProductEntry entry = fromDB.getProducts().get("P14");
        assertEquals(50, entry.getQuantity());
        assertEquals(10, entry.getDiscount());
    }

    @Test
    void testDeleteOrder() throws SQLException {
        SupplierDTO s15 = new SupplierDTO(
                "supp15",
                22,
                "222222",
                2,
                Payment.PaymentMethod.BANK_TRANSFER,
                Payment.PaymentTerm.END_OF_MONTH,
                "address15",
                false
        );
        s15.addContact("contact15", "151515151");

        controller.addSupplierDB(s15);

        Map<String, OrderDTO.ProductEntry> products = new HashMap<>();
        products.put("P15", new OrderDTO.ProductEntry(20, 5));

        OrderDTO order = new OrderDTO(
                LocalDate.of(2025, 5, 27),
                products,
                "0521515151",
                2002,
                "IN_PROGRESS"
        );

        controller.addOrderDB(order, 22);

        List<OrderDTO> beforeDelete = controller.uploadOrders(22);
        assertEquals(1, beforeDelete.size());

        controller.deleteOrder(22, 2002);

        List<OrderDTO> afterDelete = controller.uploadOrders(22);
        assertEquals(0, afterDelete.size());
    }

    @Test
    void testUpdateAgreementPhoneNumber() throws SQLException {
        SupplierDTO s16 = new SupplierDTO(
                "supp16",
                23,
                "161616",
                4,
                Payment.PaymentMethod.CASH,
                Payment.PaymentTerm.IN_ADVANCE,
                "address16",
                true
        );
        s16.addContact("contact16", "161616161");

        controller.addSupplierDB(s16);

        Map<String, ProductDTO> products = new HashMap<>();
        ProductDTO p = new ProductDTO("prod16", "P16", "manu16", 10.0, 5, 10, "PER_UNIT", "cat0", "cat1", "cat2", 7);
        products.put("P16", p);

        AgreementDTO agreement = new AgreementDTO(products, "0500000016", 16);
        controller.addAgreementDB(agreement, 23);

        controller.updateAgreementPhoneNumber(23, 16, "0509999916");

        AgreementDTO updated = controller.uploadAgreements(23).get(0);
        assertEquals("0509999916", updated.getPhoneNumber());
    }

    @Test
    void testUpdateProductCost() throws SQLException {
        SupplierDTO s18 = new SupplierDTO(
                "supp18",
                25,
                "181818",
                6,
                Payment.PaymentMethod.BANK_TRANSFER,
                Payment.PaymentTerm.BEGINNING_OF_MONTH,
                "address18",
                true
        );
        s18.addContact("contact18", "181818181");

        controller.addSupplierDB(s18);

        Map<String, ProductDTO> products = new HashMap<>();
        ProductDTO p = new ProductDTO("prod18", "P18", "manu18", 20.0, 5, 15, "PER_UNIT", "cat0", "cat1", "cat2", 8);
        products.put("P18", p);

        AgreementDTO agreement = new AgreementDTO(products, "0521818181", 18);
        controller.addAgreementDB(agreement, 25);

        controller.updateProductCost(25, 18, "P18", 42.42);

        ProductDTO updated = controller.uploadProducts(25, 18).get("P18");
        assertEquals(42.42, updated.getCost());
    }

    @Test
    void testUpdateProductDiscount() throws SQLException {
        SupplierDTO supplier = new SupplierDTO("suppFix", 25, "12345", 1, Payment.PaymentMethod.CASH, Payment.PaymentTerm.BEGINNING_OF_MONTH, "addr", true);
        supplier.addContact("contact", "0500000000");
        controller.addSupplierDB(supplier);

        Map<String, ProductDTO> products = new HashMap<>();
        products.put("P18", new ProductDTO("prodFix", "P18", "manu", 20.0, 5, 10, "PER_UNIT", "c0", "c1", "c2", 30));
        AgreementDTO agreement = new AgreementDTO(products, "0501234567", 18);
        controller.addAgreementDB(agreement, 25);

        controller.updateProductDiscount(25, 18, "P18", 12);

        ProductDTO updated = controller.uploadProducts(25, 18).get("P18");
        assertNotNull(updated);
        assertEquals(12, updated.getDiscount());
    }

    @Test
    void testUpdateOrderProductDiscount() throws SQLException {
        SupplierDTO s22 = new SupplierDTO(
                "supp22",
                122,
                "222000",
                3,
                Payment.PaymentMethod.CASH,
                Payment.PaymentTerm.UPON_DELIVERY,
                "address22",
                true
        );
        s22.addContact("contact22", "0522222222");

        controller.addSupplierDB(s22);

        Map<String, OrderDTO.ProductEntry> products = new HashMap<>();
        products.put("P22", new OrderDTO.ProductEntry(100, 5));

        OrderDTO order = new OrderDTO(
                LocalDate.of(2025, 6, 1),
                products,
                "0502222222",
                2222,
                "IN_PROGRESS"
        );

        controller.addOrderDB(order, 122);

        controller.updateOrderProductDiscount(122, 2222, "P22", 15);

        List<OrderDTO> orders = controller.uploadOrders(122);
        assertEquals(1, orders.size());

        OrderDTO fromDB = orders.get(0);
        OrderDTO.ProductEntry updatedEntry = fromDB.getProducts().get("P22");
        assertNotNull(updatedEntry);
        assertEquals(15, updatedEntry.getDiscount());
        assertEquals(100, updatedEntry.getQuantity());
    }

    @Test
    void testUpdateOrderStatus() throws SQLException {
        SupplierDTO s23 = new SupplierDTO(
                "supp23",
                123,
                "233333",
                2,
                Payment.PaymentMethod.CHECK,
                Payment.PaymentTerm.IN_ADVANCE,
                "address23",
                false
        );
        s23.addContact("contact23", "0533333333");

        controller.addSupplierDB(s23);

        Map<String, OrderDTO.ProductEntry> products = new HashMap<>();
        products.put("P23", new OrderDTO.ProductEntry(10, 0));

        OrderDTO order = new OrderDTO(
                LocalDate.of(2025, 6, 2),
                products,
                "0503333333",
                2323,
                "IN_PROGRESS"
        );

        controller.addOrderDB(order, 123);
        controller.updateOrderStatus(123, 2323, "DONE");

        List<OrderDTO> orders = controller.uploadOrders(123);
        assertEquals(1, orders.size());

        OrderDTO fromDB = orders.get(0);
        assertEquals("DONE", fromDB.getStatus());
    }

    @Test
    void testUpdateOrderPhoneNumber() throws SQLException {
        SupplierDTO s24 = new SupplierDTO(
                "supp24",
                124,
                "244444",
                4,
                Payment.PaymentMethod.BANK_TRANSFER,
                Payment.PaymentTerm.BEGINNING_OF_MONTH,
                "address24",
                true
        );
        s24.addContact("contact24", "0544444444");

        controller.addSupplierDB(s24);

        Map<String, OrderDTO.ProductEntry> products = new HashMap<>();
        products.put("P24", new OrderDTO.ProductEntry(25, 0));

        OrderDTO order = new OrderDTO(
                LocalDate.of(2025, 6, 3),
                products,
                "0500000000",
                2424,
                "IN_PROGRESS"
        );

        controller.addOrderDB(order, 124);

        controller.updateOrderPhoneNumber(124, 2424, "0509999999");

        List<OrderDTO> orders = controller.uploadOrders(124);
        assertEquals(1, orders.size());

        OrderDTO fromDB = orders.get(0);
        assertEquals("0509999999", fromDB.getPhoneNumber());
    }

    @Test
    void testDeleteAllData() throws SQLException {
        SupplierDTO s25 = new SupplierDTO(
                "supp25",
                125,
                "255555",
                1,
                Payment.PaymentMethod.CASH,
                Payment.PaymentTerm.UPON_DELIVERY,
                "address25",
                true
        );
        s25.addContact("contact25", "0555555555");

        controller.addSupplierDB(s25);

        Map<String, ProductDTO> products = new HashMap<>();
        ProductDTO p = new ProductDTO("prod25", "P25", "manu25", 12.0, 5, 10, "PER_UNIT", "cat0", "cat1", "cat2", 20);
        products.put("P25", p);
        AgreementDTO agreement = new AgreementDTO(products, "0500000025", 25);
        controller.addAgreementDB(agreement, 125);

        Map<String, OrderDTO.ProductEntry> orderProducts = new HashMap<>();
        orderProducts.put("P25", new OrderDTO.ProductEntry(5, 0));
        OrderDTO order = new OrderDTO(LocalDate.of(2025, 6, 4), orderProducts, "0501234567", 2525, "IN_PROGRESS");
        controller.addOrderDB(order, 125);

        assertNotNull(controller.uploadSupplier(125));
        assertEquals(1, controller.uploadAgreements(125).size());
        assertEquals(1, controller.uploadOrders(125).size());

        controller.deleteAllData();

        assertNull(controller.uploadSupplier(125));
        assertEquals(0, controller.getAllSupplierIds().size());
    }

    @Test
    void testGetAllSupplierIds() throws SQLException {
        SupplierDTO s1 = new SupplierDTO("supp26a", 1261, "10001", 1, Payment.PaymentMethod.CASH, Payment.PaymentTerm.IN_ADVANCE, "addr1", true);
        s1.addContact("contact26a", "0501111111");
        SupplierDTO s2 = new SupplierDTO("supp26b", 1262, "10002", 2, Payment.PaymentMethod.CHECK, Payment.PaymentTerm.UPON_DELIVERY, "addr2", false);
        s2.addContact("contact26b", "0502222222");
        SupplierDTO s3 = new SupplierDTO("supp26c", 1263, "10003", 3, Payment.PaymentMethod.BANK_TRANSFER, Payment.PaymentTerm.BEGINNING_OF_MONTH, "addr3", true);
        s3.addContact("contact26c", "0503333333");

        controller.addSupplierDB(s1);
        controller.addSupplierDB(s2);
        controller.addSupplierDB(s3);

        List<Long> ids = controller.getAllSupplierIds();

        assertTrue(ids.contains(1261L));
        assertTrue(ids.contains(1262L));
        assertTrue(ids.contains(1263L));
    }

    @Test
    void testGetMaxOrderId() throws SQLException {
        SupplierDTO s27 = new SupplierDTO(
                "supp27",
                127,
                "270000",
                1,
                Payment.PaymentMethod.CASH,
                Payment.PaymentTerm.UPON_DELIVERY,
                "address27",
                true
        );
        s27.addContact("contact27", "0527272727");
        controller.addSupplierDB(s27);

        Map<String, OrderDTO.ProductEntry> products = new HashMap<>();
        products.put("P27", new OrderDTO.ProductEntry(1, 0));

        OrderDTO o1 = new OrderDTO(LocalDate.of(2025, 6, 5), products, "0502701", 2701, "IN_PROGRESS");
        OrderDTO o2 = new OrderDTO(LocalDate.of(2025, 6, 6), products, "0502702", 2702, "IN_PROGRESS");
        OrderDTO o3 = new OrderDTO(LocalDate.of(2025, 6, 7), products, "0502705", 2705, "IN_PROGRESS");

        controller.addOrderDB(o1, 127);
        controller.addOrderDB(o2, 127);
        controller.addOrderDB(o3, 127);

        int maxId = controller.getMaxOrderId(127);
        assertEquals(2705, maxId);
    }

    @Test
    void testGetMaxAgreementId() throws SQLException {
        SupplierDTO s28 = new SupplierDTO(
                "supp28",
                128,
                "280000",
                1,
                Payment.PaymentMethod.CHECK,
                Payment.PaymentTerm.BEGINNING_OF_MONTH,
                "address28",
                false
        );
        s28.addContact("contact28", "0528282828");
        controller.addSupplierDB(s28);

        Map<String, ProductDTO> products1 = new HashMap<>();
        ProductDTO p1 = new ProductDTO("prod28a", "P28A", "manu28", 50.0, 5, 10, "PER_UNIT", "cat0", "cat1", "cat2", 55);
        products1.put("P28A", p1);
        AgreementDTO a1 = new AgreementDTO(products1, "0502801", 2801);

        Map<String, ProductDTO> products2 = new HashMap<>();
        ProductDTO p2 = new ProductDTO("prod28b", "P28B", "manu28", 55.0, 7, 12, "PER_UNIT", "cat0", "cat1", "cat2", 66);
        products2.put("P28B", p2);
        AgreementDTO a2 = new AgreementDTO(products2, "0502809", 2809);

        controller.addAgreementDB(a1, 128);
        controller.addAgreementDB(a2, 128);

        int maxAgreementId = controller.getMaxAgreementId(128);
        assertEquals(2809, maxAgreementId);
    }






//    @Test
//    void testUpdateProductODDiscount() throws SQLException {
//        controller.updateProductODDiscount(25, 18, "P18", 30);
//
//        ProductDTO updated = controller.uploadProducts(25, 18).get("P18");
//        assertEquals(30, updated.getDiscount());
//    }



}
