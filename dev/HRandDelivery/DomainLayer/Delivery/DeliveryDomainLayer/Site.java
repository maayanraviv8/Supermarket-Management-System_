package HRandDelivery.DomainLayer.Delivery.DeliveryDomainLayer;

import HRandDelivery.DTO.SiteDTO;

public class Site {
    private String address;
    private String contactName;
    private String contactNumber;


    public Site (String address, String contactName, String contactNumber){
        this.address = address;
        this.contactName = contactName;
        this.contactNumber = contactNumber;
    }

    public Site(SiteDTO dto) {
        this.address = dto.getAddress();
        this.contactName = dto.getContactName();
        this.contactNumber = dto.getContactNumber();
    }

    public String getAddress() {
        return address;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setAddress(String newAddress) {
        this.address = newAddress;
    }
}
