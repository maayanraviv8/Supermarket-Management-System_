package Supplier.Backend.domainLayer;

import Supplier.Backend.DTO.ProductDTO;

public class Product {

    public enum MeasurementType {
        PER_UNIT,
        BY_WEIGHT,
    }

    private String name;
    private String catalogNumber;
    private String manufacturer;
    private double cost;
    private int discount;
    private int quantityForDiscount;
    private MeasurementType measurementType;
    private String[] category;
    private int expirationDate;

    public Product(String name, String catalogNumber, String manufacturer, int cost, int discount, MeasurementType measurementType, int quantityForDiscount, String[] category, int expirationDate) {
        this.name = name;
        this.catalogNumber = catalogNumber;
        this.manufacturer = manufacturer;
        this.cost = cost;
        this.discount = discount;
        this.measurementType = measurementType;
        this.quantityForDiscount = quantityForDiscount;
        this.category = category;
        this.expirationDate = expirationDate;
    }

    public Product(ProductDTO productDTO) {
        this.name = productDTO.getName();
        this.catalogNumber = productDTO.getCatalogNumber();
        this.manufacturer = productDTO.getManufacturer();
        this.cost = productDTO.getCost();
        this.discount = productDTO.getDiscount();
        this.measurementType = productDTO.getMeasurementType();
        this.quantityForDiscount = productDTO.getQuantityForDiscount();
        this.category = productDTO.getCategory();
        this.expirationDate = productDTO.getExpirationDate();
    }

    public String[] getCategory() {
        return category;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCatalogNumber() {
        return catalogNumber;
    }
    public void setCatalogNumber(String catalogNumber) {
        this.catalogNumber = catalogNumber;
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
    public MeasurementType getMeasurementType() {
        return measurementType;
    }
    public int getQuantityForDiscount() {
        return quantityForDiscount;
    }
    public void setQuantityForDiscount(int quantityForDiscount) {
        this.quantityForDiscount = quantityForDiscount;
    }
    public void setMeasurementType(MeasurementType measurementType) {
        this.measurementType = measurementType;
    }
    public int getExpirationDate() {
        return expirationDate;
    }

    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", catalog number='" + catalogNumber + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", cost=" + cost +
                ", discount=" + discount +
                ", quantity for discount=" + quantityForDiscount +
                ", measurement type=" + measurementType +
                ", category=" + String.join(", ", category) +
                ", expiration days=" + expirationDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product other = (Product) o;
        return catalogNumber.equals(other.catalogNumber);
    }
    @Override
    public int hashCode() {
        return catalogNumber.hashCode();
    }
}
