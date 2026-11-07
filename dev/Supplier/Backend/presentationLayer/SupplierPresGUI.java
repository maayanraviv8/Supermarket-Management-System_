package Supplier.Backend.presentationLayer;

import Supplier.Backend.DTO.AgreementDTO;
import Supplier.Backend.DTO.OrderDTO;
import Supplier.Backend.DTO.ProductDTO;
import Supplier.Backend.DTO.SupplierDTO;
import Supplier.Backend.domainLayer.Payment;
import Supplier.Backend.serviceLayer.Message;
import Supplier.Backend.serviceLayer.SupplierService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.*;

public class SupplierPresGUI {
    private static SupplierPresGUI instance = null;
    private static Stage parentStage;

    private SupplierService supplierService;

    private SupplierPresGUI(Stage stage) throws Exception {
        this.parentStage = stage;
        this.supplierService = SupplierService.getInstance();
    }

    public static SupplierPresGUI getInstance(Stage stage) throws Exception {
        if (instance == null) {
            instance = new SupplierPresGUI(stage);
        }
        return instance;
    }

    public boolean setup(){
        try {
            supplierService.init();
            supplierService.loadSuppliers();
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

    public void supplierInfoGUI() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(parentStage);
        dialog.setTitle("Supplier Information");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        ComboBox<String> supplierCombo = new ComboBox<>();
        loadSuppliers(supplierCombo);

        ComboBox<String> detailCombo = new ComboBox<>();
        detailCombo.getItems().addAll(
                "1 - Basic Supplier Info",
                "2 - Bank Account",
                "3 - Payment Info",
                "4 - Supply Days",
                "5 - Address",
                "6 - Delivery Status",
                "7 - Contacts",
                "8 - All Products in Catalog",
                "9 - All Ordered Products",
                "10 - Agreement IDs",
                "11 - All Order IDs"
        );

        Button viewButton = new Button("View Information");
        Button cancelButton = new Button("Cancel");

        viewButton.setOnAction(e -> {
            String selectedSupplier = supplierCombo.getValue();
            String selectedDetail = detailCombo.getValue();
            if (selectedSupplier != null && selectedDetail != null) {
                try {
                    long supplierID = extractSupplierId(selectedSupplier);
                    String detailChoice = selectedDetail.substring(0, selectedDetail.indexOf(" "));
                    Message msg = null;
                    switch (detailChoice) {
                        case "1" -> msg = supplierService.getSupplier(supplierID);
                        case "2" -> msg = supplierService.getBankAccount(supplierID);
                        case "3" -> msg = supplierService.getPayment(supplierID);
                        case "4" -> msg = supplierService.getSupplyDay(supplierID);
                        case "5" -> msg = supplierService.getAddress(supplierID);
                        case "6" -> msg = supplierService.isDelivery(supplierID);
                        case "7" -> msg = supplierService.getContacts(supplierID);
                        case "8" -> msg = supplierService.getAllProductsInCatalog(supplierID);
                        case "9" -> msg = supplierService.allOrderedProducts(supplierID);
                        case "10" -> msg = supplierService.getAgreementsID(supplierID);
                        case "11" -> msg = supplierService.getAllOrdersIDs(supplierID);
                    }
                    if (msg != null) {
                        showDetailsDialog("Supplier Information", msg.toString());
                    } else {
                        showErrorDialog("Unexpected error occurred.");
                    }
                } catch (Exception ex) {
                    showErrorDialog("Error retrieving supplier information: " + ex.getMessage());
                }
            } else {
                showErrorDialog("Please select both supplier and information type.");
            }
        });

        cancelButton.setOnAction(e -> dialog.close());

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(viewButton, cancelButton);

        layout.getChildren().addAll(
                new Label("Select Supplier:"), supplierCombo,
                new Label("Select Information Type:"), detailCombo,
                buttonBox
        );

        Scene scene = new Scene(layout, 500, 300);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    public void orderInfoGUI() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(parentStage);
        dialog.setTitle("Order Information");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        ComboBox<String> supplierCombo = new ComboBox<>();
        loadSuppliers(supplierCombo);

        TextField orderIdField = new TextField();
        orderIdField.setPromptText("Enter Order ID");

        ComboBox<String> orderDetailCombo = new ComboBox<>();
        orderDetailCombo.getItems().addAll(
                "1 - Order Details",
                "2 - Products in Order",
                "3 - Contacts in Order",
                "4 - Order Date",
                "5 - Total Order Price"
        );

        Button viewButton = new Button("View Order Info");
        Button cancelButton = new Button("Cancel");

        viewButton.setOnAction(e -> {
            String selectedSupplier = supplierCombo.getValue();
            String orderIdText = orderIdField.getText().trim();
            String selectedDetail = orderDetailCombo.getValue();
            if (selectedSupplier != null && !orderIdText.isEmpty() && selectedDetail != null) {
                try {
                    long supplierID = extractSupplierId(selectedSupplier);
                    int orderID = Integer.parseInt(orderIdText);
                    String detailChoice = selectedDetail.substring(0, selectedDetail.indexOf(" "));
                    Message msg = null;
                    switch (detailChoice) {
                        case "1" -> msg = supplierService.getOrder(supplierID, orderID);
                        case "2" -> msg = supplierService.getProductsInOrder(supplierID, orderID);
                        case "3" -> msg = supplierService.getContactsInOrder(supplierID, orderID);
                        case "4" -> msg = supplierService.getOrderDateInOrder(supplierID, orderID);
                        case "5" -> msg = supplierService.getTotalOrderPrice(supplierID, orderID);
                    }
                    if (msg != null) {
                        if (msg.isSuccess()) {
                            showDetailsDialog("Order Information", msg.toString());
                        } else {
                            showErrorDialog("Error: " + msg.getMessage());
                        }
                    } else {
                        showErrorDialog("Unexpected error occurred.");
                    }
                } catch (NumberFormatException ex) {
                    showErrorDialog("Please enter a valid numeric Order ID.");
                } catch (Exception ex) {
                    showErrorDialog("Error retrieving order information: " + ex.getMessage());
                }
            } else {
                showErrorDialog("Please fill in all fields.");
            }
        });

        cancelButton.setOnAction(e -> dialog.close());

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(viewButton, cancelButton);

        layout.getChildren().addAll(
                new Label("Select Supplier:"), supplierCombo,
                new Label("Order ID:"), orderIdField,
                new Label("Select Order Detail:"), orderDetailCombo,
                buttonBox
        );

        Scene scene = new Scene(layout, 500, 350);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    public void agreementInfoGUI() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(parentStage);
        dialog.setTitle("Agreement Information");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        ComboBox<String> supplierCombo = new ComboBox<>();
        loadSuppliers(supplierCombo);

        TextField agreementIdField = new TextField();
        agreementIdField.setPromptText("Enter Agreement ID");

        ComboBox<String> agreementDetailCombo = new ComboBox<>();
        agreementDetailCombo.getItems().addAll(
                "1 - Agreement Details",
                "2 - Contacts in Agreement",
                "3 - Products in Agreement"
        );

        Button viewButton = new Button("View Agreement Info");
        Button cancelButton = new Button("Cancel");

        viewButton.setOnAction(e -> {
            String selectedSupplier = supplierCombo.getValue();
            String agreementIdText = agreementIdField.getText().trim();
            String selectedDetail = agreementDetailCombo.getValue();
            if (selectedSupplier != null && !agreementIdText.isEmpty() && selectedDetail != null) {
                try {
                    long supplierID = extractSupplierId(selectedSupplier);
                    int agreementID = Integer.parseInt(agreementIdText);
                    String detailChoice = selectedDetail.substring(0, selectedDetail.indexOf(" "));
                    Message msg = null;
                    switch (detailChoice) {
                        case "1" -> msg = supplierService.getAgreement(supplierID, agreementID);
                        case "2" -> msg = supplierService.getContacts(supplierID, agreementID);
                        case "3" -> msg = supplierService.getProductsInAgreement(supplierID, agreementID);
                    }
                    if (msg != null) {
                        showDetailsDialog("Agreement Information", msg.toString());
                    } else {
                        showErrorDialog("Unexpected error occurred.");
                    }
                } catch (NumberFormatException ex) {
                    showErrorDialog("Please enter a valid numeric Agreement ID.");
                } catch (Exception ex) {
                    showErrorDialog("Error retrieving agreement information: " + ex.getMessage());
                }
            } else {
                showErrorDialog("Please fill in all fields.");
            }
        });

        cancelButton.setOnAction(e -> dialog.close());

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(viewButton, cancelButton);

        layout.getChildren().addAll(
                new Label("Select Supplier:"), supplierCombo,
                new Label("Agreement ID:"), agreementIdField,
                new Label("Select Agreement Detail:"), agreementDetailCombo,
                buttonBox
        );

        Scene scene = new Scene(layout, 500, 300);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    public void allSuppliersIDGUI() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(parentStage);
        dialog.setTitle("All Supplier IDs");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        TextArea supplierList = new TextArea();
        supplierList.setEditable(false);
        supplierList.setPrefRowCount(15);
        supplierList.setPrefColumnCount(40);

        Button refreshButton = new Button("Refresh List");
        Button closeButton = new Button("Close");

        loadSupplierIDs(supplierList);

        refreshButton.setOnAction(e -> loadSupplierIDs(supplierList));
        closeButton.setOnAction(e -> dialog.close());

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(refreshButton, closeButton);

        layout.getChildren().addAll(
                new Label("All Supplier IDs:"),
                supplierList,
                buttonBox
        );

        Scene scene = new Scene(layout, 400, 450);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    public void printOrdersForDeliveryGUI() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(parentStage);
        dialog.setTitle("Orders for Delivery");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        TextArea ordersList = new TextArea();
        ordersList.setEditable(false);
        ordersList.setPrefRowCount(20);
        ordersList.setPrefColumnCount(60);

        Button refreshButton = new Button("Refresh Orders");
        Button closeButton = new Button("Close");

        loadOrdersForDelivery(ordersList);

        refreshButton.setOnAction(e -> loadOrdersForDelivery(ordersList));
        closeButton.setOnAction(e -> dialog.close());

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(refreshButton, closeButton);

        layout.getChildren().addAll(
                new Label("Orders Ready for Delivery:"),
                ordersList,
                buttonBox
        );

        Scene scene = new Scene(layout, 600, 500);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    public void handleNewOrderGUI() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(parentStage);
        dialog.setTitle("Create New Order");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        ComboBox<String> supplierCombo = new ComboBox<>();
        loadSuppliers(supplierCombo);

        DatePicker orderDatePicker = new DatePicker();
        orderDatePicker.setValue(LocalDate.now());

        VBox productsSection = new VBox(10);
        productsSection.setPadding(new Insets(10));
        productsSection.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5;");

        Label productsLabel = new Label("Products:");
        productsLabel.setStyle("-fx-font-weight: bold;");

        TextArea productsDisplay = new TextArea();
        productsDisplay.setEditable(false);
        productsDisplay.setPrefRowCount(5);
        productsDisplay.setPromptText("No products added yet");

        HBox productEntry = new HBox(10);
        TextField catalogNumberField = new TextField();
        catalogNumberField.setPromptText("Catalog Number");
        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");
        Button addProductButton = new Button("Add Product");
        Button clearProductsButton = new Button("Clear All");

        productEntry.getChildren().addAll(
                new Label("Catalog:"), catalogNumberField,
                new Label("Quantity:"), quantityField,
                addProductButton, clearProductsButton
        );

        TextField phoneField = new TextField();
        phoneField.setPromptText("10-digit phone number");

        Map<String, Integer> products = new HashMap<>();

        addProductButton.setOnAction(e -> {
            String catalogNumber = catalogNumberField.getText().trim();
            String quantityStr = quantityField.getText().trim();
            if (catalogNumber.isEmpty()) {
                showErrorDialog("Catalog number cannot be empty.");
                return;
            }
            if (quantityStr.isEmpty()) {
                showErrorDialog("Quantity cannot be empty.");
                return;
            }
            try {
                int quantity = Integer.parseInt(quantityStr);
                if (quantity < 0) {
                    showErrorDialog("Quantity must be a positive number.");
                    return;
                }
                products.put(catalogNumber, quantity);
                updateProductsDisplay(productsDisplay, products);
                catalogNumberField.clear();
                quantityField.clear();
            } catch (NumberFormatException ex) {
                showErrorDialog("Please enter a valid numeric quantity.");
            }
        });

        clearProductsButton.setOnAction(e -> {
            products.clear();
            updateProductsDisplay(productsDisplay, products);
        });

        productsSection.getChildren().addAll(productsLabel, productsDisplay, productEntry);

        Button createButton = new Button("Create Order");
        Button cancelButton = new Button("Cancel");

        createButton.setOnAction(e -> {
            String selectedSupplier = supplierCombo.getValue();
            LocalDate selectedDate = orderDatePicker.getValue();
            String phoneNumber = phoneField.getText().trim();

            if (selectedSupplier == null) {
                showErrorDialog("Please select a supplier.");
                return;
            }
            if (selectedDate == null) {
                showErrorDialog("Please select an order date.");
                return;
            }
            if (products.isEmpty()) {
                showErrorDialog("Please add at least one product.");
                return;
            }
            if (phoneNumber.isEmpty()) {
                showErrorDialog("Please enter a phone number.");
                return;
            }
            if (!phoneNumber.matches("\\d{10}")) {
                showErrorDialog("Phone number must be exactly 10 digits.");
                return;
            }

            try {
                long supplierID = extractSupplierId(selectedSupplier);
                OrderDTO orderDTO = new OrderDTO(selectedDate, products, phoneNumber);
                Message msg = supplierService.addOrder(orderDTO, supplierID);
                if (msg != null) {
                    if (msg.isSuccess()) {
                        showSuccessDialog("Order created successfully!", msg.getMessage());
                        dialog.close();
                    } else {
                        showErrorDialog("Error creating order: " + msg.getMessage());
                    }
                } else {
                    showErrorDialog("Unexpected error occurred.");
                }
            } catch (Exception ex) {
                showErrorDialog("Error creating order: " + ex.getMessage());
            }
        });

        cancelButton.setOnAction(e -> dialog.close());

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(createButton, cancelButton);

        layout.getChildren().addAll(
                new Label("Select Supplier:"), supplierCombo,
                new Label("Order Date:"), orderDatePicker,
                productsSection,
                new Label("Phone Number:"), phoneField,
                buttonBox
        );

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefSize(600, 500);

        Scene scene = new Scene(scrollPane, 600, 500);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    public void deleteOrderGUI() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(parentStage);
        dialog.setTitle("Delete Order");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        ComboBox<String> supplierCombo = new ComboBox<>();
        loadSuppliers(supplierCombo);

        TextField orderIdField = new TextField();
        orderIdField.setPromptText("Enter Order ID");

        Label warningLabel = new Label("This action cannot be undone!");
        warningLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        Button deleteButton = new Button("Delete Order");
        deleteButton.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white;");
        Button cancelButton = new Button("Cancel");

        deleteButton.setOnAction(e -> {
            String selectedSupplier = supplierCombo.getValue();
            String orderIdText = orderIdField.getText().trim();

            if (selectedSupplier == null) {
                showErrorDialog("Please select a supplier.");
                return;
            }
            if (orderIdText.isEmpty()) {
                showErrorDialog("Please enter an Order ID.");
                return;
            }

            try {
                long supplierID = extractSupplierId(selectedSupplier);
                int orderID = Integer.parseInt(orderIdText);

                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.initOwner(dialog);
                confirmAlert.setTitle("Confirm Delete");
                confirmAlert.setHeaderText("Delete Order Confirmation");
                confirmAlert.setContentText("Are you sure you want to delete Order ID: " + orderID +
                        " for Supplier ID: " + supplierID + "?");
                Optional<ButtonType> result = confirmAlert.showAndWait();

                if (result.isPresent() && result.get() == ButtonType.OK) {
                    Message msg = supplierService.removeOrder(supplierID, orderID);
                    if (msg != null) {
                        if (msg.isSuccess()) {
                            showSuccessDialog("Order deleted successfully!", msg.getMessage());
                            dialog.close();
                        } else {
                            showErrorDialog("Error deleting order: " + msg.getMessage());
                        }
                    } else {
                        showErrorDialog("Unexpected error occurred.");
                    }
                }
            } catch (NumberFormatException ex) {
                showErrorDialog("Please enter a valid numeric Order ID.");
            } catch (Exception ex) {
                showErrorDialog("Error deleting order: " + ex.getMessage());
            }
        });

        cancelButton.setOnAction(e -> dialog.close());

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(deleteButton, cancelButton);

        layout.getChildren().addAll(
                new Label("Select Supplier:"), supplierCombo,
                new Label("Order ID:"), orderIdField,
                warningLabel,
                buttonBox
        );

        Scene scene = new Scene(layout, 400, 250);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    public void updateOrderGUI() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(parentStage);
        dialog.setTitle("Update Order");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        ComboBox<String> supplierCombo = new ComboBox<>();
        loadSuppliers(supplierCombo);

        TextField orderIdField = new TextField();
        orderIdField.setPromptText("Enter Order ID");

        ComboBox<String> updateTypeCombo = new ComboBox<>();
        updateTypeCombo.getItems().addAll(
                "1 - Update Phone Number",
                "2 - Mark Order as Done"
        );

        VBox phoneSection = new VBox(5);
        Label phoneLabel = new Label("New Phone Number:");
        TextField phoneField = new TextField();
        phoneField.setPromptText("10-digit phone number");
        phoneSection.getChildren().addAll(phoneLabel, phoneField);
        phoneSection.setVisible(false);

        updateTypeCombo.setOnAction(e -> {
            String selected = updateTypeCombo.getValue();
            if (selected != null && selected.startsWith("1")) {
                phoneSection.setVisible(true);
            } else {
                phoneSection.setVisible(false);
                phoneField.clear();
            }
        });

        Button updateButton = new Button("Update Order");
        Button cancelButton = new Button("Cancel");

        updateButton.setOnAction(e -> {
            String selectedSupplier = supplierCombo.getValue();
            String orderIdText = orderIdField.getText().trim();
            String selectedUpdateType = updateTypeCombo.getValue();

            if (selectedSupplier == null) {
                showErrorDialog("Please select a supplier.");
                return;
            }
            if (orderIdText.isEmpty()) {
                showErrorDialog("Please enter an Order ID.");
                return;
            }
            if (selectedUpdateType == null) {
                showErrorDialog("Please select an update type.");
                return;
            }

            try {
                long supplierID = extractSupplierId(selectedSupplier);
                int orderID = Integer.parseInt(orderIdText);
                String updateChoice = selectedUpdateType.substring(0, 1);

                Message msg = null;
                switch (updateChoice) {
                    case "1": {
                        String phoneNumber = phoneField.getText().trim();
                        if (phoneNumber.isEmpty()) {
                            showErrorDialog("Please enter a phone number.");
                            return;
                        }
                        if (!phoneNumber.matches("/d{10}")) {
                            showErrorDialog("Phone number must be exactly 10 digits.");
                            return;
                        }
                        msg = supplierService.updatePhoneNumberInOrder(supplierID, orderID, phoneNumber);
                        break;
                    }
                    case "2": {
                        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                        confirmAlert.initOwner(dialog);
                        confirmAlert.setTitle("Confirm Update");
                        confirmAlert.setHeaderText("Mark Order as Done");
                        confirmAlert.setContentText("Are you sure you want to mark Order ID: " + orderID + " as done?");
                        Optional<ButtonType> result = confirmAlert.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            msg = supplierService.orderIsDone(supplierID, orderID);
                        } else {
                            return;
                        }
                        break;
                    }
                }

                if (msg != null) {
                    if (msg.isSuccess()) {
                        showSuccessDialog("Order updated successfully!", msg.getMessage());
                        dialog.close();
                    } else {
                        showErrorDialog("Error updating order: " + msg.getMessage());
                    }
                } else {
                    showErrorDialog("Unexpected error occurred.");
                }
            } catch (NumberFormatException ex) {
                showErrorDialog("Please enter a valid numeric Order ID.");
            } catch (Exception ex) {
                showErrorDialog("Error updating order: " + ex.getMessage());
            }
        });

        cancelButton.setOnAction(e -> dialog.close());

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(updateButton, cancelButton);

        layout.getChildren().addAll(
                new Label("Select Supplier:"), supplierCombo,
                new Label("Order ID:"), orderIdField,
                new Label("Update Type:"), updateTypeCombo,
                phoneSection,
                buttonBox
        );

        Scene scene = new Scene(layout, 450, 350);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    public void handleNewSupplierGUI() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(parentStage);
        dialog.setTitle("Add New Supplier");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_LEFT);
        layout.setFillWidth(true);

