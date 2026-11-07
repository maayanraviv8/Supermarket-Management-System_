package Supplier.Backend.DataAccessLayer;

public class AgreementDAO {
    private long supplierId;
    private int agreementId;
    private String phoneNumber;

    public AgreementDAO(long supplierId, int agreementId, String phoneNumber) {
        this.supplierId = supplierId;
        this.agreementId = agreementId;
        this.phoneNumber = phoneNumber;
    }
    public long getSupplierId() {
        return supplierId;
    }
    public int getAgreementId() {
        return agreementId;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
