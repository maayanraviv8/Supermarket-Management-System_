package PresentationLayer.GUI;

import HRandDelivery.PresentationLayer.HRandDeliveryPresGUI;
import PresentationLayer.EmployeePres;
import Storage.Frontend.PresentationLayer.StoragePresGUI;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import HRandDelivery.Utils.Response;
import Storage.Frontend.PresentationLayer.StoragePresGUI;
import Supplier.Backend.presentationLayer.ScannerInputSupplier;
import Supplier.Backend.presentationLayer.SupplierPresGUI;

public class GUIPres extends Application {

    private Stage primaryStage;
    private EmployeePres currentUser;
    private MenuGUI menuGUI;

    private HRandDeliveryPresGUI hrAndDeliveryPresGUI;
    private SupplierPresGUI supplierPresGUI;
    private StoragePresGUI storagePresGUI;

    public GUIPres(EmployeePres currentUser, HRandDeliveryPresGUI hrAndDeliveryPres) {
        this.currentUser = currentUser;
        this.hrAndDeliveryPresGUI = hrAndDeliveryPres;

        try {
            this.supplierPresGUI = SupplierPresGUI.getInstance(null);
            this.storagePresGUI = StoragePresGUI.getInstance(primaryStage);
            this.menuGUI = new MenuGUI();
        } catch (Exception e) {
            showAlert("Initialization Error", "Failed to initialize modules: " + e.getMessage());
        }
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        createMainScene();
    }

    private void createMainScene() {
        // Create main layout
        BorderPane mainLayout = new BorderPane();

        // Top bar with user info and logout
        HBox topBar = createTopBar();

        // Left sidebar with menu options
        VBox leftSidebar = createLeftSidebar();


        ScrollPane menuScrollPane = new ScrollPane(leftSidebar);
        menuScrollPane.setFitToWidth(true);
        menuScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        menuScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);


        menuScrollPane.setStyle(
                "-fx-background: #34495e; " +
                        "-fx-background-color: #34495e;"
        );


        menuScrollPane.getStylesheets().add("data:text/css," +
                ".scroll-pane > .viewport { -fx-background-color: #34495e; }" +
                ".scroll-pane { -fx-background-color: #34495e; }" +
                ".scroll-bar:vertical { " +
                "-fx-background-color: #2c3e50; " +
                "-fx-pref-width: 10px; " +
                "}" +
                ".scroll-bar:vertical .track { -fx-background-color: #2c3e50; }" +
                ".scroll-bar:vertical .thumb { " +
                "-fx-background-color: #3498db; " +
                "-fx-background-radius: 5px; " +
                "}" +
                ".scroll-bar:vertical .thumb:hover { -fx-background-color: #2980b9; }"
        );

        // Center content area
        StackPane centerContent = new StackPane();
        centerContent.setStyle("-fx-background-color: #ecf0f1;");

        Label welcomeLabel = new Label("Welcome to Super-Lee Management System");
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #34495e;");
        centerContent.getChildren().add(welcomeLabel);

        // Set up main layout
        mainLayout.setTop(topBar);
        mainLayout.setLeft(menuScrollPane);
        mainLayout.setCenter(centerContent);

        // Create scene
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setTitle("Super-Lee Management System - " + currentUser.name());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: #2c3e50;");

