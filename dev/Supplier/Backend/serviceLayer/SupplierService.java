package Supplier.Backend.serviceLayer;

import HRandDelivery.DomainLayer.HRandDeliveryDomainService;
import HRandDelivery.Utils.Response;
import Supplier.Backend.DataAccessLayer.SupplierRepository;
import Supplier.Backend.domainLayer.*;
import Supplier.Backend.DTO.OrderDTO;
import Supplier.Backend.DTO.ProductDTO;
import Supplier.Backend.DTO.SupplierDTO;
import Supplier.Backend.DTO.AgreementDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class SupplierService {
    private static SupplierService instance = new SupplierService();
    private static SupermarketCatalog supermarketCatalog = SupermarketCatalog.getInstance();
    private SupplierRepository supplierRepository = SupplierRepository.getInstance();
    private Map<Long, Supplier> suppliers = new HashMap<>();

    private HRandDeliveryDomainService hrAndDeliveryDomainService = HRandDeliveryDomainService.getInstance();

    private SupplierService() {
    }

    public List<Long> getAllSuppliersIDs() {
        List<Long> suppliersID = new ArrayList<>();
        for (Long id : suppliers.keySet()) {
            suppliersID.add(id);
        }
        return suppliersID;
    }

    public static SupplierService getInstance() {
        return instance;
    }
    public void loadSuppliers() {
        supplierRepository.loadSuppliers();
        Map<Long, SupplierDTO> suppliersDTO = supplierRepository.getAllSuppliers();
        for(Map.Entry<Long, SupplierDTO> entry : suppliersDTO.entrySet()) {
            SupplierDTO supplierDTO = entry.getValue();
            suppliers.put(entry.getKey(), new Supplier(supplierDTO));
            for (AgreementDTO a : supplierDTO.getAgreements()) {
                updateSupermarketCatalog(a, entry.getKey());
            }
        }
    }
    public void updateSupermarketCatalog(AgreementDTO a, long supplierId) {
        for(ProductDTO p : a.getProducts().values()) {
            supermarketCatalog.addProducts(supplierId, p);
        }
    }


    public Message isSupplierExists(long supplierId) {
        if (suppliers.containsKey(supplierId)) {
            Supplier supplier = suppliers.get(supplierId);
            return new Message(true, supplier.getName() + " exists");
        }
        return new Message(false, "Supplier not found");
    }
    public Map<Long, Supplier> getAllSuppliers() {
        return suppliers;
    }

    //agreement functions
    public Message getProductsInAgreement(long supplierId, int agreementId) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            return supplier.getProductsInAgreement(agreementId);
        }
        return new Message(false, "Supplier not found");
    }
    public Message addProductToAgreement(long supplierId, int agreementId, ProductDTO productDTO) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            Message msg =  supplier.addProductToAgreement(agreementId, productDTO);
            if (msg.isSuccess()) {
                supplierRepository.addProduct(supplierId, productDTO, agreementId);
                supermarketCatalog.addProducts(supplierId, productDTO);
            }
            return msg;
        }
        return new Message(false, "Supplier not found");
    }
    public Message updateDiscount(long supplierId, int agreementId, int discount, String productID) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            Message msg = supplier.updateDiscount(agreementId, discount, productID);
            ProductDTO product = supplier.getProductDTO(agreementId, productID);
            if (msg.isSuccess()) {
                supplierRepository.updateProductDiscount(supplierId, agreementId, productID, discount);
                if (product != null) {
                    supermarketCatalog.updateDiscount(supplierId, product, discount);
                }
            }
            return msg;
        }
        return new Message(false, "Supplier not found");
    }
    public Message updateQuantity(long supplierId, int agreementID, int quantity, String productID) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier == null) {
            return new Message(false, "Supplier not found");
        }
        Message msg = supplier.updateQuantity(agreementID, quantity, productID);
        ProductDTO product = supplier.getProductDTO(agreementID, productID);
        if (msg.isSuccess()) {
            supplierRepository.updateProductQuantity(supplierId, agreementID, productID, quantity);
            if (product != null) {
                supermarketCatalog.updateQuantity(supplierId, product, quantity);
            }
        }
        return msg;
    }


    //contact functions
    public Message getContactsInOrder(long supplierId, int orderId) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            return supplier.getContactsInOrder(orderId);
        }
        return new Message(false, "Supplier not found");
    }
    public Message getContacts(long supplierId, int agreementId) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            return supplier.getContacts(agreementId);
        }
        return new Message(false, "Supplier not found");
    }
    public Message getContacts(long supplierId) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            return new Message(true, "Contacts", supplier.getContacts());
        }
        return new Message(false, "Supplier not found");
    }
    public Message addContact(long supplierId, String contactName, String contactPhone) {
        Contact contact = new Contact(contactName, contactPhone);
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            Message msg = supplier.addContact(contact);
            if (msg.isSuccess()) {
                supplierRepository.addContact(supplierId, contact);
            }
            return msg;
        }
        return new Message(false, "Supplier not found");
    }
    public Message addContact(long supplierId, int agreementID, String contactPhone) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            Message msg = supplier.addContact(contactPhone, agreementID);
            if (msg.isSuccess()) {
                supplierRepository.addContactToAgreement(supplierId, agreementID, contactPhone);
            }
            return msg;
        }
        return new Message(false, "Supplier not found");
    }


    //order functions
    public Message addOrderFromStorage(OrderDTO orderDTO, long supplierId) throws Exception {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            Map<String, OrderDTO.ProductEntry> productsID = new HashMap<>();
            for(String productID : orderDTO.getProducts().keySet()) {
                String catalog = supermarketCatalog.catalogNumber(productID, supplierId);
                if(catalog == null) {
                    return new Message(false, "Product " + productID + " not found in catalog for supplier " + supplierId);
                }
                productsID.put(catalog, orderDTO.getProducts().get(productID));
            }
            orderDTO.setProducts(productsID);
            Message msg = supplier.addOrder(orderDTO);
            if (msg.isSuccess()) {
                supplierRepository.addOrder(supplierId, orderDTO);
            }
            if(suppliers.get(supplierId).isDelivery()){
                return msg;
            }
            else{
                Response response = hrAndDeliveryDomainService.addOrderToDelivery(supplierRepository.getSupplierById(supplierId).getAddress(), suppliers.get(supplierId).getSupplyDay());
                if (!response.isSuccess()){
                    return new Message(false, response.getMessage());
                }
                return msg;
            }
        }
        return new Message(false, "Supplier not found");
    }
    public Message addOrder(OrderDTO orderDTO, long supplierId)  {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            Message msg = supplier.addOrder(orderDTO);
            if (msg.isSuccess()) {
                supplierRepository.addOrder(supplierId, orderDTO);
            }
            if(suppliers.get(supplierId).isDelivery()){
                return msg;
            }
            else{
                try {
                    Response response = hrAndDeliveryDomainService.addOrderToDelivery(supplierRepository.getSupplierById(supplierId).getAddress(), suppliers.get(supplierId).getSupplyDay());
                    if (!response.isSuccess()){
                        return new Message(false, response.getMessage());
                    }
                    return msg;
                }
                catch (Exception e) {
                    return new Message(false, "Error adding order to delivery: " + e.getMessage());
                }

            }
        }
        return new Message(false, "Supplier not found");
    }
    public Message addOrder(OrderDTO order){
        Map<String, OrderDTO.ProductEntry> products = order.getProducts();
        Map<Long, Map<String, Integer>> suppliers = new HashMap<>();
        for (String productID : products.keySet()) {
            long supplierId = supermarketCatalog.getLowestPriceProduct(productID, products.get(productID).getQuantity());
            if (supplierId == -1) {
                return new Message(false, "Product " + productID + " not found in catalog");
            }
            suppliers.putIfAbsent(supplierId, new HashMap<>());
            suppliers.get(supplierId).put(productID, products.get(productID).getQuantity());
        }
        for (Map.Entry<Long, Map<String, Integer>> entry : suppliers.entrySet()) {
            long supplierId = entry.getKey();
            Map<String, Integer> productMap = entry.getValue();
            Supplier supplier = this.suppliers.get(supplierId);
            if (supplier != null) {
                OrderDTO orderDTO = new OrderDTO(order.getOrderDate(), productMap, order.getPhoneNumber());
                supplier.addOrder(orderDTO);
            }
        }
        return new Message(true, "Order added");
    }

    public Message removeOrder(long supplierId, int orderId) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            Message msg = supplier.removeOrder(orderId);
            if (msg.isSuccess()) {
                supplierRepository.deleteOrder(supplierId, orderId);
            }
            return msg;
        }
        return new Message(false, "Supplier not found");
    }
    public Message getOrder(long supplierId, int orderId) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            StringBuilder sb = new StringBuilder();
            if (!supplier.getOrder(orderId).isSuccess()) {
                return supplier.getOrder(orderId);
            }
            sb.append("Order ID: ").append(orderId).append("\n");
            sb.append("Supplier Name: ").append(supplier.getName()).append("\n");
            sb.append("Supplier ID: ").append(supplierId).append("\n");
            sb.append("Address: ").append(supplier.getAddress()).append("\n");
            sb.append("Products: \n");
            sb.append(supplier.getOrder(orderId).getMessage()).append("\n");
            return new Message(true, "Order:", sb.toString());

        }
        return new Message(false, "Supplier not found");
    }
    public Message getAllOrdersIDs(long supplierId) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            return new Message(true, "Orders", supplier.getAllOrdersIDs());
        }
        return new Message(false, "Supplier not found");
    }
    public Message getOrderDateInOrder(long supplierId, int orderId) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            return supplier.getOrderDate(orderId);
        }
        return new Message(false, "Supplier not found");
    }
    public Message getAllProductsInCatalog(long supplierId) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            List<Product> products = supplier.getAllProductsInCatalog();
            StringBuilder sb = new StringBuilder();
            for (Product p : products) {
                sb.append(p).append("\n");
            }
            return new Message(true, "Products in catalog:\n" + sb.toString());
        }
        return new Message(false, "Supplier not found");
    }
    public Message setDiscount(long supplierId, int orderId, int discount, String catalogNumber) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            Message msg = supplier.setDiscount(orderId, discount, catalogNumber);
            if (msg.isSuccess()) {
                supplierRepository.updateOrderProductDiscount(supplierId, orderId, catalogNumber, discount);
            }
            return msg;
        }
        return new Message(false, "Supplier not found");
    }
    public Message orderIsDone(long supplierId, int orderId) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            supplier.orderIsDone(orderId);
            supplierRepository.updateOrderStatus(supplierId, orderId, "DONE");
            return new Message(true, "Order is done");
        }
        return new Message(false, "Supplier not found");
    }
    public Message getProductsInOrder(long supplierId, int orderId) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            return supplier.getProductsInOrder(orderId);
        }
        return new Message(false, "Supplier not found");
    }
    public Message getPhoneNumberInOrder(long supplierId, int orderId) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            return supplier.getPhoneNumberInOrder(orderId);
        }
        return new Message(false, "Supplier not found");
    }
    public Message updatePhoneNumberInOrder(long supplierId, int orderId, String phoneNumber) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            Message msg = supplier.setPhoneNumberInOrder(orderId, phoneNumber);
            if (msg.isSuccess()) {
                supplierRepository.updateOrderPhoneNumber(supplierId, orderId, phoneNumber);
            }
            return msg;
        }
        return new Message(false, "Supplier not found");
    }


    //payment functions
    public Message getPayment(long supplierId) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            return new Message(true, "Payment method", supplier.getPayment());
        }
        return new Message(false, "Supplier not found");
    }
    public Message getPaymentMethod(long supplierId) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            return new Message(true, "Payment method", supplier.getPayment().getPaymentMethod());
        }
        return new Message(false, "Supplier not found");
    }
    public Message setPaymentMethod(long supplierId, String paymentMethod) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            Message msg = supplier.setPaymentMethod(paymentMethod);
            if (msg.isSuccess()) {
                supplierRepository.updateSupplierPaymentMethod(supplierId, paymentMethod);
            }
            return msg;
        }
        return new Message(false, "Supplier not found");
    }
    public Message getPaymentTerm(long supplierId) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            return new Message(true, "Payment term", supplier.getPayment().getPaymentTerm());
        }
        return new Message(false, "Supplier not found");
    }
    public Message updatePaymentTerm(long supplierId, String paymentTerm) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            Message msg = supplier.setPaymentTerm(paymentTerm);
            if (msg.isSuccess()) {
                supplierRepository.updateSupplierPaymentTerm(supplierId, paymentTerm);
            }
        }
        return new Message(false, "Supplier not found");
    }


    //supplier functions
    public Message addSupplier(SupplierDTO supplierDTO) throws Exception {
        if (suppliers.containsKey(supplierDTO.getSupplierId())) {
            return new Message(false, "Supplier already exists");
        }
        Supplier supplier = new Supplier(supplierDTO);
        supplierRepository.addSupplier(supplierDTO);
        suppliers.put(supplier.getSupplierId(), supplier);
        for (AgreementDTO a : supplierDTO.getAgreements()) {
            updateSupermarketCatalog(a, supplier.getSupplierId());
        }

        hrAndDeliveryDomainService.addSite(supplierDTO.getAddress(), supplierDTO.getContacts().get(0).getName(),  supplierDTO.getContacts().get(0).getPhone());

        return new Message(true, "Supplier added");
    }
    public Message deleteSupplier(long supplierId) throws Exception {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            suppliers.remove(supplierId);
            supplierRepository.deleteSupplier(supplierId);
            supermarketCatalog.removeAllSupplierProducts(supplierId);
            return new Message(true, "Supplier removed");
        }

        hrAndDeliveryDomainService.removeSite(supplier.getAddress());

        return new Message(false, "Supplier not found");
    }
    public Message addAgreement(AgreementDTO agreementDTO, long supplierId) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            Message msg = supplier.addAgreement(agreementDTO);
            if (msg.isSuccess()) {
                supplierRepository.addAgreement(supplierId, agreementDTO);
                updateSupermarketCatalog(agreementDTO, supplierId);
            }
            return msg;
        }
        return new Message(false, "Supplier not found");
    }
    public Message getAgreement(long supplierId, int agreementId) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            return supplier.getAgreement(agreementId);
        }
        return new Message(false, "Supplier not found");
    }
    public Message updateName(long supplierId, String name) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            supplier.setName(name);
            supplierRepository.updateSupplierName(supplierId, name);
            return new Message(true, "Name updated");
        }
        return new Message(false, "Supplier not found");
    }
    public Message getAgreementsID(long supplierId) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            return new Message(true, "Agreements", supplier.getAgreementsIDs());
        }
        return new Message(false, "Supplier not found");
    }
    public Message removeAgreement(long supplierId, int agreementId) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            List<ProductDTO> products = supplier.getProductDTOInAgreement(agreementId);
            Message msg = supplier.deleteAgreement(agreementId);
            if(msg.isSuccess()){
                supplierRepository.deleteAgreement(supplierId, agreementId);
                if(products != null && !products.isEmpty()) {
                    for (ProductDTO product : products) {
                        supermarketCatalog.removeProducts(supplierId, product);
                    }
                }
            }
            return msg;
        }
        return new Message(false, "Supplier not found");
    }
