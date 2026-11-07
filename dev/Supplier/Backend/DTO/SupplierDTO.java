package Supplier.Backend.DTO;

import Supplier.Backend.domainLayer.*;

import java.util.ArrayList;
import java.util.List;

public class SupplierDTO {
    private String name;
    private long supplierId;
    private String bankAccount;
    private int supplyDay = 0;
    private List<Contact> contacts = new ArrayList<>();
    private List<AgreementDTO> agreements = new ArrayList<>();
    private String address;
    private boolean isDelivery;
    private Payment payment;
    private List<OrderDTO> orders;
    private int nextOrderId;
    private int nextAgreementId;

    public SupplierDTO(String name, long supplierId, String bankAccount, int supplyDay, Payment.PaymentMethod method, Payment.PaymentTerm term, String address, boolean isDelivery) {
        this.name = name;
        this.supplierId = supplierId;
        this.bankAccount = bankAccount;
        this.supplyDay = supplyDay;
        this.payment = new Payment(method, term);
        this.address = address;
        this.isDelivery = isDelivery;
        this.orders = new ArrayList<>();
        this.nextOrderId = 0;
        this.nextAgreementId = 0;
    }
    public SupplierDTO(String name, long supplierId, String bankAccount, int supplyDay, Payment.PaymentMethod method, Payment.PaymentTerm term, String address, boolean isDelivery, List<Contact>contacts, List<OrderDTO> orders, List<AgreementDTO> agreements, int nextOrderId, int nextAgreementId) {
        this.name = name;
        this.supplierId = supplierId;
        this.bankAccount = bankAccount;
        this.supplyDay = supplyDay;
        this.payment = new Payment(method, term);
        this.address = address;
        this.isDelivery = isDelivery;
        this.contacts = contacts;
        this.orders = orders;
        this.agreements = agreements;
        this.nextOrderId = nextOrderId;
        this.nextAgreementId = nextAgreementId;

    }
    public void addContact(String name, String phone) {
        contacts.add(new Contact(name, phone));
    }
    public void addAgreementDTO(AgreementDTO agreementDTO) {
        agreements.add(agreementDTO);
    }

    public String getName() {
        return name;
    }
    public long getSupplierId() {
        return supplierId;
    }
    public String getBankAccount() {
        return bankAccount;
    }
    public Payment getPayment() {
        return payment;
    }
    public List<Contact> getContacts() {
        return contacts;
    }
    public List<AgreementDTO> getAgreements() {
        return agreements;
    }
    public int getSupplyDay() {
        return supplyDay;
    }
    public String getAddress() {
        return address;
    }
    public boolean isDelivery() {
        return isDelivery;
    }
    public List<OrderDTO> getOrders() {
        return orders;
    }
    public int getNextOrderId() {
        return nextOrderId;
    }
    public int getNextAgreementId() {
        return nextAgreementId;
    }


}
