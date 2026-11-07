package PresentationLayer.GUI;

import HRandDelivery.PresentationLayer.HRandDeliveryPresGUI;
import PresentationLayer.EmployeePres;
import Supplier.Backend.presentationLayer.SupplierPresGUI;
import Supplier.Backend.serviceLayer.Message;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import HRandDelivery.Utils.Response;

public class LogInPageGUI extends Application {

    private Stage primaryStage;
    private HRandDeliveryPresGUI hrAandDeliveryPresGUI;
    private SupplierPresGUI supplierPresGUI;

    public LogInPageGUI() {
        try {
            this.hrAandDeliveryPresGUI = new HRandDeliveryPresGUI(null);
            this.supplierPresGUI = SupplierPresGUI.getInstance(null);
        } catch (Exception e) {
            showAlert("Initialization Error", "Failed to initialize system: " + e.getMessage());
        }
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        createLoginScene();
    }

    private void createLoginScene() {
        // Create main container
        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(50));
        mainContainer.setStyle("-fx-background-color: #f0f0f0;");

        // Title
        Label titleLabel = new Label("Welcome to the Super-Lee Management System :)");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Login form container
        VBox loginForm = new VBox(15);
        loginForm.setAlignment(Pos.CENTER);
        loginForm.setMaxWidth(400);
        loginForm.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 30;");

        // ID input
        Label idLabel = new Label("Employee ID / Supplier ID:");
        idLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TextField idField = new TextField();
        idField.setPromptText("Enter your ID");
        idField.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
        idField.setPrefWidth(300);

        // Login button
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12 30;");
        loginButton.setPrefWidth(150);

        // Status label for messages
        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px;");

        // Event handlers
        loginButton.setOnAction(e -> handleLogin(idField.getText().trim(), statusLabel));

        idField.setOnAction(e -> handleLogin(idField.getText().trim(), statusLabel));

        // Add components to login form
        loginForm.getChildren().addAll(idLabel, idField, loginButton, statusLabel);

        // Add components to main container
        mainContainer.getChildren().addAll(titleLabel, loginForm);

        // Create scene
        Scene scene = new Scene(mainContainer, 800, 600);
        primaryStage.setTitle("Super-Lee Management System - Login");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        // Focus on ID field
        idField.requestFocus();
    }

    private void handleLogin(String id, Label statusLabel) {
        if (id.isEmpty()) {
            statusLabel.setText("Please enter your ID");
            return;
        }

        try {
            Response response = hrAandDeliveryPresGUI.validateDet(id);

            if (response.isSuccess()) {
                String[] employeeDet = response.getMessage().split("/");
                if (employeeDet.length < 2) {
                    statusLabel.setText("Invalid details format");
                    return;
                }

                String fullName = employeeDet[0];
                String role = employeeDet[1];

                // Create employee object
                EmployeePres currentUser = new EmployeePres(fullName, id, role);

                // Show success message
                showAlert("Login Successful", "Welcome, " + fullName + "!");

                // Open main application window
                openMainApplication(currentUser);

            } else {

                Message message = supplierPresGUI.validateDet(id);
                if (message.isSuccess()) {
                    String supplierName = message.getMessage().replace(" exists", "");

                    // Create employee object
                    EmployeePres currentUser = new EmployeePres(supplierName, id, "SUPPLIER");

                    // Show success message
                    showAlert("Login Successful", "Welcome, " + supplierName + "!");

                    // Open main application window
                    openMainApplication(currentUser);
                }

                else {
                    statusLabel.setText("Employee/Supplier not found. Please try again.");
                }
            }
        } catch (Exception e) {
            statusLabel.setText("Login failed: " + e.getMessage());
        }
    }

    private void openMainApplication(EmployeePres currentUser) {
        try {
            GUIPres mainApp = new GUIPres(currentUser, hrAandDeliveryPresGUI);
            Stage mainStage = new Stage();
            mainApp.start(mainStage);

            // Close login window
            primaryStage.close();
        } catch (Exception e) {
            showAlert("Error", "Failed to open main application: " + e.getMessage());
        }
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