package HRandDelivery.DTO;

public class SiteDTO {

    private String address;
    private String contactName;
    private String contactNumber;


    public SiteDTO (String address, String contactName, String contactNumber){
        this.address = address;
        this.contactName = contactName;
        this.contactNumber = contactNumber;
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
}
