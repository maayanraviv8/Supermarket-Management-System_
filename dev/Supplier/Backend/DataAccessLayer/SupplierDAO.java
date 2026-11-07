package Supplier.Backend.DataAccessLayer;

public class SupplierDAO {
    private long supplierId;
    private String supplierName;
    private int supplyDay;
    private String address;
    private String bankAccount;
    private boolean isDelivery;
    private String paymentMethod;
    private String paymentTerm;

    public SupplierDAO(long supplierId, String supplierName, int supplyDay, String address, String bankAccount, boolean isDelivery, String paymentMethod, String paymentTerm) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.supplyDay = supplyDay;
        this.address = address;
        this.bankAccount = bankAccount;
        this.isDelivery = isDelivery;
        this.paymentMethod = paymentMethod;
        this.paymentTerm = paymentTerm;
    }
    public long getSupplierId() {
        return supplierId;
    }
    public String getSupplierName() {
        return supplierName;
    }
    public int getSupplyDay() {
        return supplyDay;
    }
    public String getAddress() {
        return address;
    }
    public String getBankAccount() {
        return bankAccount;
    }
    public boolean isDelivery() {
        return isDelivery;
    }
    public String getPaymentMethod() {
        return paymentMethod;
    }
    public String getPaymentTerm() {
        return paymentTerm;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
    public void setSupplyDay(int supplyDay) {
        this.supplyDay = supplyDay;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }
    public void setDelivery(boolean delivery) {
        isDelivery = delivery;
    }
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    public void setPaymentTerm(String paymentTerm) {
        this.paymentTerm = paymentTerm;
    }



}
