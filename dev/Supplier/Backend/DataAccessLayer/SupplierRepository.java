package Supplier.Backend.DataAccessLayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Supplier.Backend.domainLayer.*;
import Supplier.Backend.DTO.AgreementDTO;
import Supplier.Backend.DTO.OrderDTO;
import Supplier.Backend.DTO.ProductDTO;
import Supplier.Backend.DTO.SupplierDTO;

public class SupplierRepository {
    private static SupplierRepository instance = null;
    private final SupplierController controller = new SupplierController();
    private final Map<Long, SupplierDTO> suppliers = new HashMap<>();

    private SupplierRepository() {
    }
    public static SupplierRepository getInstance() {
        if (instance == null) {
            instance = new SupplierRepository();
        }
        return instance;
    }

    public void loadSuppliers(){
        List<Long> supplierIds = controller.getAllSupplierIds();
        for(Long id : supplierIds) {
            suppliers.put(id, controller.uploadSupplier(id));
        }
    }
    public Map<Long, SupplierDTO> getAllSuppliers() {
        return suppliers;
    }
    public SupplierDTO getSupplierById(long id) {
        return suppliers.get(id);
    }

    //delete
    public void deleteSupplier(long id) {
        controller.deleteSupplier(id);
        suppliers.remove(id);
    }
    public void deleteAgreement(long supplierId, int agreementId) {
        controller.deleteAgreement(supplierId, agreementId);
    }
    public void deleteOrder(long supplierId, int orderId) {
        controller.deleteOrder(supplierId, orderId);
    }
    public void deleteProduct(long supplierId, String catalogNumber) {
        controller.deleteProduct(supplierId, catalogNumber);
    }
    public boolean deleteAllData(){
        boolean success = controller.deleteAllData();
        if (!success) {
            return false;
        }
        suppliers.clear();
        return true;
    }
    //add+updates
    public void addProduct(long supplierID, ProductDTO prod, int agreementID){
        controller.addProductsDB(prod, supplierID, agreementID);
    }
    public void updateProductDiscount(long supplierID, int agreementID, String catalogNumber, int newDiscount) {
        controller.updateProductDiscount(supplierID, agreementID, catalogNumber, newDiscount);
    }
    public void updateProductQuantity(long supplierID, int agreementID, String catalogNumber, int newQuantity) {
        controller.updateProductQuantityForDiscount(supplierID, agreementID, catalogNumber, newQuantity);
    }
    public void updateProductCost(long supplierID, int agreementID, String catalogNumber, double newPrice) {
        controller.updateProductCost(supplierID, agreementID, catalogNumber, newPrice);
    }

    public void addContact(long supplierID, Contact contact) {
        controller.addContactDB(contact, supplierID);
    }
    public void addContactToAgreement(long supplierId, int agreementId, String newPhoneNumber) {
        controller.updateAgreementPhoneNumber(supplierId, agreementId, newPhoneNumber);
    }
    public void addAgreement(long supplierID, AgreementDTO agreement) {
        controller.addAgreementDB(agreement, supplierID);
    }

    public void addOrder(long supplierID, OrderDTO order){
        controller.addOrderDB(order, supplierID);
    }
    public void updateOrderProductDiscount(long supplierID, int orderID, String catalogNumber, int newDiscount) {
        controller.updateOrderProductDiscount(supplierID, orderID, catalogNumber, newDiscount);
    }
    public void updateOrderStatus(long supplierID, int orderID, String newStatus) {
        controller.updateOrderStatus(supplierID, orderID, newStatus);
    }
    public void updateOrderPhoneNumber(long supplierID, int orderID, String newPhoneNumber) {
        controller.updateOrderPhoneNumber(supplierID, orderID, newPhoneNumber);
    }

    public void updateSupplierAddress(long supplierID, String newAddress) {
        controller.updateSupplierAddress(supplierID, newAddress);
    }
    public void updateSupplierBankAccount(long supplierID, String newBankAccount) {
        controller.updateSupplierBankAccount(supplierID, newBankAccount);
    }
    public void updateSupplierPaymentMethod(long supplierID, String newMethod) {
        controller.updateSupplierPaymentMethod(supplierID, newMethod);
    }
    public void updateSupplierPaymentTerm(long supplierID, String newTerm) {
        controller.updateSupplierPaymentTerm(supplierID, newTerm);
    }
    public void updateSupplierDelivery(long supplierID, boolean isDelivery) {
        controller.updateSupplierDelivery(supplierID, isDelivery);
    }
    public void updateSupplierSupplyDay(long supplierID, int newSupplyDay) {
        controller.updateSupplierSupplyDay(supplierID, newSupplyDay);
    }
    public void updateSupplierName(long supplierID, String newName) {
        controller.updateSupplierName(supplierID, newName);
    }
    public void addSupplier(SupplierDTO supplier) {
        controller.addSupplierDB(supplier);
        suppliers.put(supplier.getSupplierId(), supplier);
    }
}
