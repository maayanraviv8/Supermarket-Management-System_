package Supplier.Backend.presentationLayer;

import Supplier.Backend.DTO.AgreementDTO;
import Supplier.Backend.DTO.OrderDTO;
import Supplier.Backend.DTO.ProductDTO;
import Supplier.Backend.DTO.SupplierDTO;
import Supplier.Backend.DataAccessLayer.FactoryDAL;
import Supplier.Backend.domainLayer.Payment;
import Supplier.Backend.serviceLayer.Message;
import Supplier.Backend.serviceLayer.SupplierService;
import Supplier.SingeltonScanner;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ScannerInputSupplier {
    private final SupplierService supplierService = SupplierService.getInstance();
    //private final Scanner scanner = new Scanner(System.in);
    private final Scanner scanner = SingeltonScanner.getInstance();
    public ScannerInputSupplier() {
        new FactoryDAL();
        supplierService.loadSuppliers();}

    public boolean setup() throws Exception {
        try {
            supplierService.init();
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean reset(){
        return supplierService.deleteAllDB();
    }
    public void supplierInfo(){
        Message msg;
        long supplierID = getSupplierId();
        if(supplierID == -1){
            System.out.println("Exiting...");
            return;
        }
        printSupplierDetails();
        String detailsChoice = scanner.nextLine();
        switch (detailsChoice) {
            case "1":
                msg = supplierService.getSupplier(supplierID);
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
                break;
            case "2":
                msg = supplierService.getBankAccount(supplierID);
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
                break;
            case "3":
                msg = supplierService.getPayment(supplierID);
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
                break;
            case "4":
                msg = supplierService.getSupplyDay(supplierID);
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
                break;
            case "5":
                msg = supplierService.getAddress(supplierID);
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
                break;
            case "6":
                msg = supplierService.isDelivery(supplierID);
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
                break;
            case "7":
                msg = supplierService.getContacts(supplierID);
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
                break;
            case "8":
                msg = supplierService.getAllProductsInCatalog(supplierID);
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
                break;
            case "9":
                msg = supplierService.allOrderedProducts(supplierID);
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
                break;
            case "10":
                msg = supplierService.getAgreementsID(supplierID);
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
                break;
            case "11":
                msg = supplierService.getAllOrdersIDs(supplierID);
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
        }
    }
    public void orderInfo() {
        Message msg = new Message(false, "null");
        long supplierID = getSupplierId();
        if (supplierID == -1) {
            System.out.println("Exiting...");
            return;
        }
        int orderID = getOrderID();
        if (orderID == -1) {
            System.out.println("Exiting...");
            return;
        }
        String orderDetailsChoice;
        printOrderDetails();
        orderDetailsChoice = scanner.nextLine();
        switch (orderDetailsChoice) {
            case "1":
                msg = supplierService.getOrder(supplierID, orderID);
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
                break;
            case "2":
                msg = supplierService.getProductsInOrder(supplierID, orderID);
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
                break;
            case "3":
                msg = supplierService.getContactsInOrder(supplierID, orderID);
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
                break;
            case "4":
                msg = supplierService.getOrderDateInOrder(supplierID, orderID);
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
                break;
            case "5":
                msg = supplierService.getTotalOrderPrice(supplierID, orderID);
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
                break;
            case "0":
                System.out.println("Returning to Order Menu...");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
        if (msg !=null && !msg.isSuccess()) {
            System.out.println("Error: " + msg.getMessage());
        } else {
            System.out.println("Order details retrieved successfully.");
        }
    }
    public void agreementInfo(){
        boolean isValidAgreementID = false;
        int agreementID = -1;
        String agreementIDS = "";
        Message msg;
        long supplierID = getSupplierId();
        if(supplierID == -1){
            System.out.println("Exiting...");
            return;
        }
        while (!isValidAgreementID) {
            System.out.println("Please enter agreement ID:");
            agreementIDS = scanner.nextLine().trim();
            if (agreementIDS.isEmpty()) {
                System.out.println("Agreement ID cannot be empty.");
                continue;
            }
            try {
                agreementID = Integer.parseInt(agreementIDS);
                isValidAgreementID = true;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid numeric agreement ID.");
            }
        }
        printAgreementDetails();
        String agreementDetailsChoice = scanner.nextLine();
        switch (agreementDetailsChoice) {
            case "1":
                msg = supplierService.getAgreement(supplierID, agreementID);
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
                break;
            case "2":
                msg = supplierService.getContacts(supplierID, agreementID);
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
                break;
            case "3":
                msg = supplierService.getProductsInAgreement(supplierID, agreementID);
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
        }
    }
    public void allSuppliersID(){
        List<Long> supplierIDs = supplierService.getAllSuppliersIDs();
        if (supplierIDs != null && !supplierIDs.isEmpty()) {
            System.out.println("All Supplier IDs:");
            for (Long id : supplierIDs) {
                System.out.println(id);
            }
        } else {
            System.out.println("No suppliers found.");
        }
    }
    public void printOrdersForDelivery(){
        Message msg = supplierService.getOrdersForDelivery();
        if (msg != null)
            System.out.println(msg);
        else
            System.out.println("Unexpected error occurred.");
    }

    public void handleNewOrder(){
        long supplierID = getSupplierId();
        if(supplierID == -1){
            System.out.println("Exiting...");
            return;
        }
        LocalDate orderDate = null;
        boolean isValidDate = false;
        while (!isValidDate) {
            System.out.println("Please enter order date - as YYYY-MM-DD:");
            String input = scanner.nextLine().trim();
            if(input.isEmpty()) {
                System.out.println("Order date cannot be empty. Please enter order date:");
                continue;
            }
            try {
                orderDate = LocalDate.parse(input);
                isValidDate = true;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please enter a valid date in YYYY-MM-DD format.");
            }
        }
        Map<String, Integer> products = new HashMap<>();
        boolean moreProduct = true;
        while(moreProduct){
            System.out.println("Please enter product catalog number:");
            String catalogNumber = scanner.nextLine().trim();
            while (catalogNumber.isEmpty()) {
                System.out.println("Catalog number cannot be empty. Please enter product catalog number:");
                catalogNumber = scanner.nextLine().trim();
            }
            System.out.println("Please enter product quantity:");
            String quantity = scanner.nextLine().trim();
            while (quantity.isEmpty()) {
                System.out.println("Quantity cannot be empty. Please enter product quantity:");
                quantity = scanner.nextLine().trim();
            }
            int quantityInt = 0;
            boolean isValidQuantity = false;
            while (!isValidQuantity){
                try {
                    quantityInt = Integer.parseInt(quantity);
                    if (quantityInt >= 0) {
                        isValidQuantity = true;
                    } else {
                        System.out.println("Invalid quantity. Please enter a valid quantity:");
                        quantity = scanner.nextLine().trim();
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input.");
                    quantity = scanner.nextLine();
                }
            }
            products.put(catalogNumber, quantityInt);
            System.out.println("Do you want to add another product? (1 for yes, 0 for no):");
            String moreProductInput = scanner.nextLine().trim();
            while (moreProductInput.isEmpty()) {
                System.out.println("Please enter 1 for yes or 0 for no:");
                moreProductInput = scanner.nextLine().trim();
            }
            boolean isValidMoreProduct = false;
            while (!isValidMoreProduct) {
                if (moreProductInput.equals("1")) {
                    isValidMoreProduct = true;
                } else if (moreProductInput.equals("0")) {
                    moreProduct = false;
                    isValidMoreProduct = true;
                } else {
                    System.out.println("Invalid input. Please enter 1 for yes or 0 for no:");
                    moreProductInput = scanner.nextLine().trim();
                }
            }
        }
        String phoneNumber = "";
        while (phoneNumber.isEmpty()) {
            System.out.println("Please enter phone number:");
            phoneNumber = scanner.nextLine().trim();
            if (!phoneNumber.matches("\\d{10}")) {
                System.out.println("Invalid phone number. Please enter a valid phone number (10 digits):");
                phoneNumber = "";
            }
        }
        Message msg = supplierService.addOrder(new OrderDTO(orderDate, products,phoneNumber), supplierID);
        if (msg != null)
            System.out.println(msg);
        else
            System.out.println("Unexpected error occurred.");
    }
    public void deleteOrder(){
        Message msg;
        long supplierID = getSupplierId();
        if(supplierID == -1){
            System.out.println("Exiting...");
            return;
        }
        int orderID = getOrderID();
        if(orderID == -1){
            System.out.println("Exiting...");
            return;
        }
        msg = supplierService.removeOrder(supplierID, orderID);
        if (msg != null)
            System.out.println(msg);
        else
            System.out.println("Unexpected error occurred.");
    }
    public void updateOrder(){
        Message msg;
        long supplierID = getSupplierId();
        if(supplierID == -1){
            System.out.println("Exiting...");
            return;
        }
        int orderID = getOrderID();
        if(orderID == -1){
            System.out.println("Exiting...");
            return;
        }
        String orderUpdateChoice;
        printOrderUpdate();
        orderUpdateChoice = scanner.nextLine();
        switch (orderUpdateChoice) {
            case "1":
                String newPhoneNumber = "";
                boolean isValidPhoneNumber = false;
                while (!isValidPhoneNumber) {
                    System.out.println("Please enter new phone number:");
                    newPhoneNumber = scanner.nextLine().trim();
                    if (newPhoneNumber.isEmpty()) {
                        System.out.println("Phone number cannot be empty. Please enter new phone number:");
                        continue;
                    }
                    if (newPhoneNumber.matches("\\d{10}")) {
                        isValidPhoneNumber = true;
                    } else {
                        System.out.println("Invalid phone number. Please enter a valid phone number (10 digits):");
                        newPhoneNumber = scanner.nextLine().trim();
                    }
                }
                msg = supplierService.updatePhoneNumberInOrder(supplierID, orderID, newPhoneNumber);
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
                break;
            case "2":
                msg = supplierService.orderIsDone(supplierID, orderID);
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
                break;
            case "0":
                System.out.println("Returning to Order Menu...");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }


    public void handleNewSupplier() throws Exception {
        Message msg = supplierService.addSupplier(newSupplier());
        if (!msg.isSuccess()) {
            System.out.println("Failed to add supplier: " + msg.getMessage());
        } else {
            System.out.println("Supplier added successfully.");
        }
    }
    private SupplierDTO newSupplier(){
        System.out.println("Please enter supplier name:");
        String name = scanner.nextLine().trim();
        while (name.isEmpty()) {
            System.out.println("Name cannot be empty. Please enter supplier name:");
            name = scanner.nextLine().trim();
        }
        System.out.println("Please enter supplier ID:");
        String supplierID = scanner.nextLine().trim();
        while (supplierID.isEmpty()) {
            System.out.println("Supplier ID cannot be empty. Please enter supplier ID:");
            supplierID = scanner.nextLine().trim();
        }
        boolean isValidSupplierID = false;
        while (!isValidSupplierID) {
            try {
                if(supplierService.isSupplierExists(Long.parseLong(supplierID)).isSuccess()){
                    System.out.println("Supplier "+ supplierID+" already exists. Please enter a new supplier ID:");
                    supplierID = scanner.nextLine().trim();
                }
                else {
                    isValidSupplierID = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid numeric supplier ID:");
                supplierID = scanner.nextLine().trim();
            }
        }
        System.out.println("Please enter supplier bank account:");
        String bankAccount = scanner.nextLine().trim();
        while (bankAccount.isEmpty()) {
            System.out.println("Bank account cannot be empty. Please enter supplier bank account:");
            bankAccount = scanner.nextLine().trim();
        }
        System.out.println("Please enter supplier supply day:");
        String supplyDay = scanner.nextLine().trim();
        while (supplyDay.isEmpty()) {
            System.out.println("Supply day cannot be empty. Please enter supplier supply day:");
            supplyDay = scanner.nextLine().trim();
        }
        int supplyDayInt = 0;
        boolean isValidSupplyDay = false;
        while (!isValidSupplyDay){
            try {
                supplyDayInt = Integer.parseInt(supplyDay);
                if (supplyDayInt >= 0 && supplyDayInt <= 7) {
                    isValidSupplyDay = true;
                } else {
                    System.out.println("Invalid supply day. Please enter a valid supply day (0-7):");
                    supplyDay = scanner.nextLine().trim();
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
                supplyDay = scanner.nextLine();
            }
        }
        System.out.println("Please enter supplier payment method:CASH, CHECK, BANK_TRANSFER");
        String paymentMethod = scanner.nextLine().trim().toUpperCase();
        while (paymentMethod.isEmpty()) {
            System.out.println("Payment method cannot be empty. Please enter supplier payment method (CASH, CHECK, BANK_TRANSFER):");
            paymentMethod = scanner.nextLine().trim().toUpperCase();
        }
        boolean isValidPaymentMethod = false;
        while (!isValidPaymentMethod){
            if(paymentMethod.equals("CASH") || paymentMethod.equals("CHECK") || paymentMethod.equals("BANK_TRANSFER")){
                isValidPaymentMethod = true;
            } else {
                System.out.println("Invalid payment method. Please enter a valid payment method (CASH, CHECK, BANK_TRANSFER):");
                paymentMethod = scanner.nextLine().trim().toUpperCase();
            }
        }
        System.out.println("Please enter supplier payment term:IN_ADVANCE, UPON_DELIVERY, BEGINNING_OF_MONTH, END_OF_MONTH");
        String paymentTerm = scanner.nextLine().trim().toUpperCase();
        while (paymentTerm.isEmpty()) {
            System.out.println("Payment term cannot be empty. Please enter supplier payment term (IN_ADVANCE, UPON_DELIVERY, BEGINNING_OF_MONTH, END_OF_MONTH):");
            paymentTerm = scanner.nextLine().trim().toUpperCase();
        }
        boolean isValidPaymentTerm = false;
        while (!isValidPaymentTerm){
            if(paymentTerm.equals("IN_ADVANCE") || paymentTerm.equals("UPON_DELIVERY") || paymentTerm.equals("BEGINNING_OF_MONTH") || paymentTerm.equals("END_OF_MONTH")){
                isValidPaymentTerm = true;
            } else {
                System.out.println("Invalid payment term. Please enter a valid payment term (IN ADVANCE, UPON DELIVERY, BEGINNING OF MONTH, END OF MONTH):");
                paymentTerm = scanner.nextLine().trim().toUpperCase();
            }
        }
        System.out.println("Please enter supplier address:");
        String address = scanner.nextLine().trim();
        while (address.isEmpty()) {
            System.out.println("Address cannot be empty. Please enter supplier address:");
            address = scanner.nextLine().trim();
        }
        System.out.println("Please enter supplier delivery option: Deliveries-true/Does not deliver-false");
        String deliveryOption = scanner.nextLine().trim();
        while (deliveryOption.trim().isEmpty()) {
            System.out.println("Delivery option cannot be empty. Please enter supplier delivery option (Deliveries-true/Does not deliver-false):");
            deliveryOption = scanner.nextLine().trim();
        }
        boolean isDelivery = true;
        boolean isValidDeliveryOption = false;
        while (!isValidDeliveryOption){
            if(deliveryOption.equals("true")){
                isDelivery = true;
                isValidDeliveryOption = true;
            }
            else if(deliveryOption.equals("false")){
                isDelivery = false;
                isValidDeliveryOption = true;
            } else {
                System.out.println("Invalid delivery option. Please enter a valid delivery option (Deliveries-true/Does not deliver-false):");
                deliveryOption = scanner.nextLine().trim();
            }
        }
        SupplierDTO supp = new SupplierDTO(name, Long.parseLong(supplierID), bankAccount, supplyDayInt, Payment.PaymentMethod.valueOf(paymentMethod), Payment.PaymentTerm.valueOf(paymentTerm), address, isDelivery);
        int moreContact = -1;
        while (moreContact!=0) {
            System.out.println("Please enter supplier name contact:");
            String contactName = scanner.nextLine().trim();
            while (contactName.trim().isEmpty()) {
                System.out.println("Contact name cannot be empty. Please enter supplier contact name:");
                contactName = scanner.nextLine().trim();
            }
            System.out.println("Please enter supplier contact phone:");
            String contactPhone = scanner.nextLine().trim();
            while (contactPhone.trim().isEmpty()) {
                System.out.println("Contact phone cannot be empty. Please enter supplier contact phone:");
                contactPhone = scanner.nextLine().trim();
            }
            boolean isValidPhone = false;
            while (!isValidPhone) {
                if (contactPhone.matches("\\d{10}")) {
                    isValidPhone = true;
                } else {
                    System.out.println("Invalid phone number. Please enter a valid phone number (10 digits):");
                    contactPhone = scanner.nextLine().trim();
                }
            }
            supp.addContact(contactName, contactPhone);
            System.out.println("Do you want to add another contact? (1 for yes, 0 for no):");
            String moreContactInput = scanner.nextLine().trim();
            while (moreContactInput.trim().isEmpty()) {
                System.out.println("Please enter 1 for yes or 0 for no:");
                moreContactInput = scanner.nextLine().trim();
            }
            boolean isValidMoreContact = false;
            while (!isValidMoreContact) {
                if (moreContactInput.equals("1")) {
                    isValidMoreContact = true;
                } else if (moreContactInput.equals("0")) {
                    moreContact = 0;
                    isValidMoreContact = true;
                } else {
                    System.out.println("Invalid input. Please enter 1 for yes or 0 for no:");
                    moreContactInput = scanner.nextLine().trim();
                }
            }
        }
        int moreAgreement = -1;
        while (moreAgreement!=0){
            System.out.println("Do you want to add an agreement? (1 for yes, 0 for no):");
            String moreAgreementInput = scanner.nextLine().trim();
            while (moreAgreementInput.trim().isEmpty()) {
                System.out.println("Please enter 1 for yes or 0 for no:");
                moreAgreementInput = scanner.nextLine().trim();
            }
            boolean isValidMoreAgreement = false;
            while (!isValidMoreAgreement) {
                if (moreAgreementInput.equals("1")) {
                    isValidMoreAgreement = true;
                    supp.addAgreementDTO(newAgreement());
                } else if (moreAgreementInput.equals("0")) {
                    moreAgreement = 0;
                    isValidMoreAgreement = true;
                } else {
                    System.out.println("Invalid input. Please enter 1 for yes or 0 for no:");
                    moreAgreementInput = scanner.nextLine().trim();
                }
            }
        }
        return supp;
    }
    public void handleRemoveSupplier() throws Exception {
        long supplierID = getSupplierId();
        if(supplierID == -1){
            System.out.println("Exiting...");
            return;
        }
        Message msg = supplierService.deleteSupplier(supplierID);
        if (msg != null)
            System.out.println(msg);
        else
            System.out.println("Unexpected error occurred.");
    }
    public void handleEditSupplier() throws Exception {
        Message msg = new Message(false, "null");
        long supplierID = getSupplierId();
        if(supplierID == -1){
            System.out.println("Exiting...");
            return;
        }
        printSupplierUpdate();
        String updateChoice = scanner.nextLine();
        switch (updateChoice) {
            case "1":
                System.out.println("Please enter new name:");
                String newName = scanner.nextLine().trim();
                while (newName.isEmpty()) {
                    System.out.println("Name cannot be empty. Please enter new name:");
                    newName = scanner.nextLine().trim();
                }
                msg = supplierService.updateName(supplierID, newName);
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
                break;
            case "2":
                System.out.println("Please enter new bank account:");
                String newBankAccount = scanner.nextLine().trim();
                while (newBankAccount.isEmpty()) {
                    System.out.println("Bank account cannot be empty. Please enter new bank account:");
                    newBankAccount = scanner.nextLine().trim();
                }
                msg = supplierService.updateBankAccount(supplierID, newBankAccount);
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
                break;
            case "3":
                System.out.println("Please enter new supply day:");
                String newSupplyDay = scanner.nextLine().trim();
                while (newSupplyDay.isEmpty()) {
                    System.out.println("Supply day cannot be empty. Please enter new supply day:");
                    newSupplyDay = scanner.nextLine().trim();
                }
                int supplyDayInt = 0;
                boolean isValidSupplyDay = false;
                while (!isValidSupplyDay) {
                    try {
                        supplyDayInt = Integer.parseInt(newSupplyDay);
                        if (supplyDayInt >= 0 && supplyDayInt <= 7) {
                            isValidSupplyDay = true;
                        } else {
                            System.out.println("Invalid supply day. Please enter a valid supply day (0-7):");
                            newSupplyDay = scanner.nextLine().trim();
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input.");
                        newSupplyDay = scanner.nextLine().trim();
                    }
                }
                msg = supplierService.updateSupplyDay(supplierID, supplyDayInt);
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
                break;
            case "4":
                System.out.println("Please enter new payment method (CASH, CHECK, BANK_TRANSFER):");
                String newPaymentMethod = scanner.nextLine().trim().toUpperCase();
                while (newPaymentMethod.isEmpty()) {
                    System.out.println("Payment method cannot be empty. Please enter new payment method (CASH, CHECK, BANK_TRANSFER):");
                    newPaymentMethod = scanner.nextLine().trim().toUpperCase();
                }
                boolean isValidPaymentMethod = false;
                while (!isValidPaymentMethod) {
                    if (newPaymentMethod.equals("CASH") || newPaymentMethod.equals("CHECK") || newPaymentMethod.equals("BANK_TRANSFER")) {
                        isValidPaymentMethod = true;
                    } else {
                        System.out.println("Invalid payment method. Please enter a valid payment method (CASH, CHECK, BANK_TRANSFER):");
                        newPaymentMethod = scanner.nextLine().trim().toUpperCase();
                    }
                }
                msg = supplierService.setPaymentMethod(supplierID, newPaymentMethod);
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
                break;
            case "5":
                System.out.println("Please enter new payment term (IN_ADVANCE, UPON_DELIVERY, BEGINNING_OF_MONTH, END_OF_MONTH):");
                String newPaymentTerm = scanner.nextLine().trim().toUpperCase();
                while (newPaymentTerm.isEmpty()) {
                    System.out.println("Payment term cannot be empty. Please enter new payment term (IN_ADVANCE, UPON_DELIVERY, BEGINNING_OF_MONTH, END_OF_MONTH):");
                    newPaymentTerm = scanner.nextLine().trim().toUpperCase();
                }
                boolean isValidPaymentTerm = false;
                while (!isValidPaymentTerm) {
                    if (newPaymentTerm.equals("IN_ADVANCE") || newPaymentTerm.equals("UPON_DELIVERY") || newPaymentTerm.equals("BEGINNING_OF_MONTH") || newPaymentTerm.equals("END_OF_MONTH")) {
                        isValidPaymentTerm = true;
                    } else {
                        System.out.println("Invalid payment term. Please enter a valid payment term (IN_ADVANCE, UPON_DELIVERY, BEGINNING_OF_MONTH, END_OF_MONTH):");
                        newPaymentTerm = scanner.nextLine().trim().toUpperCase();
                    }
                }
                msg = supplierService.updatePaymentTerm(supplierID, newPaymentTerm);
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
                break;
            case "6":
                System.out.println("Please enter new address:");
                String newAddress = scanner.nextLine().trim();
                while (newAddress.isEmpty()) {
                    System.out.println("Address cannot be empty. Please enter new address:");
                    newAddress = scanner.nextLine().trim();
                }
                msg = supplierService.updateAddress(supplierID, newAddress);
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
                break;
            case "7":
                System.out.println("Please enter new delivery option (Deliveries- true/Does not deliver- false):");
                String newDeliveryOption = scanner.nextLine().trim();
                while (newDeliveryOption.isEmpty()) {
                    System.out.println("Delivery option cannot be empty. Please enter new delivery option (Deliveries- true/Does not deliver- false):");
                    newDeliveryOption = scanner.nextLine().trim();
                }
                boolean isDelivery = true;
                boolean isValidDeliveryOption = false;
                while (!isValidDeliveryOption) {
                    if (newDeliveryOption.equals("true")) {
                        isDelivery = true;
                        isValidDeliveryOption = true;
                    } else if (newDeliveryOption.equals("false")) {
                        isDelivery = false;
                        isValidDeliveryOption = true;
                    } else {
                        System.out.println("Invalid delivery option. Please enter a valid delivery option (Deliveries- true/Does not deliver- false):");
                        newDeliveryOption = scanner.nextLine().trim();
                    }
                }
                msg = supplierService.setDelivery(supplierID, isDelivery);
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
                break;
            case "8":
                System.out.println("Please enter supplier contact:");
                String contactName = scanner.nextLine().trim();
                while (contactName.trim().isEmpty()) {
                    System.out.println("Contact name cannot be empty. Please enter supplier contact name:");
                    contactName = scanner.nextLine().trim();
                }
                System.out.println("Please enter supplier contact phone:");
                String contactPhone = scanner.nextLine().trim();
                while (contactPhone.trim().isEmpty()) {
                    System.out.println("Contact phone cannot be empty. Please enter supplier contact phone:");
                    contactPhone = scanner.nextLine().trim();
                }
                boolean isValidPhone = false;
                while (!isValidPhone) {
                    if (contactPhone.matches("\\d{10}")) {
                        isValidPhone = true;
                    } else {
                        System.out.println("Invalid phone number. Please enter a valid phone number (10 digits):");
                        contactPhone = scanner.nextLine().trim();
                    }
                }
                msg = supplierService.addContact(supplierID, contactName, contactPhone);
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
                break;
            case "0":
                System.out.println("Returning to Main Menu...");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
        if (msg != null && !msg.isSuccess()) {
            System.out.println("Error: " + msg.getMessage());
        } else {
            System.out.println("Supplier updated successfully.");
        }
    }

    public void handleNewAgreement(){
        long supplierID = getSupplierId();
        if(supplierID == -1){
            System.out.println("Exiting...");
            return;
        }
        Message msg = supplierService.addAgreement(newAgreement(), supplierID);
    }
    private AgreementDTO newAgreement(){
        System.out.println("Please enter phone number:");
        String phoneNumber = scanner.nextLine().trim();
        Map<String, ProductDTO> products= new HashMap<>();
        while (phoneNumber.isEmpty()) {
            System.out.println("Phone number cannot be empty. Please enter phone number:");
            phoneNumber = scanner.nextLine().trim();
        }
        boolean isValidPhone = false;
        while (!isValidPhone) {
            if (phoneNumber.matches("\\d{10}")) {
                isValidPhone = true;
            } else {
                System.out.println("Invalid phone number. Please enter a valid phone number (10 digits):");
                phoneNumber = scanner.nextLine().trim();
            }
        }
        int moreProduct = -1;
        System.out.println(("Enter product catalog number:"));
        String catalogNumber = scanner.nextLine().trim();
        while (catalogNumber.isEmpty()) {
            System.out.println("Catalog number cannot be empty. Please enter product catalog number:");
            catalogNumber = scanner.nextLine().trim();
        }
        products.put(catalogNumber, handleNewProduct(catalogNumber));
        while (moreProduct !=0){
            System.out.println("Do you want to add another product? (1 for yes, 0 for no):");
            String moreProductInput = scanner.nextLine().trim();
            while (moreProductInput.isEmpty()) {
                System.out.println("Please enter 1 for yes or 0 for no:");
                moreProductInput = scanner.nextLine().trim();
            }
            boolean isValidMoreProduct = false;
            while (!isValidMoreProduct) {
                if (moreProductInput.equals("1")) {
                    isValidMoreProduct = true;
                    System.out.println("Enter product catalog number:");
                    catalogNumber = scanner.nextLine().trim();
                    while (catalogNumber.isEmpty()) {
                        System.out.println("Catalog number cannot be empty. Please enter product catalog number:");
                        catalogNumber = scanner.nextLine().trim();
                    }
                    products.put(catalogNumber, handleNewProduct(catalogNumber));
                } else if (moreProductInput.equals("0")) {
                    moreProduct = 0;
                    isValidMoreProduct = true;
                } else {
                    System.out.println("Invalid input. Please enter 1 for yes or 0 for no:");
                    moreProductInput = scanner.nextLine().trim();
                }
            }
        }
        return new AgreementDTO(products, phoneNumber);
    }
    public void handleEditAgreement(){
        String agreementIDS;
        int agreementID = -1;
        Message msg = new Message(false, "null");
        long supplierID = getSupplierId();
        if(supplierID == -1){
            System.out.println("Exiting...");
            return;
        }
        boolean isValidAgreementID = false;
        while (!isValidAgreementID) {
            System.out.println("Please enter agreement ID:");
            agreementIDS = scanner.nextLine().trim();
            if (agreementIDS.isEmpty()) {
                System.out.println("Agreement ID cannot be empty.");
                continue;
            }
            try {
                agreementID = Integer.parseInt(agreementIDS);
                isValidAgreementID = true;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid numeric agreement ID.");
            }
        }
        printAgreementUpdate();
        String agreementUpdateChoice = scanner.nextLine();
        switch (agreementUpdateChoice) {
            case "1":
                System.out.println("Please enter product catalog number:");
                String catalogNumber = scanner.nextLine().trim();
                while (catalogNumber.isEmpty()) {
                    System.out.println("Catalog number cannot be empty. Please enter product catalog number:");
                    catalogNumber = scanner.nextLine().trim();
                }
                msg = supplierService.addProductToAgreement(supplierID, agreementID, handleNewProduct(catalogNumber));
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
                break;
            case "2":
                String productID = productID();
                msg = supplierService.removeProduct(supplierID, agreementID, productID);
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
                break;
            case "3":
                String newContactName = scanner.nextLine().trim();
                System.out.println("Please enter new contact phone number:");
                String newContactPhone = scanner.nextLine().trim();
                while (newContactPhone.isEmpty()) {
                    System.out.println("Contact phone number cannot be empty. Please enter new contact phone number:");
                    newContactPhone = scanner.nextLine().trim();
                }
                boolean isValidPhoneNumber = false;
                while (!isValidPhoneNumber) {
                    if (newContactPhone.matches("\\d{10}")) {
                        isValidPhoneNumber = true;
                    } else {
                        System.out.println("Invalid phone number. Please enter a valid phone number (10 digits):");
                        newContactPhone = scanner.nextLine().trim();
                    }
                }
                msg = supplierService.addContact(supplierID, agreementID, newContactPhone);
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
                break;
            case "4":
                productID = productID();
                String newDiscount;
                int discount = 0;
                boolean isValidDiscount = false;
                while (!isValidDiscount) {
                    System.out.println("Please enter new discount:");
                    newDiscount = scanner.nextLine().trim();
                    if (newDiscount.isEmpty()) {
                        System.out.println("Discount cannot be empty.");
                        continue;
                    }
                    try {
                        discount = Integer.parseInt(newDiscount);
                        if (discount < 0 || discount > 100) {
                            System.out.println("Discount must be between 0 and 100.");
                            continue;
                        }
                        isValidDiscount = true;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid numeric discount.");
                    }
                }
                msg = supplierService.updateDiscount(supplierID, agreementID, discount, productID);
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
                break;
            case "5":
                productID = productID();
                String newQuantity;
                int quantity = 0;
                boolean isValidQuantity = false;
                while (!isValidQuantity) {
                    System.out.println("Please enter new quantity:");
                    newQuantity = scanner.nextLine().trim();
                    if (newQuantity.isEmpty()) {
                        System.out.println("Quantity cannot be empty.");
                        continue;
                    }
                    try {
                        quantity = Integer.parseInt(newQuantity);
                        if (quantity < 0) {
                            System.out.println("Quantity must be a positive number.");
                            continue;
                        }
                        isValidQuantity = true;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid numeric quantity.");
                    }
                }
                msg = supplierService.updateQuantity(supplierID, agreementID, quantity, productID);
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
                break;
            case "6":
                productID = productID();
                String newPrice;
                int price = 0;
                boolean isValidPrice = false;
                while (!isValidPrice) {
                    System.out.println("Please enter new price:");
                    newPrice = scanner.nextLine().trim();
                    if (newPrice.isEmpty()) {
                        System.out.println("Price cannot be empty.");
                        continue;
                    }
                    try {
                        price = Integer.parseInt(newPrice);
                        if (price < 0) {
                            System.out.println("Price must be a positive number.");
                            continue;
                        }
                        isValidPrice = true;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid numeric price.");
                    }
                }
                msg = supplierService.setCost(supplierID, agreementID, price, productID);
                if (msg != null)
                    System.out.println(msg);
                else
                    System.out.println("Unexpected error occurred.");
                break;
            case "0":
                System.out.println("Returning to Agreement Menu...");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
        if (msg != null && !msg.isSuccess()) {
            System.out.println("Error: " + msg.getMessage());
        } else {
            System.out.println("Agreement updated successfully.");
        }
    }
    public void handleRemoveAgreement(){
        boolean isValidAgreementID = false;
        String agreementIDS;
        int agreementID = -1;
        Message msg = new Message(false, "null");
        long supplierID = getSupplierId();
        if(supplierID == -1){
            System.out.println("Exiting...");
            return;
        }
        while (!isValidAgreementID) {
            System.out.println("Please enter agreement ID:");
            agreementIDS = scanner.nextLine().trim();
            if (agreementIDS.isEmpty()) {
                System.out.println("Agreement ID cannot be empty.");
                continue;
            }
            try {
                agreementID = Integer.parseInt(agreementIDS);
                isValidAgreementID = true;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid numeric agreement ID.");
            }
        }
        msg = supplierService.removeAgreement(supplierID, agreementID);
        if (msg != null)
            System.out.println(msg);
        else
            System.out.println("Unexpected error occurred.");
    }

    public void supplierUser(){
        long supplierID = getSupplierId();
        if(supplierID == -1){
            System.out.println("Exiting...");
            return;
        }
        if(!supplierService.isSupplierExists(supplierID).isSuccess()){
            System.out.println("Supplier not found.");
            return;
        }
        int orderID = getOrderID();
        if(orderID == -1){
            System.out.println("Exiting...");
            return;
        }
        boolean isValidDiscount = false;
        int discount = 0;
        while (!isValidDiscount) {
            System.out.println("Please enter new discount:");
            String newDiscount = scanner.nextLine().trim();
            if (newDiscount.isEmpty()) {
                System.out.println("Discount cannot be empty. Please enter new discount:");
                continue;
            }
            try {
                discount = Integer.parseInt(newDiscount);
                if (discount < 0 || discount > 100) {
                    System.out.println("Discount must be between 0 and 100. Please enter new discount:");
                    continue;
                }
                isValidDiscount = true;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid numeric discount:");
            }
        }
        System.out.println("Please enter product catalog number:");
        String catalogNumber = scanner.nextLine().trim();
        while (catalogNumber.isEmpty()) {
            System.out.println("Catalog number cannot be empty. Please enter product catalog number:");
            catalogNumber = scanner.nextLine().trim();
        }
        Message msg = supplierService.setDiscount(supplierID, orderID, discount, catalogNumber);
        if (msg != null)
            System.out.println(msg);
        else
            System.out.println("Unexpected error occurred.");
    }

    private long getSupplierId() {
        boolean isValidSupplierID = false;
        System.out.println("Please enter supplierID, or -1 for exit:");
        String supplierIDInput = "";
        long id = 0;
        while (!isValidSupplierID) {
            supplierIDInput = scanner.nextLine().trim();
            if (supplierIDInput.isEmpty()) {
                System.out.println("Supplier ID cannot be empty. Please enter supplier ID, or -1 for exit:");
            } else {
                try {
                    id = Long.parseLong(supplierIDInput);
                    Message msg = supplierService.isSupplierExists(id);
                    if (id == -1) {
                        return -1;
                    }
                    if(!msg.isSuccess() ){
                        System.out.println("Supplier not found. Please enter a valid supplierID, or -1 for exit:");

                    } else {
                        isValidSupplierID = true;
                    }
                }
                catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid numeric supplier ID, or -1 for exit:");
                }
            }
        }
        return id;
    }
    private void printSupplierDetails(){
        System.out.println("\n=== Supplier Details ===");
        System.out.println("1. View All Supplier Details");
        System.out.println("2. Bank Account");
        System.out.println("3. Payment Methods and Terms");
        System.out.println("4. Supply Day");
        System.out.println("5. Address");
        System.out.println("6. Delivery Option");
        System.out.println("7. All Contacts");
        System.out.println("8. All Products in Supplier Catalog");
        System.out.println("9. All Ordered Products");
        System.out.println("10. All Agreements ID");
        System.out.println("11. All Orders ID");
        System.out.println("0. Back to Supplier Menu");
        System.out.print("Enter your choice: ");
    }
    private void printOrderDetails(){
        System.out.println("\n=== Order Details ===");
        System.out.println("1. View All Order Details");
        System.out.println("2. Products in Order");
        System.out.println("3. Contact Number in Order");
        System.out.println("4. Order Date");
        System.out.println("5. Get Order's Total Price");
        System.out.println("0. Back to Order Menu");
        System.out.print("Enter your choice: ");
    }
    private String productID(){
        System.out.println("Please enter product ID:");
        String productID = scanner.nextLine().trim();
        while (productID.isEmpty()) {
            System.out.println("Product ID cannot be empty. Please enter product ID:");
            productID = scanner.nextLine().trim();
        }
        return productID;
    }
    private int getOrderID(){
        boolean isValidOrderID = false;
        int orderID = -1;
        while (!isValidOrderID) {
            System.out.println("Please enter order ID, or -1 for exit:");
            String orderIDS = scanner.nextLine().trim();
            if (orderIDS.isEmpty()) {
                System.out.println("Order ID cannot be empty. Please enter order ID, or -1 for exit:");
            } else {
                try {
                    orderID = Integer.parseInt(orderIDS);
                    isValidOrderID = true;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid numeric order ID, or -1 for exit:");
                }
            }
        }
        return orderID;
    }
    private void printOrderUpdate(){
        System.out.println("\n=== Update Order ===");
        System.out.println("1. Update Phone Number");
        System.out.println("2. Update Order's Status");
        System.out.println("0. Back to Order Menu");
        System.out.print("Enter your choice: ");
    }
    private void printSupplierUpdate(){
        System.out.println("\n=== Update Supplier ===");
        System.out.println("1. Update Name");
        System.out.println("2. Update Bank Account");
        System.out.println("3. Update Supply Day");
        System.out.println("4. Update Payment Method");
        System.out.println("5. Update Payment Term");
        System.out.println("6. Update Address");
        System.out.println("7. Update Delivery Option");
        System.out.println("8. Add Contact");
        System.out.println("0. Back to Supplier Menu");
        System.out.print("Enter your choice: ");
    }
    private ProductDTO handleNewProduct(String catalogNumber){
        System.out.println("Please enter product name:");
        String name = scanner.nextLine().trim();
        while (name.isEmpty()) {
            System.out.println("Name cannot be empty. Please enter product name:");
            name = scanner.nextLine().trim();
        }
        System.out.println("Please enter product price:");
        String price = scanner.nextLine().trim();
        while (price.isEmpty()) {
            System.out.println("Price cannot be empty. Please enter product price:");
            price = scanner.nextLine().trim();
        }
        double priceInt = 0;
        boolean isValidPrice = false;
        while (!isValidPrice){
            try {
                priceInt = Double.parseDouble(price);
                if (priceInt >= 0) {
                    isValidPrice = true;
                } else {
                    System.out.println("Invalid price. Please enter a valid price:");
                    price = scanner.nextLine().trim();
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
                price = scanner.nextLine().trim();
            }
        }
        System.out.println("Please enter product quantity for discount:");
        String quantity = scanner.nextLine().trim();
        while (quantity.isEmpty()) {
            System.out.println("Quantity cannot be empty. Please enter product quantity:");
            quantity = scanner.nextLine().trim();
        }
        int quantityInt = 0;
        boolean isValidQuantity = false;
        while (!isValidQuantity){
            try {
                quantityInt = Integer.parseInt(quantity);
                if (quantityInt >= 0) {
                    isValidQuantity = true;
                } else {
                    System.out.println("Invalid quantity. Please enter a valid quantity:");
                    quantity = scanner.nextLine().trim();
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
                quantity = scanner.nextLine();
            }
        }
        System.out.println("Please enter product discount:");
        String discount = scanner.nextLine().trim();
        while (discount.isEmpty()) {
            System.out.println("Discount cannot be empty. Please enter product discount:");
            discount = scanner.nextLine().trim();
        }
        int discountInt = 0;
        boolean isValidDiscount = false;
        while (!isValidDiscount){
            try {
                discountInt = Integer.parseInt(discount);
                if (discountInt >= 0 && discountInt <= 100) {
                    isValidDiscount = true;
                } else {
                    System.out.println("Invalid discount. Please enter a valid discount (0-100):");
                    discount = scanner.nextLine().trim();
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
                discount = scanner.nextLine();
            }
        }
        System.out.println("Please enter product measurement type- PER_UNIT, BY_WEIGHT: ");
        String measurementType = scanner.nextLine().trim().toUpperCase();
        while (measurementType.isEmpty()) {
            System.out.println("Measurement type cannot be empty. Please enter product measurement type (PER_UNIT, BY_WEIGHT):");
            measurementType = scanner.nextLine().trim().toUpperCase();
        }
        boolean isValidMeasurementType = false;
        while (!isValidMeasurementType) {
            if (measurementType.equals("PER_UNIT") || measurementType.equals("BY_WEIGHT")) {
                isValidMeasurementType = true;
            } else {
                System.out.println("Invalid measurement type. Please enter a valid measurement type (PER_UNIT, BY_WEIGHT):");
                measurementType = scanner.nextLine().trim().toUpperCase();
            }
        }
        System.out.println("Please enter manufacturer:");
        String manufacturer = scanner.nextLine().trim();
        while (manufacturer.isEmpty()) {
            System.out.println("Manufacturer cannot be empty. Please enter manufacturer:");
            manufacturer = scanner.nextLine().trim();
        }
        String category1 = "";
        String category2 = "";
        String category3 = "";
        for(int i=0; i<3; i++){
            System.out.println("Please enter product category " + (i+1) + ":");
            String category = scanner.nextLine().trim();
            while (category.isEmpty()) {
                System.out.println("Category cannot be empty. Please enter product category " + (i+1) + ":");
                category = scanner.nextLine().trim();
            }
            if(i==0) {
                category1 = category;
            } else if(i==1) {
                category2 = category;
            } else {
                category3 = category;
            }
        }
        int days = -1;
        while (days < 0) {
            System.out.println("Please enter product expiration date in days:");
            String expirationDate = scanner.nextLine().trim();
            if (expirationDate.isEmpty()) {
                System.out.println("Expiration date cannot be empty. Please enter product expiration date in days:");
                continue;
            }
            try {
                days = Integer.parseInt(expirationDate);
                if (days < 0) {
                    System.out.println("Expiration date cannot be negative. Please enter product expiration date in days:");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid numeric expiration date in days:");
            }
        }
        return new ProductDTO(name, catalogNumber, manufacturer, priceInt, discountInt, quantityInt, measurementType, category1, category2, category3, days);
    }
    public void printAgreementUpdate(){
        System.out.println("\n=== Update Agreement ===");
        System.out.println("1. Add Product");
        System.out.println("2. Delete Product");
        System.out.println("3. Update Phone Number");
        System.out.println("4. Update Discount for Product in Agreement");
        System.out.println("5. Update Quantity for Discount in Agreement");
        System.out.println("6. Update Price for Product in Agreement");
        System.out.println("0. Back to Agreement Menu");
        System.out.print("Enter your choice: ");
    }
    public void printAgreementDetails(){
        System.out.println("\n=== Agreement Details ===");
        System.out.println("1. View All Agreement Details");
        System.out.println("2. Contact Number");
        System.out.println("3. Products in Agreement");
        System.out.println("0. Back to Agreement Menu");
        System.out.print("Enter your choice: ");
    }




//    public void start(){
//        new FactoryDAL();
//        System.out.println("Do you want to initialize the system with default data? (yes/no)");
//        String userChoice = "";
//        while (userChoice.isEmpty()) {
//            userChoice = scanner.nextLine().trim().toLowerCase();
//            if (!userChoice.equals("yes") && !userChoice.equals("no")) {
//                System.out.println("Invalid choice. Please enter 'yes' or 'no':");
//                userChoice = "";
//            }
//            if(userChoice.equals("yes")) {
//                supplierService.init();
//                System.out.println("All data has been initialized in the database.");
//            }
//        }
//        supplierService.loadSuppliers();
//        System.out.println("Welcome to the Supplier System!");
//        while(true){
//            printEnterMenu();
//            String choice = scanner.nextLine();
//            switch (choice) {
//                case "1":
//                    orderManager();
//                    break;
//                case "2":
//                    printOrdersForDelivery();
//                    break;
//                case "3":
//                    supplierUser();
//                    break;
//                case "4":
//                    managerUser();
//                    break;
//                case "0":
//                    System.out.println("Exiting...");
//                    System.out.println("Do you want to delete all information from the Data Base? (yes/no)");
//                    userChoice = "";
//                    while (userChoice.isEmpty()) {
//                        userChoice = scanner.nextLine().trim().toLowerCase();
//                        if (!userChoice.equals("yes") && !userChoice.equals("no")) {
//                            System.out.println("Invalid choice. Please enter 'yes' or 'no':");
//                            userChoice = "";
//                        }
//                        if(userChoice.equals("yes")) {
//                            supplierService.deleteAllDB();
//                            System.out.println("All data has been deleted from the database.");
//                        }
//                    }
//                    return;
//                default:
//                    System.out.println("Invalid choice. Please try again.");
//            }
//        }
//    }

//    private void printEnterMenu(){
//        System.out.println("\n=== Main Menu ===");
//        System.out.println("Please select your role:");
//        System.out.println("1. Order Manager");
//        System.out.println("2. Delivery Manager");
//        System.out.println("3. Supplier");
//        System.out.println("4. Administrator");
//        System.out.println("0. Exit");
//        System.out.print("Enter your choice: ");
//    }

//    private void orderManager(){
//        printOrderMenu();
//        String choice;
//            choice = scanner.nextLine();
//            switch (choice) {
//                case "1":
//                    handleNewOrder();
//                    break;
//                case "2":
//                    updateOrder();
//                    break;
//                case "3":
//                    deleteOrder();
//                    break;
//                case "4":
//                    orderInfo();
//                    break;
//                case "0":
//                    System.out.println("Exiting...");
//                    return;
//                default:
//                    System.out.println("Invalid choice. Please try again.");
//            }
//    }
//    public void printOrderMenu() {
//        System.out.println("\n=== Order Menu ===");
//        System.out.println("1. Add Order");
//        System.out.println("2. Update Order Info");
//        System.out.println("3. Delete Order");
//        System.out.println("4. Get Details of Order");
//        System.out.println("0. Back to Main Menu");
//        System.out.print("Enter your choice: ");
//    }
//    public void printSupplierMenu() {
//        System.out.println("\n=== Supplier Menu ===");
//        System.out.println("1. Add Supplier");
//        System.out.println("2. Update Supplier Info");
//        System.out.println("3. Delete Supplier");
//        System.out.println("4. Get Details of Supplier");
//        System.out.println("0. Back to Main Menu");
//        System.out.print("Enter your choice: ");
//    }
//    public void printAgreementMenu() {
//        System.out.println("\n=== Agreement Menu ===");
//        System.out.println("1. Add Agreement");
//        System.out.println("2. Update Agreement Info");
//        System.out.println("3. Delete Agreement");
//        System.out.println("4. Get Details of Agreement");
//        System.out.println("0. Back to Main Menu");
//        System.out.print("Enter your choice: ");
//    }
//    private void managerUser(){
//        String choice = "";
//        boolean isValidInput = false;
//        while ((!isValidInput)){
//            printMainMenu();
//            choice = scanner.nextLine();
//            if (choice.isEmpty()) {
//                System.out.println("Choice cannot be empty. Please enter your choice:");
//            }
//            else {
//                isValidInput = true;
//            }
//        }
//        switch (choice) {
//            case "1":
//                suppliers();
//                break;
//            case "2":
//                agreements();
//                break;
//            case "3":
//                orderManager();
//                break;
//            case "0":
//                System.out.println("Exiting...");
//                return;
//            default:
//                System.out.println("Invalid choice. Please try again.");
//        }
//
//    }
//    private void printMainMenu() {
//        System.out.println("\n=== Administrator Menu ===");
//        System.out.println("1. Suppliers");
//        System.out.println("2. Agreements");
//        System.out.println("3. Orders");
//        System.out.println("0. Exit");
//        System.out.print("Enter your choice: ");
//    }
//    private void suppliers(){
//        Message msg;
//        long supplierID = 0;
//        printSupplierMenu();
//        String userChoice = scanner.nextLine();
//        switch (userChoice) {
//            case "1":
//                msg = supplierService.addSupplier(handleNewSupplier());
//                if (msg != null)
//                    System.out.println(msg);
//                else
//                    System.out.println("Unexpected error occurred.");
//                break;
//            case "2":
//                supplierID = getSupplierId();
//                if(supplierID == -1){
//                    System.out.println("Exiting...");
//                    return;
//                }
//                printSupplierUpdate();
//                String updateChoice = scanner.nextLine();
//                switch (updateChoice) {
//                    case "1":
//                        System.out.println("Please enter new name:");
//                        String newName = scanner.nextLine().trim();
//                        while (newName.isEmpty()) {
//                            System.out.println("Name cannot be empty. Please enter new name:");
//                            newName = scanner.nextLine().trim();
//                        }
//                        msg = supplierService.updateName(supplierID, newName);
//                        if (msg != null)
//                            System.out.println(msg);
//                        else
//                            System.out.println("Unexpected error occurred.");
//                        break;
//                    case "2":
//                        System.out.println("Please enter new bank account:");
//                        String newBankAccount = scanner.nextLine().trim();
//                        while (newBankAccount.isEmpty()) {
//                            System.out.println("Bank account cannot be empty. Please enter new bank account:");
//                            newBankAccount = scanner.nextLine().trim();
//                        }
//                        msg = supplierService.updateBankAccount(supplierID, newBankAccount);
//                        if (msg != null)
//                            System.out.println(msg);
//                        else
//                            System.out.println("Unexpected error occurred.");
//                        break;
//                    case "3":
//                        System.out.println("Please enter new supply day:");
//                        String newSupplyDay = scanner.nextLine().trim();
//                        while (newSupplyDay.isEmpty()) {
//                            System.out.println("Supply day cannot be empty. Please enter new supply day:");
//                            newSupplyDay = scanner.nextLine().trim();
//                        }
//                        int supplyDayInt = 0;
//                        boolean isValidSupplyDay = false;
//                        while (!isValidSupplyDay) {
//                            try {
//                                supplyDayInt = Integer.parseInt(newSupplyDay);
//                                if (supplyDayInt >= 0 && supplyDayInt <= 7) {
//                                    isValidSupplyDay = true;
//                                } else {
//                                    System.out.println("Invalid supply day. Please enter a valid supply day (0-7):");
//                                    newSupplyDay = scanner.nextLine().trim();
//                                }
//                            } catch (NumberFormatException e) {
//                                System.out.println("Invalid input.");
//                                newSupplyDay = scanner.nextLine().trim();
//                            }
//                        }
//                        msg = supplierService.updateSupplyDay(supplierID, supplyDayInt);
//                        if (msg != null)
//                            System.out.println(msg);
//                        else
//                            System.out.println("Unexpected error occurred.");
//                        break;
//                    case "4":
//                        System.out.println("Please enter new payment method (CASH, CHECK, BANK_TRANSFER):");
//                        String newPaymentMethod = scanner.nextLine().trim().toUpperCase();
//                        while (newPaymentMethod.isEmpty()) {
//                            System.out.println("Payment method cannot be empty. Please enter new payment method (CASH, CHECK, BANK_TRANSFER):");
//                            newPaymentMethod = scanner.nextLine().trim().toUpperCase();
//                        }
//                        boolean isValidPaymentMethod = false;
//                        while (!isValidPaymentMethod) {
//                            if (newPaymentMethod.equals("CASH") || newPaymentMethod.equals("CHECK") || newPaymentMethod.equals("BANK_TRANSFER")) {
//                                isValidPaymentMethod = true;
//                            } else {
//                                System.out.println("Invalid payment method. Please enter a valid payment method (CASH, CHECK, BANK_TRANSFER):");
//                                newPaymentMethod = scanner.nextLine().trim().toUpperCase();
//                            }
//                        }
//                        msg = supplierService.setPaymentMethod(supplierID, newPaymentMethod);
//                        if (msg != null)
//                            System.out.println(msg);
//                        else
//                            System.out.println("Unexpected error occurred.");
//                        break;
//                    case "5":
//                        System.out.println("Please enter new payment term (IN_ADVANCE, UPON_DELIVERY, BEGINNING_OF_MONTH, END_OF_MONTH):");
//                        String newPaymentTerm = scanner.nextLine().trim().toUpperCase();
//                        while (newPaymentTerm.isEmpty()) {
//                            System.out.println("Payment term cannot be empty. Please enter new payment term (IN_ADVANCE, UPON_DELIVERY, BEGINNING_OF_MONTH, END_OF_MONTH):");
//                            newPaymentTerm = scanner.nextLine().trim().toUpperCase();
//                        }
//                        boolean isValidPaymentTerm = false;
//                        while (!isValidPaymentTerm) {
//                            if (newPaymentTerm.equals("IN_ADVANCE") || newPaymentTerm.equals("UPON_DELIVERY") || newPaymentTerm.equals("BEGINNING_OF_MONTH") || newPaymentTerm.equals("END_OF_MONTH")) {
//                                isValidPaymentTerm = true;
//                            } else {
//                                System.out.println("Invalid payment term. Please enter a valid payment term (IN_ADVANCE, UPON_DELIVERY, BEGINNING_OF_MONTH, END_OF_MONTH):");
//                                newPaymentTerm = scanner.nextLine().trim().toUpperCase();
//                            }
//                        }
//                        msg = supplierService.updatePaymentTerm(supplierID, newPaymentTerm);
//                        if (msg != null)
//                            System.out.println(msg);
//                        else
//                            System.out.println("Unexpected error occurred.");
//                        break;
//                    case "6":
//                        System.out.println("Please enter new address:");
//                        String newAddress = scanner.nextLine().trim();
//                        while (newAddress.isEmpty()) {
//                            System.out.println("Address cannot be empty. Please enter new address:");
//                            newAddress = scanner.nextLine().trim();
//                        }
//                        msg = supplierService.updateAddress(supplierID, newAddress);
//                        if (msg != null)
//                            System.out.println(msg);
//                        else
//                            System.out.println("Unexpected error occurred.");
//                        break;
//                    case "7":
//                        System.out.println("Please enter new delivery option (Deliveries- true/Does not deliver- false):");
//                        String newDeliveryOption = scanner.nextLine().trim();
//                        while (newDeliveryOption.isEmpty()) {
//                            System.out.println("Delivery option cannot be empty. Please enter new delivery option (Deliveries- true/Does not deliver- false):");
//                            newDeliveryOption = scanner.nextLine().trim();
//                        }
//                        boolean isDelivery = true;
//                        boolean isValidDeliveryOption = false;
//                        while (!isValidDeliveryOption) {
//                            if (newDeliveryOption.equals("true")) {
//                                isDelivery = true;
//                                isValidDeliveryOption = true;
//                            } else if (newDeliveryOption.equals("false")) {
//                                isDelivery = false;
//                                isValidDeliveryOption = true;
//                            } else {
//                                System.out.println("Invalid delivery option. Please enter a valid delivery option (Deliveries- true/Does not deliver- false):");
//                                newDeliveryOption = scanner.nextLine().trim();
//                            }
//                        }
//                        msg = supplierService.setDelivery(supplierID, isDelivery);
//                        if (msg != null)
//                            System.out.println(msg);
//                        else
//                            System.out.println("Unexpected error occurred.");
//                        break;
//                    case "8":
//                        System.out.println("Please enter supplier contact:");
//                        String contactName = scanner.nextLine().trim();
//                        while (contactName.trim().isEmpty()) {
//                            System.out.println("Contact name cannot be empty. Please enter supplier contact name:");
//                            contactName = scanner.nextLine().trim();
//                        }
//                        System.out.println("Please enter supplier contact phone:");
//                        String contactPhone = scanner.nextLine().trim();
//                        while (contactPhone.trim().isEmpty()) {
//                            System.out.println("Contact phone cannot be empty. Please enter supplier contact phone:");
//                            contactPhone = scanner.nextLine().trim();
//                        }
//                        boolean isValidPhone = false;
//                        while (!isValidPhone) {
//                            if (contactPhone.matches("\\d{10}")) {
//                                isValidPhone = true;
//                            } else {
//                                System.out.println("Invalid phone number. Please enter a valid phone number (10 digits):");
//                                contactPhone = scanner.nextLine().trim();
//                            }
//                        }
//                        msg = supplierService.addContact(supplierID, contactName, contactPhone);
//                        if (msg != null)
//                            System.out.println(msg);
//                        else
//                            System.out.println("Unexpected error occurred.");
//                        break;
//                    case "0":
//                        System.out.println("Returning to Main Menu...");
//                        break;
//                    default:
//                        System.out.println("Invalid choice. Please try again.");
//                }
//                break;
//            case "3":
//                supplierID = getSupplierId();
//                if(supplierID == -1){
//                    System.out.println("Exiting...");
//                    return;
//                }
//                msg = supplierService.deleteSupplier(supplierID);
//                if (msg != null)
//                    System.out.println(msg);
//                else
//                    System.out.println("Unexpected error occurred.");
//                break;
//            case "4":
//                supplierID = getSupplierId();
//                if(supplierID == -1){
//                    System.out.println("Exiting...");
//                    return;
//                }
//                printSupplierDetails();
//                String detailsChoice = scanner.nextLine();
//                switch (detailsChoice) {
//                    case "1":
//                        msg = supplierService.getSupplier(supplierID);
//                        if (msg != null)
//                            System.out.println(msg);
//                        else
//                            System.out.println("Unexpected error occurred.");
//                        break;
//                    case "2":
//                        msg = supplierService.getBankAccount(supplierID);
//                        if (msg != null)
//                            System.out.println(msg);
//                        else
//                            System.out.println("Unexpected error occurred.");
//                        break;
//                    case "3":
//                        msg = supplierService.getPayment(supplierID);
//                        if (msg != null)
//                            System.out.println(msg);
//                        else
//                            System.out.println("Unexpected error occurred.");
//                        break;
//                    case "4":
//                        msg = supplierService.getSupplyDay(supplierID);
//                        if (msg != null)
//                            System.out.println(msg);
//                        else
//                            System.out.println("Unexpected error occurred.");
//                        break;
//                    case "5":
//                        msg = supplierService.getAddress(supplierID);
//                        if (msg != null)
//                            System.out.println(msg);
//                        else
//                            System.out.println("Unexpected error occurred.");
//                        break;
//                    case "6":
//                        msg = supplierService.isDelivery(supplierID);
//                        if (msg != null)
//                            System.out.println(msg);
//                        else
//                            System.out.println("Unexpected error occurred.");
//                        break;
//                    case "7":
//                        msg = supplierService.getContacts(supplierID);
//                        if (msg != null)
//                            System.out.println(msg);
//                        else
//                            System.out.println("Unexpected error occurred.");
//                        break;
//                    case "8":
//                        msg = supplierService.getAllProductsInCatalog(supplierID);
//                        if (msg != null)
//                            System.out.println(msg);
//                        else
//                            System.out.println("Unexpected error occurred.");
//                        break;
//                    case "9":
//                        msg = supplierService.allOrderedProducts(supplierID);
//                        if (msg != null)
//                            System.out.println(msg);
//                        else
//                            System.out.println("Unexpected error occurred.");
//                        break;
//                    case "10":
//                        msg = supplierService.getAgreementsID(supplierID);
//                        if (msg != null)
//                            System.out.println(msg);
//                        else
//                            System.out.println("Unexpected error occurred.");
//                        break;
//                    case "11":
//                        msg = supplierService.getAllOrdersIDs(supplierID);
//                        if (msg != null)
//                            System.out.println(msg);
//                        else
//                            System.out.println("Unexpected error occurred.");
//                        break;
//                    case "0":
//                        System.out.println("Returning to Main Menu...");
//                        break;
//                    default:
//                        System.out.println("Invalid choice. Please try again.");
//                }
//                break;
//            case "0":
//                System.out.println("Returning to Main Menu...");
//                break;
//            default:
//                System.out.println("Invalid choice. Please try again.");
//        }
//
//    }
//    private void agreements(){
//        long supplierID = getSupplierId();
//        if(supplierID == -1){
//            System.out.println("Exiting...");
//            return;
//        }
//        Message msg;
//        printAgreementMenu();
//        int agreementID = 0;
//        String agreementChoice = scanner.nextLine();
//        switch (agreementChoice) {
//            case "1":
//                msg = supplierService.addAgreement(handleNewAgreement(), supplierID);
//                if (msg != null)
//                    System.out.println(msg);
//                else
//                    System.out.println("Unexpected error occurred.");
//                break;
//            case "2":
//                String agreementIDS;
//                boolean isValidAgreementID = false;
//                while (!isValidAgreementID) {
//                    System.out.println("Please enter agreement ID:");
//                    agreementIDS = scanner.nextLine().trim();
//                    if (agreementIDS.isEmpty()) {
//                        System.out.println("Agreement ID cannot be empty.");
//                        continue;
//                    }
//                    try {
//                        agreementID = Integer.parseInt(agreementIDS);
//                        isValidAgreementID = true;
//                    } catch (NumberFormatException e) {
//                        System.out.println("Invalid input. Please enter a valid numeric agreement ID.");
//                    }
//                }
//                printAgreementUpdate();
//                String agreementUpdateChoice = scanner.nextLine();
//                switch (agreementUpdateChoice) {
//                    case "1":
//                        System.out.println("Please enter product catalog number:");
//                        String catalogNumber = scanner.nextLine().trim();
//                        while (catalogNumber.isEmpty()) {
//                            System.out.println("Catalog number cannot be empty. Please enter product catalog number:");
//                            catalogNumber = scanner.nextLine().trim();
//                        }
//                        msg = supplierService.addProductToAgreement(supplierID, agreementID, handleNewProduct(catalogNumber));
//                        if (msg != null)
//                            System.out.println(msg);
//                        else
//                            System.out.println("Unexpected error occurred.");
//                        break;
//                    case "2":
//                        String productID = productID();
//                        msg = supplierService.removeProduct(supplierID, agreementID, productID);
//                        if (msg != null)
//                            System.out.println(msg);
//                        else
//                            System.out.println("Unexpected error occurred.");
//                        break;
//                    case "3":
//                        String newContactName = scanner.nextLine().trim();
//                        System.out.println("Please enter new contact phone number:");
//                        String newContactPhone = scanner.nextLine().trim();
//                        while (newContactPhone.isEmpty()) {
//                            System.out.println("Contact phone number cannot be empty. Please enter new contact phone number:");
//                            newContactPhone = scanner.nextLine().trim();
//                        }
//                        boolean isValidPhoneNumber = false;
//                        while (!isValidPhoneNumber) {
//                            if (newContactPhone.matches("\\d{10}")) {
//                                isValidPhoneNumber = true;
//                            } else {
//                                System.out.println("Invalid phone number. Please enter a valid phone number (10 digits):");
//                                newContactPhone = scanner.nextLine().trim();
//                            }
//                        }
//                        msg = supplierService.addContact(supplierID, agreementID, newContactPhone);
//                        if (msg != null)
//                            System.out.println(msg);
//                        else
//                            System.out.println("Unexpected error occurred.");
//                        break;
//                    case "4":
//                        productID = productID();
//                        String newDiscount;
//                        int discount = 0;
//                        boolean isValidDiscount = false;
//                        while (!isValidDiscount) {
//                            System.out.println("Please enter new discount:");
//                            newDiscount = scanner.nextLine().trim();
//                            if (newDiscount.isEmpty()) {
//                                System.out.println("Discount cannot be empty.");
//                                continue;
//                            }
//                            try {
//                                discount = Integer.parseInt(newDiscount);
//                                if (discount < 0 || discount > 100) {
//                                    System.out.println("Discount must be between 0 and 100.");
//                                    continue;
//                                }
//                                isValidDiscount = true;
//                            } catch (NumberFormatException e) {
//                                System.out.println("Invalid input. Please enter a valid numeric discount.");
//                            }
//                        }
//                        msg = supplierService.updateDiscount(supplierID, agreementID, discount, productID);
//                        if (msg != null)
//                            System.out.println(msg);
//                        else
//                            System.out.println("Unexpected error occurred.");
//                        break;
//                    case "5":
//                        productID = productID();
//                        String newQuantity;
//                        int quantity = 0;
//                        boolean isValidQuantity = false;
//                        while (!isValidQuantity) {
//                            System.out.println("Please enter new quantity:");
//                            newQuantity = scanner.nextLine().trim();
//                            if (newQuantity.isEmpty()) {
//                                System.out.println("Quantity cannot be empty.");
//                                continue;
//                            }
//                            try {
//                                quantity = Integer.parseInt(newQuantity);
//                                if (quantity < 0) {
//                                    System.out.println("Quantity must be a positive number.");
//                                    continue;
//                                }
//                                isValidQuantity = true;
//                            } catch (NumberFormatException e) {
//                                System.out.println("Invalid input. Please enter a valid numeric quantity.");
//                            }
//                        }
//                        msg = supplierService.updateQuantity(supplierID, agreementID, quantity, productID);
//                        if (msg != null)
//                            System.out.println(msg);
//                        else
//                            System.out.println("Unexpected error occurred.");
//                        break;
//                    case "6":
//                        productID = productID();
//                        String newPrice;
//                        int price = 0;
//                        boolean isValidPrice = false;
//                        while (!isValidPrice) {
//                            System.out.println("Please enter new price:");
//                            newPrice = scanner.nextLine().trim();
//                            if (newPrice.isEmpty()) {
//                                System.out.println("Price cannot be empty.");
//                                continue;
//                            }
//                            try {
//                                price = Integer.parseInt(newPrice);
//                                if (price < 0) {
//                                    System.out.println("Price must be a positive number.");
//                                    continue;
//                                }
//                                isValidPrice = true;
//                            } catch (NumberFormatException e) {
//                                System.out.println("Invalid input. Please enter a valid numeric price.");
//                            }
//                        }
//                        msg = supplierService.setCost(supplierID, agreementID, price, productID);
//                        if (msg != null)
//                            System.out.println(msg);
//                        else
//                            System.out.println("Unexpected error occurred.");
//                        break;
//                    case "0":
//                        System.out.println("Returning to Agreement Menu...");
//                        break;
//                    default:
//                        System.out.println("Invalid choice. Please try again.");
//                }
//                break;
//            case "3":
//                isValidAgreementID = false;
//                while (!isValidAgreementID) {
//                    System.out.println("Please enter agreement ID:");
//                    agreementIDS = scanner.nextLine().trim();
//                    if (agreementIDS.isEmpty()) {
//                        System.out.println("Agreement ID cannot be empty.");
//                        continue;
//                    }
//                    try {
//                        agreementID = Integer.parseInt(agreementIDS);
//                        isValidAgreementID = true;
//                    } catch (NumberFormatException e) {
//                        System.out.println("Invalid input. Please enter a valid numeric agreement ID.");
//                    }
//                }
//                msg = supplierService.removeAgreement(supplierID, agreementID);
//                if (msg != null)
//                    System.out.println(msg);
//                else
//                    System.out.println("Unexpected error occurred.");
//                break;
//            case "4":
//                isValidAgreementID = false;
//                while (!isValidAgreementID) {
//                    System.out.println("Please enter agreement ID:");
//                    agreementIDS = scanner.nextLine().trim();
//                    if (agreementIDS.isEmpty()) {
//                        System.out.println("Agreement ID cannot be empty.");
//                        continue;
//                    }
//                    try {
//                        agreementID = Integer.parseInt(agreementIDS);
//                        isValidAgreementID = true;
//                    } catch (NumberFormatException e) {
//                        System.out.println("Invalid input. Please enter a valid numeric agreement ID.");
//                    }
//                }
//                printAgreementDetails();
//                String agreementDetailsChoice = scanner.nextLine();
//                switch (agreementDetailsChoice) {
//                    case "1":
//                        msg = supplierService.getAgreement(supplierID, agreementID);
//                        if (msg != null)
//                            System.out.println(msg);
//                        else
//                            System.out.println("Unexpected error occurred.");
//                        break;
//                    case "2":
//                        msg = supplierService.getContacts(supplierID, agreementID);
//                        if (msg != null)
//                            System.out.println(msg);
//                        else
//                            System.out.println("Unexpected error occurred.");
//                        break;
//                    case "3":
//                        msg = supplierService.getProductsInAgreement(supplierID, agreementID);
//                        if (msg != null)
//                            System.out.println(msg);
//                        else
//                            System.out.println("Unexpected error occurred.");
//                        break;
//                    case "0":
//                        System.out.println("Returning to Agreement Menu...");
//                        break;
//                    default:
//                        System.out.println("Invalid choice. Please try again.");
//                }
//                break;
//            case "0":
//                System.out.println("Returning to Main Menu...");
//                break;
//            default:
//                System.out.println("Invalid choice. Please try again.");
//        }
//    }

}
