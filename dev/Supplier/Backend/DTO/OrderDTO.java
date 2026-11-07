package Supplier.Backend.DTO;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class OrderDTO {
    private LocalDate orderDate;
    private Map<String, ProductEntry> products;
    private String phoneNumber;
    private int orderId;
    private String status;

    public OrderDTO(LocalDate orderDate, Map<String, Integer> products, String phoneNumber) {
        this.orderDate = orderDate;
        this.products = new HashMap<>();
        for(Map.Entry<String, Integer> entry : products.entrySet()) {
            this.products.put(entry.getKey(), new ProductEntry(entry.getValue(), 0));
        }
        this.phoneNumber = phoneNumber;
        this.orderId = -1;
        this.status = "IN_PROGRESS";
    }
    public OrderDTO(LocalDate orderDate, Map<String, ProductEntry> products, String phoneNumber, int orderId, String status) {
        this.orderDate = orderDate;
        this.products = products;
        this.phoneNumber = phoneNumber;
        this.orderId = orderId;
        this.status = status;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }
    public Map<String, ProductEntry> getProducts() {
        return products;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public int getOrderId() {
        return orderId;
    }
    public String getStatus() {
        return status;
    }

    public static class ProductEntry {
        private int quantity;
        private int discount;

        public ProductEntry(int quantity, int discount) {
            this.quantity = quantity;
            this.discount = discount;
        }

        public int getQuantity() {
            return quantity;
        }

        public int getDiscount() {
            return discount;
        }
    }
    public void setProducts(Map<String, ProductEntry> products) {
        this.products = products;
    }
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

}
