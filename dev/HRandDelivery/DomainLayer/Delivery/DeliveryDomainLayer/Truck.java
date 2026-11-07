package HRandDelivery.DomainLayer.Delivery.DeliveryDomainLayer;

import HRandDelivery.DTO.TruckDTO;
import HRandDelivery.DomainLayer.Delivery.enums.License;

public class Truck {

    private String truckNumber;
    private String model;
    private int netWeight;
    private int maxWeight;
    private License license;


    public Truck(String truckNumber, String model, int netWeight, int maxWeight, License license) {
        this.truckNumber = truckNumber;
        this.model = model;
        this.netWeight = netWeight;
        this.maxWeight = maxWeight;
        this.license = license;
    }

    public Truck(TruckDTO dto) {
        this.truckNumber = dto.getTruckNumber();
        this.model = dto.getModel();
        this.netWeight = dto.getNetWeight();
        this.maxWeight = dto.getMaxWeight();
        this.license = dto.getLicense();
    }

    public String getTruckNumber() {
        return truckNumber;
    }

    public String getModel() {
        return model;
    }

    public int getNetWeight() {
        return netWeight;
    }

    public int getMaxWeight() {
        return maxWeight;
    }

    public License getLicense() {
        return license;
    }
}