        TextField nameField = new TextField(); nameField.setPromptText("Supplier Name");
        TextField supplierIdField = new TextField(); supplierIdField.setPromptText("Supplier ID");
        TextField bankAccountField = new TextField(); bankAccountField.setPromptText("Bank Account");
        TextField supplyDayField = new TextField(); supplyDayField.setPromptText("Supply Day (0-7)");

        ComboBox<String> paymentMethodCombo = new ComboBox<>();
        paymentMethodCombo.getItems().addAll("CASH", "CHECK", "BANK_TRANSFER");
        paymentMethodCombo.setPromptText("Select Payment Method");

        ComboBox<String> paymentTermCombo = new ComboBox<>();
        paymentTermCombo.getItems().addAll("IN_ADVANCE", "UPON_DELIVERY", "BEGINNING_OF_MONTH", "END_OF_MONTH");
        paymentTermCombo.setPromptText("Select Payment Term");

        TextField addressField = new TextField(); addressField.setPromptText("Address");

        ComboBox<String> deliveryCombo = new ComboBox<>();
        deliveryCombo.getItems().addAll("true", "false");
        deliveryCombo.setPromptText("Delivery Option");

        VBox contactsBox = new VBox(10);
        Label contactsLabel = new Label("Contacts:"); contactsLabel.setStyle("-fx-font-weight: bold;");
        contactsBox.getChildren().add(contactsLabel);
        Button addContactButton = new Button("Add Contact");
        List<ContactEntry> contacts = new ArrayList<>();

