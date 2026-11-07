package Supplier.Backend.DataAccessLayer;

public class ProductOrderDataDAO {
    private long supplierId;
    private int orderId;
    private int productId;
    private int quantity;
    private int discount;

    public ProductOrderDataDAO(long supplierId, int orderId, int productId, int quantity, int discount) {
        this.supplierId = supplierId;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.discount = discount;
    }
    public long getSupplierId() {
        return supplierId;
    }
    public int getOrderId() {
        return orderId;
    }
    public int getProductId() {
        return productId;
    }
    public int getQuantity() {
        return quantity;
    }
    public int getDiscount() {
        return discount;
    }
    public void setDiscount(int discount) {
        this.discount = discount;
    }
}
