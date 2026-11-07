package PresentationLayer.CLI;

import HRandDelivery.PresentationLayer.HRandDeliveryPres;
import HRandDelivery.Utils.Response;
import HRandDelivery.Utils.UserInput;
import HRandDelivery.Utils.UserOutput;
import PresentationLayer.EmployeePres;
import Frontend.PresentationLayer.StorageMain;
import Supplier.Backend.presentationLayer.ScannerInputSupplier;

public class CLIPres {

    private LogInPage logInPage;
    private Menu menu;
    private UserInput userInput;
    private UserOutput userOutput;

    private HRandDeliveryPres hrAndDeliveryPres;
    private ScannerInputSupplier supplierPres;
    private StorageMain storagePres ;


    public CLIPres() throws Exception {
        // Initialize the presentation layer components
        this.userInput = UserInput.getInstance();
        this.userOutput = UserOutput.getInstance();
        this.logInPage = new LogInPage();
        this.menu = new Menu();

        // Initialize the presentations
        this.hrAndDeliveryPres = new HRandDeliveryPres();
        this.supplierPres = new ScannerInputSupplier();
        this.storagePres = new StorageMain();
    }


    public void start() {
        
        EmployeePres currentUser = null;
        
        try {
            currentUser = handleLogin();
        } catch (Exception e) {
            userOutput.print("An error occurred: " + e.getMessage() + ". Please try again.");
        }

        while (true) {
            try {
                if (currentUser == null) {
                    currentUser = handleLogin();
                }

                // After successful login, display the main menu
                // Display the menu per role
                String choose = menu.display(currentUser.getRole());

                switch (choose) {

                    //HR and Delivery
                    case "1.1" -> hrAndDeliveryPres.handleResponse(hrAndDeliveryPres.handleAddEmployee());
                    case "1.2" -> hrAndDeliveryPres.handleResponse(hrAndDeliveryPres.handleRemoveEmployee());
                    case "1.3", "2.2" -> hrAndDeliveryPres.handleResponse(hrAndDeliveryPres.handleViewEmployeeDetails());
                    case "1.4" -> hrAndDeliveryPres.handleResponse(hrAndDeliveryPres.handleUpdateEmployeeDetails());
                    case "1.5" -> hrAndDeliveryPres.handleResponse(hrAndDeliveryPres.handleSetShiftRequirements());
                    case "1.6" -> hrAndDeliveryPres.handleResponse(hrAndDeliveryPres.handleEmployeeShiftAssignment());
                    case "1.7" -> hrAndDeliveryPres.handleResponse(hrAndDeliveryPres.handleEditShift());
                    case "1.8", "2.1" -> hrAndDeliveryPres.handleResponse(hrAndDeliveryPres.handleViewShiftDetails());
                    case "1.9" -> hrAndDeliveryPres.handleResponse(hrAndDeliveryPres.handleSetShiftSubmissionDedline());
                    case "1.10" -> hrAndDeliveryPres.handleResponse(hrAndDeliveryPres.handleAddDelivery());
                    case "1.11" -> hrAndDeliveryPres.handleResponse(hrAndDeliveryPres.handleAddTruck());
                    case "1.12" -> hrAndDeliveryPres.handleResponse(hrAndDeliveryPres.handleAddBranch());
                    case "2.3", "3.1" -> hrAndDeliveryPres.handleResponse(hrAndDeliveryPres.handleViewMyShifts(currentUser.getId()));
                    case "2.4", "3.2" -> hrAndDeliveryPres.handleResponse(hrAndDeliveryPres.handleSubmitAvailability(currentUser.getId()));

                    //Supplier
                    case "2.18" -> supplierPres.supplierInfo();
                    case "2.19" -> supplierPres.orderInfo();
                    case "2.20" -> supplierPres.agreementInfo();
                    case "2.21" -> supplierPres.allSuppliersID();
                    case "2.22" -> supplierPres.printOrdersForDelivery();
                    case "3.9" -> supplierPres.handleNewOrder();
                    case "3.10" -> supplierPres.deleteOrder();
                    case "3.11" -> supplierPres.updateOrder();
                    case "4.1" -> supplierPres.handleNewSupplier();
                    case "4.2" -> supplierPres.handleRemoveSupplier();
                    case "4.3" -> supplierPres.supplierInfo();
                    case "4.4" -> supplierPres.handleEditSupplier();
                    case "4.5" -> supplierPres.handleNewAgreement();
                    case "4.6" -> supplierPres.handleRemoveAgreement();
                    case "4.7" -> supplierPres.agreementInfo();
                    case "4.8" -> supplierPres.handleEditAgreement();
                    case "5.1" -> supplierPres.supplierUser();


                    // Storage
                    case "2.5"  -> storagePres.handleAddProduct();
                    case "2.6"  -> storagePres.handleDeleteProduct();
                    case "2.7"  -> storagePres.handleMoveProduct();
                    case "2.8"  -> storagePres.handleDamagedReport();
                    case "2.9"  -> storagePres.handleExpiredReport();
                    case "2.10" -> storagePres.handleApplySaleByCategory();
                    case "2.11" -> storagePres.handleApplySaleByProduct();
                    case "2.12" -> storagePres.handleAddDamaged();
                    case "2.13" -> storagePres.handleAddExpired();
                    case "2.14" -> storagePres.handleCategoryReport();
                    case "2.15" -> storagePres.handleSubCategoryReport();
                    case "2.16" -> storagePres.handleSubSubCategoryReport();
                    case "2.17" -> storagePres.handleAnnualOrder();
                    case "3.3" -> storagePres.handleAddProduct();
                    case "3.4" -> storagePres.handleDeleteProduct();
                    case "3.5" -> storagePres.handleMoveProduct();
                    case "3.6" -> storagePres.handleApplySaleByCategory();
                    case "3.7" -> storagePres.handleApplySaleByProduct();
                    case "3.8" -> storagePres.handleAddDamaged();



                    //General
                    case "0" -> {
                        currentUser = null;
                        hrAndDeliveryPres.handleResponse(new Response(true, "User logged out."));
                    }
                    case "1" -> hrAndDeliveryPres.handleResponse(setup());
                    case "2" -> hrAndDeliveryPres.handleResponse(reset());
                    default -> hrAndDeliveryPres.handleResponse(new Response(false, "Invalid option. Please try again."));
                };

            } catch (Exception e) {
                userOutput.print("An error occurred: " + e.getMessage() + ". Please try again.");
            }
        }
    }

