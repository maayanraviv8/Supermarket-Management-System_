package HRandDelivery.DTO;

import HRandDelivery.DomainLayer.Delivery.enums.License;

public class TruckDTO {

    private String truckNumber;
    private String model;
    private int netWeight;
    private int maxWeight;
    private License license;

    // No-args constructor
    public TruckDTO() {
    }

    // All-args constructor
    public TruckDTO(String truckNumber, String model, int netWeight, int maxWeight, License license) {
        this.truckNumber = truckNumber;
        this.model = model;
        this.netWeight = netWeight;
        this.maxWeight = maxWeight;
        this.license = license;
    }

    // Getters and Setters

    public String getTruckNumber() {
        return truckNumber;
    }

    public void setTruckNumber(String truckNumber) {
        this.truckNumber = truckNumber;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(int netWeight) {
        this.netWeight = netWeight;
    }

    public int getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(int maxWeight) {
        this.maxWeight = maxWeight;
    }

    public License getLicense() {
        return license;
    }

    public void setLicense(License license) {
        this.license = license;
    }
}
