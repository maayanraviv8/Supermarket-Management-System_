package PresentationLayer.GUI;

import HRandDelivery.DomainLayer.HR.enums.Role;
import java.util.*;

public class MenuGUI {

    private Map<Role, MenuOptions> menuOptionsMap;

    public MenuGUI() {
        initializeMenuOptions();
    }

    private void initializeMenuOptions() {
        menuOptionsMap = new HashMap<>();

        // ADMIN menu
        MenuOptions adminOptions = new MenuOptions();
        adminOptions.hrOptions = Arrays.asList(
                new MenuOption("1.1", "Add Employee"),
                new MenuOption("1.2", "Remove Employee"),
                new MenuOption("1.3", "View Employee Details"),
                new MenuOption("1.4", "Update Employee Details"),
                new MenuOption("1.5", "Set Shift Requirements"),
                new MenuOption("1.6", "Employee Shift Assignment"),
                new MenuOption("1.7", "Edit Shift"),
                new MenuOption("1.8", "View Shift Details"),
                new MenuOption("1.9", "Set Shift Submission Deadline")
        );
        adminOptions.deliveryOptions = Arrays.asList(
                new MenuOption("1.10", "Add Delivery"),
                new MenuOption("1.11", "Add Truck")
        );
        adminOptions.generalOptions = Arrays.asList(
                new MenuOption("1.12", "Add Branch"),
                new MenuOption("1", "Load Data"),
                new MenuOption("2", "Reset Data")
        );
        menuOptionsMap.put(Role.ADMIN, adminOptions);

        // SHIFT_MANAGER menu
        MenuOptions shiftManagerOptions = new MenuOptions();
        shiftManagerOptions.hrOptions = Arrays.asList(
                new MenuOption("2.1", "View Shift Details"),
                new MenuOption("2.2", "View Employee Details"),
                new MenuOption("2.3", "View My Shifts"),
                new MenuOption("2.4", "Submit Availability")
        );
        shiftManagerOptions.storageOptions = Arrays.asList(
                new MenuOption("2.5", "Add new product"),
                new MenuOption("2.6", "Delete or buy a product"),
                new MenuOption("2.7", "Move from warehouse to store"),
                new MenuOption("2.8", "Produce report about damaged product"),
                new MenuOption("2.9", "Produce report about expired product"),
                new MenuOption("2.10", "Apply sale by category"),
                new MenuOption("2.11", "Apply sale by product id"),
                new MenuOption("2.12", "Add damaged product"),
                new MenuOption("2.13", "Check for expired products and add to expired list"),
                new MenuOption("2.14", "Produce report about category"),
                new MenuOption("2.15", "Produce Report About Sub Category"),
                new MenuOption("2.16", "Produce Report About Sub Sub Category"),
                new MenuOption("2.17", "Check Annual Order")
        );
        shiftManagerOptions.supplierOptions = Arrays.asList(
                new MenuOption("2.18", "Get Supplier Info"),
                new MenuOption("2.19", "Get Supplier Order Info"),
                new MenuOption("2.20", "Get Supplier Agreement Info"),
                new MenuOption("2.21", "Get All Suppliers ID"),
                new MenuOption("2.22", "Get Orders for Delivery")
        );
        shiftManagerOptions.generalOptions = Arrays.asList();
        menuOptionsMap.put(Role.SHIFT_MANAGER, shiftManagerOptions);

        // STORE_KEEPER menu
        MenuOptions storeKeeperOptions = new MenuOptions();
        storeKeeperOptions.hrOptions = Arrays.asList(
                new MenuOption("3.1", "View My Shifts"),
                new MenuOption("3.2", "Submit Availability")
        );
        storeKeeperOptions.storageOptions = Arrays.asList(
                new MenuOption("3.3", "Add new product"),
                new MenuOption("3.4", "Delete or buy a product"),
                new MenuOption("3.5", "Move from warehouse to store"),
                new MenuOption("3.6", "Apply sale by category"),
                new MenuOption("3.7", "Apply sale by product id"),
                new MenuOption("3.8", "Add damaged product"),
                new MenuOption("3.9", "Add New Order"),
                new MenuOption("3.10", "Delete Order"),
                new MenuOption("3.11", "Update Order")
        );
        storeKeeperOptions.generalOptions = Arrays.asList();
        menuOptionsMap.put(Role.STORE_KEEPER, storeKeeperOptions);

        // SUPPLIER_MANAGER menu
        MenuOptions supplierManagerOptions = new MenuOptions();
        supplierManagerOptions.supplierOptions = Arrays.asList(
                new MenuOption("4.1", "Add Supplier"),
                new MenuOption("4.2", "Remove Supplier"),
                new MenuOption("4.3", "View Supplier Details"),
                new MenuOption("4.4", "Update Supplier Details"),
                new MenuOption("4.5", "Add Supplier Agreement"),
                new MenuOption("4.6", "Remove Supplier Agreement"),
                new MenuOption("4.7", "View Supplier Agreement"),
                new MenuOption("4.8", "Update Supplier Agreement")
        );
        supplierManagerOptions.generalOptions = Arrays.asList();
        menuOptionsMap.put(Role.SUPPLIER_MANAGER, supplierManagerOptions);

        // SUPPLIER menu
        MenuOptions supplierOptions = new MenuOptions();
        supplierOptions.supplierOptions = Arrays.asList(
                new MenuOption("5.1", "Update Price")
        );
        supplierOptions.generalOptions = Arrays.asList();
        menuOptionsMap.put(Role.SUPPLIER, supplierOptions);

        // Default menu for other roles
        MenuOptions defaultOptions = new MenuOptions();
        defaultOptions.hrOptions = Arrays.asList(
                new MenuOption("3.1", "View My Shifts"),
                new MenuOption("3.2", "Submit Availability")
        );
        defaultOptions.generalOptions = Arrays.asList();

        // Apply default menu to all roles that don't have specific menus
        for (Role role : Role.values()) {
            menuOptionsMap.putIfAbsent(role, defaultOptions);
        }
    }

    public MenuOptions getMenuOptions(Role role) {
        return menuOptionsMap.getOrDefault(role, menuOptionsMap.get(Role.ADMIN));
    }
}

class MenuOptions {
    public List<MenuOption> hrOptions = new ArrayList<>();
    public List<MenuOption> deliveryOptions = new ArrayList<>();
    public List<MenuOption> storageOptions = new ArrayList<>();
    public List<MenuOption> supplierOptions = new ArrayList<>();
    public List<MenuOption> generalOptions = new ArrayList<>();
}

class MenuOption {
    public String code;
    public String display;

    public MenuOption(String code, String display) {
        this.code = code;
        this.display = display;
    }
}