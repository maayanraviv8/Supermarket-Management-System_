package Supplier.Backend.domainLayer;

import Supplier.Backend.DTO.AgreementDTO;
import Supplier.Backend.DTO.ProductDTO;
import Supplier.Backend.serviceLayer.Message;

import java.util.List;
import java.util.*;

public class Agreement {

    private int agreementID;
    private Map<String, Product> products;
    private String phoneNumber;


    public Agreement( List<Product> products, String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.products = new HashMap<>();
        for (Product product : products) {
            this.products.put(product.getCatalogNumber(), product);
        }
    }
    public Agreement(AgreementDTO agreement){
        this.products = new HashMap<>();
        for (Map.Entry<String, ProductDTO> entry : agreement.getProducts().entrySet()) {
            Product product = new Product(entry.getValue());
            this.products.put(entry.getKey(), product);
        }
        this.phoneNumber = agreement.getPhoneNumber();
    }

    public void setAgreementID(int agreementID) {
        this.agreementID = agreementID;
    }

    public List<Product> getProducts() {
        return products.values().stream().toList();
    }
    public Message addProduct(Product product) {
        if (products.containsKey(product.getCatalogNumber())) {
            return new Message(false, "Product "+product.getCatalogNumber()+" already exists in agreement");
        }
        this.products.put(product.getCatalogNumber(), product);
        return new Message(true, "Product added to agreement");
    }
    public int getAgreementID() {
        return agreementID;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public Message updateDiscount(int discount, String productID){
        Product product = products.get(productID);
        if (product == null) {
            return new Message(false, "Product "+productID+" not found in agreement");
        }
        else{
            product.setDiscount(discount);
            return new Message(true, "Discount updated successfully");
        }
    }
    public Message updateQuantity(int quantity, String productID){
        Product product = products.get(productID);
        if (product == null) {
            return new Message(false, "Product "+productID+" not found in agreement");
        }
        else{
            product.setQuantityForDiscount(quantity);
            return new Message(true, "Quantity updated successfully");
        }
    }

    public Message updateCost(double cost, String productID){
        Product product = products.get(productID);
        if (product == null) {
            return new Message(false, "Product "+productID+" not found in agreement");
        }
        else{
            product.setCost(cost);
            return new Message(true, "Cost updated successfully");
        }
    }

    public Message removeProductFromAgreement(String productID) {
        Product product = products.get(productID);
        if (product == null) {
            return new Message(false, "Product "+productID+" not found in agreement");
        }
        else{
            products.remove(productID);
            return new Message(true, "Product removed from agreement");
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Agreement{\n");
        sb.append("  agreementID=").append(agreementID).append(",\n");
        sb.append("  phone number='").append(phoneNumber).append("',\n");
        sb.append("  products:\n");
        for (Product p : products.values()) {
            sb.append("    ").append(p).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
