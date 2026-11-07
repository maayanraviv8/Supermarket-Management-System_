package Supplier.Backend.DataAccessLayer;

import java.time.LocalDate;

public class OrderDAO {
    private long supplierId;
    private int orderId;
    private LocalDate orderDate;
    private String orderStatus;
    private String phoneNumber;

    public OrderDAO(long supplierId, int orderId, LocalDate orderDate, String orderStatus, String phoneNumber) {
        this.supplierId = supplierId;
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.phoneNumber = phoneNumber;
    }
    public long getSupplierId() {
        return supplierId;
    }
    public int getOrderId() {
        return orderId;
    }
    public LocalDate getOrderDate() {
        return orderDate;
    }
    public String getOrderStatus() {
        return orderStatus;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