        Label userLabel = new Label("Welcome, " + currentUser.name() + " (" + currentUser.getRole() + ")");
        userLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        logoutButton.setOnAction(e -> handleLogout());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topBar.getChildren().addAll(userLabel, spacer, logoutButton);
        return topBar;
    }

    private VBox createLeftSidebar() {
        VBox sidebar = new VBox(5);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #34495e;");
        sidebar.setPrefWidth(300);

        // Get menu options for current user role
        MenuOptions menuOptions = menuGUI.getMenuOptions(currentUser.getRole());

        // Create sections
        if (!menuOptions.hrOptions.isEmpty()) {
            sidebar.getChildren().add(createMenuSection("HR", menuOptions.hrOptions));
        }

        if (!menuOptions.deliveryOptions.isEmpty()) {
            sidebar.getChildren().add(createMenuSection("Delivery", menuOptions.deliveryOptions));
        }

        if (!menuOptions.storageOptions.isEmpty()) {
            sidebar.getChildren().add(createMenuSection("Storage", menuOptions.storageOptions));
        }

        if (!menuOptions.supplierOptions.isEmpty()) {
            sidebar.getChildren().add(createMenuSection("Supplier", menuOptions.supplierOptions));
        }

        if (!menuOptions.generalOptions.isEmpty()) {
            sidebar.getChildren().add(createMenuSection("General", menuOptions.generalOptions));
        }

        return sidebar;
    }

    private VBox createMenuSection(String sectionTitle, java.util.List<MenuOption> options) {
        VBox section = new VBox(3);
        section.setPadding(new Insets(10, 0, 15, 0));

        Label sectionLabel = new Label(sectionTitle);
        sectionLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        section.getChildren().add(sectionLabel);

        for (MenuOption option : options) {
            Button optionButton = new Button(option.display);
            optionButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 8;");
            optionButton.setPrefWidth(280);
            optionButton.setAlignment(Pos.CENTER_LEFT);

            optionButton.setOnAction(e -> handleMenuAction(option.code));

            optionButton.setOnMouseEntered(e -> optionButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 8;"));
            optionButton.setOnMouseExited(e -> optionButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 8;"));

            section.getChildren().add(optionButton);
        }

        return section;
    }

    private void handleMenuAction(String actionCode) {
        try {
            Response response = null;

            switch (actionCode) {
                // HR and Delivery
                case "1.1" -> hrAndDeliveryPresGUI.handleAddEmployeeGUI();
                case "1.2" -> hrAndDeliveryPresGUI.handleRemoveEmployeeGUI();
                case "1.3", "2.2" -> hrAndDeliveryPresGUI.handleViewEmployeeDetailsGUI();
                case "1.4" -> hrAndDeliveryPresGUI.handleUpdateEmployeeDetailsGUI();
                case "1.5" -> hrAndDeliveryPresGUI.handleSetShiftRequirementsGUI();
                case "1.6" -> hrAndDeliveryPresGUI.handleEmployeeShiftAssignmentGUI();
                case "1.7" -> hrAndDeliveryPresGUI.handleEditShiftGUI();
                case "1.8", "2.1" -> hrAndDeliveryPresGUI.handleViewShiftDetailsGUI();
                case "1.9" -> hrAndDeliveryPresGUI.handleSetShiftSubmissionDeadlineGUI();
                case "1.10" -> hrAndDeliveryPresGUI.handleAddDeliveryGUI();
                case "1.11" -> hrAndDeliveryPresGUI.handleAddTruckGUI();
                case "1.12" -> hrAndDeliveryPresGUI.handleAddBranchGUI();
                case "2.3", "3.1" -> hrAndDeliveryPresGUI.handleViewMyShiftsGUI(currentUser.getId());
                case "2.4", "3.2" -> hrAndDeliveryPresGUI.handleSubmitAvailabilityGUI(currentUser.getId());

                // Supplier
                case "2.18" -> supplierPresGUI.supplierInfoGUI();
                case "2.19" -> supplierPresGUI.orderInfoGUI();
                case "2.20" -> supplierPresGUI.agreementInfoGUI();
                case "2.21" -> supplierPresGUI.allSuppliersIDGUI();
                case "2.22" -> supplierPresGUI.printOrdersForDeliveryGUI();
                case "3.9" -> supplierPresGUI.handleNewOrderGUI();
                case "3.10" -> supplierPresGUI.deleteOrderGUI();
                case "3.11" -> supplierPresGUI.updateOrderGUI();
                case "4.1" -> supplierPresGUI.handleNewSupplierGUI();
                case "4.2" -> supplierPresGUI.handleRemoveSupplierGUI();
                case "4.3" -> supplierPresGUI.supplierInfoGUI();
                case "4.4" -> supplierPresGUI.handleEditSupplierGUI();
                case "4.5" -> supplierPresGUI.handleNewAgreementGUI();
                case "4.6" -> supplierPresGUI.handleRemoveAgreementGUI();
                case "4.7" -> supplierPresGUI.agreementInfoGUI();
                case "4.8" -> supplierPresGUI.handleEditAgreementGUI();
                case "5.1" -> supplierPresGUI.supplierUserGUI();

                // Storage
                case "2.5", "3.3" -> storagePresGUI.handleAddProduct();
                case "2.6", "3.4" -> storagePresGUI.handleDeleteProduct();
                case "2.7", "3.5" -> storagePresGUI.handleMoveProduct();
                case "2.8" -> storagePresGUI.handleDamagedReport();
                case "2.9" -> storagePresGUI.handleExpiredReport();
                case "2.10", "3.6" -> storagePresGUI.handleApplySaleByCategory();
                case "2.11", "3.7" -> storagePresGUI.handleApplySaleByProduct();
                case "2.12", "3.8" -> storagePresGUI.handleAddDamaged();
                case "2.13" -> storagePresGUI.handleAddExpired();
                case "2.14" -> storagePresGUI.handleCategoryReport();
                case "2.15" -> storagePresGUI.handleSubCategoryReport();
                case "2.16" -> storagePresGUI.handleSubSubCategoryReport();
                case "2.17" -> storagePresGUI.handleAnnualOrder();

                // General
                case "1" -> response = setup();
                case "2" -> response = reset();

                default -> response = new Response(false, "Invalid option. Please try again.");
            }

            if (response != null) {
                handleResponse(response);
            }

        } catch (Exception e) {
            showAlert("Error", "An error occurred: " + e.getMessage());
        }
    }

    private void handleResponse(Response response) {
        if (response.isSuccess()) {
            showAlert("Success", response.getMessage());
        } else {
            showAlert("Error", response.getMessage());
        }
    }

    private Response setup() throws Exception {
        if (hrAndDeliveryPresGUI.setup() &&
                supplierPresGUI.setup() &&
                storagePresGUI.setup()) {
            return new Response(true, "Data loaded successfully.");
        }
        return new Response(false, "Unable to load data. Please try again.");
    }

    private Response reset() throws Exception {
        if (hrAndDeliveryPresGUI.reset() &&
                supplierPresGUI.reset() &&
                storagePresGUI.reset()) {
            return new Response(true, "Data reset successfully.");
        }
        return new Response(false, "Unable to reset data. Please try again.");
    }

    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Are you sure you want to logout?");
        alert.setContentText("You will be returned to the login screen.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    LogInPageGUI loginPage = new LogInPageGUI();
                    Stage loginStage = new Stage();
                    loginPage.start(loginStage);

                    primaryStage.close();
                } catch (Exception e) {
                    showAlert("Error", "Failed to return to login: " + e.getMessage());
                }
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}