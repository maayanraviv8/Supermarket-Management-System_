package SupplierTest.domainLayer;

import Supplier.Backend.domainLayer.*;
import Supplier.Backend.DTO.OrderDTO;
import Supplier.Backend.DTO.ProductDTO;
import Supplier.Backend.serviceLayer.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SupplierTest {
    private Supplier supplier;
    private Payment payment;
    private Contact contact;
    private Agreement agreement;
    private Product product;
    private Product product2;
    private Product product3;



        @BeforeEach
    void setUp() {
        contact = new Contact("Alice", "050-1234567");
        payment = new Payment(Payment.PaymentMethod.CASH, Payment.PaymentTerm.UPON_DELIVERY);

        String[] c = {"Dairy", "Milk", "Liquid"};
        product = new Product("Milk", "100", "Tnuva", 10, 10, Product.MeasurementType.PER_UNIT, 5, c, 7);
        c = new String[]{"Dairy", "Cheese", "Solid"};
        product2 = new Product("Cheese", "101", "Tnuva", 20, 5, Product.MeasurementType.PER_UNIT, 10, c, 14);
        c = new String[]{"Bakery", "Bread", "Solid"};
        product3 = new Product("Bread", "102", "Angel", 5, 0, Product.MeasurementType.PER_UNIT, 0, c, 3);

        List<Product> productList1 = new ArrayList<>();
        productList1.add(product);
        agreement = new Agreement(productList1, "050-9876543");

        List<Contact> contactList = new ArrayList<>();
        contactList.add(contact);
        List<Agreement> agreementList = new ArrayList<>();
        supplier = new Supplier("TestSupplier", 1L, "123-456", 2, contactList, agreementList, "Main Street", true, payment);
    }

    private void testAddMultipleOrders() {
        List<Product> list = new ArrayList<>();
        list.add(product);
        list.add(product2);
        Agreement a1 = new Agreement(list, "050-1");
        supplier.addAgreement(a1);

        Map<String, OrderDTO.ProductEntry> order1 = new HashMap<>();
        order1.put("100", new OrderDTO.ProductEntry(2, 0));
        order1.put("101", new OrderDTO.ProductEntry(1, 0));

        Map<String, OrderDTO.ProductEntry> order2 = new HashMap<>();
        order2.put("100", new OrderDTO.ProductEntry(1, 0));

        supplier.addOrder(LocalDate.now(), "050-1111111", order1, -1, "IN_PROGRESS");
        supplier.addOrder(LocalDate.now(), "050-2222222", order2, -1, "IN_PROGRESS");
    }


    // Tests for agreement
    @Test
    void testOrderAddedMessageContent() {
        List<Product> list = new ArrayList<>();
        list.add(product);
        Agreement agreement = new Agreement(list, "050-4444444");
        supplier.addAgreement(agreement);
        Map<String, OrderDTO.ProductEntry> orderProducts = new HashMap<>();
        orderProducts.put("100", new OrderDTO.ProductEntry(1, 0));
        Message orderMsg = supplier.addOrder(LocalDate.now(), "050-9999999", orderProducts, -1, "IN_PROGRESS");
        assertTrue(orderMsg.isSuccess());
        assertTrue(orderMsg.getMessage().contains("Order added successfully. Order ID: "));
        int lastOrderId = supplier.getAllOrdersIDs().get(supplier.getAllOrdersIDs().size() - 1);
        assertTrue(orderMsg.getMessage().contains(String.valueOf(lastOrderId)));
    }
    @Test
    void testAddAndRemoveAgreement() {
        Message addMsg = supplier.addAgreement(agreement);
        assertTrue(addMsg.isSuccess());
        assertEquals(1, supplier.getAgreements().size());
        assertEquals(1, supplier.getAllProductsInCatalog().size());

        Message delMsg = supplier.deleteAgreement(0);
        assertTrue(delMsg.isSuccess());
        assertEquals(0, supplier.getAgreements().size());
        assertEquals(0, supplier.getAllProductsInCatalog().size());
    }
    @Test
    void testRemoveProductsFromCatalogAfterMultipleAgreements() {
        List<Product> list1 = new ArrayList<>();
        list1.add(product);
        Agreement a1 = new Agreement(list1, "050-1");

        List<Product> list2 = new ArrayList<>();
        list2.add(product2);
        Agreement a2 = new Agreement(list2, "050-2");

        List<Product> list3 = new ArrayList<>();
        list3.add(product3);
        Agreement a3 = new Agreement(list3, "050-3");

        supplier.addAgreement(a1);
        supplier.addAgreement(a2);
        supplier.addAgreement(a3);

        assertEquals(3, supplier.getAllProductsInCatalog().size());

        supplier.deleteAgreement(1); // remove cheese
        assertEquals(2, supplier.getAllProductsInCatalog().size());

        supplier.deleteAgreement(0); // remove milk
        assertEquals(1, supplier.getAllProductsInCatalog().size());

        supplier.deleteAgreement(2); // remove bread
        assertEquals(0, supplier.getAllProductsInCatalog().size());
    }
    @Test
    void testAddAndRemoveProductFromAgreementAndCatalog() {
        supplier.addAgreement(agreement);
        //Product newProduct = new Product("Yogurt", "103", "Tnuva", 8, 5, Product.MeasurementType.PER_UNIT, 6);
        ProductDTO newProductDTO = new ProductDTO("Yogurt", "103", "Tnuva", 8, 5,6, "PER_UNIT", "Dairy", "Yogurt", "Liquid", 10);

        Message addMsg = supplier.addProductToAgreement(0, newProductDTO);
        assertTrue(addMsg.isSuccess());
        assertEquals(2, supplier.getAllProductsInCatalog().size());

        Message removeMsg = supplier.removeProductFromAgreement(0, "103");
        assertTrue(removeMsg.isSuccess());
        assertEquals(1, supplier.getAllProductsInCatalog().size());
    }
    @Test
    void testUpdateDiscountQuantityPriceOnProduct() {
        supplier.addAgreement(agreement);

        Message discountMsg = supplier.updateDiscount(0, 25, "100");
        assertTrue(discountMsg.isSuccess());

        Message quantityMsg = supplier.updateQuantity(0, 20, "100");
        assertTrue(quantityMsg.isSuccess());

        supplier.setCost(15.0, 0, "100");
        assertEquals(15.0, supplier.getCost("100"));
    }
    @Test
    void testUpdateDiscountWithInvalidAgreementId() {
        Message result = supplier.updateDiscount(99, 10, "100");
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Agreement 99 not found"));
    }
    @Test
    void testUpdateQuantityWithInvalidAgreementId() {
        Message result = supplier.updateQuantity(42, 50, "100");
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Agreement 42 not found"));
    }
    @Test
    void testSetCostWithInvalidAgreementId() {
        supplier.setCost(99.9, 77, "100");
        double cost = supplier.getCost("100");
        assertEquals(-1, cost); // assuming product was never added
    }
    @Test
    void testAddProductToInvalidAgreementId() {
        ProductDTO dto = new ProductDTO("Juice", "200", "Prigat", 7, 3, 4, "PER_UNIT", "Drink", "Juice", "Liquid", 10);
        Message result = supplier.addProductToAgreement(77, dto);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Agreement 77 not found"));
    }
    @Test
    void testRemoveProductFromInvalidAgreementId() {
        Message result = supplier.removeProductFromAgreement(66, "100");
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Agreement 66 not found"));
    }
    @Test
    void testUpdatePhoneNumberInvalidAgreement() {
        Message result = supplier.updatePhoneNumber(123, "050-0000000");
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Agreement 123 not found"));
    }
    @Test
    void testAddAgreementWithProductAlreadyInCatalog() {
        List<Product> list1 = new ArrayList<>();
        list1.add(product);
        Agreement agreement1 = new Agreement(list1, "050-1");
        Message msg1 = supplier.addAgreement(agreement1);
        assertTrue(msg1.isSuccess());
        assertEquals(1, supplier.getAllProductsInCatalog().size());
        assertEquals(1, supplier.getAgreements().size());
        String [] category = {"Drink", "Juice", "Liquid"};
        Product newProduct = new Product("Juice", "200", "Prigat", 7, 3, Product.MeasurementType.PER_UNIT, 4, category, 10);
        List<Product> list2 = new ArrayList<>();
        list2.add(product);  // already in catalog
        list2.add(newProduct);
        Agreement agreement2 = new Agreement(list2, "050-2");

        Message msg2 = supplier.addAgreement(agreement2);
        assertFalse(msg2.isSuccess());
        assertEquals(1, supplier.getAgreements().size());

        assertNull(supplier.getProduct("200"));
    }




    // Tests for Order
//    @Test
//    void testAddOrderWithInvalidAgreementId() {
//        Map<String, OrderDTO.ProductEntry> products = new HashMap<>();
//        products.put("100", new OrderDTO.ProductEntry(2, 0));
//        Message result = supplier.addOrder(LocalDate.now(), "050-0000000", products, -1, "IN_PROGRESS");
//        assertFalse(result.isSuccess());
//        System.out.println(">>>>>"+result.getMessage());
//        assertTrue(result.getMessage().contains("Agreement 99 not found"));
//    }
    @Test
    void testAddOrderWithProductNotInAgreement() {
        List<Product> list = new ArrayList<>();
        list.add(product);
        Agreement a1 = new Agreement(list, "050-1");
        supplier.addAgreement(a1);

        Map<String, OrderDTO.ProductEntry> products = new HashMap<>();
        products.put("999", new OrderDTO.ProductEntry(1, 0)); // product not in agreement
        Message result = supplier.addOrder(LocalDate.now(), "050-0000000", products, -1, "IN_PROGRESS");
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Product 999 not found in catalog"));
    }
    @Test
    void testAddOrderWithSomeProductsNotInAgreement() {
        List<Product> list = new ArrayList<>();
        list.add(product);
        Agreement a1 = new Agreement(list, "050-1");
        supplier.addAgreement(a1);

        Map<String, OrderDTO.ProductEntry> products = new HashMap<>();
        products.put("100", new OrderDTO.ProductEntry(1, 0)); // valid
        products.put("101", new OrderDTO.ProductEntry(1, 0)); // invalid

        Message result = supplier.addOrder(LocalDate.now(), "050-2222222", products, -1, "IN_PROGRESS");
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Product 101 not found in catalog"));
        assertTrue(supplier.getOrders().isEmpty());
        assertNull(supplier.getProduct("101"));
    }
    @Test
    void testSetOrderDoneValidId() {
        testAddMultipleOrders();
        int orderId = supplier.getAllOrdersIDs().get(0);
        Message result = supplier.orderIsDone(orderId);
        assertTrue(result.isSuccess());
    }
    @Test
    void testSetOrderDoneInvalidId() {
        Message result = supplier.orderIsDone(999);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Order 999 not found"));
    }
    @Test
    void testSetDiscountInOrderValidId() {
        testAddMultipleOrders();
        int orderId = supplier.getAllOrdersIDs().get(0);
        Message result = supplier.setDiscount(orderId, 20, "100");
        assertTrue(result.isSuccess());
    }
    @Test
    void testSetDiscountInOrderInvalidId() {
        Message result = supplier.setDiscount(999, 20, "100");
        assertFalse(result.isSuccess());
    }
    @Test
    void testSetAndGetPhoneNumberInOrderValidId() {
        testAddMultipleOrders();
        int orderId = supplier.getAllOrdersIDs().get(0);
        supplier.setPhoneNumberInOrder(orderId, "050-1110000");
        Message result = supplier.getPhoneNumberInOrder(orderId);
        assertTrue(result.getMessage().contains("050-1110000"));
    }
    @Test
    void testSetPhoneNumberInOrderInvalidId() {
        Message result = supplier.setPhoneNumberInOrder(999, "050-0000000");
        assertFalse(result.isSuccess());
    }
    @Test
    void testTotalPriceOfOrderFromSupplier() {
        List<Product> products = new ArrayList<>();
        products.add(product); // price 10
        products.add(product2); // price 20
        Agreement agreement = new Agreement(products, "050-4000000");
        supplier.addAgreement(agreement);

        Map<String, OrderDTO.ProductEntry> productQuantities = new HashMap<>();
        productQuantities.put("100", new OrderDTO.ProductEntry(2, 0)); // 2 x 10 = 20
        productQuantities.put("101", new OrderDTO.ProductEntry(1, 0)); // 1 x 20 = 20

        Message addOrderMsg = supplier.addOrder(LocalDate.now(), "050-5555555", productQuantities, -1, "IN_PROGRESS");
        assertTrue(addOrderMsg.isSuccess());

        int orderId = supplier.getAllOrdersIDs().get(0);
        Message totalPriceMsg = supplier.totalPricePerOrder(orderId);
        assertTrue(totalPriceMsg.isSuccess());
        assertTrue(totalPriceMsg.getMessage().contains("40.0"));
    }
    @Test
    void testTotalPriceWithDiscountApplied() {
        String[] category = {"Dairy", "Butter", "Solid"};
        Product discountedProduct = new Product("Butter", "300", "Tnuva", 15, 20, Product.MeasurementType.PER_UNIT, 3, category, 35);
        List<Product> products = new ArrayList<>();
        products.add(discountedProduct);
        Agreement agreement = new Agreement(products, "050-6000000");
        supplier.addAgreement(agreement);

        Map<String, OrderDTO.ProductEntry> productQuantities = new HashMap<>();
        productQuantities.put("300", new OrderDTO.ProductEntry(5, 0)); // qualifies for discount: 15 * 0.8 * 5 = 60.0

        Message addOrderMsg = supplier.addOrder(LocalDate.now(), "050-6666666", productQuantities, -1, "IN_PROGRESS");
        assertTrue(addOrderMsg.isSuccess());

        int orderId = supplier.getAllOrdersIDs().get(supplier.getAllOrdersIDs().size() - 1);
        Message totalPriceMsg = supplier.totalPricePerOrder(orderId);
        assertTrue(totalPriceMsg.isSuccess());
        assertTrue(totalPriceMsg.getMessage().contains("60.0"));
    }
    @Test
    void testGetOrdersRequiringDelivery() {
        supplier.setDelivery(false);
        List<Product> list = new ArrayList<>();
        list.add(product);
        Agreement agreement = new Agreement(list, "050-3000000");
        supplier.addAgreement(agreement);
        Map<String, OrderDTO.ProductEntry> orderProducts = new HashMap<>();
        orderProducts.put("100", new OrderDTO.ProductEntry(2, 0));
        Message orderMsg = supplier.addOrder(LocalDate.now(), "050-8888888", orderProducts, -1, "IN_PROGRESS");
        assertTrue(orderMsg.isSuccess());
        List<Order> deliveryOrders = supplier.getOrdersForDelivery();
        assertEquals(1, deliveryOrders.size());

        int orderId = supplier.getAllOrdersIDs().get(0);
        supplier.orderIsDone(orderId);
        List<Order> afterDone = supplier.getOrdersForDelivery();
        assertEquals(0, afterDone.size());
    }


    // Tests for Supplier
    @Test
    void testUpdateSupplierName() {
        supplier.setName("NewSupplierName");
        assertEquals("NewSupplierName", supplier.getName());
    }
    @Test
    void testUpdateBankAccount() {
        supplier.updateBankAccount("456-789");
        assertEquals("456-789", supplier.getBankAccount());
    }
    @Test
    void testUpdateSupplierAddress() {
        supplier.setAddress("New Address 5");
        assertEquals("New Address 5", supplier.getAddress());
    }
    @Test
    void testSetAndGetPaymentDetails() {
        Message methodMsg = supplier.setPaymentMethod("BANK_TRANSFER");
        assertTrue(methodMsg.isSuccess());
        assertEquals(Payment.PaymentMethod.BANK_TRANSFER, supplier.getPayment().getPaymentMethod());

        Message termMsg = supplier.setPaymentTerm("END_OF_MONTH");
        assertTrue(termMsg.isSuccess());
        assertEquals(Payment.PaymentTerm.END_OF_MONTH, supplier.getPayment().getPaymentTerm());
    }
    @Test
    void testAddContactToSupplier() {
        int originalSize = supplier.getContacts().size();
        Message msg = supplier.addContact(new Contact("Dana", "050-7777777"));
        assertTrue(msg.isSuccess());
        assertEquals(originalSize + 1, supplier.getContacts().size());
    }
    @Test
    void testAddContactToInvalidAgreementId() {
        Message msg = supplier.addContact(new Contact("InvalidAg", "050-1231231"), 99);
        assertFalse(msg.isSuccess());
        assertTrue(msg.getMessage().contains("Agreement 99 not found"));
    }
    @Test
    void testGetAllAvailableProductsWhenNoAgreements() {
        Supplier emptySupplier = new Supplier("Empty", 2L, "000", 2, new ArrayList<>(), new ArrayList<>(), "Nowhere", false, payment);
        Message result = emptySupplier.getAllAvailableProducts();
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("No agreements found"));
    }

}