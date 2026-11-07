package PresentationLayer.CLI;


import HRandDelivery.DomainLayer.HR.enums.Role;
import HRandDelivery.Utils.UserInput;
import HRandDelivery.Utils.UserOutput;

import java.util.HashMap;
import java.util.Map;

public class Menu {
    // This class is responsible for displaying the menu according to the User Permissions.
    private Map<Role, String> menuOptions;
    private UserInput userInput;
    private UserOutput userOutput;

    public Menu() {
        // Initialize the menu options based on user permission
        this.userInput = UserInput.getInstance();
        this.userOutput = UserOutput.getInstance();
        menuOptions = new HashMap<>();

        menuOptions.put(Role.ADMIN,
                """
                        HR
                            1.1 Add Employee
                            1.2 Remove Employee
                            1.3 View Employee Details
                            1.4 Update Employee Details
                            1.5 Set Shift Requirements
                            1.6 Employee Shift Assignment
                            1.7 Edit Shift
                            1.8 View Shift Details
                            1.9 Set Shift Submission Dedline
                       \s
                        Delivery
                            1.10 Add Delivery
                            1.11 Add Truck
                        \s
                       General
                            1.12 Add Branch                
                           \s
                            0 Logout
                            1 Load Data
                            2 Reset Data
                       \s"""
                );


        menuOptions.put(Role.SHIFT_MANAGER,
                """
                        HR
                            2.1 View Shift Details
                            2.2 View Employee Details
                            2.3 View My Shifts
                            2.4 Submit Availability
                       \s
                        Storage
                            2.5  Add new product
                            2.6  Delete or buy a product
                            2.7  Move from warehouse to store
                            2.8  Produce report about damaged product
                            2.9  Produce report about expired product
                            2.10  Apply sale by category
                            2.11  Apply sale by product id
                            2.12  Add damaged product
                            2.13  Check for expired products and add to expired list
                            2.14  Produce report about category
                            2.15  Produce Report About Sub Category
                            2.16  Produce Report About Sub Sub Category
                            2.17  Check Annual Order   
                       \s
                        Supplier
                            2.18 Get Supplier Info  
                            2.19 Get Supplier Order Info
                            2.20 Get Supplier Agreement Info
                            2.21 Get All Suppliers ID
                            2.22 Get Orders for Delivery     
                        \s
                        General
                            0 Logout                         
                       \s"""
        );

        menuOptions.put(Role.STORE_KEEPER,
                """
                        HR
                            3.1 View My Shifts
                            3.2 Submit Availability
                       \s
                        Storage
                            3.3  Add new product
                            3.4  Delete or buy a product
                            3.5  Move from warehouse to store
                            3.6  Apply sale by category
                            3.7  Apply sale by product id
                            3.8  Add damaged product   
                            3.9 Add New Order
                            3.10 Delete Order
                            3.11 Update Order     
                       \s
                        General
                            0 Logout                         
                       \s"""
        );

        menuOptions.put(Role.SUPPLIER_MANAGER,
                """
                        Supplier
                            4.1 Add Supplier
                            4.2 Remove Supplier
                            4.3 View Supplier Details
                            4.4 Update Supplier Details
                            4.5 Add Supplier Agreement
                            4.6 Remove Supplier Agreement
                            4.7 View Supplier Agreement
                            4.8 Update Supplier Agreement
                       \s
                        General
                            0 Logout                         
                       \s"""
        );
        menuOptions.put(Role.SUPPLIER,
                """
                        Supplier
                            5.1 Update Price
                       \s
                        General
                            0 Logout                         
                       \s"""
        );


        for(Role role : Role.values()) {
            menuOptions.putIfAbsent(role,
                    """
                        HR
                            3.1 View My Shifts
                            3.2 Submit Availability
                       \s
                        General
                            0 Logout                         
                       \s"""
            );
        }
    }


    public String display(Role role) {
        userOutput.print("\nPlease select an option from the menu below:");
        userOutput.print(menuOptions.get(role));
        userOutput.print("Enter your choice: ");
        return userInput.getUserInput();
    }

}
