package Supplier.Backend.DataAccessLayer;

public class ContactDAO {
    private long supplierId;
    private String contactName;
    private String phoneNumber;

    public ContactDAO(long supplierId, String contactName, String phoneNumber) {
        this.supplierId = supplierId;
        this.contactName = contactName;
        this.phoneNumber = phoneNumber;
    }
    public long getSupplierId() {
        return supplierId;
    }
    public String getContactName() {
        return contactName;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
