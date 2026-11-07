package HRandDelivery.PresentationLayer;

import HRandDelivery.DomainLayer.HR.enums.Role;
import HRandDelivery.DomainLayer.HRandDeliveryDomainService;
import HRandDelivery.Utils.GUIutils;
import HRandDelivery.Utils.Response;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class HRandDeliveryPresGUI {

    private HRandDeliveryDomainService domainService;
    private Stage parentStage;
    private GUIutils guiUtils = new GUIutils();

    public HRandDeliveryPresGUI(Stage parentStage) throws Exception {
        this.domainService = HRandDeliveryDomainService.getInstance();
        this.parentStage = parentStage;

        domainService.addBranch("Gilat", "01", "Gilat 81","Gaya Aviv","050-88899");
        domainService.addEmployee("Shani Kroitoro", "123456789", "4545 789 67868","545 23/09/2014 56 76 4567890","ADMIN", "01", "");
    }


    public void handleAddEmployeeGUI() {
        try {
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(parentStage);
            dialog.setTitle("Add Employee");

            // Create ScrollPane for the main content
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: #f8f9fa;");

            VBox layout = new VBox(10);
            layout.setPadding(new Insets(15));
            layout.setStyle("-fx-background-color: #f8f9fa;");

            // Title
            Label titleLabel = new Label("Add Employee");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            // Form section
            VBox formBox = new VBox(8);
            formBox.setStyle("-fx-background-color: #ffffff; -fx-padding: 12px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-border-color: #e0e0e0; -fx-border-width: 1px;");

            // Create form fields using guiUtils
            VBox nameBox = guiUtils.createFormField("Full Name:", "Enter full name");
            VBox idBox = guiUtils.createFormField("Employee ID:", "Enter employee ID");
            VBox rolesBox = guiUtils.createFormField("Roles:", "e.g. SHIFT_MANAGER CASHIER");
            VBox bankBox = guiUtils.createFormField("Bank Account:", "[account Id, bank Id, bank branch]");
            VBox workConditionsBox = guiUtils.createFormField("Work Conditions:", "[salary, start date, vacation days, sick days, funds]");
            VBox licensesBox = guiUtils.createFormField("Licenses:", "Enter licenses (if driver)");

            // Create branch selection box using guiUtils
            VBox branchBox = guiUtils.createComboBoxField("Branch:", "Select branch");
            ComboBox<String> branchCombo = (ComboBox<String>) branchBox.getChildren().get(1);
            loadBranches(branchCombo);

            // Get text fields for later use
            TextField nameField = (TextField) nameBox.getChildren().get(1);
            TextField idField = (TextField) idBox.getChildren().get(1);
            TextField rolesField = (TextField) rolesBox.getChildren().get(1);
            TextField bankField = (TextField) bankBox.getChildren().get(1);
            TextField workConditionsField = (TextField) workConditionsBox.getChildren().get(1);
            TextField licensesField = (TextField) licensesBox.getChildren().get(1);

            formBox.getChildren().addAll(nameBox, idBox, rolesBox, bankBox, workConditionsBox, branchBox, licensesBox);

            // Buttons section
            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setPadding(new Insets(8, 0, 5, 0));

            Button addButton = guiUtils.createStyledButton("Add Employee", "#27ae60", 130);
            Button cancelButton = guiUtils.createStyledButton("Cancel", "#95a5a6", 110);

            buttonBox.getChildren().addAll(addButton, cancelButton);

            // Result label for messages
            Label resultLabel = new Label();
            resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
            resultLabel.setWrapText(true);
            resultLabel.setMaxWidth(380);

            // Button actions
            cancelButton.setOnAction(e -> dialog.close());

            addButton.setOnAction(e -> {
                try {
                    // Validate input fields
                    if (nameField.getText().isEmpty() || idField.getText().isEmpty() ||
                            rolesField.getText().isEmpty() || bankField.getText().isEmpty() ||
                            workConditionsField.getText().isEmpty() || branchCombo.getValue() == null) {
                        resultLabel.setText("Please enter all required fields.");
                        resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
                        return;
                    }

                    Response response = domainService.addEmployee(
                            nameField.getText(),
                            idField.getText(),
                            bankField.getText(),
                            workConditionsField.getText(),
                            rolesField.getText(),
                            extractBranchId(branchCombo.getValue()),
                            licensesField.getText()
                    );

                    if (response.isSuccess()) {
                        guiUtils.showDetailsDialog("Employee Added Successfully", response.getMessage());
                        dialog.close();
                    } else {
                        resultLabel.setText("Error: " + response.getMessage());
                        resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
                    }

                } catch (Exception ex) {
                    resultLabel.setText("Error: " + ex.getMessage());
                    resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
                    ex.printStackTrace();
                }
            });

            // Add all components to layout
            layout.getChildren().addAll(titleLabel, formBox, buttonBox, resultLabel);

            // Set content to scroll pane
            scrollPane.setContent(layout);

            // Create and show scene with better sizing
            Scene scene = new Scene(scrollPane, 420, 570);
            dialog.setScene(scene);
            dialog.setResizable(true);
            dialog.setMinWidth(380);
            dialog.setMinHeight(450);
            dialog.showAndWait();

        } catch (Exception e) {
            guiUtils.showErrorDialog("Error opening add employee dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleViewEmployeeDetailsGUI() {
        try {
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(parentStage);
            dialog.setTitle("View Employee Details");

            // Create ScrollPane for the main content
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: #f8f9fa;");

            VBox layout = new VBox(10);
            layout.setPadding(new Insets(15));
            layout.setStyle("-fx-background-color: #f8f9fa;");

            // Title
            Label titleLabel = new Label("View Employee Details");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            // Form section
            VBox formBox = new VBox(8);
            formBox.setStyle("-fx-background-color: #ffffff; -fx-padding: 12px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-border-color: #e0e0e0; -fx-border-width: 1px;");

            // Create employee selection box using guiUtils
            VBox employeeBox = guiUtils.createComboBoxField("Select Employee:", "Choose employee");
            ComboBox<String> employeeCombo = (ComboBox<String>) employeeBox.getChildren().get(1);

            // Load employees into the combo box
            try {
                loadEmployees(employeeCombo, domainService.displayEmployeesAndRole().getMessage());
            } catch (Exception e) {
                guiUtils.showErrorDialog("Error loading employees: " + e.getMessage());
                return;
            }

            formBox.getChildren().add(employeeBox);

            // Buttons section
            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setPadding(new Insets(8, 0, 5, 0));

            Button viewButton = guiUtils.createStyledButton("View Details", "#27ae60", 110);
            Button cancelButton = guiUtils.createStyledButton("Cancel", "#95a5a6", 110);

            buttonBox.getChildren().addAll(viewButton, cancelButton);

            // Result label for messages
            Label resultLabel = new Label();
            resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
            resultLabel.setWrapText(true);
            resultLabel.setMaxWidth(380);

            // Button actions
            cancelButton.setOnAction(e -> dialog.close());

            viewButton.setOnAction(e -> {
                try {
                    String selectedEmployee = employeeCombo.getValue();
                    if (selectedEmployee == null) {
                        resultLabel.setText("Please select an employee.");
                        resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
                        return;
                    }

                    String employeeId = extractEmployeeId(selectedEmployee);
                    Response response = domainService.displayEmployeeDetails(employeeId);

                    if (response.isSuccess()) {
                        showEmployeeDetailsDialog(response.getMessage());
                    } else {
                        resultLabel.setText("Error: " + response.getMessage());
                        resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
                    }

                } catch (Exception ex) {
                    resultLabel.setText("Error: " + ex.getMessage());
                    resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
                    ex.printStackTrace();
                }
            });

            // Add all components to layout
            layout.getChildren().addAll(titleLabel, formBox, buttonBox, resultLabel);

            // Set content to scroll pane
            scrollPane.setContent(layout);

            // Create and show scene
            Scene scene = new Scene(scrollPane, 420, 300);
            dialog.setScene(scene);
            dialog.setResizable(true);
            dialog.setMinWidth(380);
            dialog.setMinHeight(450);
            dialog.showAndWait();

        } catch (Exception e) {
            guiUtils.showErrorDialog("Error opening view employee dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleSetShiftRequirementsGUI() {
        try {
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(parentStage);
            dialog.setTitle("Set Shift Requirements");

            // Create ScrollPane for the main content
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: #f8f9fa;");

            VBox layout = new VBox(10);
            layout.setPadding(new Insets(15));
            layout.setStyle("-fx-background-color: #f8f9fa;");

            // Title
            Label titleLabel = new Label("Set Shift Requirements");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            // Form section
            VBox formBox = new VBox(8);
            formBox.setStyle("-fx-background-color: #ffffff; -fx-padding: 12px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-border-color: #e0e0e0; -fx-border-width: 1px;");

            // Create form components using helper functions
            VBox branchBox = guiUtils.createComboBoxField("Branch:", "Select branch");
            ComboBox<String> branchCombo = (ComboBox<String>) branchBox.getChildren().get(1);
            loadBranches(branchCombo);

            VBox dateBox = guiUtils.createDatePickerField("Date:");
            DatePicker datePicker = (DatePicker) dateBox.getChildren().get(1);

            VBox timeSlotBox = guiUtils.createComboBoxField("Time Slot:", "Select time slot");
            ComboBox<String> timeSlotCombo = (ComboBox<String>) timeSlotBox.getChildren().get(1);
            timeSlotCombo.getItems().addAll("M", "E");

            // Create form fields using guiUtils
            VBox startTimeBox = guiUtils.createFormField("Start Time:", "HH:MM");
            VBox endTimeBox = guiUtils.createFormField("End Time:", "HH:MM");

            // Time fields in a row
            HBox timeRow = new HBox(10);
            timeRow.getChildren().addAll(startTimeBox, endTimeBox);

            // Get text fields for later use
            TextField startTimeField = (TextField) startTimeBox.getChildren().get(1);
            TextField endTimeField = (TextField) endTimeBox.getChildren().get(1);
            startTimeField.setPrefWidth(160);
            endTimeField.setPrefWidth(160);

            // Roles section
            VBox rolesSection = guiUtils.createStyledSection("#f8f9fa", "#e9ecef");

            Label rolesTitle = new Label("Required Quantities:");
            rolesTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-size: 13px;");

            Map<Role, Spinner<Integer>> roleSpinners = new HashMap<>();
            VBox rolesGrid = new VBox(6);

            for (Role role : Role.values()) {
                if(role == Role.ADMIN || role == Role.SUPPLIER_MANAGER || role == Role.SUPPLIER) {
                    continue;
                }
                HBox roleBox = new HBox(10);
                roleBox.setAlignment(Pos.CENTER_LEFT);

                Label roleLabel = new Label(role.toString() + ":");
                roleLabel.setPrefWidth(150);
                roleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #34495e;");

                Spinner<Integer> spinner = new Spinner<>(0, 10, 0);
                spinner.setPrefWidth(80);
                spinner.setStyle("-fx-font-size: 12px;");
                roleSpinners.put(role, spinner);

                roleBox.getChildren().addAll(roleLabel, spinner);
                rolesGrid.getChildren().add(roleBox);
            }

            rolesSection.getChildren().addAll(rolesTitle, rolesGrid);

            formBox.getChildren().addAll(branchBox, dateBox, timeSlotBox, timeRow, rolesSection);

            // Buttons section
            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setPadding(new Insets(8, 0, 5, 0));

            Button submitButton = guiUtils.createStyledButton("Set Requirements", "#27ae60", 140);
            Button cancelButton = guiUtils.createStyledButton("Cancel", "#95a5a6", 110);

            buttonBox.getChildren().addAll(submitButton, cancelButton);

            // Result label for messages
            Label resultLabel = new Label();
            resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
            resultLabel.setWrapText(true);
            resultLabel.setMaxWidth(380);

            // Button actions
            cancelButton.setOnAction(e -> dialog.close());

            submitButton.setOnAction(e -> {
                try {
                    // Validate input fields
                    if (branchCombo.getValue() == null || datePicker.getValue() == null ||
                            timeSlotCombo.getValue() == null || startTimeField.getText().isEmpty() ||
                            endTimeField.getText().isEmpty()) {
                        resultLabel.setText("Please fill in all required fields.");
                        resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
                        return;
                    }

                    String branchId = extractBranchId(branchCombo.getValue());
                    String date = datePicker.getValue().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    String timeSlot = timeSlotCombo.getValue();
                    String startTime = startTimeField.getText();
                    String endTime = endTimeField.getText();

                    Map<Role, Integer> rolesQuantities = new HashMap<>();
                    for (Map.Entry<Role, Spinner<Integer>> entry : roleSpinners.entrySet()) {
                        int quantity = entry.getValue().getValue();
                        if (quantity > 0) {
                            rolesQuantities.put(entry.getKey(), quantity);
                        }
                    }

                    Response response = domainService.setShiftRequirements(
                            branchId, date, timeSlot, startTime, endTime, rolesQuantities
                    );

                    if (response.isSuccess()) {
                        guiUtils.showDetailsDialog("Shift Requirements Set Successfully", response.getMessage());
                        dialog.close();
                    } else {
                        resultLabel.setText("Error: " + response.getMessage());
                        resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
                    }

                } catch (Exception ex) {
                    resultLabel.setText("Error: " + ex.getMessage());
                    resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
                    ex.printStackTrace();
                }
            });

            // Add all components to layout
            layout.getChildren().addAll(titleLabel, formBox, buttonBox, resultLabel);

            // Set content to scroll pane
            scrollPane.setContent(layout);

            // Create and show scene with better sizing
            Scene scene = new Scene(scrollPane, 420, 650);
            dialog.setScene(scene);
            dialog.setResizable(true);
            dialog.setMinWidth(380);
            dialog.setMinHeight(500);
            dialog.showAndWait();

        } catch (Exception e) {
            guiUtils.showErrorDialog("Error opening shift requirements dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleRemoveEmployeeGUI() {
        try {
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(parentStage);
            dialog.setTitle("Remove Employee");

            // Create ScrollPane for the main content
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: #f8f9fa;");

            VBox layout = new VBox(10);
            layout.setPadding(new Insets(15));
            layout.setStyle("-fx-background-color: #f8f9fa;");

            // Title
            Label titleLabel = new Label("Remove Employee");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            // Form section
            VBox formBox = new VBox(8);
            formBox.setStyle("-fx-background-color: #ffffff; -fx-padding: 12px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-border-color: #e0e0e0; -fx-border-width: 1px;");

            // Create employee selection using helper function
            VBox employeeBox = guiUtils.createComboBoxField("Select Employee:", "Choose employee to remove");
            ComboBox<String> employeeCombo = (ComboBox<String>) employeeBox.getChildren().get(1);

            // Load employees into the combo box
            try {
                loadEmployees(employeeCombo, domainService.displayEmployeesAndRole().getMessage());
            } catch (Exception e) {
                guiUtils.showErrorDialog("Error loading employees: " + e.getMessage());
                return;
            }

            // Warning section
            VBox warningBox = guiUtils.createWarningBox("Warning: This action cannot be undone. The employee will be permanently removed from the system.");

            formBox.getChildren().addAll(employeeBox, warningBox);

            // Buttons section
            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setPadding(new Insets(8, 0, 5, 0));

            Button removeButton = guiUtils.createStyledButton("Remove Employee", "#e74c3c", 140);
            Button cancelButton = guiUtils.createStyledButton("Cancel", "#95a5a6", 110);

            buttonBox.getChildren().addAll(removeButton, cancelButton);

            // Result label for messages
            Label resultLabel = new Label();
            resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
            resultLabel.setWrapText(true);
            resultLabel.setMaxWidth(380);

            // Button actions
            cancelButton.setOnAction(e -> dialog.close());

            removeButton.setOnAction(e -> {
                String selectedEmployee = employeeCombo.getValue();
                if (selectedEmployee == null) {
                    resultLabel.setText("Please select an employee to remove.");
                    resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
                    return;
                }

                try {
                    // Show confirmation dialog to user
                    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmAlert.setTitle("Confirm Employee Removal");
                    confirmAlert.setHeaderText("Are you sure you want to remove this employee?");
                    confirmAlert.setContentText("Selected employee: " + selectedEmployee + "\n\nThis action cannot be undone.");

                    // Set custom buttons
                    ButtonType yesButton = new ButtonType("Yes, Remove", ButtonBar.ButtonData.YES);
                    ButtonType noButton = new ButtonType("Cancel", ButtonBar.ButtonData.NO);
                    confirmAlert.getButtonTypes().setAll(yesButton, noButton);

                    // Wait for user response
                    Optional<ButtonType> result = confirmAlert.showAndWait();

                    // Execute action only if user confirmed
                    if (result.isPresent() && result.get() == yesButton) {
                        String employeeId = extractEmployeeId(selectedEmployee);
                        Response response = domainService.removeEmployee(employeeId);

                        if (response.isSuccess()) {
                            guiUtils.showDetailsDialog("Employee Removed Successfully", response.getMessage());
                            dialog.close();
                        } else {
                            resultLabel.setText("Error: " + response.getMessage());
                            resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
                        }
                    }
                } catch (Exception ex) {
                    resultLabel.setText("Error: " + ex.getMessage());
                    resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
                    ex.printStackTrace();
                }
            });

            // Add all components to layout
            layout.getChildren().addAll(titleLabel, formBox, buttonBox, resultLabel);

            // Set content to scroll pane
            scrollPane.setContent(layout);

            // Create and show scene with better sizing
            Scene scene = new Scene(scrollPane, 420, 320);
            dialog.setScene(scene);
            dialog.setResizable(true);
            dialog.setMinWidth(380);
            dialog.setMinHeight(280);
            dialog.showAndWait();

        } catch (Exception e) {
            guiUtils.showErrorDialog("Error opening remove employee dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleViewShiftDetailsGUI() {
        String[] shiftDetails = getShiftDetailsDialog();
        if (shiftDetails == null) {
            return; // User cancelled the dialog
        }

        // Call the domain service with the retrieved details
        try {
            Response response = domainService.displayShiftDetails(
                    shiftDetails[0], // branchId
                    shiftDetails[1], // date
                    shiftDetails[2]  // timeSlot
            );

            if (response.isSuccess()) {
                String shiftData = response.getMessage();

                Stage dialog = new Stage();
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.initOwner(parentStage);
                dialog.setTitle("Shift Details");

                // Create ScrollPane for the main content
                ScrollPane scrollPane = new ScrollPane();
                scrollPane.setFitToWidth(true);
                scrollPane.setStyle("-fx-background-color: #f8f9fa;");

                VBox layout = new VBox(10);
                layout.setPadding(new Insets(15));
                layout.setStyle("-fx-background-color: #f8f9fa;");

                // Title
                Label titleLabel = new Label("Shift Details");
                titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

                // Details section
                VBox detailsSection = guiUtils.createStyledSection("#ffffff", "#e9ecef");
                detailsSection.setPadding(new Insets(12));

                VBox shiftContainer = createShiftsContainer(shiftData);
                shiftContainer.setStyle("-fx-background-color: transparent;");
                detailsSection.getChildren().add(shiftContainer);

                // Buttons section
                HBox buttonBox = new HBox(10);
                buttonBox.setAlignment(Pos.CENTER);
                buttonBox.setPadding(new Insets(8, 0, 5, 0));

                Button closeButton = guiUtils.createStyledButton("Close", "#95a5a6", 110);
                closeButton.setOnAction(e -> dialog.close());
                buttonBox.getChildren().add(closeButton);

                // Add all components to layout
                layout.getChildren().addAll(titleLabel, detailsSection, buttonBox);

                // Set content to scroll pane
                scrollPane.setContent(layout);

                // Create and show scene with better sizing
                Scene scene = new Scene(scrollPane, 700, 600);
                dialog.setScene(scene);
                dialog.setResizable(true);
                dialog.setMinWidth(600);
                dialog.setMinHeight(500);
                dialog.showAndWait();

            } else {
                guiUtils.showErrorDialog(response.getMessage());
            }
        } catch (Exception ex) {
            guiUtils.showErrorDialog("Error viewing shift details: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void handleSetShiftSubmissionDeadlineGUI() {
        try {
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(parentStage);
            dialog.setTitle("Set Shift Submission Deadline");

            // Create ScrollPane for the main content
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: #f8f9fa;");

            VBox layout = new VBox(10);
            layout.setPadding(new Insets(15));
            layout.setStyle("-fx-background-color: #f8f9fa;");

            // Title
            Label titleLabel = new Label("Set Shift Submission Deadline");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            // Form section
            VBox formBox = new VBox(8);
            formBox.setStyle("-fx-background-color: #ffffff; -fx-padding: 12px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-border-color: #e0e0e0; -fx-border-width: 1px;");

            // Current deadline section
            VBox currentSection = guiUtils.createStyledSection("#f8f9fa", "#e9ecef");
            Label currentTitleLabel = new Label("Current Deadline:");
            currentTitleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-size: 13px;");

            Label currentTimeLabel = new Label();
            currentTimeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #34495e;");
            try {
                String currentDeadline = domainService.getCurrentDeadline();
                currentTimeLabel.setText(currentDeadline != null ? currentDeadline : "Not set");
            } catch (Exception ex) {
                currentTimeLabel.setText("Unable to load current deadline");
            }
            currentSection.getChildren().addAll(currentTitleLabel, currentTimeLabel);

            // New deadline section
            VBox timeSection = guiUtils.createStyledSection("#f8f9fa", "#e9ecef");
            Label timeTitleLabel = new Label("Select New Deadline Time:");
            timeTitleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-size: 13px;");

            // Time selection using spinners
            HBox timeRow = new HBox(10);
            timeRow.setAlignment(Pos.CENTER_LEFT);

            // Hour spinner section
            VBox hourBox = new VBox(3);
            Label hourLabel = new Label("Hour:");
            hourLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #34495e;");
            Spinner<Integer> hourSpinner = new Spinner<>(0, 23, 12);
            hourSpinner.setEditable(true);
            hourSpinner.setPrefWidth(80);
            hourSpinner.setStyle("-fx-font-size: 12px;");
            hourBox.getChildren().addAll(hourLabel, hourSpinner);

            // Minute spinner section
            VBox minuteBox = new VBox(3);
            Label minuteLabel = new Label("Minute:");
            minuteLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #34495e;");
            Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, 0, 15);
            minuteSpinner.setEditable(true);
            minuteSpinner.setPrefWidth(80);
            minuteSpinner.setStyle("-fx-font-size: 12px;");
            minuteBox.getChildren().addAll(minuteLabel, minuteSpinner);

            timeRow.getChildren().addAll(hourBox, minuteBox);

            // Format note
            Label formatLabel = new Label("(24-hour format)");
            formatLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");

            // Selected time display
            HBox timeDisplayBox = new HBox(5);
            timeDisplayBox.setAlignment(Pos.CENTER_LEFT);
            timeDisplayBox.setPadding(new Insets(5, 0, 0, 0));
            Label selectedTimeLabel = new Label("Selected time:");
            selectedTimeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #34495e;");
            Label timeDisplayLabel = new Label("12:00");
            timeDisplayLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");
            timeDisplayBox.getChildren().addAll(selectedTimeLabel, timeDisplayLabel);

            // Update display when spinners change
            Runnable updateTimeDisplay = () -> {
                String timeString = String.format("%02d:%02d",
                        hourSpinner.getValue(), minuteSpinner.getValue());
                timeDisplayLabel.setText(timeString);
            };

            hourSpinner.valueProperty().addListener((obs, oldVal, newVal) -> updateTimeDisplay.run());
            minuteSpinner.valueProperty().addListener((obs, oldVal, newVal) -> updateTimeDisplay.run());

            timeSection.getChildren().addAll(timeTitleLabel, timeRow, formatLabel, timeDisplayBox);

            formBox.getChildren().addAll(currentSection, timeSection);

            // Buttons section
            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setPadding(new Insets(8, 0, 5, 0));

            Button setButton = guiUtils.createStyledButton("Set Deadline", "#27ae60", 130);
            Button cancelButton = guiUtils.createStyledButton("Cancel", "#95a5a6", 110);

            buttonBox.getChildren().addAll(setButton, cancelButton);

            // Result label for messages
            Label resultLabel = new Label();
            resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
            resultLabel.setWrapText(true);
            resultLabel.setMaxWidth(380);

            // Button actions
            cancelButton.setOnAction(e -> dialog.close());

            setButton.setOnAction(e -> {
                try {
                    String newDeadline = String.format("%02d:%02d",
                            hourSpinner.getValue(), minuteSpinner.getValue());

                    Response response = domainService.setShiftSubmissionDeadline(newDeadline);

                    if (response.isSuccess()) {
                        guiUtils.showDetailsDialog("Deadline Updated Successfully",
                                "Shift submission deadline has been set to: " + newDeadline);
                        dialog.close();
                    } else {
                        resultLabel.setText("Error: " + response.getMessage());
                        resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
                    }

                } catch (Exception ex) {
                    resultLabel.setText("Error: " + ex.getMessage());
                    resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
                    ex.printStackTrace();
                }
            });

            // Add all components to layout
            layout.getChildren().addAll(titleLabel, formBox, buttonBox, resultLabel);

            // Set content to scroll pane
            scrollPane.setContent(layout);

            // Create and show scene with better sizing
            Scene scene = new Scene(scrollPane, 450, 550);
            dialog.setScene(scene);
            dialog.setResizable(true);
            dialog.setMinWidth(400);
            dialog.setMinHeight(450);
            dialog.showAndWait();

        } catch (Exception e) {
            guiUtils.showErrorDialog("Error opening deadline setting dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void handleUpdateEmployeeDetailsGUI() {
        try {
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(parentStage);
            dialog.setTitle("Employee Editor");

            // Create ScrollPane for the main content
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: #f8f9fa;");

            VBox layout = new VBox(10);
            layout.setPadding(new Insets(15));
            layout.setStyle("-fx-background-color: #f8f9fa;");

            // Title
            Label titleLabel = new Label("Employee Information Editor");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            // Form section
            VBox formBox = new VBox(8);
            formBox.setStyle("-fx-background-color: #ffffff; -fx-padding: 12px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-border-color: #e0e0e0; -fx-border-width: 1px;");

            // Employee selection
            VBox employeeBox = guiUtils.createComboBoxField("Selected Employee:", "Choose employee");
            ComboBox<String> employeeCombo = (ComboBox<String>) employeeBox.getChildren().get(1);
            try {
                loadEmployees(employeeCombo, domainService.displayEmployeesAndRole().getMessage());
            } catch (Exception e) {
                guiUtils.showErrorDialog("Error loading employees: " + e.getMessage());
                return;
            }

            // Category selection
            VBox categoryBox = guiUtils.createComboBoxField("Category:", "Choose category");
            ComboBox<String> categoryCombo = (ComboBox<String>) categoryBox.getChildren().get(1);
            categoryCombo.getItems().addAll("Name", "Bank Account", "Work Conditions", "Roles");

            // New details input
            VBox detailsBox = guiUtils.createFormField("New Details:", "Enter new details");
            TextField newDetailsField = (TextField) detailsBox.getChildren().get(1);

            // License section (conditional)
            VBox licenseSection = guiUtils.createStyledSection("#f8f9fa", "#e9ecef");
            licenseSection.setVisible(false);
            licenseSection.setManaged(false);

            Label licenseTitleLabel = new Label("Driver License (Required for Role Updates):");
            licenseTitleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-size: 13px;");

            VBox licenseBox = guiUtils.createFormField("License:", "Enter driver license");
            TextField licenseField = (TextField) licenseBox.getChildren().get(1);

            licenseSection.getChildren().addAll(licenseTitleLabel, licenseBox);

            // Show/hide license field based on category selection
            categoryCombo.setOnAction(e -> {
                boolean showLicense = "Roles".equals(categoryCombo.getValue());
                licenseSection.setVisible(showLicense);
                licenseSection.setManaged(showLicense);
            });

            formBox.getChildren().addAll(employeeBox, categoryBox, detailsBox, licenseSection);

            // Buttons section
            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setPadding(new Insets(8, 0, 5, 0));

            Button submitButton = guiUtils.createStyledButton("Set New Details", "#27ae60", 140);
            Button cancelButton = guiUtils.createStyledButton("Cancel", "#95a5a6", 110);

            buttonBox.getChildren().addAll(submitButton, cancelButton);

            // Result label for messages
            Label resultLabel = new Label();
            resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
            resultLabel.setWrapText(true);
            resultLabel.setMaxWidth(380);

            // Button actions
            cancelButton.setOnAction(e -> dialog.close());

            submitButton.setOnAction(e -> {
                try {
                    // Validate input fields
                    if (employeeCombo.getValue() == null || categoryCombo.getValue() == null ||
                            newDetailsField.getText().isEmpty()) {
                        resultLabel.setText("Please fill in all required fields.");
                        resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
                        return;
                    }

                    String employeeID = extractEmployeeId(employeeCombo.getValue());
                    String category = categoryCombo.getValue();
                    String newDet = newDetailsField.getText();

                    Response response = switch (category) {
                        case "Name" -> domainService.updateEmployeeName(employeeID, newDet);
                        case "Bank Account" -> domainService.updateEmployeeBankAccount(employeeID, newDet);
                        case "Work Conditions" -> domainService.updateEmployeeWorkConditions(employeeID, newDet);
                        case "Roles" -> {
                            String license = licenseField.getText();
                            if (license.isEmpty()) {
                                resultLabel.setText("Please enter driver license when updating roles.");
                                resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
                                yield null;
                            }
                            yield domainService.updateEmployeeRoles(employeeID, newDet, license);
                        }
                        default -> null;
                    };

                    if (response != null) {
                        if (response.isSuccess()) {
                            guiUtils.showDetailsDialog("Employee Updated Successfully", response.getMessage());
                            dialog.close();
                        } else {
                            resultLabel.setText("Error: " + response.getMessage());
                            resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
                        }
                    }

                } catch (Exception ex) {
                    resultLabel.setText("Error: " + ex.getMessage());
                    resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
                    ex.printStackTrace();
                }
            });

            // Add all components to layout
            layout.getChildren().addAll(titleLabel, formBox, buttonBox, resultLabel);

            // Set content to scroll pane
            scrollPane.setContent(layout);

            // Create and show scene with better sizing
            Scene scene = new Scene(scrollPane, 450, 550);
            dialog.setScene(scene);
            dialog.setResizable(true);
            dialog.setMinWidth(400);
            dialog.setMinHeight(450);
            dialog.showAndWait();

        } catch (Exception e) {
            guiUtils.showErrorDialog("Error opening employee details dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleEmployeeShiftAssignmentGUI() {

        try {
            //get shift details from user
            String[] shiftDetails = getShiftDetailsDialog();
            if (shiftDetails == null) {
                return; // User cancelled the dialog
            }

            String branchId = shiftDetails[0];
            String date = shiftDetails[1];
            String timeSlot = shiftDetails[2];

            //Display shift details
            Response shiftDetailsResponse = domainService.displayShiftDetails(branchId, date, timeSlot);
            if (!shiftDetailsResponse.isSuccess()) {
                guiUtils.showErrorDialog("Shift Error: " + shiftDetailsResponse.getMessage());
                return;
            }
            guiUtils.showDetailsDialog("Shift Details", shiftDetailsResponse.getMessage());


            //Arlert if not enough employees are available
            Response availabilityResponse = domainService.alertNotEnoughEmployeeAvailability(branchId, date, timeSlot);
            if (!availabilityResponse.getMessage().isEmpty()) {
                guiUtils.showWarningDialog("Employee Availability Warning", availabilityResponse.getMessage());
            }

            //Open assignment dialog
            showEmployeeAssignmentDialog(branchId, date, timeSlot, shiftDetailsResponse.getMessage());

        } catch (Exception e) {
            guiUtils.showErrorDialog("Error: " + e.getMessage());
            e.printStackTrace();
        }

    }


    public void handleEditShiftGUI() {
        try {
            // Get shift details from user
            String[] shiftDetails = getShiftDetailsDialog();
            if (shiftDetails == null) {
                return; // User cancelled the dialog
            }

            // Display shift details
            Response shiftDetailsResponse = domainService.displayShiftDetails(
                    shiftDetails[0], // branchId
                    shiftDetails[1], // date
                    shiftDetails[2]  // timeSlot
            );

            if (!shiftDetailsResponse.isSuccess()) {
                guiUtils.showErrorDialog("Shift Error: " + shiftDetailsResponse.getMessage());
                return;
            }

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(parentStage);
            dialog.setTitle("Edit Shift");

            // Create ScrollPane for the main content
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: #f8f9fa;");

            VBox layout = new VBox(10);
            layout.setPadding(new Insets(15));
            layout.setStyle("-fx-background-color: #f8f9fa;");

            // Title
            Label titleLabel = new Label("Edit Shift");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            // Shift info section
            VBox shiftInfoBox = new VBox(8);
            shiftInfoBox.setStyle("-fx-background-color: #ffffff; -fx-padding: 12px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-border-color: #e0e0e0; -fx-border-width: 1px;");

            Label shiftInfoTitle = new Label("Shift Information");
            shiftInfoTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-size: 13px;");

            Label branchLabel = new Label("Branch: " + shiftDetails[0]);
            branchLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #34495e;");

            Label dateLabel = new Label("Date: " + shiftDetails[1]);
            dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #34495e;");

            Label timeSlotLabel = new Label("Time Slot: " + shiftDetails[2]);
            timeSlotLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #34495e;");

            Label shiftDetailsLabel = new Label("Details:");
            shiftDetailsLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

            TextArea shiftDetailsArea = new TextArea(shiftDetailsResponse.getMessage());
            shiftDetailsArea.setEditable(false);
            shiftDetailsArea.setPrefRowCount(4);
            shiftDetailsArea.setWrapText(true);
            shiftDetailsArea.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #d0d0d0; -fx-border-radius: 4px; -fx-font-size: 11px;");

            shiftInfoBox.getChildren().addAll(shiftInfoTitle, branchLabel, dateLabel, timeSlotLabel, shiftDetailsLabel, shiftDetailsArea);

            // Options section
            VBox optionsBox = guiUtils.createStyledSection("#f8f9fa", "#e9ecef");

            Label optionsTitle = new Label("Select Action:");
            optionsTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-size: 13px;");

            ToggleGroup optionsGroup = new ToggleGroup();

            RadioButton removeOption = new RadioButton("Remove an employee from the shift");
            removeOption.setStyle("-fx-font-size: 12px; -fx-text-fill: #34495e;");
            removeOption.setToggleGroup(optionsGroup);

            RadioButton addOption = new RadioButton("Add an employee to the shift");
            addOption.setStyle("-fx-font-size: 12px; -fx-text-fill: #34495e;");
            addOption.setToggleGroup(optionsGroup);

            RadioButton editSlotOption = new RadioButton("Edit shift time-slot");
            editSlotOption.setStyle("-fx-font-size: 12px; -fx-text-fill: #34495e;");
            editSlotOption.setToggleGroup(optionsGroup);

            VBox radioButtonsBox = new VBox(6);
            radioButtonsBox.getChildren().addAll(removeOption, addOption, editSlotOption);

            optionsBox.getChildren().addAll(optionsTitle, radioButtonsBox);

            // Buttons section
            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setPadding(new Insets(8, 0, 5, 0));

            Button proceedButton = guiUtils.createStyledButton("Proceed", "#27ae60", 120);
            Button cancelButton = guiUtils.createStyledButton("Cancel", "#95a5a6", 110);

            buttonBox.getChildren().addAll(proceedButton, cancelButton);

            // Result label for messages
            Label resultLabel = new Label();
            resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
            resultLabel.setWrapText(true);
            resultLabel.setMaxWidth(450);

            // Button actions
            cancelButton.setOnAction(e -> dialog.close());

            proceedButton.setOnAction(e -> {
                RadioButton selected = (RadioButton) optionsGroup.getSelectedToggle();
                if (selected == null) {
                    resultLabel.setText("Please select an action to proceed.");
                    resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
                    return;
                }

                try {
                    boolean success = false;

                    if (selected == removeOption) {
                        success = openRemoveEmployeeDialog(shiftDetails, shiftDetailsResponse.getMessage());
                    } else if (selected == addOption) {
                        success = openAddEmployeeDialog(shiftDetails, shiftDetailsResponse.getMessage());
                    } else { // editSlotOption
                        success = openEditTimeSlotDialog(shiftDetails, shiftDetailsResponse.getMessage());
                    }

                    if (success) {
                        dialog.close();
                    }

                } catch (Exception ex) {
                    resultLabel.setText("Error: " + ex.getMessage());
                    resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
                    ex.printStackTrace();
                }
            });

            // Add all components to layout
            layout.getChildren().addAll(titleLabel, shiftInfoBox, optionsBox, buttonBox, resultLabel);

            // Set content to scroll pane
            scrollPane.setContent(layout);

            // Create and show scene with better sizing
            Scene scene = new Scene(scrollPane, 480, 620);
            dialog.setScene(scene);
            dialog.setResizable(true);
            dialog.setMinWidth(450);
            dialog.setMinHeight(550);
            dialog.showAndWait();

        } catch (Exception e) {
            guiUtils.showErrorDialog("Error opening edit shift dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleAddDeliveryGUI() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add New Delivery");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f8f9fa;");

        // Title
        Label titleLabel = new Label("Add New Delivery");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Date input
        Label dateLabel = new Label("Delivery Date (DD/MM/YYYY):");
        dateLabel.setStyle("-fx-font-weight: bold;");
        DatePicker datePicker = new DatePicker();
        datePicker.setPrefWidth(200);

        // Departure time input
        Label departureLabel = new Label("Departure Time (HH:MM):");
        departureLabel.setStyle("-fx-font-weight: bold;");
        TextField departureField = new TextField();
        departureField.setPromptText("e.g., 14:30");
        departureField.setPrefWidth(200);

        // Time slot selection
        Label timeSlotLabel = new Label("Shift Time Slot:");
        timeSlotLabel.setStyle("-fx-font-weight: bold;");
        ComboBox<String> timeSlotCombo = new ComboBox<>();
        timeSlotCombo.getItems().addAll("M (Morning)", "E (Evening)");
        timeSlotCombo.setPrefWidth(200);

        // Truck selection
        Label truckLabel = new Label("Select Truck:");
        truckLabel.setStyle("-fx-font-weight: bold;");
        ComboBox<String> truckCombo = new ComboBox<>();
        loadTrucks(truckCombo);
        truckCombo.setPrefWidth(200);

        // Driver selection
        Label driverLabel = new Label("Select Driver:");
        driverLabel.setStyle("-fx-font-weight: bold;");
        ComboBox<String> driverCombo = new ComboBox<>();
        loadDrivers(driverCombo);
        driverCombo.setPrefWidth(200);

        // Source site selection
        Label sourceLabel = new Label("Source Site:");
        sourceLabel.setStyle("-fx-font-weight: bold;");
        ComboBox<String> sourceCombo = new ComboBox<>();
        loadSites(sourceCombo);
        sourceCombo.setPrefWidth(200);

        // Destination sites input
        Label destinationsLabel = new Label("Destination Sites (comma separated addresses):");
        destinationsLabel.setStyle("-fx-font-weight: bold;");
        TextArea destinationsArea = new TextArea();
        destinationsArea.setPromptText("Enter addresses separated by commas\ne.g., address1, address2, address3");
        destinationsArea.setPrefRowCount(3);
        destinationsArea.setPrefWidth(400);
        destinationsArea.setWrapText(true);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button addButton = new Button("Add Delivery");
        addButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 20px;");

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 20px;");

        buttonBox.getChildren().addAll(addButton, cancelButton);

        // Result label for messages
        Label resultLabel = new Label();
        resultLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        resultLabel.setWrapText(true);

        // Button actions
        cancelButton.setOnAction(e -> dialog.close());

        addButton.setOnAction(e -> {
            try {
                // Validate inputs
                if (datePicker.getValue() == null || departureField.getText().trim().isEmpty() || timeSlotCombo.getValue() == null ||
                        truckCombo.getValue() == null || driverCombo.getValue() == null ||
                        sourceCombo.getValue() == null || destinationsArea.getText().trim().isEmpty()) {
                    resultLabel.setText("Please fill in all fields.");
                    resultLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    return;
                }

                // Format date
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String formattedDate = datePicker.getValue().format(formatter);

                // Extract time slot letter
                String timeSlot = timeSlotCombo.getValue().substring(0, 1); // "M" or "E"

                // Extract IDs from combo selections (assuming format includes ID)
                String truckId = extractTruckId(truckCombo.getValue());
                String driverId = extractEmployeeId(driverCombo.getValue());

                // Call domain service
                Response response = domainService.addDelivery(
                        formattedDate,
                        departureField.getText().trim(),
                        timeSlot,
                        truckId,
                        driverId,
                        extractSiteAddress(sourceCombo.getValue()),
                        destinationsArea.getText().trim()
                );

                if (response.isSuccess()) {
                    guiUtils.showDetailsDialog("", response.getMessage());
                    dialog.close();
                } else {
                    guiUtils.showErrorDialog("Error adding delivery:\n" + response.getMessage());
                }

            } catch (Exception ex) {
                guiUtils.showErrorDialog("Error adding delivery:\n" + ex.getMessage());
            }
        });

        // Add all components to layout
        layout.getChildren().addAll(
                titleLabel,
                new Separator(),
                dateLabel, datePicker,
                departureLabel, departureField,
                timeSlotLabel, timeSlotCombo,
                truckLabel, truckCombo,
                driverLabel, driverCombo,
                sourceLabel, sourceCombo,
                destinationsLabel, destinationsArea,
                new Separator(),
                buttonBox,
                resultLabel
        );

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #f8f9fa;");

        Scene scene = new Scene(scrollPane, 500, 700);
        dialog.setScene(scene);
        dialog.setMinWidth(450);
        dialog.setMinHeight(600);
        dialog.showAndWait();
    }


    public void handleAddTruckGUI() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add New Truck");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f8f9fa;");

        // Title
        Label titleLabel = new Label("Add New Truck");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Truck ID input
        Label idLabel = new Label("Truck ID:");
        idLabel.setStyle("-fx-font-weight: bold;");
        TextField idField = new TextField();
        idField.setPromptText("Enter truck ID");
        idField.setPrefWidth(200);

        // Truck model input
        Label modelLabel = new Label("Truck Model:");
        modelLabel.setStyle("-fx-font-weight: bold;");
        TextField modelField = new TextField();
        modelField.setPromptText("Enter truck model");
        modelField.setPrefWidth(200);

        // Truck Net Weight input
        Label capacityLabel = new Label("Truck Net Weight (kg):");
        capacityLabel.setStyle("-fx-font-weight: bold;");
        TextField capacityField = new TextField();
        capacityField.setPromptText("Enter truck net weight in kg");
        capacityField.setPrefWidth(200);

        // Truck Max Weight input
        Label maxLabel = new Label("Truck Max Weight (kg):");
        maxLabel.setStyle("-fx-font-weight: bold;");
        TextField maxField = new TextField();
        maxField.setPromptText("Enter truck max weight in kg");
        maxField.setPrefWidth(200);

        // Truck License Plate input
        Label licenseLabel = new Label("Truck License:");
        licenseLabel.setStyle("-fx-font-weight: bold;");
        TextField licenseField = new TextField();
        licenseField.setPromptText("Enter truck license plate");
        licenseField.setPrefWidth(200);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button addButton = new Button("Add Truck");
        addButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 20px;");

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 20px;");

        buttonBox.getChildren().addAll(addButton, cancelButton);

        // Result label for messages
        Label resultLabel = new Label();
        resultLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        resultLabel.setWrapText(true);

        // Add all components to layout
        layout.getChildren().addAll(
                titleLabel,
                idLabel, idField,
                modelLabel, modelField,
                capacityLabel, capacityField,
                maxLabel, maxField,
                licenseLabel, licenseField,
                buttonBox,
                resultLabel
        );

        // Button actions
        cancelButton.setOnAction(e -> dialog.close());

        addButton.setOnAction(e -> {
            try {
                // Validate inputs
                if (idField.getText().trim().isEmpty() || modelField.getText().trim().isEmpty() ||
                        capacityField.getText().trim().isEmpty() || maxField.getText().trim().isEmpty() ||
                        licenseField.getText().trim().isEmpty()) {
                    resultLabel.setText("Please fill in all fields.");
                    resultLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    return;
                }

                // Parse and validate numeric inputs
                int netWeight, maxWeight;
                try {
                    netWeight = Integer.parseInt(capacityField.getText().trim());
                    maxWeight = Integer.parseInt(maxField.getText().trim());
                } catch (NumberFormatException ex) {
                    resultLabel.setText("Please enter valid numeric values for weights.");
                    resultLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    return;
                }

                // Call domain service to add truck
                Response response = domainService.addTruck(
                        idField.getText().trim(),
                        modelField.getText().trim(),
                        netWeight,
                        maxWeight,
                        licenseField.getText().trim()
                );

                if (response.isSuccess()) {
                    guiUtils.showDetailsDialog("Success", response.getMessage());
                    dialog.close();
                } else {
                    guiUtils.showErrorDialog("Error adding truck:\n" + response.getMessage());
                }

            } catch (Exception ex) {
                guiUtils.showErrorDialog("Error adding truck:\n" + ex.getMessage());
            }
        });

        Scene scene = new Scene(layout, 350, 500);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.showAndWait();
    }

    public void handleAddBranchGUI() {
        try {
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Add New Branch");

            // Create ScrollPane for the main content
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: #f8f9fa;");

            VBox layout = new VBox(10); // Reduced from 15
            layout.setPadding(new Insets(15)); // Reduced from 20
            layout.setStyle("-fx-background-color: #f8f9fa;");

            // Title
            Label titleLabel = new Label("Add New Branch");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;"); // Reduced from 20px

            // Form section
            VBox formBox = new VBox(8); // Reduced from 12
            formBox.setStyle("-fx-background-color: #ffffff; -fx-padding: 12px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-border-color: #e0e0e0; -fx-border-width: 1px;"); // Reduced padding from 15px

            // Create form fields with more compact styling
            VBox nameBox = guiUtils.createFormField("Branch Name:", "Enter branch name");
            VBox idBox = guiUtils.createFormField("Branch ID:", "Enter branch ID");
            VBox addressBox = guiUtils.createFormField("Branch Address:", "Enter branch address");
            VBox contactNameBox = guiUtils.createFormField("Contact Name:", "Enter contact name");
            VBox contactNumberBox = guiUtils.createFormField("Contact Number:", "Enter contact number");

            // Get text fields for later use
            TextField nameField = (TextField) nameBox.getChildren().get(1);
            TextField idField = (TextField) idBox.getChildren().get(1);
            TextField addressField = (TextField) addressBox.getChildren().get(1);
            TextField contactNameField = (TextField) contactNameBox.getChildren().get(1);
            TextField contactNumberField = (TextField) contactNumberBox.getChildren().get(1);

            formBox.getChildren().addAll(nameBox, idBox, addressBox, contactNameBox, contactNumberBox);

            // Buttons section
            HBox buttonBox = new HBox(10); // Reduced from 15
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setPadding(new Insets(8, 0, 5, 0)); // Reduced padding

            Button addButton = new Button("Add Branch");
            addButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8px 16px; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-cursor: hand;"); // Reduced padding
            addButton.setPrefWidth(110); // Reduced from 120

            Button cancelButton = new Button("Cancel");
            cancelButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8px 16px; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-cursor: hand;"); // Reduced padding
            cancelButton.setPrefWidth(110); // Reduced from 120

            buttonBox.getChildren().addAll(addButton, cancelButton);

            // Result label for messages
            Label resultLabel = new Label();
            resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;"); // Reduced font size
            resultLabel.setWrapText(true);
            resultLabel.setMaxWidth(380); // Reduced from 400

            // Button actions
            cancelButton.setOnAction(e -> dialog.close());

            addButton.setOnAction(e -> {
                try {
                    // Validate input fields
                    if (nameField.getText().isEmpty() || idField.getText().isEmpty() ||
                            addressField.getText().isEmpty() || contactNameField.getText().isEmpty() ||
                            contactNumberField.getText().isEmpty()) {
                        resultLabel.setText("Please enter all fields.");
                        resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
                        return;
                    }

                    // Call domain service to add branch
                    Response response = domainService.addBranch(
                            nameField.getText(),
                            idField.getText().trim(),
                            addressField.getText(),
                            contactNameField.getText(),
                            contactNumberField.getText().trim()
                    );

                    if (response.isSuccess()) {
                        guiUtils.showDetailsDialog("Branch Added Successfully", response.getMessage());
                        dialog.close();
                    } else {
                        resultLabel.setText("Error: " + response.getMessage());
                        resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
                    }

                } catch (Exception ex) {
                    resultLabel.setText("Error: " + ex.getMessage());
                    resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
                    ex.printStackTrace();
                }
            });

            // Add all components to layout
            layout.getChildren().addAll(titleLabel, formBox, buttonBox, resultLabel);

            // Set content to scroll pane
            scrollPane.setContent(layout);

            // Create and show scene with better sizing
            Scene scene = new Scene(scrollPane, 420, 480); // Reduced from 450x600
            dialog.setScene(scene);
            dialog.setResizable(true);
            dialog.setMinWidth(380); // Reduced from 400
            dialog.setMinHeight(450); // Reduced from 500
            dialog.showAndWait();

        } catch (Exception e) {
            guiUtils.showErrorDialog("Error opening add branch dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void handleViewMyShiftsGUI(String id) {
        try {
            Response response = domainService.employeeFutureShifts(id);

            // Check if the response is valid
            if (!response.isSuccess()) {
                // Display error message to user
                guiUtils.showErrorDialog("Error retrieving shifts: " + response.getMessage());
                return;
            }

            // Format and display the shifts
            showShiftsDialog(response.getMessage());

        } catch (Exception e) {
            // Handle exceptions
            guiUtils.showErrorDialog("Error retrieving shifts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleSubmitAvailabilityGUI(String id) {
        try {
            // Check if it's valid time to submit
            Response checkValidTimeToSubmit = domainService.checkValidTimeToSubmit();

            if (!checkValidTimeToSubmit.isSuccess()) {
                guiUtils.showErrorDialog("Error: " + checkValidTimeToSubmit.getMessage());
                return;
            }

            // Create the stage
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Submit Weekly Availability");
            dialog.setResizable(false);

            // Main container
            VBox mainContainer = new VBox(20);
            mainContainer.setPadding(new Insets(20));
            mainContainer.setAlignment(Pos.CENTER);

            // Title
            Label titleLabel = new Label("Select Your Preferred Shifts for Next Week");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

            // Instructions
            Label instructionsLabel = new Label("Choose Morning (M) and/or Evening (E) shifts for each day:");
            instructionsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");

            // Days container
            VBox daysContainer = new VBox(15);
            daysContainer.setAlignment(Pos.CENTER_LEFT);
            daysContainer.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1px; -fx-border-radius: 5px;");
            daysContainer.setPadding(new Insets(15));

            // Store checkbox references for each day
            Map<LocalDate, CheckBox[]> dayCheckboxes = new HashMap<>();
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMM dd");

            // Create checkboxes for each day of the week
            for (int i = 1; i <= 7; i++) {
                LocalDate date = today.plusDays(i);

                // Day container
                HBox dayContainer = new HBox(15);
                dayContainer.setAlignment(Pos.CENTER_LEFT);

                // Day label
                Label dayLabel = new Label(date.format(formatter));
                dayLabel.setPrefWidth(150);
                dayLabel.setStyle("-fx-font-weight: bold;");

                // Morning checkbox
                CheckBox morningCheckBox = new CheckBox("Morning");
                morningCheckBox.setStyle("-fx-font-size: 12px;");

                // Evening checkbox
                CheckBox eveningCheckBox = new CheckBox("Evening");
                eveningCheckBox.setStyle("-fx-font-size: 12px;");

                // Store checkboxes
                dayCheckboxes.put(date, new CheckBox[]{morningCheckBox, eveningCheckBox});

                dayContainer.getChildren().addAll(dayLabel, morningCheckBox, eveningCheckBox);
                daysContainer.getChildren().add(dayContainer);
            }

            // Buttons container
            HBox buttonsContainer = new HBox(10);
            buttonsContainer.setAlignment(Pos.CENTER);

            // Submit button
            Button submitButton = new Button("Submit Availability");
            submitButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 20px;");
            submitButton.setOnAction(e -> {
                try {
                    // Collect availability data
                    Map<LocalDate, List<String>> availability = new HashMap<>();

                    for (Map.Entry<LocalDate, CheckBox[]> entry : dayCheckboxes.entrySet()) {
                        LocalDate date = entry.getKey();
                        CheckBox[] checkboxes = entry.getValue();

                        List<String> shifts = new ArrayList<>();

                        if (checkboxes[0].isSelected()) { // Morning
                            shifts.add("M");
                        }
                        if (checkboxes[1].isSelected()) { // Evening
                            shifts.add("E");
                        }

                        availability.put(date, shifts);
                    }

                    // Submit to domain service
                    Response response = domainService.employeeShiftAvailability(id, availability);

                    // Show result - BOTH success and failure cases
                    if (response.isSuccess()) {
                        guiUtils.showDetailsDialog("Availability Submitted", response.getMessage());
                        dialog.close();
                    } else {
                        // This was missing - show error message
                        guiUtils.showErrorDialog("Failed to submit availability: " + response.getMessage());
                    }

                } catch (Exception ex) {
                    guiUtils.showErrorDialog("Error submitting availability: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });

            // Cancel button
            Button cancelButton = new Button("Cancel");
            cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 20px;");
            cancelButton.setOnAction(e -> dialog.close());

            // Select All button for convenience
            Button selectAllButton = new Button("Select All");
            selectAllButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-padding: 8px 16px;");
            selectAllButton.setOnAction(e -> {
                for (CheckBox[] checkboxes : dayCheckboxes.values()) {
                    checkboxes[0].setSelected(true); // Morning
                    checkboxes[1].setSelected(true); // Evening
                }
            });

            // Clear All button
            Button clearAllButton = new Button("Clear All");
            clearAllButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-padding: 8px 16px;");
            clearAllButton.setOnAction(e -> {
                for (CheckBox[] checkboxes : dayCheckboxes.values()) {
                    checkboxes[0].setSelected(false);
                    checkboxes[1].setSelected(false);
                }
            });

            // Quick actions container
            HBox quickActionsContainer = new HBox(10);
            quickActionsContainer.setAlignment(Pos.CENTER);
            quickActionsContainer.getChildren().addAll(selectAllButton, clearAllButton);

            buttonsContainer.getChildren().addAll(cancelButton, submitButton);

            // Add all components to main container
            mainContainer.getChildren().addAll(
                    titleLabel,
                    instructionsLabel,
                    daysContainer,
                    quickActionsContainer,
                    buttonsContainer
            );

            // Create scene and show
            Scene scene = new Scene(mainContainer, 500, 600);
            dialog.setScene(scene);
            dialog.showAndWait();

        } catch (Exception ex) {
            guiUtils.showErrorDialog("Error submitting availability: " + ex.getMessage());
        }
    }


    public Response validateDet(String id) {
        try {
            return domainService.validateDet(id);
        } catch (Exception e) {
            return new Response(false, "Error validating details: " + e.getMessage());
        }
    }

    public boolean setup() throws Exception {
        //Branches
        domainService.addBranch("Oren", "02", "Oren Center","Shani Kro","050-111222");
        domainService.addBranch("Hen", "03", "Hen Center","Dudi Cohen","050-666555");

        //Branch 01
        domainService.addEmployee("Gaya Aviv", "000111222", "4545 789 67868","545 23/09/2014 56 76 4567890","SHIFT_MANAGER CASHIER", "01", "");
        domainService.addEmployee("Dani Kushmaro", "999999990", "4545 789 67868","545 23/09/2014 56 76 4567890","DRIVER", "01", "A");
        domainService.addEmployee("Justin Biber", "999999990", "4545 789 67868","545 23/09/2014 56 76 4567890","DRIVER", "01", "A B");
        domainService.addEmployee("Kobi Aflalo", "999999991", "4545 789 67868","545 23/09/2014 56 76 4567890","CASHIER", "01", "");
        domainService.addEmployee("Beyonce", "999999992", "4545 789 67868","545 23/09/2014 56 76 pension:4567890","STORE_KEEPER", "01", "");

        //Branch 02
        domainService.addEmployee("Lior Azulay", "111222333", "4545 789 67868", "545 23/09/2014 56 76 1234567", "SHIFT_MANAGER CASHIER", "02", "");
        domainService.addEmployee("Ron Cohen", "345678912", "4545 789 67868", "545 23/09/2014 56 76 3456789", "DRIVER", "02", "B");
        domainService.addEmployee("Ella Friedman", "456789123", "4545 789 67868", "545 23/09/2014 56 76 4567891", "DRIVER", "02", "A B");
        domainService.addEmployee("Yossi Peretz", "567891234", "4545 789 67868", "545 23/09/2014 56 76 5678912", "CASHIER", "02", "");
        domainService.addEmployee("Neta Barzilai", "678912345", "4545 789 67868", "545 23/09/2014 56 76 pension:6789123", "STORE_KEEPER", "02", "");

        //Branch 03
        domainService.addEmployee("Noa Tishby", "789123456", "4545 789 67868", "545 23/09/2014 56 76 7891234", "SHIFT_MANAGER CASHIER", "03", "");
        domainService.addEmployee("Dana Abramovich", "912345678", "4545 789 67868", "545 23/09/2014 56 76 9123456", "DRIVER", "03", "A");
        domainService.addEmployee("Amit Gross", "102345679", "4545 789 67868", "545 23/09/2014 56 76 1023456", "DRIVER", "03", "A B");
        domainService.addEmployee("Shira Hadad", "112345680", "4545 789 67868", "545 23/09/2014 56 76 1123456", "CASHIER", "03", "");
        domainService.addEmployee("Maya Cohen", "122345681", "4545 789 67868", "545 23/09/2014 56 76 pension:1223456", "STORE_KEEPER", "03", "");

        //Submission Deadline
        domainService.setShiftSubmissionDeadline("16:00");

        //Shifts for the Upcoming Week
        Map<Role, Integer> rolesQuantities = new HashMap<>();
        rolesQuantities.put(Role.SHIFT_MANAGER, 1);
        rolesQuantities.put(Role.CASHIER, 2);
        rolesQuantities.put(Role.DRIVER, 1);
        rolesQuantities.put(Role.ADMIN, 0);
        rolesQuantities.put(Role.STORE_KEEPER, 1);
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for(int i = 1; i < 8; i++) {
            LocalDate shiftDate = today.plusDays(i);
            String dateStr = shiftDate.format(formatter);
            domainService.setShiftRequirements("01", dateStr, "M", "08:00", "16:00", rolesQuantities);
            domainService.setShiftRequirements("01" ,dateStr, "E", "16:00", "22:00", rolesQuantities);
            domainService.setShiftRequirements("02", dateStr, "M", "08:00", "16:00", rolesQuantities);
            domainService.setShiftRequirements("02" ,dateStr, "E", "16:00", "22:00", rolesQuantities);
            domainService.setShiftRequirements("03", dateStr, "M", "08:00", "16:00", rolesQuantities);
            domainService.setShiftRequirements("03" ,dateStr, "E", "16:00", "22:00", rolesQuantities);

        }

        //Trucks
        domainService.addTruck("111", "Toyota", 100, 200, "A");
        domainService.addTruck("112", "Volvo", 120, 220, "B");
        domainService.addTruck("113", "Mercedes", 90, 180, "A");
        domainService.addTruck("114", "Isuzu", 110, 210, "B");
        domainService.addTruck("115", "Scania", 130, 250, "A");

        //Sites
        domainService.addSite("Hashmonaim 3", "Gal", "052-444555");
        domainService.addSite("Herzliya 5", "Dana", "053-111222");
        domainService.addSite("Ashdod 1", "Eli", "054-333444");
        domainService.addSite("Haifa 4", "Maya", "055-555666");
        domainService.addSite("Be'er Sheva 5", "Itay", "056-777888");
        domainService.addSite("Jerusalem 7", "Roni", "057-999000");

        //Supplier Manager
        domainService.addEmployee("supplierMan", "12345", "4545 789 67868","545 23/09/2014 56 76 pension:4567890","SUPPLIER_MANAGER", "01", "");

        return true;
    }

    public boolean reset() throws Exception {
        domainService.reset();
        return true;
    }

    //Utility methods

    private void loadBranches(ComboBox<String> comboBox) {
        try {
            Response response = domainService.displayBranches();
            if (response.isSuccess()) {
                String[] branches = response.getMessage().split("\n");
                String[] branchesWithoutFirst = Arrays.copyOfRange(branches, 1, branches.length);
                comboBox.getItems().addAll(branchesWithoutFirst);
            }
        } catch (Exception e) {
            guiUtils.showErrorDialog("Error loading branches: " + e.getMessage());
        }
    }

    private void loadRoles(ComboBox<String> roleCombo) {
        try {
            for (Role role : Role.values()) {
                if (role != Role.ADMIN && role != Role.SUPPLIER_MANAGER && role != Role.SUPPLIER) {
                    roleCombo.getItems().add(role.toString());
                }
            }
        } catch (Exception e) {
            guiUtils.showErrorDialog("Error loading roles: " + e.getMessage());
        }
    }

    private void loadTrucks(ComboBox<String> truckCombo) {
        try {
            Response response = domainService.displayTrucks();
            if (response.isSuccess()) {
                String[] trucks = response.getMessage().split("\n");
                String[] withoutFirst = Arrays.copyOfRange(trucks, 1, trucks.length);
                truckCombo.getItems().addAll(withoutFirst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDrivers(ComboBox<String> driverCombo) {
        try {
            Response response = domainService.displayDrivers();
            if (response.isSuccess()) {
                String[] drivers = response.getMessage().split("\n");
                String[] withoutFirst = Arrays.copyOfRange(drivers, 1, drivers.length);
                driverCombo.getItems().addAll(withoutFirst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSites(ComboBox<String> siteCombo) {
        try {
            Response response = domainService.displaySites();
            if (response.isSuccess()) {
                String[] sites = response.getMessage().split("\n");
                String[] withoutFirst = Arrays.copyOfRange(sites, 1, sites.length);
                siteCombo.getItems().addAll(withoutFirst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadEmployees(ComboBox<String> comboBox, String employees) {
        try {
            String[] empArr = employees.split("\n");
            comboBox.getItems().addAll(empArr);
        } catch (Exception e) {
            guiUtils.showErrorDialog("Error loading employees: " + e.getMessage());
        }
    }

    private String extractBranchId(String value) {
        return value.substring(value.length() - 2);
    }

    private String extractEmployeeId(String employeeString) {

        if(employeeString.contains("ID:")) {
            // "John Doe (ID: 123456789)" format
            int start = employeeString.lastIndexOf("(") + 5;
            int end = employeeString.lastIndexOf(")");
            return employeeString.substring(start, end);
        }

        // "John Doe (123456789)" format
        int start = employeeString.lastIndexOf("(") + 1;
        int end = employeeString.lastIndexOf(")");
        return employeeString.substring(start, end);
    }

    private String extractSiteAddress(String value) {

        if(value.contains("Branch")) {
            // format "Branch Id: 123 ,456 Main St , Contact name: John Doe , Contact number: 555-1234"
            int start = value.indexOf(",") + 1; // Start after the first comma
            String afterBranchId = value.substring(start);
            // find the index of the first comma after the branch id
            int end = afterBranchId.indexOf(",") - 1 ;
            return afterBranchId.substring(0, end); // Extract the address part
        }

        // format "- 123 Main St , Contact name: John Doe , Contact number: 555-1234"
        if (value != null && value.startsWith("- ")) {
            //remove the leading "- "
            String withoutDash = value.substring(2);

            // find the index of the first comma
            int commaIndex = withoutDash.indexOf(" ,");
            if (commaIndex != -1) {
                return withoutDash.substring(0, commaIndex).trim();
            }
        }
        return value; // fallback
    }

    private String[] getShiftDetailsDialog() {
        try {
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(parentStage);
            dialog.setTitle("Enter Shift Details");

            // Create ScrollPane for the main content
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: #f8f9fa;");

            VBox layout = new VBox(10);
            layout.setPadding(new Insets(15));
            layout.setStyle("-fx-background-color: #f8f9fa;");

            // Title
            Label titleLabel = new Label("Enter Shift Details");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            // Form section
            VBox formBox = new VBox(8);
            formBox.setStyle("-fx-background-color: #ffffff; -fx-padding: 12px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-border-color: #e0e0e0; -fx-border-width: 1px;");

            // Create form components using helper functions
            VBox branchBox = guiUtils.createComboBoxField("Select Branch:", "Choose a branch");
            ComboBox<String> branchCombo = (ComboBox<String>) branchBox.getChildren().get(1);
            loadBranches(branchCombo);

            VBox dateBox = guiUtils.createDatePickerField("Select Date:");
            DatePicker datePicker = (DatePicker) dateBox.getChildren().get(1);

            VBox timeSlotBox = guiUtils.createComboBoxField("Select Time Slot:", "Choose time slot");
            ComboBox<String> timeSlotCombo = (ComboBox<String>) timeSlotBox.getChildren().get(1);
            timeSlotCombo.getItems().addAll("M", "E");

            formBox.getChildren().addAll(branchBox, dateBox, timeSlotBox);

            // Buttons section
            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setPadding(new Insets(8, 0, 5, 0));

            Button submitButton = guiUtils.createStyledButton("Submit", "#27ae60", 130);
            Button cancelButton = guiUtils.createStyledButton("Cancel", "#95a5a6", 110);

            buttonBox.getChildren().addAll(submitButton, cancelButton);

            // Add all components to layout
            layout.getChildren().addAll(titleLabel, formBox, buttonBox);

            // Set content to scroll pane
            scrollPane.setContent(layout);

            Scene scene = new Scene(scrollPane, 420, 350);
            dialog.setScene(scene);
            dialog.setResizable(true);
            dialog.setMinWidth(380);
            dialog.setMinHeight(300);

            String[] result = new String[3];
            boolean[] submitted = {false};

            submitButton.setOnAction(e -> {
                // Validate selections
                if (branchCombo.getValue() == null) {
                    guiUtils.showErrorDialog("Please select a branch");
                    return;
                }
                if (datePicker.getValue() == null) {
                    guiUtils.showErrorDialog("Please select a date");
                    return;
                }
                if (timeSlotCombo.getValue() == null) {
                    guiUtils.showErrorDialog("Please select a time slot");
                    return;
                }

                result[0] = extractBranchId(branchCombo.getValue());
                result[1] = datePicker.getValue().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                result[2] = timeSlotCombo.getValue();

                submitted[0] = true;
                dialog.close();
            });

            cancelButton.setOnAction(e -> dialog.close());

            // Handle window close button
            dialog.setOnCloseRequest(e -> submitted[0] = false);
            dialog.showAndWait();

            return submitted[0] ? result : null;

        } catch (Exception e) {
            guiUtils.showErrorDialog("Error opening shift details dialog: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    private void showEmployeeAssignmentDialog(String branchId, String date, String timeSlot, String shiftDetails) {

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(parentStage);
        dialog.setTitle("Employee Shift Assignment - Branch " + branchId + " | " + date + " (" + timeSlot + ")");

        VBox mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(20));

        Label titleLabel = new Label("Assign Employees to Roles");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button showEmployeesButton = new Button("Show Available Employees");
        showEmployeesButton.setOnAction(e -> guiUtils.showDetailsDialog("Available Employees", getAvailableEmployees(shiftDetails)));

        VBox rolesLayout = new VBox(10);

        //Map to hold role assignments
        Map<Role, Map<String, CheckBox>> roleAssignments = new HashMap<>();

        Map<Role, Integer> roleNeeded = getRequirementRoles(shiftDetails);


        try {
            for (Role role : Role.values()) {
                if (role == Role.ADMIN || role == Role.SUPPLIER_MANAGER || role == Role.SUPPLIER) {
                    continue;
                }

                VBox roleSection = new VBox(5);
                roleSection.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-padding: 10;");

                Label roleLabel = new Label(role.name() + ":");
                roleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                VBox employeeCheckboxContainer = new VBox(5);
                employeeCheckboxContainer.setStyle("-fx-padding: 5;");

                Response response = domainService.findEmployeesByRoleInBranch(role, branchId);
                List<String> availableEmployees = new ArrayList<>();
                if (response.isSuccess()) {
                    String[] employees = response.getMessage().split("\n");
                    availableEmployees.addAll(Arrays.asList(employees));
                }

                Map<String, CheckBox> employeeCheckboxes = new HashMap<>();

                for (String employee : availableEmployees) {
                    CheckBox checkBox = new CheckBox(employee);
                    checkBox.setStyle("-fx-font-size: 12px;");
                    employeeCheckboxes.put(employee, checkBox);
                    employeeCheckboxContainer.getChildren().add(checkBox);
                }

                ScrollPane employeeScrollPane = new ScrollPane();
                employeeScrollPane.setContent(employeeCheckboxContainer);
                employeeScrollPane.setFitToWidth(true);
                employeeScrollPane.setPrefHeight(100);
                employeeScrollPane.setStyle("-fx-border-color: #dddddd; -fx-border-radius: 3;");

                String requirementText = String.format("Required: %d ", roleNeeded.getOrDefault(role, 0));
                Label requirementLabel = new Label(requirementText);

                roleAssignments.put(role, employeeCheckboxes);
                roleSection.getChildren().addAll(roleLabel, employeeScrollPane, requirementLabel);
                rolesLayout.getChildren().add(roleSection);
            }
        } catch (Exception e) {
            guiUtils.showErrorDialog("Error loading roles: " + e.getMessage());
            return;
        }

        ScrollPane rolesScrollPane = new ScrollPane();
        rolesScrollPane.setContent(rolesLayout);
        rolesScrollPane.setFitToWidth(true);
        rolesScrollPane.setPrefHeight(400);

        HBox buttonBox = new HBox(10);
        Button assignButton = new Button("Assign Employees");
        Button clearButton = new Button("Clear All");
        Button cancelButton = new Button("Cancel");

        assignButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        clearButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        buttonBox.getChildren().addAll(assignButton, clearButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);

        mainLayout.getChildren().addAll(titleLabel, showEmployeesButton, new Separator(), rolesScrollPane, buttonBox);

        Scene scene = new Scene(mainLayout, 650, 700);
        dialog.setScene(scene);

        assignButton.setOnAction(e -> {
            try {
                Map<Role, String> assignmentPerRole = new HashMap<>();

                for (Map.Entry<Role, Map<String, CheckBox>> entry : roleAssignments.entrySet()) {
                    Role role = entry.getKey();
                    Map<String, CheckBox> employeeCheckBoxes = entry.getValue();

                    List<String> selectedIds = new ArrayList<>();

                    for (Map.Entry<String, CheckBox> empEntry : employeeCheckBoxes.entrySet()) {
                        if (empEntry.getValue().isSelected()) {
                            selectedIds.add(extractEmployeeId(empEntry.getKey()));
                        }
                    }
                    String idsJoined = String.join(" ", selectedIds);
                    assignmentPerRole.put(role, idsJoined);
                }

                Response assignmentResponse = domainService.employeeShiftAssignment(branchId, date, timeSlot, assignmentPerRole);

                if (assignmentResponse.isSuccess()) {
                    guiUtils.showDetailsDialog("Assignment Successful", "Assignment Successful!\n\n"+ assignmentResponse.getMessage());
                    dialog.close();
                } else {
                    guiUtils.showErrorDialog("Assignment Failed:\n" + assignmentResponse.getMessage());
                }

            } catch (Exception ex) {
                guiUtils.showErrorDialog("Error during assignment: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        clearButton.setOnAction(e -> {
            for (Map<String, CheckBox> checkBoxMap : roleAssignments.values()) {
                for (CheckBox cb : checkBoxMap.values()) {
                    cb.setSelected(false);
                }
            }
        });

        cancelButton.setOnAction(e -> dialog.close());

        dialog.showAndWait();
    }

    private Map<Role, Integer> getRequirementRoles(String shiftDetails) {
        Map<Role, Integer> rolesNeeded = new HashMap<>();
        boolean inRolesSection = false;

        String[] lines = shiftDetails.split("\\R");

        for (String line : lines) {
            if (line.trim().equals("Roles Needed:")) {
                inRolesSection = true;
                continue;
            }

            if (inRolesSection) {
                //skip empty lines and lines that do not start with spaces
                if (line.trim().isEmpty() || !line.startsWith("  ")) {
                    break;
                }

                // Process the line to extract role and count , e.g. "CASHIER: 2"
                String trimmedLine = line.trim();
                if (trimmedLine.contains(":")) {
                    String[] parts = trimmedLine.split(":");
                    try {
                        Role role = Role.valueOf(parts[0].trim());
                        int count = Integer.parseInt(parts[1].trim());
                        rolesNeeded.put(role, count);
                    } catch (IllegalArgumentException e) {
                    }
                }
            }
        }

        return rolesNeeded;
    }

    private String getAvailableEmployees(String shiftDetails) {
        StringBuilder sb = new StringBuilder();

        boolean inAvailableSection = false;

        String[] lines = shiftDetails.split("\\R");

        for (String line : lines) {
            if (line.trim().equals("Available Employees:")) {
                inAvailableSection = true;
                continue;
            }

            //skip empty lines and lines that do not start with spaces
            if (inAvailableSection) {
                if (line.trim().isEmpty() || !line.startsWith("  ")) {
                    break;
                }
                sb.append(line.trim()).append("\n");
            }
        }

        if (sb.length() == 0) {
            return "No available employees";
        }

        return sb.toString().trim();

    }

    private boolean openRemoveEmployeeDialog(String[] shiftDetails, String shiftText) {
        try {
            Stage subDialog = new Stage();
            subDialog.initModality(Modality.APPLICATION_MODAL);
            subDialog.initOwner(parentStage);
            subDialog.setTitle("Remove Employee from Shift");

            // Variable to store the result
            final boolean[] success = {false};

            // Create ScrollPane for the main content
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: #f8f9fa;");

            VBox layout = new VBox(10);
            layout.setPadding(new Insets(15));
            layout.setStyle("-fx-background-color: #f8f9fa;");

            // Title
            Label titleLabel = new Label("Remove Employee from Shift");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            // Form section
            VBox formBox = new VBox(8);
            formBox.setStyle("-fx-background-color: #ffffff; -fx-padding: 12px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-border-color: #e0e0e0; -fx-border-width: 1px;");

            // Create form components using helper functions
            VBox roleBox = guiUtils.createComboBoxField("Role:", "Choose a role");
            ComboBox<String> roleCombo = (ComboBox<String>) roleBox.getChildren().get(1);
            loadRoles(roleCombo);

            VBox employeeBox = guiUtils.createComboBoxField("Employee:", "Choose an employee");
            ComboBox<String> employeeCombo = (ComboBox<String>) employeeBox.getChildren().get(1);
            loadEmployees(employeeCombo, getAssignEmployees(shiftText));

            formBox.getChildren().addAll(roleBox, employeeBox);

            // Buttons section
            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setPadding(new Insets(8, 0, 5, 0));

            Button removeButton = guiUtils.createStyledButton("Remove", "#e74c3c", 120);
            Button cancelButton = guiUtils.createStyledButton("Cancel", "#95a5a6", 110);

            buttonBox.getChildren().addAll(removeButton, cancelButton);

            // Result label for messages
            Label resultLabel = new Label();
            resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
            resultLabel.setWrapText(true);
            resultLabel.setMaxWidth(350);

            // Button actions
            cancelButton.setOnAction(e -> {
                success[0] = false;
                subDialog.close();
            });

            removeButton.setOnAction(e -> {
                try {
                    if (roleCombo.getValue() == null || employeeCombo.getValue() == null) {
                        resultLabel.setText("Please select both role and employee.");
                        resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
                        return;
                    }

                    Role role = Role.valueOf(roleCombo.getValue());
                    String employeeId = extractEmployeeId(employeeCombo.getValue());

                    Response response = domainService.handleRemoveEmployeeFromShift(
                            shiftDetails[0], shiftDetails[1], shiftDetails[2], employeeId, role
                    );

                    if (response.isSuccess()) {
                        guiUtils.showDetailsDialog("Employee Removed Successfully", response.getMessage());
                        success[0] = true;
                        subDialog.close();
                    } else {
                        resultLabel.setText("Error: " + response.getMessage());
                        resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
                    }

                } catch (Exception ex) {
                    success[0] = false;
                    resultLabel.setText("Error: " + ex.getMessage());
                    resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
                    ex.printStackTrace();
                }
            });

            // Add all components to layout
            layout.getChildren().addAll(titleLabel, formBox, buttonBox, resultLabel);

            // Set content to scroll pane
            scrollPane.setContent(layout);

            // Create and show scene
            Scene scene = new Scene(scrollPane, 380, 300);
            subDialog.setScene(scene);
            subDialog.setResizable(true);
            subDialog.setMinWidth(350);
            subDialog.setMinHeight(280);
            subDialog.showAndWait();

            return success[0];

        } catch (Exception e) {
            guiUtils.showErrorDialog("Error opening remove employee dialog: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private boolean openAddEmployeeDialog(String[] shiftDetails, String shiftText) {
        try {
            Stage subDialog = new Stage();
            subDialog.initModality(Modality.APPLICATION_MODAL);
            subDialog.initOwner(parentStage);
            subDialog.setTitle("Assign Employee to Shift");

            // Variable to store the result
            final boolean[] success = {false};

            // Create ScrollPane for the main content
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: #f8f9fa;");

            VBox layout = new VBox(10);
            layout.setPadding(new Insets(15));
            layout.setStyle("-fx-background-color: #f8f9fa;");

            // Title
            Label titleLabel = new Label("Assign Employee to Shift");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            // Form section
            VBox formBox = new VBox(8);
            formBox.setStyle("-fx-background-color: #ffffff; -fx-padding: 12px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-border-color: #e0e0e0; -fx-border-width: 1px;");

            // Create form components using helper functions
            VBox roleBox = guiUtils.createComboBoxField("Role:", "Choose a role");
            ComboBox<String> roleCombo = (ComboBox<String>) roleBox.getChildren().get(1);
            loadRoles(roleCombo);

            VBox employeeBox = guiUtils.createComboBoxField("Employee:", "Choose an employee");
            ComboBox<String> employeeCombo = (ComboBox<String>) employeeBox.getChildren().get(1);

            try {
                loadEmployees(employeeCombo, domainService.displayEmployeesAndRole().getMessage());
            } catch (Exception ex) {
                guiUtils.showErrorDialog("Error loading employees: " + ex.getMessage());
                ex.printStackTrace();
            }

            formBox.getChildren().addAll(roleBox, employeeBox);

            // Buttons section
            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setPadding(new Insets(8, 0, 5, 0));

            Button assignButton = guiUtils.createStyledButton("Assign", "#27ae60", 120);
            Button cancelButton = guiUtils.createStyledButton("Cancel", "#95a5a6", 110);

            buttonBox.getChildren().addAll(assignButton, cancelButton);

            // Result label for messages
            Label resultLabel = new Label();
            resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
            resultLabel.setWrapText(true);
            resultLabel.setMaxWidth(350);

            // Button actions
            cancelButton.setOnAction(e -> {
                success[0] = false;
                subDialog.close();
            });

            assignButton.setOnAction(e -> {
                try {
                    if (roleCombo.getValue() == null || employeeCombo.getValue() == null) {
                        resultLabel.setText("Please select both role and employee.");
                        resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
                        return;
                    }

                    Role role = Role.valueOf(roleCombo.getValue());
                    String employeeId = extractEmployeeId(employeeCombo.getValue());

                    Response response = domainService.handleAddEmployeeToShift(
                            shiftDetails[0], shiftDetails[1], shiftDetails[2], employeeId, role
                    );

                    if (response.isSuccess()) {
                        guiUtils.showDetailsDialog("Employee Assigned Successfully", response.getMessage());
                        success[0] = true;
                        subDialog.close();
                    } else {
                        resultLabel.setText("Error: " + response.getMessage());
                        resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
                    }

                } catch (Exception ex) {
                    success[0] = false;
                    resultLabel.setText("Error: " + ex.getMessage());
                    resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
                    ex.printStackTrace();
                }
            });

            // Add all components to layout
            layout.getChildren().addAll(titleLabel, formBox, buttonBox, resultLabel);

            // Set content to scroll pane
            scrollPane.setContent(layout);

            // Create and show scene
            Scene scene = new Scene(scrollPane, 380, 300);
            subDialog.setScene(scene);
            subDialog.setResizable(true);
            subDialog.setMinWidth(350);
            subDialog.setMinHeight(280);
            subDialog.showAndWait();

            return success[0];

        } catch (Exception e) {
            guiUtils.showErrorDialog("Error opening assign employee dialog: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private boolean openEditTimeSlotDialog(String[] shiftDetails, String shiftText) {
        try {
            Stage subDialog = new Stage();
            subDialog.initModality(Modality.APPLICATION_MODAL);
            subDialog.initOwner(parentStage);
            subDialog.setTitle("Edit Shift Time Slot");

            // Variable to store the result
            final boolean[] success = {false};

            // Create ScrollPane for the main content
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: #f8f9fa;");

            VBox layout = new VBox(10);
            layout.setPadding(new Insets(15));
            layout.setStyle("-fx-background-color: #f8f9fa;");

            // Title
            Label titleLabel = new Label("Edit Shift Time Slot");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            // Form section
            VBox formBox = new VBox(8);
            formBox.setStyle("-fx-background-color: #ffffff; -fx-padding: 12px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-border-color: #e0e0e0; -fx-border-width: 1px;");

            // Create form fields using guiUtils
            VBox startTimeBox = guiUtils.createFormField("Start Time:", "HH:MM");
            VBox endTimeBox = guiUtils.createFormField("End Time:", "HH:MM");

            // Time fields in a row
            HBox timeRow = new HBox(10);
            timeRow.getChildren().addAll(startTimeBox, endTimeBox);

            // Get text fields for later use
            TextField startTimeField = (TextField) startTimeBox.getChildren().get(1);
            TextField endTimeField = (TextField) endTimeBox.getChildren().get(1);
            startTimeField.setPrefWidth(160);
            endTimeField.setPrefWidth(160);

            formBox.getChildren().add(timeRow);

            // Buttons section
            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setPadding(new Insets(8, 0, 5, 0));

            Button editButton = guiUtils.createStyledButton("Edit Time Slot", "#3498db", 140);
            Button cancelButton = guiUtils.createStyledButton("Cancel", "#95a5a6", 110);

            buttonBox.getChildren().addAll(editButton, cancelButton);

            // Result label for messages
            Label resultLabel = new Label();
            resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
            resultLabel.setWrapText(true);
            resultLabel.setMaxWidth(350);

            // Button actions
            cancelButton.setOnAction(e -> {
                success[0] = false;
                subDialog.close();
            });

            editButton.setOnAction(e -> {
                try {
                    if (startTimeField.getText().trim().isEmpty() || endTimeField.getText().trim().isEmpty()) {
                        resultLabel.setText("Please fill in both start and end times.");
                        resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
                        return;
                    }

                    String newStart = startTimeField.getText().trim();
                    String newEnd = endTimeField.getText().trim();

                    Response response = domainService.editShiftTimeSlot(
                            shiftDetails[0], shiftDetails[1], shiftDetails[2], newStart, newEnd);

                    if (response.isSuccess()) {
                        guiUtils.showDetailsDialog("Time Slot Updated Successfully", response.getMessage());
                        success[0] = true;
                        subDialog.close();
                    } else {
                        resultLabel.setText("Error: " + response.getMessage());
                        resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
                    }

                } catch (Exception ex) {
                    success[0] = false;
                    resultLabel.setText("Error: " + ex.getMessage());
                    resultLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
                    ex.printStackTrace();
                }
            });

            // Add all components to layout
            layout.getChildren().addAll(titleLabel, formBox, buttonBox, resultLabel);

            // Set content to scroll pane
            scrollPane.setContent(layout);

            // Create and show scene
            Scene scene = new Scene(scrollPane, 380, 280);
            subDialog.setScene(scene);
            subDialog.setResizable(true);
            subDialog.setMinWidth(350);
            subDialog.setMinHeight(260);
            subDialog.showAndWait();

            return success[0];

        } catch (Exception e) {
            guiUtils.showErrorDialog("Error opening edit time slot dialog: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    private String getAssignEmployees(String shiftDetails) {
        StringBuilder sb = new StringBuilder();

        boolean inAssignedSection = false;

        String[] lines = shiftDetails.split("\\R");

        for (String line : lines) {
            if (line.trim().equals("Roles Assigned:")) {
                inAssignedSection = true;
                continue;
            }

            if (inAssignedSection) {
                if (line.trim().isEmpty() || !line.startsWith("  ")) {
                    break;
                }

                String[] parts = line.split(":", 2);
                if (parts.length < 2) continue;

                String employeesStr = parts[1].trim();
                if (!employeesStr.equalsIgnoreCase("None")) {
                    String[] employees = employeesStr.split(",");
                    for (String emp : employees) {
                        sb.append(emp.trim()).append("\n");
                    }
                }
            }
        }

        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }

        return sb.toString();
    }

    private String extractTruckId(String value) {
        // format "- 123 Model: Toyota License: ABC-123"
        int start = 2; // Skip "- "
        int end = value.lastIndexOf("Model:") - 1; // Exclude " Model:" part
        return value.substring(start, end).trim();
    }

    private void showShiftsDialog(String shiftsData) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("My Future Shifts");
        dialog.setWidth(700);
        dialog.setHeight(600);

        // Create main layout
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: #f8f9fa;");

        // Title
        Label titleLabel = new Label("📅 My Future Shifts");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#2c3e50"));
        titleLabel.setAlignment(Pos.CENTER);

        // Create scroll pane for shifts
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        VBox shiftsContainer = createShiftsContainer(shiftsData);
        scrollPane.setContent(shiftsContainer);

        // Close button
        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        closeButton.setOnAction(e -> dialog.close());

        HBox buttonBox = new HBox(closeButton);
        buttonBox.setAlignment(Pos.CENTER);

        mainLayout.getChildren().addAll(titleLabel, scrollPane, buttonBox);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        Scene scene = new Scene(mainLayout);
        dialog.setScene(scene);
        dialog.show();
    }

    private VBox createShiftsContainer(String shiftsData) {
        VBox container = new VBox(15);
        container.setPadding(new Insets(10));

        if (shiftsData == null || shiftsData.trim().isEmpty()) {
            Label noShiftsLabel = new Label("No future shifts found.");
            noShiftsLabel.setFont(Font.font("System", 16));
            noShiftsLabel.setTextFill(Color.web("#7f8c8d"));
            container.getChildren().add(noShiftsLabel);
            return container;
        }

        // Split shifts - look for pattern "Branch:" as start of new shift
        String[] lines = shiftsData.split("\n");
        List<String> shifts = new ArrayList<>();
        StringBuilder currentShift = new StringBuilder();

        for (String line : lines) {
            if (line.trim().startsWith("Branch:") && currentShift.length() > 0) {
                // Start of new shift, save the previous one
                shifts.add(currentShift.toString().trim());
                currentShift = new StringBuilder();
            }
            currentShift.append(line).append("\n");
        }

        // Add the last shift
        if (currentShift.length() > 0) {
            shifts.add(currentShift.toString().trim());
        }

        // Create cards for each shift
        for (int i = 0; i < shifts.size(); i++) {
            String shift = shifts.get(i).trim();
            if (!shift.isEmpty()) {
                VBox shiftCard = createShiftCard(shift, i + 1);
                container.getChildren().add(shiftCard);
            }
        }

        return container;
    }

    private VBox createShiftCard(String shiftData, int shiftNumber) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1; " +
                "-fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        // Shift title
        Label shiftTitle = new Label("📋 Shift #" + shiftNumber);
        shiftTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        shiftTitle.setTextFill(Color.web("#2980b9"));

        card.getChildren().add(shiftTitle);

        String[] lines = shiftData.split("\n");
        VBox currentSection = null;

        for (String line : lines) {
            if (line.isEmpty()) continue;

            // Check if it's a main info line (not indented)
            if (!line.startsWith("  ")) {
                if (line.startsWith("Branch:")) {
                    Label branchLabel = new Label("🏢 " + line);
                    branchLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
                    branchLabel.setTextFill(Color.web("#34495e"));
                    card.getChildren().add(branchLabel);

                } else if (line.startsWith("Shift Date:")) {
                    Label dateLabel = new Label("📅 " + line);
                    dateLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
                    dateLabel.setTextFill(Color.web("#34495e"));
                    card.getChildren().add(dateLabel);

                } else if (line.startsWith("Time Slot:")) {
                    Label timeLabel = new Label("⏰ " + line);
                    timeLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
                    timeLabel.setTextFill(Color.web("#34495e"));
                    card.getChildren().add(timeLabel);

                    // Add separator
                    Separator separator = new Separator();
                    separator.setStyle("-fx-background-color: #ecf0f1;");
                    card.getChildren().add(separator);

                } else if (line.equals("Roles Needed:")) {
                    currentSection = new VBox(5);
                    currentSection.setPadding(new Insets(0, 0, 0, 10));

                    Label sectionTitle = new Label("👥 ROLES NEEDED:");
                    sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
                    sectionTitle.setTextFill(Color.web("#e74c3c"));

                    card.getChildren().addAll(sectionTitle, currentSection);

                } else if (line.equals("Roles Assigned:")) {
                    currentSection = new VBox(5);
                    currentSection.setPadding(new Insets(0, 0, 0, 10));

                    Label sectionTitle = new Label("✅ ROLES ASSIGNED:");
                    sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
                    sectionTitle.setTextFill(Color.web("#27ae60"));

                    card.getChildren().addAll(sectionTitle, currentSection);

                } else if (line.equals("Available Employees:")) {
                    currentSection = new VBox(5);
                    currentSection.setPadding(new Insets(0, 0, 0, 10));

                    Label sectionTitle = new Label("👤 AVAILABLE EMPLOYEES:");
                    sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
                    sectionTitle.setTextFill(Color.web("#8e44ad"));

                    card.getChildren().addAll(sectionTitle, currentSection);
                }
            } else {
                // This is an indented line (role/employee info)
                if (currentSection != null) {
                    String itemText = line.trim();
                    if (!itemText.isEmpty()) {
                        Label itemLabel = new Label("• " + itemText);
                        itemLabel.setFont(Font.font("System", 12));
                        itemLabel.setTextFill(Color.web("#555555"));
                        itemLabel.setWrapText(true);
                        currentSection.getChildren().add(itemLabel);
                    }
                }
            }
        }

        return card;
    }

    private void showEmployeeDetailsDialog(String employeeData) {
        try {
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Employee Details");

            // Create ScrollPane for the main content
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: #f8f9fa;");

            VBox layout = new VBox(10);
            layout.setPadding(new Insets(15));
            layout.setStyle("-fx-background-color: #f8f9fa;");

            // Title with icon
            HBox titleBox = new HBox(8);
            titleBox.setAlignment(Pos.CENTER_LEFT);
            Label iconLabel = new Label("👤");
            iconLabel.setStyle("-fx-font-size: 20px;");
            Label titleLabel = new Label("Employee Details");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            titleBox.getChildren().addAll(iconLabel, titleLabel);

            // Main content box
            VBox contentBox = new VBox(12);
            contentBox.setStyle("-fx-background-color: #ffffff; -fx-padding: 15px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-border-color: #e0e0e0; -fx-border-width: 1px;");

            // Parse the employee data
            String[] lines = employeeData.split("\n");

            // Personal Info Section
            VBox personalSection = guiUtils.createInfoSection("👨‍💼 Personal Information", "#3498db");

            // Bank Account Section
            VBox bankSection = guiUtils.createInfoSection("🏦 Bank Account", "#e67e22");

            // Work Conditions Section
            VBox workSection = guiUtils.createInfoSection("💼 Work Conditions", "#27ae60");

            // Other Info Section
            VBox otherSection = guiUtils.createInfoSection("📋 Additional Information", "#9b59b6");

            // Parse and categorize the data
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty() || line.equals("Employee's details:")) continue;

                Label infoLabel = new Label();
                infoLabel.setWrapText(true);
                infoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #2c3e50; -fx-padding: 2px 0;");

                if (line.startsWith("Name:")) {
                    infoLabel.setText("📝 " + line);
                    personalSection.getChildren().add(infoLabel);
                } else if (line.startsWith("Id:")) {
                    infoLabel.setText("🆔 " + line);
                    personalSection.getChildren().add(infoLabel);
                } else if (line.contains("Bank") || line.contains("Account")) {
                    if (line.startsWith("Bank Account:")) {
                        infoLabel.setText("🏦 " + line.substring(13));
                    } else if (line.contains("Bank Branch Id:")) {
                        infoLabel.setText("🏢 " + line);
                    } else if (line.contains("Bank Id:")) {
                        infoLabel.setText("🏛️ " + line);
                    } else if (line.contains("Account Id:")) {
                        infoLabel.setText("💳 " + line);
                    } else {
                        infoLabel.setText("💰 " + line);
                    }
                    bankSection.getChildren().add(infoLabel);
                } else if (line.contains("Work Conditions:") || line.contains("Salary:") ||
                        line.contains("Start Date:") || line.contains("Vacation") ||
                        line.contains("Sick") || line.contains("Funds:")) {
                    if (line.startsWith("Work Conditions:")) {
                        infoLabel.setText("💼 " + line.substring(16));
                    } else if (line.contains("Salary:")) {
                        infoLabel.setText("💵 " + line);
                    } else if (line.contains("Start Date:")) {
                        infoLabel.setText("📅 " + line);
                    } else if (line.contains("Vacation Days")) {
                        infoLabel.setText("🏖️ " + line);
                    } else if (line.contains("Sick Days")) {
                        infoLabel.setText("🤒 " + line);
                    } else if (line.contains("Funds:")) {
                        infoLabel.setText("💰 " + line);
                    } else {
                        infoLabel.setText("⚙️ " + line);
                    }
                    workSection.getChildren().add(infoLabel);
                } else if (line.contains("Roles:")) {
                    infoLabel.setText("👔 " + line);
                    otherSection.getChildren().add(infoLabel);
                } else if (line.contains("Branch:")) {
                    infoLabel.setText("🏪 " + line);
                    otherSection.getChildren().add(infoLabel);
                } else if (line.contains("License:")) {
                    infoLabel.setText("🚗 " + line);
                    otherSection.getChildren().add(infoLabel);
                } else if (line.contains("Is Active:")) {
                    String status = line.contains("true") ? "✅ Active" : "❌ Inactive";
                    infoLabel.setText("📊 Status: " + status);
                    infoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " +
                            (line.contains("true") ? "#27ae60" : "#e74c3c") +
                            "; -fx-font-weight: bold; -fx-padding: 2px 0;");
                    otherSection.getChildren().add(infoLabel);
                } else {
                    infoLabel.setText("ℹ️ " + line);
                    otherSection.getChildren().add(infoLabel);
                }
            }

            // Add sections to content box (only if they have content)
            if (personalSection.getChildren().size() > 1) {
                contentBox.getChildren().add(personalSection);
            }
            if (bankSection.getChildren().size() > 1) {
                contentBox.getChildren().add(bankSection);
            }
            if (workSection.getChildren().size() > 1) {
                contentBox.getChildren().add(workSection);
            }
            if (otherSection.getChildren().size() > 1) {
                contentBox.getChildren().add(otherSection);
            }

            // Close button
            HBox buttonBox = new HBox();
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setPadding(new Insets(8, 0, 5, 0));

            Button closeButton = new Button("Close");
            closeButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8px 16px; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-cursor: hand;");
            closeButton.setPrefWidth(110);
            closeButton.setOnAction(e -> dialog.close());

            buttonBox.getChildren().add(closeButton);

            // Add all components to layout
            layout.getChildren().addAll(titleBox, contentBox, buttonBox);

            // Set content to scroll pane
            scrollPane.setContent(layout);

            // Create and show scene
            Scene scene = new Scene(scrollPane, 420, 480);
            dialog.setScene(scene);
            dialog.setResizable(true);
            dialog.setMinWidth(380);
            dialog.setMinHeight(450);
            dialog.showAndWait();

        } catch (Exception e) {
            guiUtils.showErrorDialog("Error displaying employee details: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