        Button submitButton = new Button("Add Supplier");
        Button cancelButton = new Button("Cancel");
        HBox buttonBox = new HBox(10, submitButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(
                new Label("Supplier Name:"), nameField,
                new Label("Supplier ID:"), supplierIdField,
                new Label("Bank Account:"), bankAccountField,
                new Label("Supply Day (0-7):"), supplyDayField,
                new Label("Payment Method:"), paymentMethodCombo,
                new Label("Payment Term:"), paymentTermCombo,
                new Label("Address:"), addressField,
                new Label("Delivery Option:"), deliveryCombo,
                contactsBox, addContactButton,
                buttonBox
        );

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPrefViewportHeight(650);
        layout.setMinHeight(1500);

        BorderPane root = new BorderPane();
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 550, 800);
        dialog.setScene(scene);

        addContactButton.setOnAction(e -> {
            ContactEntry c = new ContactEntry();
            contacts.add(c);
            contactsBox.getChildren().add(c.getLayout());
            Platform.runLater(() -> scrollPane.setVvalue(1.0));
        });

        submitButton.setOnAction(e -> {
            try {
                if (nameField.getText().trim().isEmpty() || supplierIdField.getText().trim().isEmpty()
                        || bankAccountField.getText().trim().isEmpty() || supplyDayField.getText().trim().isEmpty()
                        || paymentMethodCombo.getValue() == null || paymentTermCombo.getValue() == null
                        || addressField.getText().trim().isEmpty() || deliveryCombo.getValue() == null) {
                    showErrorDialog("Please fill in all fields.");
                    return;
                }

                long supplierId;
                try {
                    supplierId = Long.parseLong(supplierIdField.getText().trim());
                } catch (NumberFormatException ex) {
                    showErrorDialog("Invalid supplier ID");
                    return;
                }

                Message existsMsg = supplierService.isSupplierExists(supplierId);
                if (existsMsg.isSuccess()) {
                    showErrorDialog("Supplier already exists.");
                    return;
                }

                int supplyDay;
                try {
                    supplyDay = Integer.parseInt(supplyDayField.getText().trim());
                    if (supplyDay < 0 || supplyDay > 7) {
                        showErrorDialog("Supply day must be between 0–7");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    showErrorDialog("Invalid supply day");
                    return;
                }

                for (ContactEntry c : contacts)
                    if (!c.isValid()) {
                        showErrorDialog("Invalid contact info");
                        return;
                    }

                SupplierDTO supplier = new SupplierDTO(
                        nameField.getText().trim(),
                        supplierId,
                        bankAccountField.getText().trim(),
                        supplyDay,
                        Payment.PaymentMethod.valueOf(paymentMethodCombo.getValue()),
                        Payment.PaymentTerm.valueOf(paymentTermCombo.getValue()),
                        addressField.getText().trim(),
                        Boolean.parseBoolean(deliveryCombo.getValue())
                );

                for (ContactEntry c : contacts)
                    supplier.addContact(c.getName(), c.getPhone());

                Message msg = supplierService.addSupplier(supplier);
                if (msg.isSuccess()) {
                    showSuccessDialog("Supplier added successfully");
                    dialog.close();
                } else showErrorDialog("Failed to add supplier: " + msg.getMessage());
            } catch (Exception ex) {
                showErrorDialog("Error adding supplier: " + ex.getMessage());
            }
        });

        cancelButton.setOnAction(e -> dialog.close());

        dialog.showAndWait();
    }
    public void handleRemoveSupplierGUI() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(parentStage);
        dialog.setTitle("Remove Supplier");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER_LEFT);
        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPrefViewportHeight(600);
        scrollPane.setPrefViewportWidth(580);

        TextField supplierIdField = new TextField();
        supplierIdField.setPromptText("Enter Supplier ID to remove");

        Button removeButton = new Button("Remove Supplier");
        Button cancelButton = new Button("Cancel");

        removeButton.setOnAction(e -> {
            try {
                String supplierIdText = supplierIdField.getText().trim();

                if (supplierIdText.isEmpty()) {
                    showErrorDialog("Supplier ID cannot be empty");
                    return;
                }

                long supplierId;
                try {
                    supplierId = Long.parseLong(supplierIdText);
                } catch (NumberFormatException ex) {
                    showErrorDialog("Invalid supplier ID. Please enter a valid numeric ID");
                    return;
                }

                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirm Deletion");
                confirmAlert.setHeaderText("Remove Supplier");
                confirmAlert.setContentText("Are you sure you want to remove supplier with ID: " + supplierId + "?");

                Optional<ButtonType> result = confirmAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    Message msg = supplierService.deleteSupplier(supplierId);

                    if (msg != null) {
                        if (msg.isSuccess()) {
                            showSuccessDialog("Supplier removed successfully");
                            dialog.close();
                        } else {
                            showErrorDialog("Failed to remove supplier: " + msg.getMessage());
                        }
                    } else {
                        showErrorDialog("Unexpected error occurred");
                    }
                }

            } catch (Exception ex) {
                showErrorDialog("Error removing supplier: " + ex.getMessage());
            }
        });

        cancelButton.setOnAction(e -> dialog.close());

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(removeButton, cancelButton);

        layout.getChildren().addAll(
                new Label("Supplier ID:"), supplierIdField,
                buttonBox
        );
        Scene scene = new Scene(layout, 400, 200);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    public void handleEditSupplierGUI() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(parentStage);
        dialog.setTitle("Edit Supplier");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER_LEFT);
        TextField supplierIdField = new TextField();
        supplierIdField.setPromptText("Enter Supplier ID to edit");

        Button loadSupplierButton = new Button("Load Supplier");
        VBox editOptionsBox = new VBox(10);
        editOptionsBox.setVisible(false);

        ComboBox<String> editOptionCombo = new ComboBox<>();
        editOptionCombo.getItems().addAll(
                "1 - Update Name",
                "2 - Update Bank Account",
                "3 - Update Supply Day",
                "4 - Update Payment Method",
                "5 - Update Payment Term",
                "6 - Update Address",
                "7 - Update Delivery Option",
                "8 - Add Contact"
        );
        editOptionCombo.setPromptText("Select what to edit");
        VBox inputBox = new VBox(10);
        loadSupplierButton.setOnAction(e -> {
            try {
                String supplierIdText = supplierIdField.getText().trim();

                if (supplierIdText.isEmpty()) {
                    showErrorDialog("Supplier ID cannot be empty");
                    return;
                }
                long supplierId;
                try {
                    supplierId = Long.parseLong(supplierIdText);
                } catch (NumberFormatException ex) {
                    showErrorDialog("Invalid supplier ID. Please enter a valid numeric ID");
                    return;
                }
                Message existsMsg = supplierService.isSupplierExists(supplierId);
                if (!existsMsg.isSuccess()) {
                    showErrorDialog("Supplier with ID " + supplierId + " does not exist");
                    return;
                }
                editOptionsBox.setVisible(true);
                supplierIdField.setEditable(false);
                loadSupplierButton.setDisable(true);

            } catch (Exception ex) {
                showErrorDialog("Error loading supplier: " + ex.getMessage());
            }
        });

        editOptionCombo.setOnAction(e -> {
            inputBox.getChildren().clear();
            String selectedOption = editOptionCombo.getValue();

            if (selectedOption != null) {
                switch (selectedOption.substring(0, 1)) {
                    case "1": // Name
                        TextField nameField = new TextField();
                        nameField.setPromptText("Enter new name");
                        inputBox.getChildren().addAll(new Label("New Name:"), nameField);
                        break;
                    case "2": // Bank Account
                        TextField bankField = new TextField();
                        bankField.setPromptText("Enter new bank account");
                        inputBox.getChildren().addAll(new Label("New Bank Account:"), bankField);
                        break;
                    case "3": // Supply Day
                        TextField supplyDayField = new TextField();
                        supplyDayField.setPromptText("Enter supply day (0-7)");
                        inputBox.getChildren().addAll(new Label("New Supply Day (0-7):"), supplyDayField);
                        break;
                    case "4": // Payment Method
                        ComboBox<String> paymentMethodCombo = new ComboBox<>();
                        paymentMethodCombo.getItems().addAll("CASH", "CHECK", "BANK_TRANSFER");
                        inputBox.getChildren().addAll(new Label("New Payment Method:"), paymentMethodCombo);
                        break;
                    case "5": // Payment Term
                        ComboBox<String> paymentTermCombo = new ComboBox<>();
                        paymentTermCombo.getItems().addAll("IN_ADVANCE", "UPON_DELIVERY", "BEGINNING_OF_MONTH", "END_OF_MONTH");
                        inputBox.getChildren().addAll(new Label("New Payment Term:"), paymentTermCombo);
                        break;
                    case "6": // Address
                        TextField addressField = new TextField();
                        addressField.setPromptText("Enter new address");
                        inputBox.getChildren().addAll(new Label("New Address:"), addressField);
                        break;
                    case "7": // Delivery Option
                        ComboBox<String> deliveryCombo = new ComboBox<>();
                        deliveryCombo.getItems().addAll("true", "false");
                        inputBox.getChildren().addAll(new Label("New Delivery Option:"), deliveryCombo);
                        break;
                    case "8": // Add Contact
                        TextField contactNameField = new TextField();
                        contactNameField.setPromptText("Contact name");
                        TextField contactPhoneField = new TextField();
                        contactPhoneField.setPromptText("Contact phone (10 digits)");
                        inputBox.getChildren().addAll(
                                new Label("Contact Name:"), contactNameField,
                                new Label("Contact Phone:"), contactPhoneField
                        );
                        break;
                }
            }
        });

        Button updateButton = new Button("Update");
        Button cancelButton = new Button("Cancel");

        updateButton.setOnAction(e -> {
            try {
                long supplierId = Long.parseLong(supplierIdField.getText().trim());
                String selectedOption = editOptionCombo.getValue();

                if (selectedOption == null) {
                    showErrorDialog("Please select an option to edit");
                    return;
                }

                Message msg = null;
                String optionCode = selectedOption.substring(0, 1);

                switch (optionCode) {
                    case "1": // Name
                        TextField nameField = (TextField) inputBox.getChildren().get(1);
                        String newName = nameField.getText().trim();
                        if (newName.isEmpty()) {
                            showErrorDialog("Name cannot be empty");
                            return;
                        }
                        msg = supplierService.updateName(supplierId, newName);
                        break;
                    case "2": // Bank Account
                        TextField bankField = (TextField) inputBox.getChildren().get(1);
                        String newBank = bankField.getText().trim();
                        if (newBank.isEmpty()) {
                            showErrorDialog("Bank account cannot be empty");
                            return;
                        }
                        msg = supplierService.updateBankAccount(supplierId, newBank);
                        break;
                    case "3": // Supply Day
                        TextField supplyDayField = (TextField) inputBox.getChildren().get(1);
                        String supplyDayText = supplyDayField.getText().trim();
                        if (supplyDayText.isEmpty()) {
                            showErrorDialog("Supply day cannot be empty");
                            return;
                        }
                        try {
                            int supplyDay = Integer.parseInt(supplyDayText);
                            if (supplyDay < 0 || supplyDay > 7) {
                                showErrorDialog("Invalid supply day. Please enter a valid supply day (0-7)");
                                return;
                            }
                            msg = supplierService.updateSupplyDay(supplierId, supplyDay);
                        } catch (NumberFormatException ex) {
                            showErrorDialog("Invalid supply day format");
                            return;
                        }
                        break;
                    case "4": // Payment Method
                        ComboBox<String> paymentMethodCombo = (ComboBox<String>) inputBox.getChildren().get(1);
                        String paymentMethod = paymentMethodCombo.getValue();
                        if (paymentMethod == null) {
                            showErrorDialog("Please select a payment method");
                            return;
                        }
                        msg = supplierService.setPaymentMethod(supplierId, paymentMethod);
                        break;
                    case "5": // Payment Term
                        ComboBox<String> paymentTermCombo = (ComboBox<String>) inputBox.getChildren().get(1);
                        String paymentTerm = paymentTermCombo.getValue();
                        if (paymentTerm == null) {
                            showErrorDialog("Please select a payment term");
                            return;
                        }
                        msg = supplierService.updatePaymentTerm(supplierId, paymentTerm);
                        break;
                    case "6": // Address
                        TextField addressField = (TextField) inputBox.getChildren().get(1);
                        String newAddress = addressField.getText().trim();
                        if (newAddress.isEmpty()) {
                            showErrorDialog("Address cannot be empty");
                            return;
                        }
                        msg = supplierService.updateAddress(supplierId, newAddress);
                        break;
                    case "7": // Delivery Option
                        ComboBox<String> deliveryCombo = (ComboBox<String>) inputBox.getChildren().get(1);
                        String deliveryOption = deliveryCombo.getValue();
                        if (deliveryOption == null) {
                            showErrorDialog("Please select a delivery option");
                            return;
                        }
                        boolean isDelivery = Boolean.parseBoolean(deliveryOption);
                        msg = supplierService.setDelivery(supplierId, isDelivery);
                        break;
                    case "8": // Add Contact
                        TextField contactNameField = (TextField) inputBox.getChildren().get(1);
                        TextField contactPhoneField = (TextField) inputBox.getChildren().get(3);
                        String contactName = contactNameField.getText().trim();
                        String contactPhone = contactPhoneField.getText().trim();

                        if (contactName.isEmpty()) {
                            showErrorDialog("Contact name cannot be empty");
                            return;
                        }
                        if (contactPhone.isEmpty()) {
                            showErrorDialog("Contact phone cannot be empty");
                            return;
                        }
                        if (!contactPhone.matches("\\d{10}")) {
                            showErrorDialog("Invalid phone number. Please enter a valid phone number (10 digits)");
                            return;
                        }
                        msg = supplierService.addContact(supplierId, contactName, contactPhone);
                        break;
                }
                if (msg != null) {
                    if (msg.isSuccess()) {
                        showSuccessDialog("Supplier updated successfully");
                        dialog.close();
                    } else {
                        showErrorDialog("Error: " + msg.getMessage());
                    }
                } else {
                    showErrorDialog("Unexpected error occurred");
                }

            } catch (Exception ex) {
                showErrorDialog("Error updating supplier: " + ex.getMessage());
            }
        });
        cancelButton.setOnAction(e -> dialog.close());
        HBox supplierIdBox = new HBox(10);
        supplierIdBox.getChildren().addAll(supplierIdField, loadSupplierButton);

        editOptionsBox.getChildren().addAll(
                new Label("Select Edit Option:"), editOptionCombo,
                inputBox
        );
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(updateButton, cancelButton);
        layout.getChildren().addAll(
                new Label("Supplier ID:"), supplierIdBox,
                editOptionsBox,
                buttonBox
        );
        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        Scene scene = new Scene(scrollPane, 500, 600);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    public void handleNewAgreementGUI() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(parentStage);
        dialog.setTitle("Add New Agreement");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER_LEFT);

        TextField supplierIdField = new TextField();
        supplierIdField.setPromptText("Supplier ID");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");

        AgreementEntry agreementEntry = new AgreementEntry();