//    public Message setName(long supplierId, String name) {
//        Supplier supplier = suppliers.get(supplierId);
//        if (supplier != null) {
//            supplier.setName(name);
//            return new Message(true, "Name set");
//        }
//        return new Message(false, "Supplier not found");
//    }
    public Message getName(long supplierId) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            return new Message(true, "Name", supplier.getName());
        }
        return new Message(false, "Supplier not found");
    }
    public Message updateSupplyDay(long supplierId, int supplyDay) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            supplier.setSupplyDay(supplyDay);
            supplierRepository.updateSupplierSupplyDay(supplierId, supplyDay);
            return new Message(true, "Supply day updated");
        }
        return new Message(false, "Supplier not found");
    }
    public Message getSupplyDay(long supplierId) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            return new Message(true, "Supply day: ", supplier.getSupplyDay());
        }
        return new Message(false, "Supplier not found");
    }
    public Message updateBankAccount(long supplierId, String bankAccount) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            supplier.updateBankAccount(bankAccount);
            supplierRepository.updateSupplierBankAccount(supplierId, bankAccount);
            return new Message(true, "Bank account updated");
        }
        return new Message(false, "Supplier not found");
    }
    public Message getBankAccount(long supplierId) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            return new Message(true, "Bank account", supplier.getBankAccount());
        }
        return new Message(false, "Supplier not found");
    }
    public Message updateAddress(long supplierId, String address) throws Exception {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            supplier.setAddress(address);
            supplierRepository.updateSupplierAddress(supplierId, address);
            return new Message(true, "Address updated");
        }

        hrAndDeliveryDomainService.updateSiteAddress(supplier.getAddress(), address);

        return new Message(false, "Supplier not found");
    }
    public Message getAddress(long supplierId) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            return new Message(true, "Address: ", supplier.getAddress());
        }
        return new Message(false, "Supplier not found");
    }
    public Message getSupplier(long supplierId) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            return new Message(true, "Supplier", supplier.toString());
        }
        return new Message(false, "Supplier not found");
    }
    public Message removeProduct(long supplierId, int agreementId, String productID) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            ProductDTO product = supplier.getProductDTO(agreementId, productID);
            Message msg = supplier.removeProductFromAgreement(agreementId, productID);
            if (msg.isSuccess()) {
                supplierRepository.deleteProduct(supplierId, productID);
                if (product != null) {
                    supermarketCatalog.removeProducts(supplierId, product);
                }
            }
            return msg;
        }
        return new Message(false, "Supplier not found");
    }
    public Message isDelivery(long supplierId) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            return new Message(true, " Self Delivery: ", supplier.isDelivery());
        }
        return new Message(false, "Supplier not found");
    }
    public Message setDelivery(long supplierId, boolean isDelivery) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            supplier.setDelivery(isDelivery);
            supplierRepository.updateSupplierDelivery(supplierId, isDelivery);
            return new Message(true, "Delivery status set: ", supplier.isDelivery());
        }
        return new Message(false, "Supplier not found");
    }
    public Message getCost(long supplierId, String productID) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            return new Message(true, "Cost", supplier.getCost(productID));
        }
        return new Message(false, "Supplier not found");
    }
    public Message setCost(long supplierId, int agreementID, double cost, String productID) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            Message msg = supplier.setCost(cost,agreementID ,productID);
            ProductDTO product = supplier.getProductDTO(agreementID, productID);
            if (msg.isSuccess()) {
                supplierRepository.updateProductCost(supplierId, agreementID, productID, cost);
                if (product != null) {
                    supermarketCatalog.updatePrice(supplierId, product, cost);
                }
            }
            return msg;
        }
        return new Message(false, "Supplier not found");
    }
    public Message allOrderedProducts(long supplierId) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            return supplier.getOrderedProducts();
        }
        return new Message(false, "Supplier not found");
    }
    public Message getAllAvailableProducts(long supplierId) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            return supplier.getAllAvailableProducts();
        }
        return new Message(false, "Supplier not found");
    }
    public Message getTotalOrderPrice(long supplierId, int orderId) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier != null) {
            return supplier.totalPricePerOrder(orderId);
        }
        return new Message(false, "Supplier not found");
    }
    public Message getOrdersForDelivery() {
        StringBuilder sb = new StringBuilder();
        for (Supplier supplier : suppliers.values()) {
            List<Order> orders = supplier.getOrdersForDelivery();
            if (orders != null && !orders.isEmpty()) {
                sb.append("Supplier: ").append(supplier.getName()).append("\n");
                for (Order order : orders) {
                    sb.append(order.toString()).append("\n");
                }
            }
        }
        if(sb.length() == 0) {
            return new Message(true, "No orders for delivery");
        }
        return new Message(true, "Orders for delivery", sb.toString());
    }
    //initialization
    public void init() throws Exception {
        SupplierDTO s1 = new SupplierDTO(
                "Supplier1",
                100,
                "1111",
                2,
                Payment.PaymentMethod.CASH,
                Payment.PaymentTerm.IN_ADVANCE,
                "Tel Aviv",
                false
        );
        s1.addContact("Dan", "0501111111");
        this.addSupplier(s1);
        //Supplier sup1 = new Supplier(s1);
        //System.out.println("Supplier 1 added");


        SupplierDTO s2 = new SupplierDTO(
                "Supplier2",
                101,
                "2222",
                0,
                Payment.PaymentMethod.CHECK,
                Payment.PaymentTerm.BEGINNING_OF_MONTH,
                "Beer-Sheva",
                true
        );
        s2.addContact("Dana", "0502222222");
        addSupplier(s2);
        Map<String, ProductDTO> agr2a = new HashMap<>();
        agr2a.put("T001", new ProductDTO("Milk", "T001", "Tnuva", 5, 10, 10, "PER_UNIT", "Dairy", "Milk", "Liquid", 10));
        agr2a.put("T002", new ProductDTO("Cheese", "T002", "Tnuva", 6, 5, 8, "BY_WEIGHT", "Dairy", "Cheese", "Solid", 14));
        AgreementDTO agreement1 = new AgreementDTO(agr2a, "0502222222");
        Map<String, ProductDTO> agr2b = new HashMap<>();
        agr2b.put("V010", new ProductDTO("Tomatoes", "V010", "Vegetables", 4, 3, 20, "PER_UNIT", "Greengrocer", "Vegetables", "Fresh", 7));
        agr2b.put("V011", new ProductDTO("Cucumbers", "V011", "Vegetables", 3, 2, 15, "BY_WEIGHT", "Greengrocer", "Vegetables", "Fresh", 5));
        AgreementDTO agreement2 = new AgreementDTO(agr2b, "0502222222");
        //Supplier sup2 = suppliers.get(101);
        addAgreement(agreement1, 101);
        addAgreement(agreement2, 101);
//        sup2.addAgreement(agreement1);
//        sup2.addAgreement(agreement2);
//        this.addSupplier(s2);
        //System.out.println("Supplier 2 added");


        SupplierDTO s3 = new SupplierDTO(
                "Supplier3",
                103,
                "3333",
                1,
                Payment.PaymentMethod.BANK_TRANSFER,
                Payment.PaymentTerm.END_OF_MONTH,
                "Beer Sheva",
                false
        );
        s3.addContact("Yossi", "0503333333");
        this.addSupplier(s3);
        Map<String, ProductDTO> agr3a = new HashMap<>();
        agr3a.put("D001", new ProductDTO("Milk", "D001", "Tnuva", 6, 10, 2, "PER_UNIT", "Dairy", "Milk", "Liquid", 12));
        agr3a.put("D002", new ProductDTO("Cheese", "D002", "Tnuva", 15, 5, 1, "BY_WEIGHT", "Dairy", "Cheese", "Solid", 14));
        Map<String, ProductDTO> agr3b = new HashMap<>();
        agr3b.put("B001", new ProductDTO("Bread", "B001", "Angel", 4, 20, 10, "PER_UNIT", "Bakery", "Bread", "Fresh", 5));
        agr3b.put("B002", new ProductDTO("Bans", "B002", "Angel", 2, 0, 20, "PER_UNIT", "Bakery", "Bans", "Fresh", 3));
        agr3b.put("B003", new ProductDTO("Pita", "B003", "Angel", 3, 50, 10, "PER_UNIT", "Bakery", "Pita", "Fresh", 4));
        AgreementDTO agr3dto1 = new AgreementDTO(agr3a, "0503333333");
        AgreementDTO agr3dto2 = new AgreementDTO(agr3b, "0503333333");
//        Supplier sup3 = suppliers.get(103);
//        sup3.addAgreement(agr3dto1);
//        sup3.addAgreement(agr3dto2);
        addAgreement(agr3dto1, 103);
        addAgreement(agr3dto2, 103);
        this.addSupplier(s3);

        Map<String, Integer> order1 = new HashMap<>();
        order1.put("D001", 3);
        order1.put("D002", 2);

        Map<String, Integer> order2 = new HashMap<>();
        order2.put("D001", 10);

        Map<String, Integer> order3 = new HashMap<>();
        order3.put("B002", 2);
        order3.put("B001", 5);

//        System.out.println(this.addOrder(new OrderDTO(java.time.LocalDate.now().minusDays(2), order1, "0505555555"), 103));
//        System.out.println(this.addOrder(new OrderDTO(java.time.LocalDate.now().minusDays(1), order2, "1234567890"), 103));
//        System.out.println(this.addOrder(new OrderDTO(java.time.LocalDate.now(), order3, "1001005555"), 103));
//        System.out.println("\n\n\n");

        this.addOrder(new OrderDTO(java.time.LocalDate.now().minusDays(2), order1, "0505555555"), 103);
        this.addOrder(new OrderDTO(java.time.LocalDate.now().minusDays(1), order2, "1234567890"), 103);
        this.addOrder(new OrderDTO(java.time.LocalDate.now(), order3, "1001005555"), 103);
    }

    public boolean deleteAllDB(){
        return supplierRepository.deleteAllData();
    }
}