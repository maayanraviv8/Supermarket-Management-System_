package Supplier.Backend.DTO;

import java.util.Map;

public class AgreementDTO {

    private Map<String, ProductDTO> products;
    private String phoneNumber;
    private int agreementId;

    public AgreementDTO(Map<String, ProductDTO> products, String phoneNumber) {
        this.products = products;
        this.phoneNumber = phoneNumber;
        this.agreementId = -1;
    }
    public AgreementDTO(Map<String, ProductDTO> products, String phoneNumber, int agreementId) {
        this.products = products;
        this.phoneNumber = phoneNumber;
        this.agreementId = agreementId;
    }

    public Map<String, ProductDTO> getProducts() {
        return products;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public int getAgreementId() {
        return agreementId;
    }
    public void setAgreementId(int agreementId) {
        this.agreementId = agreementId;
    }
}
