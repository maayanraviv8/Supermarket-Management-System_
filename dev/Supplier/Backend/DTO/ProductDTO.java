package Supplier.Backend.DTO;

import Supplier.Backend.domainLayer.Product;

public class ProductDTO {
    private String name;
    private String catalogNumber;
    private String manufacturer;
    private double cost;
    private int discount;
    private int quantityForDiscount;
    private Product.MeasurementType measurementType;
    private String[] category;
    private int expirationDate;

    public ProductDTO(String name, String catalogNumber, String manufacturer, double cost, int discount, int quantityForDiscount, String measurementType, String category1, String category2, String category3, int expirationDate) {
        this.name = name;
        this.catalogNumber = catalogNumber;
        this.manufacturer = manufacturer;
        this.cost = cost;
        this.discount = discount;
        this.quantityForDiscount = quantityForDiscount;
        this.measurementType = Product.MeasurementType.valueOf(measurementType);
        this.category = new String[3];
        this.category[0] = category1;
        this.category[1] = category2;
        this.category[2] = category3;
        this.expirationDate = expirationDate;
    }

    public String getName() {
        return name;
    }

    public String getCatalogNumber() {
        return catalogNumber;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public double getCost() {
        return cost;
    }

    public int getDiscount() {
        return discount;
    }

    public int getQuantityForDiscount() {
        return quantityForDiscount;
    }

    public Product.MeasurementType getMeasurementType() {
        return measurementType;
    }

    public String[] getCategory() {
        return category;
    }
    public String getCategory0() {
        return category[0];
    }
    public String getCategory1() {
        return category[1];
    }
    public String getCategory2() {
        return category[2];
    }
    public int getExpirationDate() {
        return expirationDate;
    }


}
