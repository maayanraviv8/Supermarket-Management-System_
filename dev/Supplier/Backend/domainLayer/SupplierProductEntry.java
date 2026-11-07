package Supplier.Backend.domainLayer;

import java.util.Map;

public class SupplierProductEntry {
    private long supplierId;
    private String supplierCatalogNumber;
    private double pricePerUnit;
    private int quantityForDiscount;
    private int discount;

    public SupplierProductEntry(long supplierId, String supplierCatalogNumber, double pricePerUnit, int quantityForDiscount, int discount) {
        this.supplierId = supplierId;
        this.supplierCatalogNumber = supplierCatalogNumber;
        this.pricePerUnit = pricePerUnit;
        this.quantityForDiscount = quantityForDiscount;
        this.discount = discount;
    }
    public long getSupplierId() {
        return supplierId;
    }
    public String getSupplierCatalogNumber() {
        return supplierCatalogNumber;
    }
    public double getPricePerUnit() {
        return pricePerUnit;
    }
   public int getQuantityForDiscount() {
        return quantityForDiscount;
    }
    public int getDiscount() {
        return discount;
    }

    public void setSupplierCatalogNumber(String supplierCatalogNumber) {
        this.supplierCatalogNumber = supplierCatalogNumber;
    }
    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }
    public void setQuantityForDiscount(int quantityForDiscount) {
        this.quantityForDiscount = quantityForDiscount;
    }
    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public double getTotalPrice(int quantity) {
        double totalPrice = pricePerUnit * quantity;
        if (quantity >= quantityForDiscount) {
            totalPrice -= (totalPrice * discount / 100);
        }
        return totalPrice;
    }

}
