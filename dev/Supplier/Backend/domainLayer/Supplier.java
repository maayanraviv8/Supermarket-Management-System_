package Supplier.Backend.domainLayer;

import Supplier.Backend.DTO.ProductDTO;
import Supplier.Backend.DTO.SupplierDTO;
import Supplier.Backend.DTO.AgreementDTO;
import Supplier.Backend.serviceLayer.Message;
import Supplier.Backend.DTO.OrderDTO;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Supplier {
    private String name;
    private long supplierId;
    private String bankAccount;
    private int supplyDay = 0;
    private List<Contact> contacts;
    private Map<Integer, Agreement> agreements = new HashMap<>();
    private String address;
    private Map<Integer, Order> orders = new HashMap<>();
    private SupplierSystem system = new SupplierSystem();
    private int agreementCounter = 0;
    private int orderCounter = 0;
    private boolean isDelivery;
    private Payment payment;
    private Map<String, String> orderedProducts = new HashMap<>();

    public Supplier(String name, long supplierId, String bankAccount, int supplyDay , List<Contact> contacts, List<Agreement> agreements, String address, boolean isDelivery, Payment payment) {
        this.name = name;
        this.supplierId = supplierId;
        this.bankAccount = bankAccount;
        this.supplyDay = supplyDay;
        this.contacts = contacts;
        this.address = address;
        this.isDelivery = isDelivery;
        this.payment = payment;
        for(Agreement agreement : agreements) {
            agreement.setAgreementID(agreementCounter);
            this.agreements.put(agreement.getAgreementID(), agreement);
            agreementCounter++;
            system.updateCatalog(agreement.getProducts());
        }
    }
    public Supplier(String name, long supplierId, String bankAccount, List<Contact> contacts, List<Agreement> agreements, String address, boolean isDelivery, Payment payment) {
        this.name = name;
        this.supplierId = supplierId;
        this.bankAccount = bankAccount;
        this.contacts = contacts;
        this.address = address;
        this.isDelivery = isDelivery;
        this.payment = payment;
        for(Agreement agreement : agreements) {
            agreement.setAgreementID(agreementCounter);
            this.agreements.put(agreement.getAgreementID(), agreement);
            agreementCounter++;
            system.updateCatalog(agreement.getProducts());
        }
    }
    public Supplier(SupplierDTO supplier) {
        this.name = supplier.getName();
        this.supplierId = supplier.getSupplierId();
        this.bankAccount = supplier.getBankAccount();
        this.supplyDay = supplier.getSupplyDay();
        this.contacts = supplier.getContacts();
        this.agreements = new HashMap<>();
        for (AgreementDTO entry : supplier.getAgreements()) {
            Agreement agreement = new Agreement(entry);
            if(entry.getAgreementId()!=-1){
                agreement.setAgreementID(entry.getAgreementId());
            }
            else{
                agreement.setAgreementID(agreementCounter);
                agreementCounter++;
            }
            this.agreements.put(agreement.getAgreementID(), agreement);
            System.out.println("Agreement added successfully. AgreementID: "+agreement.getAgreementID());
            system.updateCatalog(agreement.getProducts());
        }
        for (OrderDTO entry : supplier.getOrders()) {
            if(entry.getOrderId() == -1){
                entry.setOrderId(orderCounter);
                generateOrderID();
            }
            HashMap productOrderData = new HashMap<>();
            for(Map.Entry<String, OrderDTO.ProductEntry> prod : entry.getProducts().entrySet()) {
                productOrderData.put(prod.getKey(), new ProductOrderData(prod.getValue().getQuantity(), system.getProduct(prod.getKey()), prod.getValue().getDiscount()));
            }
            this.orders.put(entry.getOrderId(), new Order(entry.getOrderId(), entry.getOrderDate(), productOrderData, entry.getPhoneNumber(), entry.getStatus()));
            for(Map.Entry<String, OrderDTO.ProductEntry> prod : entry.getProducts().entrySet()) {
                orderedProducts.put(prod.getKey(), system.getProduct(prod.getKey()).getName());
            }
        }
        this.address = supplier.getAddress();
        this.isDelivery = supplier.isDelivery();
        this.payment = supplier.getPayment();
        this.agreementCounter = supplier.getNextAgreementId();
        this.orderCounter = supplier.getNextOrderId();
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public long getSupplierId() {
        return supplierId;
    }
    public String getBankAccount() {
        return bankAccount;
    }
    public void updateBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }
    public int getSupplyDay() {
        return supplyDay;
    }
    public void setSupplyDay(int supplyDay) {
        this.supplyDay = supplyDay;
    }
    public Message getContacts(int agreementID) {
        if(!agreements.containsKey(agreementID)){
            return new Message(false, "Agreement "+agreementID+" not found");
        }
        return new Message(true, "Contacts: "+agreements.get(agreementID).getPhoneNumber());
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public Message getContactsInOrder(int orderID) {
        Order order = orders.get(orderID);
        if (order == null) {
            return new Message(false, "Order "+orderID+" not found");
        }
        return new Message(true, "Contacts: "+order.getPhoneNumber());
    }

    public Message addContact(Contact contact) {
        this.contacts.add(contact);
        return new Message(true, "Contact added successfully");
    }

    public Message addContact(Contact contact, int agreementID) {
        if(!agreements.containsKey(agreementID)){
            return new Message(false, "Agreement "+agreementID+" not found");
        }
        this.contacts.add(contact);
        return new Message(true, "Contact added successfully");
    }
    public Message addContact(String contact, int agreementID) {
        if(!agreements.containsKey(agreementID)){
            return new Message(false, "Agreement "+agreementID+" not found");
        }
        agreements.get(agreementID).updatePhoneNumber(contact);
        return new Message(true, "Contact added successfully");
    }

    public List<Agreement> getAgreements() {
        return agreements.values().stream().toList();
    }
    public List<Integer> getAgreementsIDs() {
        return new ArrayList<>(agreements.keySet());
    }
    public boolean isDelivery() {
        return isDelivery;
    }
    public void setDelivery(boolean isDelivery) {
        this.isDelivery = isDelivery;
    }
    public Message setCost(double cost, int agreementID, String productID){
        if(!agreements.containsKey(agreementID)){
            return new Message(false, "Agreement "+agreementID+" not found");
        }
        Product product = system.getProduct(productID);
        if (product == null) {
            return new Message(false, "Product "+productID+" not found in catalog");
        }
        else{
            product.setCost(cost);
        }
        return new Message(true, "Cost updated successfully");
    }

    public double getCost(String productID){
        Product product = system.getProduct(productID);
        if (product == null) {
            new Message(false, "Product "+productID+" not found in catalog");
        }
        else{
            return product.getCost();
        }
        return -1;
    }

    public Payment getPayment() {
        return payment;
    }
    public Message setPaymentMethod(String paymentMethod) {
        return payment.updatePaymentMethod(paymentMethod);
    }
    public Message setPaymentTerm(String paymentTerm) {
        return payment.updatePaymentTerm(paymentTerm);
    }

    public Message addAgreement(Agreement agreement) {
        Message msg = system.updateCatalog(agreement.getProducts());
        if(!msg.isSuccess()){
            return msg;
        }
        agreement.setAgreementID(agreementCounter);
        this.agreements.put(agreementCounter, agreement);
        Message msg2 = new Message(true, "Agreement added successfully. Agreement ID: "+agreementCounter);
        generateAgreementID();
        return msg2;
    }
    public Message addAgreement(AgreementDTO agreement){
        if(agreement.getAgreementId() == -1) {
            agreement.setAgreementId(this.agreementCounter);
            generateAgreementID();
        }
        return addAgreement(new Agreement(agreement));
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public Message getOrder(int orderId) {
        if (!orders.containsKey(orderId)) {
            return new Message(false, "Order "+orderId+" not found");
        }
        return new Message(true, "order: " +orders.get(orderId));
    }
    public List<Order> getOrders() {
        return new ArrayList<>(orders.values());
    }

    public Message addOrder(LocalDate orderDate, String phoneNumber, Map<String, OrderDTO.ProductEntry> products, int orderCounter, String status) {
        Message success = productsValidation(products);
        if(!success.isSuccess()){
            return success;
        }
        Map<String, ProductOrderData> productOrderData = new HashMap<>();
        for(Map.Entry<String, OrderDTO.ProductEntry> prod : products.entrySet()) {
            productOrderData.put(prod.getKey(), new ProductOrderData(prod.getValue().getQuantity(), system.getProduct(prod.getKey()), prod.getValue().getDiscount()));
            orderedProducts.put(prod.getKey(), system.getProduct(prod.getKey()).getName());
        }
        if(orderCounter == -1) {
            orderCounter = this.orderCounter;
            generateOrderID();
        }
        orders.put(orderCounter, new Order(orderCounter, orderDate, productOrderData, phoneNumber, status));
        Message msg = new Message(true, "Order added successfully. Order ID: "+orderCounter);
        return msg;
    }
    public Message productsValidation(Map<String, OrderDTO.ProductEntry> products){
        if(products.isEmpty()){
            return new Message(false, "No products in order");
        }
        for(String catalog: products.keySet()){
            if(system.getProduct(catalog) == null){
                return new Message(false, "Product "+catalog+" not found in catalog");
            }
        }
        return new Message(true, "All products are valid");
    }

    public Message addOrder(OrderDTO orderDTO) {
        if(orderDTO.getOrderId() == -1) {
            orderDTO.setOrderId(this.orderCounter);
            generateOrderID();
        }
        return addOrder(orderDTO.getOrderDate(), orderDTO.getPhoneNumber(), orderDTO.getProducts(), orderDTO.getOrderId(), orderDTO.getStatus());
    }

    public Message removeOrder(int orderId) {
        if (!orders.containsKey(orderId)) {
            return new Message(false, "Order "+orderId+" not found");
        }
        if(orders.get(orderId).getIfOrderStatus()) {
            return new Message(false, "Order "+orderId+" is already done");
        }
        orders.remove(orderId);
        return new Message(true, "Order deleted successfully");
    }

    public Message deleteAgreement(int agreementId) {
        if(!agreements.containsKey(agreementId)){
            return new Message(false, "Agreement "+agreementId+" not found");
        }
        system.deleteAgreement(agreements.get(agreementId).getProducts());
        agreements.remove(agreementId);
        return new Message(true, "Agreement cancelled successfully");
    }

    public Message getOrderData(int orderId) {
        if (!orders.containsKey(orderId)) {
            return new Message(false, "Order " + orderId + " not found");
        }
        Order order = orders.get(orderId);
        return new Message(true, "Order data: " + order.toString());
    }


    public Product getProduct(String catalogNumber) {
        return system.getProduct(catalogNumber);
    }

    public Message removeProductFromAgreement(int agreementId, String catalogNumber) {
        if(!agreements.containsKey(agreementId)){
            return new Message(false, "Agreement "+agreementId+" not found");
        }
        Product product = system.getProduct(catalogNumber);
        if (product == null) {
            return new Message(false, "Product "+catalogNumber+" not found in catalog");
        }
        Message msg = agreements.get(agreementId).removeProductFromAgreement(catalogNumber);
        if(!msg.isSuccess()){
            return msg;
        }
        system.removeProductFromCatalog(product);
        return msg;
    }

    public Message getAgreement(int agreementId) {
        if (!agreements.containsKey(agreementId)) {
            return new Message(false, "Agreement "+agreementId+" not found");
        }
        return new Message(true, "Agreement: "+agreements.get(agreementId));
    }
    public List<Product> getAllProductsInCatalog() {
        return system.getAllProductsInCatalog();
    }

    public List<Integer> getAllOrdersIDs() {
        return new ArrayList<>(orders.keySet());
    }

    public void generateAgreementID() {
        this.agreementCounter++;
    }
    public void generateOrderID() {
        this.orderCounter++;
    }

    public Map<String, String> allOrderedProducts(){
        HashMap<String, String> allOrderedProducts = new HashMap<>();
        for(Order order: orders.values()){
            for(Product product: order.getProducts()){
                allOrderedProducts.put(product.getCatalogNumber(), product.getName());
            }
        }
        return allOrderedProducts;
    }

    public Message getAllAvailableProducts() {
        if(agreements.isEmpty()){
            return new Message(false, "No agreements found- No products available");
        }
        HashMap<String, String> allOrderedProducts = new HashMap<>();
        for(Agreement agreement: agreements.values()){
            for(Product product: agreement.getProducts()){
                allOrderedProducts.put(product.getCatalogNumber(), product.getName());
            }
        }
        return new Message(true, "All available products: "+allOrderedProducts);
    }

    public Message addProductToAgreement(int agreementId, ProductDTO productDTO) {
        Product product = system.getProduct(productDTO.getCatalogNumber());
        if (product != null) {
            return new Message(false, "Product "+product.getCatalogNumber()+" found in catalog");
        }
        if(!agreements.containsKey(agreementId)){
            return new Message(false, "Agreement "+agreementId+" not found");
        }
        product = new Product(productDTO);
        Message msg = agreements.get(agreementId).addProduct(product);
        if(!msg.isSuccess()){
            return msg;
        }
        system.updateCatalog(product);
        return msg;
    }
    public Message updatePhoneNumber(int agreementId, String phoneNumber) {
        if(!agreements.containsKey(agreementId)){
            return new Message(false, "Agreement "+agreementId+" not found");
        }
        agreements.get(agreementId).updatePhoneNumber(phoneNumber);
        return new Message(true, "Phone number updated successfully");
    }
    public Message updateDiscount(int agreementId, int discount, String productID) {
        if(!agreements.containsKey(agreementId)){
            return new Message(false, "Agreement "+agreementId+" not found");
        }
        return agreements.get(agreementId).updateDiscount(discount, productID);
    }
    public Message updateQuantity(int agreementId, int quantity, String productID) {
        if(!agreements.containsKey(agreementId)){
            return new Message(false, "Agreement "+agreementId+" not found");
        }
        return agreements.get(agreementId).updateQuantity(quantity, productID);
    }

    public Message setDiscount(int orderID, int discount, String catalogNumber) {
        Order order = orders.get(orderID);
        if (order == null) {
            return new Message(false, "Order "+orderID+" not found");
        }
        return order.setDiscount(discount, catalogNumber);
    }
    public Message orderIsDone(int orderID){
        Order order = orders.get(orderID);
        if (order == null) {
            return new Message(false, "Order "+orderID+" not found");
        }
        order.orderIsDone();
        return new Message(true, "Order "+orderID+" is done");
    }
    public Message getProductsInOrder(int orderID) {
        Order order = orders.get(orderID);
        if (order == null) {
            return new Message(false, "Order "+orderID+" not found");
        }
        return new Message(true, "Products in order: "+order.getProducts());
    }
    public Message getPhoneNumberInOrder(int orderID) {
        Order order = orders.get(orderID);
        if (order == null) {
            return new Message(false, "Order "+orderID+" not found");
        }
        return new Message(true, "Phone number in order: "+order.getPhoneNumber());
    }
    public Message setPhoneNumberInOrder(int orderID, String phoneNumber) {
        Order order = orders.get(orderID);
        if (order == null) {
            return new Message(false, "Order "+orderID+" not found");
        }
        order.setPhoneNumber(phoneNumber);
        return new Message(true, "set phone number in order");
    }

    public Message getOrderedProducts() {
        if(orderedProducts.isEmpty()){
            return new Message(true, "No products ordered");
        }
        return new Message(true, "Ordered products: "+orderedProducts);
    }

    public Message totalPricePerOrder(int orderID) {
        if (!orders.containsKey(orderID)) {
            return new Message(false, "Order "+orderID+" not found");
        }
        Order order = orders.get(orderID);
        return new Message(true, "Total price per order: "+order.totalPrice());
    }
    public Message getProductsInAgreement(int agreementID) {
        if(!agreements.containsKey(agreementID)){
            return new Message(false, "Agreement "+agreementID+" not found");
        }
        return new Message(true, "Products in agreement: "+agreements.get(agreementID).getProducts());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Supplier ID: ").append(supplierId).append("\n");
        sb.append("Name: ").append(name).append("\n");
        sb.append("Bank Account: ").append(bankAccount).append("\n");
        sb.append("Supply Day: ").append(supplyDay).append("\n");
        sb.append("Contacts: ").append(contacts).append("\n");
        sb.append("Agreements: ").append(agreements).append("\n");
        sb.append("Address: ").append(address).append("\n");
        sb.append("Orders: ").append(orders).append("\n");
        sb.append("Is Delivery: ").append(isDelivery).append("\n");
        sb.append("Payment: ").append(payment).append("\n");
        return sb.toString();
    }

    public List<Order> getOrdersForDelivery() {
        List<Order> ordersForDelivery = new ArrayList<>();
        if(isDelivery){
            return ordersForDelivery;
        }
        for (Order order : orders.values()) {
            if (!order.getIfOrderStatus()) {
                ordersForDelivery.add(order);
            }
        }
        return ordersForDelivery;
    }

    public Message getOrderDate(int orderId) {
        if (!orders.containsKey(orderId)) {
            return new Message(false, "Order "+orderId+" not found");
        }
        Order order = orders.get(orderId);
        return new Message(true, "Order date: "+order.getOrderDate());
    }
    public List<ProductDTO> getProductDTOInAgreement(int agreementId) {
        if(!agreements.containsKey(agreementId)){
            return new ArrayList<>();
        }
        List<ProductDTO> productDTOs = new ArrayList<>();
        for(Product product : agreements.get(agreementId).getProducts()){
            productDTOs.add(newProductDTO(product));
        }
        return productDTOs;
    }
    private ProductDTO newProductDTO(Product p) {
        return new ProductDTO(p.getName(), p.getCatalogNumber(), p.getManufacturer(), p.getCost(), p.getDiscount(), p.getQuantityForDiscount(), p.getMeasurementType().name(), p.getCategory()[0], p.getCategory()[1], p.getCategory()[2], p.getExpirationDate());
    }
    public ProductDTO getProductDTO(int agreementId, String productID){
        if(!agreements.containsKey(agreementId)){
            return null;
        }
        return newProductDTO(system.getProduct(productID));
    }



    }
