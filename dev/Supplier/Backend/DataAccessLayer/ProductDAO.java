package Supplier.Backend.DataAccessLayer;
import Supplier.Backend.domainLayer.Product.MeasurementType;

public class ProductDAO {
    private long supplierID;
    private long agreementID;
    private String catalogNumber;
    private String name;
    private String manufacturer;
    private double cost;
    private int discount;
    private int quantityForDiscount;
    private String measurementType;
    private String category0;
    private String category1;
    private String category2;
    private int expirationDate;

    public long getSupplierID() {
        return supplierID;
    }
    public long getAgreementID() {
        return agreementID;
    }
    public String getCatalogNumber() {
        return catalogNumber;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getManufacturer() {
        return manufacturer;
    }
    public double getCost() {
        return cost;
    }
    public void setCost(double cost) {
        this.cost = cost;
    }
    public int getDiscount() {
        return discount;
    }
    public void setDiscount(int discount) {
        this.discount = discount;
    }
    public int getQuantityForDiscount() {
        return quantityForDiscount;
    }
    public void setQuantityForDiscount(int quantityForDiscount) {
        this.quantityForDiscount = quantityForDiscount;
    }
    public String getMeasurementType() {
        return measurementType;
    }
    public String getCategory0() {
        return category0;
    }
    public String getCategory1() {
        return category1;
    }
    public String getCategory2() {
        return category2;
    }
    public int getExpirationDate() {
        return expirationDate;
    }

}