//        Button addProductButton = new Button("Add Product");
//        addProductButton.setOnAction(e -> agreementEntry.addProduct());

        Button submitButton = new Button("Submit Agreement");
        Button cancelButton = new Button("Cancel");

        submitButton.setOnAction(e -> {
            try {
                long supplierId = Long.parseLong(supplierIdField.getText().trim());
                String phone = phoneField.getText().trim();

                if (!phone.matches("\\d{10}")) {
                    showErrorDialog("Phone must be 10 digits");
                    return;
                }

                if (!supplierService.isSupplierExists(supplierId).isSuccess()) {
                    showErrorDialog("Supplier not found");
                    return;
                }

                if (agreementEntry.getProducts() == null || agreementEntry.getProducts().isEmpty()) {
                    showErrorDialog("No products were added to the agreement");
                    return;
                }

                AgreementDTO agreement = new AgreementDTO(
                        agreementEntry.getProducts(),
                        phone
                );

                Message msg = supplierService.addAgreement(agreement, supplierId);
                if (msg.isSuccess()) {
                    showSuccessDialog("Agreement added successfully!");
                    dialog.close();
                } else {
                    showErrorDialog("Failed to add agreement: " + msg.getMessage());
                }

            } catch (NumberFormatException ex) {
                showErrorDialog("Supplier ID must be numeric");
            } catch (Exception ex) {
                showErrorDialog("Unexpected error: " + ex.getMessage());
            }
        });

        cancelButton.setOnAction(e -> dialog.close());

        HBox buttonBox = new HBox(10, submitButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(
                new Label("Supplier ID:"), supplierIdField,
                new Label("Phone Number:"), phoneField,
                new Label("Add Products to Agreement:"), agreementEntry.getLayout(),
                buttonBox
        );

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        Scene scene = new Scene(scrollPane, 600, 650);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    public void handleRemoveAgreementGUI() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(parentStage);
        dialog.setTitle("Remove Agreement");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER_LEFT);
        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        TextField supplierIdField = new TextField();
        supplierIdField.setPromptText("Supplier ID");

        TextField agreementIdField = new TextField();
        agreementIdField.setPromptText("Agreement ID");

        Label supplierIdLabel = new Label("Supplier ID:");
        Label agreementIdLabel = new Label("Agreement ID:");
        Label warningLabel = new Label("This action cannot be undone!");
        warningLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button removeButton = new Button("Remove Agreement");
        removeButton.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white;");
        Button cancelButton = new Button("Cancel");

        removeButton.setOnAction(e -> {
            try {
                if (supplierIdField.getText().trim().isEmpty() ||
                        agreementIdField.getText().trim().isEmpty()) {
                    showAlert("Error", "Please fill in all required fields");
                    return;
                }

                long supplierId = Long.parseLong(supplierIdField.getText().trim());
                long agreementId = Long.parseLong(agreementIdField.getText().trim());

                Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
                confirmDialog.setTitle("Confirm Removal");
                confirmDialog.setHeaderText("Remove Agreement");
                confirmDialog.setContentText("Are you sure you want to remove this agreement?");

                Optional<ButtonType> result = confirmDialog.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    boolean success = removeAgreement(supplierId, agreementId);

                    if (success) {
                        showAlert("Success", "Agreement removed successfully");
                        dialog.close();
                    } else {
                        showAlert("Error", "Failed to remove agreement or agreement not found");
                    }
                }

            } catch (NumberFormatException ex) {
                showAlert("Error", "Invalid ID format");
            } catch (Exception ex) {
                showAlert("Error", "An error occurred: " + ex.getMessage());
            }
        });

        cancelButton.setOnAction(e -> dialog.close());
        buttonBox.getChildren().addAll(removeButton, cancelButton);

        layout.getChildren().addAll(
                supplierIdLabel, supplierIdField,
                agreementIdLabel, agreementIdField,
                warningLabel,
                buttonBox
        );

        Scene scene = new Scene(layout, 350, 250);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    public void handleEditAgreementGUI() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(parentStage);
        dialog.setTitle("Edit Agreement");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_LEFT);

        TextField supplierIdField = new TextField();
        supplierIdField.setPromptText("Supplier ID");

        TextField agreementIdField = new TextField();
        agreementIdField.setPromptText("Agreement ID");

        ComboBox<String> updateTypeCombo = new ComboBox<>();
        updateTypeCombo.getItems().addAll(
                "Add Product",
                "Delete Product",
                "Update Phone Number",
                "Update Discount",
                "Update Quantity",
                "Update Price"
        );
        updateTypeCombo.setPromptText("Select Update Type");

        TextField productIdField = new TextField();
        productIdField.setPromptText("Product Catalog Number");

        TextField inputField = new TextField();
        inputField.setPromptText("Value (Discount/Quantity/Price)");

        VBox contactBox = new VBox(10);
        TextField contactNameField = new TextField();
        contactNameField.setPromptText("Contact Name");
        TextField contactPhoneField = new TextField();
        contactPhoneField.setPromptText("Phone Number");
        contactBox.getChildren().addAll(new Label("Add Contact:"), contactNameField, contactPhoneField);
        contactBox.setVisible(false);

        VBox addProductBox = new VBox(10);
        TextField nameField = new TextField(); nameField.setPromptText("Product Name");
        TextField manufacturerField = new TextField(); manufacturerField.setPromptText("Manufacturer");
        TextField priceField = new TextField(); priceField.setPromptText("Price");
        TextField quantityField = new TextField(); quantityField.setPromptText("Quantity for Discount");
        TextField discountField = new TextField(); discountField.setPromptText("Discount (0–100)");
        ComboBox<String> measureCombo = new ComboBox<>();
        measureCombo.getItems().addAll("PER_UNIT", "BY_WEIGHT");
        measureCombo.setPromptText("Measurement Type");
        TextField category1 = new TextField(); category1.setPromptText("Category 1");
        TextField category2 = new TextField(); category2.setPromptText("Category 2");
        TextField category3 = new TextField(); category3.setPromptText("Category 3");
        TextField expirationDays = new TextField(); expirationDays.setPromptText("Expiration Days");

        addProductBox.getChildren().addAll(
                new Label("Add Product:"),
                productIdField,
                nameField, manufacturerField,
                priceField, quantityField, discountField, measureCombo,
                category1, category2, category3, expirationDays
        );
        addProductBox.setVisible(false);

        updateTypeCombo.setOnAction(e -> {
            String selected = updateTypeCombo.getValue();
            addProductBox.setVisible(false);
            contactBox.setVisible(false);
            productIdField.setVisible(false);
            inputField.setVisible(false);

            if (selected.equals("Add Product")) {
                addProductBox.setVisible(true);
            } else if (selected.equals("Update Phone Number")) {
                contactBox.setVisible(true);
            } else {
                if (selected.equals("Delete Product") ||
                        selected.equals("Update Discount") ||
                        selected.equals("Update Quantity") ||
                        selected.equals("Update Price")) {
                    productIdField.setVisible(true);
                }
                if (selected.equals("Update Discount") ||
                        selected.equals("Update Quantity") ||
                        selected.equals("Update Price")) {
                    inputField.setVisible(true);
                }
            }
        });

        Button confirmButton = new Button("Apply Change");
        Button cancelButton = new Button("Cancel");

        confirmButton.setOnAction(e -> {
            try {
                long supplierId = Long.parseLong(supplierIdField.getText().trim());
                int agreementId = Integer.parseInt(agreementIdField.getText().trim());
                String type = updateTypeCombo.getValue();
                String productId = productIdField.getText().trim();
                String value = inputField.getText().trim();

                SupplierService supplierService = SupplierService.getInstance();
                Message msg = null;

                switch (type) {
                    case "Add Product":
                        if (productId.isEmpty() || nameField.getText().trim().isEmpty() ||
                                manufacturerField.getText().trim().isEmpty() || priceField.getText().trim().isEmpty() ||
                                quantityField.getText().trim().isEmpty() || discountField.getText().trim().isEmpty() ||
                                measureCombo.getValue() == null || category1.getText().trim().isEmpty() ||
                                category2.getText().trim().isEmpty() || category3.getText().trim().isEmpty() ||
                                expirationDays.getText().trim().isEmpty()) {
                            showAlert(Alert.AlertType.ERROR, "Please fill all product fields");
                            return;
                        }
                        ProductDTO p = new ProductDTO(
                                nameField.getText().trim(),
                                productId,
                                manufacturerField.getText().trim(),
                                Double.parseDouble(priceField.getText().trim()),
                                Integer.parseInt(discountField.getText().trim()),
                                Integer.parseInt(quantityField.getText().trim()),
                                measureCombo.getValue(),
                                category1.getText().trim(),
                                category2.getText().trim(),
                                category3.getText().trim(),
                                Integer.parseInt(expirationDays.getText().trim())
                        );
                        msg = supplierService.addProductToAgreement(supplierId, agreementId, p);
                        break;

                    case "Delete Product":
                        msg = supplierService.removeProduct(supplierId, agreementId, productId);
                        break;

                    case "Update Phone Number":
                        String name = contactNameField.getText().trim();
                        String phone = contactPhoneField.getText().trim();
                        if (name.isEmpty() || !phone.matches("\\d{10}")) {
                            showAlert(Alert.AlertType.ERROR, "Please enter valid name and 10-digit phone number");
                            return;
                        }
                        msg = supplierService.addContact(supplierId, name, phone);
                        break;

                    case "Update Discount":
                        msg = supplierService.updateDiscount(supplierId, agreementId, Integer.parseInt(value), productId);
                        break;

                    case "Update Quantity":
                        msg = supplierService.updateQuantity(supplierId, agreementId, Integer.parseInt(value), productId);
                        break;

                    case "Update Price":
                        msg = supplierService.setCost(supplierId, agreementId, Integer.parseInt(value), productId);
                        break;
                }

                if (msg != null && msg.isSuccess()) {
                    showAlert(Alert.AlertType.INFORMATION, "Update successful");
                    dialog.close();
                } else {
                    showAlert(Alert.AlertType.ERROR, msg != null ? msg.getMessage() : "Operation failed");
                }

            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid input: " + ex.getMessage());
            }
        });

        cancelButton.setOnAction(e -> dialog.close());

        layout.getChildren().addAll(
                new Label("Supplier ID:"), supplierIdField,
                new Label("Agreement ID:"), agreementIdField,
                new Label("Update Type:"), updateTypeCombo,
                new Label("Product ID (if relevant):"), productIdField,
                new Label("New Value (if relevant):"), inputField,
                contactBox,
                addProductBox,
                new HBox(10, confirmButton, cancelButton)
        );

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPrefViewportHeight(800);
        layout.setMinHeight(1200);
        Scene scene = new Scene(scrollPane, 550, 800);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    // Alert helper method
    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle("Message");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public void supplierUserGUI() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(parentStage);
        dialog.setTitle("Update Product Discount in Order");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER_LEFT);

        TextField supplierIdField = new TextField();
        supplierIdField.setPromptText("Supplier ID");

        TextField orderIdField = new TextField();
        orderIdField.setPromptText("Order ID");

        TextField catalogField = new TextField();
        catalogField.setPromptText("Catalog Number (Supplier)");

        TextField discountField = new TextField();
        discountField.setPromptText("Discount (0–100)");

        Button updateButton = new Button("Update Discount");
        Button cancelButton = new Button("Cancel");

        updateButton.setOnAction(e -> {
            try {
                if (supplierIdField.getText().trim().isEmpty() ||
                        orderIdField.getText().trim().isEmpty() ||
                        catalogField.getText().trim().isEmpty() ||
                        discountField.getText().trim().isEmpty()) {
                    showAlert("Error", "Please fill in all fields");
                    return;
                }

                long supplierId = Long.parseLong(supplierIdField.getText().trim());
                int orderId = Integer.parseInt(orderIdField.getText().trim());
                String catalogNumber = catalogField.getText().trim();
                int discount = Integer.parseInt(discountField.getText().trim());

                if (discount < 0 || discount > 100) {
                    showAlert("Error", "Discount must be between 0 and 100");
                    return;
                }

                Message msg = supplierService.setDiscount(supplierId, orderId, discount, catalogNumber);

                if (msg != null && msg.isSuccess()) {
                    showSuccessDialog("Discount updated successfully!");
                    dialog.close();
                } else {
                    showAlert("Error", msg != null ? msg.getMessage() : "Unexpected error");
                }

            } catch (NumberFormatException ex) {
                showAlert("Error", "Supplier ID, Order ID, and Discount must be numeric");
            } catch (Exception ex) {
                showAlert("Error", "An error occurred: " + ex.getMessage());
            }
        });

        cancelButton.setOnAction(e -> dialog.close());

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(updateButton, cancelButton);

        layout.getChildren().addAll(
                new Label("Supplier ID:"), supplierIdField,
                new Label("Order ID:"), orderIdField,
                new Label("Catalog Number:"), catalogField,
                new Label("Discount (%):"), discountField,
                buttonBox
        );

        Scene scene = new Scene(layout, 400, 350);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    // Business logic implementations
    private boolean addNewAgreement(long supplierId, String agreementType, LocalDate startDate, LocalDate endDate, String terms, String description) {
        try {
            // Validate supplier exists
            if (!supplierExists(supplierId)) {
                showAlert("Error", "Supplier not found with ID: " + supplierId);
                return false;
            }

            // Validate dates
            if (endDate != null && startDate.isAfter(endDate)) {
                showAlert("Error", "Start date cannot be after end date");
                return false;
            }

            // Create new agreement
            Agreement newAgreement = new Agreement();
            newAgreement.setId(generateAgreementId());
            newAgreement.setSupplierId(supplierId);
            newAgreement.setType(agreementType);
            newAgreement.setStartDate(startDate);
            newAgreement.setEndDate(endDate);
            newAgreement.setTerms(terms);
            newAgreement.setDescription(description);
            newAgreement.setCreatedDate(LocalDate.now());
            newAgreement.setActive(true);

            // Save to database/storage
            return saveAgreement(newAgreement);

        } catch (Exception e) {
            System.err.println("Error adding agreement: " + e.getMessage());
            return false;
        }
    }

    private boolean removeAgreement(long supplierId, long agreementId) {
        try {
            // Validate supplier exists
            if (!supplierExists(supplierId)) {
                showAlert("Error", "Supplier not found with ID: " + supplierId);
                return false;
            }

            // Find agreement
            Agreement agreement = findAgreement(supplierId, agreementId);
            if (agreement == null) {
                showAlert("Error", "Agreement not found");
                return false;
            }

            // Check if agreement can be deleted (e.g., not referenced by active contracts)
            if (hasActiveReferences(agreementId)) {
                showAlert("Error", "Cannot delete agreement - it has active references");
                return false;
            }

            // Remove from database/storage
            return deleteAgreement(agreementId);

        } catch (Exception e) {
            System.err.println("Error removing agreement: " + e.getMessage());
            return false;
        }
    }

    private Agreement getAgreementDetails(long supplierId, long agreementId) {
        try {
            // Validate supplier exists
            if (!supplierExists(supplierId)) {
                return null;
            }

            // Find and return agreement
            return findAgreement(supplierId, agreementId);

        } catch (Exception e) {
            System.err.println("Error getting agreement details: " + e.getMessage());
            return null;
        }
    }

    private boolean updateAgreement(long supplierId, long agreementId, String agreementType, LocalDate startDate, LocalDate endDate, String terms, String description) {
        try {
            // Validate supplier exists
            if (!supplierExists(supplierId)) {
                showAlert("Error", "Supplier not found with ID: " + supplierId);
                return false;
            }

            // Find existing agreement
            Agreement agreement = findAgreement(supplierId, agreementId);
            if (agreement == null) {
                showAlert("Error", "Agreement not found");
                return false;
            }

            // Validate dates
            if (endDate != null && startDate.isAfter(endDate)) {
                showAlert("Error", "Start date cannot be after end date");
                return false;
            }

            // Update agreement details
            agreement.setType(agreementType);
            agreement.setStartDate(startDate);
            agreement.setEndDate(endDate);
            agreement.setTerms(terms);
            agreement.setDescription(description);
            agreement.setModifiedDate(LocalDate.now());

            // Save changes
            return updateAgreementInStorage(agreement);

        } catch (Exception e) {
            System.err.println("Error updating agreement: " + e.getMessage());
            return false;
        }
    }

    private boolean createSupplierUser(long supplierId, String username, String password, String email, String phone, String role, boolean isActive) {
        try {
            // Validate supplier exists
            if (!supplierExists(supplierId)) {
                showAlert("Error", "Supplier not found with ID: " + supplierId);
                return false;
            }

            // Check if username already exists
            if (usernameExists(username)) {
                showAlert("Error", "Username already exists");
                return false;
            }

            // Check if email already exists
            if (emailExists(email)) {
                showAlert("Error", "Email already exists");
                return false;
            }

            // Validate password strength
            if (!isValidPassword(password)) {
                showAlert("Error", "Password must be at least 6 characters long");
                return false;
            }

            // Create new supplier user
            SupplierUser newUser = new SupplierUser();
            newUser.setId(generateUserId());
            newUser.setSupplierId(supplierId);
            newUser.setUsername(username);
            newUser.setPassword(hashPassword(password)); // Hash the password
            newUser.setEmail(email);
            newUser.setPhone(phone);
            newUser.setRole(role);
            newUser.setActive(isActive);
            newUser.setCreatedDate(LocalDate.now());

            // Save to database/storage
            return saveSupplierUser(newUser);

        } catch (Exception e) {
            System.err.println("Error creating supplier user: " + e.getMessage());
            return false;
        }
    }

    // Database/Storage helper methods
    private boolean supplierExists(long supplierId) {
        // Check if supplier exists in your database/storage
        // This is a mock implementation - replace with your actual data access logic
        return supplierId > 0; // Simple validation for demo
    }

    private long generateAgreementId() {
        // Generate unique agreement ID
        return System.currentTimeMillis(); // Simple ID generation - use proper ID generation in production
    }

    private long generateUserId() {
        // Generate unique user ID
        return System.currentTimeMillis() + 1000; // Simple ID generation - use proper ID generation in production
    }

    private boolean saveAgreement(Agreement agreement) {
        // Save agreement to database/storage
        // This is where you'd implement your data persistence logic
        // For now, we'll simulate success
        System.out.println("Saving agreement: " + agreement.toString());
        return true;
    }

    private Agreement findAgreement(long supplierId, long agreementId) {
        // Find agreement by supplier ID and agreement ID
        // This is a mock implementation - replace with your actual data access logic
        Agreement mockAgreement = new Agreement();
        mockAgreement.setId(agreementId);
        mockAgreement.setSupplierId(supplierId);
        mockAgreement.setType("Standard Supply Agreement");
        mockAgreement.setStartDate(LocalDate.now().minusMonths(1));
        mockAgreement.setEndDate(LocalDate.now().plusMonths(11));
        mockAgreement.setTerms("Standard terms and conditions");
        mockAgreement.setDescription("Standard supply agreement for materials");
        mockAgreement.setActive(true);

        return mockAgreement; // Return mock data - replace with actual database query
    }

    private boolean deleteAgreement(long agreementId) {
        // Delete agreement from database/storage
        System.out.println("Deleting agreement with ID: " + agreementId);
        return true; // Mock success - implement actual deletion logic
    }

    private boolean hasActiveReferences(long agreementId) {
        // Check if agreement has active references (contracts, orders, etc.)
        // This is a business rule check
        return false; // Mock - implement actual reference checking
    }

    private boolean updateAgreementInStorage(Agreement agreement) {
        // Update agreement in database/storage
        System.out.println("Updating agreement: " + agreement.toString());
        return true; // Mock success - implement actual update logic
    }

    private boolean usernameExists(String username) {
        // Check if username already exists
        // Mock implementation - replace with actual database check
        return username.equalsIgnoreCase("admin") || username.equalsIgnoreCase("test");
    }

    private boolean emailExists(String email) {
        // Check if email already exists
        // Mock implementation - replace with actual database check
        return email.equalsIgnoreCase("admin@test.com");
    }

    private boolean isValidPassword(String password) {
        // Validate password strength
        return password != null && password.length() >= 6;
    }

    private String hashPassword(String password) {
        // Hash password for security - this is a simple example
        // In production, use proper password hashing like BCrypt
        return "hashed_" + password;
    }

    private boolean saveSupplierUser(SupplierUser user) {
        // Save supplier user to database/storage
        System.out.println("Saving supplier user: " + user.toString());
        return true; // Mock success - implement actual save logic
    }

    public Message validateDet(String id) {
        return supplierService.isSupplierExists(Integer.parseInt(id));
    }

    // Data model classes
    public static class Agreement {
        private long id;
        private long supplierId;
        private String type;
        private LocalDate startDate;
        private LocalDate endDate;
        private String terms;
        private String description;
        private LocalDate createdDate;
        private LocalDate modifiedDate;
        private boolean active;

        // Constructors
        public Agreement() {}

        // Getters and Setters
        public long getId() { return id; }
        public void setId(long id) { this.id = id; }

        public long getSupplierId() { return supplierId; }
        public void setSupplierId(long supplierId) { this.supplierId = supplierId; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

        public String getTerms() { return terms; }
        public void setTerms(String terms) { this.terms = terms; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public LocalDate getCreatedDate() { return createdDate; }
        public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }

        public LocalDate getModifiedDate() { return modifiedDate; }
        public void setModifiedDate(LocalDate modifiedDate) { this.modifiedDate = modifiedDate; }

        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }

        @Override
        public String toString() {
            return "Agreement{" +
                    "id=" + id +
                    ", supplierId=" + supplierId +
                    ", type='" + type + '\'' +
                    ", startDate=" + startDate +
                    ", endDate=" + endDate +
                    ", terms='" + terms + '\'' +
                    ", description='" + description + '\'' +
                    ", active=" + active +
                    '}';
        }
    }

    public static class SupplierUser {
        private long id;
        private long supplierId;
        private String username;
        private String password;
        private String email;
        private String phone;
        private String role;
        private boolean active;
        private LocalDate createdDate;
        private LocalDate lastLoginDate;

        // Constructors
        public SupplierUser() {}

        // Getters and Setters
        public long getId() { return id; }
        public void setId(long id) { this.id = id; }

        public long getSupplierId() { return supplierId; }
        public void setSupplierId(long supplierId) { this.supplierId = supplierId; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }

        public LocalDate getCreatedDate() { return createdDate; }
        public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }

        public LocalDate getLastLoginDate() { return lastLoginDate; }
        public void setLastLoginDate(LocalDate lastLoginDate) { this.lastLoginDate = lastLoginDate; }

        @Override
        public String toString() {
            return "SupplierUser{" +
                    "id=" + id +
                    ", supplierId=" + supplierId +
                    ", username='" + username + '\'' +
                    ", email='" + email + '\'' +
                    ", phone='" + phone + '\'' +
                    ", role='" + role + '\'' +
                    ", active=" + active +
                    '}';
        }
    }

    private class ContactEntry {
        private TextField nameField;
        private TextField phoneField;
        private HBox layout;

        public ContactEntry() {
            nameField = new TextField();
            nameField.setPromptText("Contact Name");
            phoneField = new TextField();
            phoneField.setPromptText("Phone (10 digits)");

            Button removeButton = new Button("Remove");
            removeButton.setOnAction(e -> {
                VBox parent = (VBox) layout.getParent();
                parent.getChildren().remove(layout);
            });

            layout = new HBox(10);
            layout.getChildren().addAll(nameField, phoneField, removeButton);
        }
        public HBox getLayout() { return layout; }
        public String getName() { return nameField.getText().trim(); }
        public String getPhone() { return phoneField.getText().trim(); }
        public boolean isValid() {
            return !getName().isEmpty() && !getPhone().isEmpty() && getPhone().matches("\\d{10}");
        }
    }

    private class AgreementEntry {
        private VBox layout;
        private TextField phoneField;
        private Map<String, ProductDTO> products;
        private TextField catalogField;
        private TextField nameField;
        private TextField manufacturerField;
        private TextField priceField;
        private TextField quantityField;
        private TextField discountField;
        private ComboBox<String> measureCombo;
        private TextField category1;
        private TextField category2;
        private TextField category3;
        private TextField expirationDays;

        public AgreementEntry() {
            this.phoneField = new TextField();
            phoneField.setPromptText("Phone Number");

            this.products = new HashMap<>();

            Label phoneLabel = new Label("Contact Phone:");
            VBox productSection = new VBox(5);

            TextField catalogField = new TextField();
            catalogField.setPromptText("Catalog Number");

            TextField nameField = new TextField();
            nameField.setPromptText("Product Name");

            TextField manufacturerField = new TextField();
            manufacturerField.setPromptText("Manufacturer");

            TextField priceField = new TextField();
            priceField.setPromptText("Price");

            TextField quantityField = new TextField();
            quantityField.setPromptText("Quantity for Discount");

            TextField discountField = new TextField();
            discountField.setPromptText("Discount (0–100)");

            ComboBox<String> measureCombo = new ComboBox<>();
            measureCombo.getItems().addAll("PER_UNIT", "BY_WEIGHT");
            measureCombo.setPromptText("Measurement Type");

            TextField category1 = new TextField(); category1.setPromptText("Category 1");
            TextField category2 = new TextField(); category2.setPromptText("Category 2");
            TextField category3 = new TextField(); category3.setPromptText("Category 3");

            TextField expirationDays = new TextField(); expirationDays.setPromptText("Expiration Days");

            Button addProductBtn = new Button("Add Product");

            addProductBtn.setOnAction(e -> {
                try {
                    String catalog = catalogField.getText().trim();
                    if (catalog.isEmpty()) {
                        showError("Catalog number required");
                        return;
                    }
                    ProductDTO p = new ProductDTO(
                            nameField.getText().trim(),
                            catalog,
                            manufacturerField.getText().trim(),
                            Double.parseDouble(priceField.getText().trim()),
                            Integer.parseInt(discountField.getText().trim()),
                            Integer.parseInt(quantityField.getText().trim()),
                            measureCombo.getValue(),
                            category1.getText().trim(),
                            category2.getText().trim(),
                            category3.getText().trim(),
                            Integer.parseInt(expirationDays.getText().trim())
                    );
                    products.put(catalog, p);
                    showInfo("Product added: " + catalog);
                    catalogField.clear();
                    nameField.clear();
                    manufacturerField.clear();
                    priceField.clear();
                    quantityField.clear();
                    discountField.clear();
                    measureCombo.setValue(null);
                    category1.clear();
                    category2.clear();
                    category3.clear();
                    expirationDays.clear();
                    catalogField.requestFocus();

                } catch (Exception ex) {
                    showError("Invalid input: " + ex.getMessage());
                }
            });

            layout = new VBox(8);
            layout.getChildren().addAll(
                    new Label("Add Product:"), catalogField, nameField, manufacturerField,
                    priceField, quantityField, discountField, measureCombo,
                    category1, category2, category3, expirationDays,
                    addProductBtn
            );
        }

        public AgreementDTO createAgreementDTO() {
            return new AgreementDTO(products, phoneField.getText().trim());
        }

        public VBox getLayout() {
            return layout;
        }

        public boolean isValid() {
            return !phoneField.getText().trim().isEmpty() && !products.isEmpty();
        }

//        public void addProduct() {
//            try {
//                String catalog = catalogField.getText().trim();
//                if (catalog.isEmpty()) {
//                    showError("Catalog number required");
//                    return;
//                }
//                ProductDTO p = new ProductDTO(
//                        nameField.getText().trim(),
//                        catalog,
//                        manufacturerField.getText().trim(),
//                        Double.parseDouble(priceField.getText().trim()),
//                        Integer.parseInt(discountField.getText().trim()),
//                        Integer.parseInt(quantityField.getText().trim()),
//                        measureCombo.getValue(),
//                        category1.getText().trim(),
//                        category2.getText().trim(),
//                        category3.getText().trim(),
//                        Integer.parseInt(expirationDays.getText().trim())
//                );
//                products.put(catalog, p);
//                showInfo("Product added: " + catalog);
//
//                catalogField.clear();
//                nameField.clear();
//                manufacturerField.clear();
//                priceField.clear();
//                quantityField.clear();
//                discountField.clear();
//                measureCombo.getSelectionModel().clearSelection();
//                category1.clear();
//                category2.clear();
//                category3.clear();
//                expirationDays.clear();
//                catalogField.requestFocus();
//
//            } catch (Exception ex) {
//                showError("Invalid input: " + ex.getMessage());
//            }
//        }

        public Map<String, ProductDTO> getProducts() {
            return products;
        }
    }


    private void showSuccessDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void updateProductsDisplay(TextArea productsDisplay, Map<String, Integer> products) {
        if (products.isEmpty()) {
            productsDisplay.setText("No products added yet");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Added Products:\n");
        sb.append("================\n");

        for (Map.Entry<String, Integer> entry : products.entrySet()) {
            sb.append("Catalog: ").append(entry.getKey())
                    .append(" | Quantity: ").append(entry.getValue())
                    .append("\n");
        }

        productsDisplay.setText(sb.toString());
    }
    private void showSuccessDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(parentStage);
        alert.setTitle("Success");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void loadSuppliers(ComboBox<String> supplierCombo) {
        try {
            List<Long> supplierIDs = supplierService.getAllSuppliersIDs();
            if (supplierIDs != null && !supplierIDs.isEmpty()) {
                supplierCombo.getItems().clear();
                for (Long id : supplierIDs) {
                    // Verify supplier exists before adding to combo
                    Message msg = supplierService.isSupplierExists(id);
                    if (msg != null && msg.isSuccess()) {
                        supplierCombo.getItems().add("ID: " + id + " - Supplier " + id);
                    }
                }
            }
        } catch (Exception e) {
            showErrorDialog("Error loading suppliers: " + e.getMessage());
        }
    }
    private long extractSupplierId(String supplierString) {
        String idPart = supplierString.substring(supplierString.indexOf("ID: ") + 4, supplierString.indexOf(" - "));
        return Long.parseLong(idPart);
    }
    private void loadSupplierIDs(TextArea textArea) {
        try {
            List<Long> supplierIDs = supplierService.getAllSuppliersIDs();
            if (supplierIDs != null && !supplierIDs.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append("All Supplier IDs:\n\n");
                for (Long id : supplierIDs) {
                    sb.append(id).append("\n");
                }
                textArea.setText(sb.toString());
            } else {
                textArea.setText("No suppliers found.");
            }
        } catch (Exception e) {
            textArea.setText("Error loading supplier IDs: " + e.getMessage());
        }
    }
    private void loadOrdersForDelivery(TextArea textArea) {
        try {
            Message msg = supplierService.getOrdersForDelivery();
            if (msg != null) {
                textArea.setText(msg.toString());
            } else {
                textArea.setText("Unexpected error occurred.");
            }
        } catch (Exception e) {
            textArea.setText("Error loading orders for delivery: " + e.getMessage());
        }
    }
    private void showDetailsDialog(String title, String content) {
        Stage detailDialog = new Stage();
        detailDialog.initModality(Modality.APPLICATION_MODAL);
        detailDialog.initOwner(parentStage);
        detailDialog.setTitle(title);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        TextArea textArea = new TextArea(content);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefWidth(560);
        textArea.setPrefHeight(400);

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> detailDialog.close());

        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(closeButton);

        layout.getChildren().addAll(textArea, buttonBox);

        Scene scene = new Scene(layout, 600, 500);
        detailDialog.setScene(scene);
        detailDialog.showAndWait();
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(parentStage);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(msg);
        alert.showAndWait();
    }
    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText(msg);
        alert.showAndWait();
    }



}