    private Response setup() throws Exception {
        if(hrAndDeliveryPres.setup().isSuccess() && supplierPres.setup() && storagePres.setup()){
            return new Response(true, "Data loaded successfully.");
        }
        return new Response(false, "Unable to load data. Please try again.");
    }

    private Response reset() throws Exception {
        if(hrAndDeliveryPres.reset().isSuccess() && supplierPres.reset() && storagePres.reset()){
            return new Response(true, "Data reset successfully.");
        }
        return new Response(false, "Unable to reset data. Please try again.");
    }

    private EmployeePres handleLogin() throws Exception {
        Response response = new Response();

        while (!response.isSuccess()) {
            try {
                // Display the login page
                String id = logInPage.display();

                // Validate login credentials
                response = hrAndDeliveryPres.validateDet(id); //return full name + roles as a string

                if (response.isSuccess()) {
                    String[] employeeDet = response.getMessage().split("/");
                    if (employeeDet.length < 2) {
                        throw new IllegalArgumentException("Invalid employee details format.");
                    }

                    String fullName = employeeDet[0] ;
                    String role = employeeDet[1];

                    userOutput.print("Login successful!");
                    return new EmployeePres(fullName, id, role);
                } else {
                    hrAndDeliveryPres.handleResponse(response);
                }
            } catch (Exception e) {
                userOutput.print("Login failed due to error: " + e.getMessage());
            }
        }

        return null; // never reach this line
    }


}
