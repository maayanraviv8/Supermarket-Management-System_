package Supplier.Backend.domainLayer;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import Supplier.Backend.serviceLayer.Message;

public class Order{

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public enum OrderStatus {
        IN_PROGRESS,
        DONE
    }

    private int orderId;
    private LocalDate orderDate;
    private Map<String, ProductOrderData> products;
    private OrderStatus orderStatus;
    private String phoneNumber;

    public Order(int orderId, LocalDate orderDate, Map<String, ProductOrderData> products, String phoneNumber) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.products = products;
        this.orderStatus = OrderStatus.IN_PROGRESS;
        this.phoneNumber = phoneNumber;
    }
    public Order(int orderId, LocalDate orderDate, Map<String, ProductOrderData> products, String phoneNumber, String orderStatus) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.products = products;
        this.orderStatus = OrderStatus.valueOf(orderStatus);
        this.phoneNumber = phoneNumber;
    }


    public boolean getIfOrderStatus() {
        if(orderStatus == OrderStatus.DONE) {
            return true;
        }
        return false;
    }

    public Message setDiscount(int discount, String catalogNumber) {
//        if (orderStatus == OrderStatus.DONE) {
//            return new Message(false, "Order is already done");
//        }
        if (!products.containsKey(catalogNumber)) {
            return new Message(false, "Product " + catalogNumber + " not found in order");
        }
        products.get(catalogNumber).setDiscount(discount);
        return new Message(true, "Discount set successfully");
    }

    public void orderIsDone() {
        orderStatus = OrderStatus.DONE;
    }
    public List<Product> getProducts() {
        return products.values().stream().map(ProductOrderData::getProduct).toList();
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order ID: ").append(orderId).append("\n");
        sb.append("Order Date: ").append(orderDate).append("\n");
        sb.append("Phone Number: ").append(phoneNumber).append("\n");
        sb.append("Order Status: ").append(orderStatus).append("\n");
        sb.append("Products: \n");
        for (Map.Entry<String, ProductOrderData> entry : products.entrySet()) {
            sb.append(entry.getValue().toString()).append("\n");
        }
        sb.append("Total Price: ").append(totalPrice()).append("\n");
        return sb.toString();
    }

    public double totalPrice() {
        double total = 0;
        for (Map.Entry<String, ProductOrderData> entry : products.entrySet()) {
            total += entry.getValue().totalPricePerProduct();
        }
        return total;
    }



}
