package HRandDelivery.Utils;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class GUIutils {


    public void showResponseDialog(Response response) {
        Alert alert = new Alert(response.isSuccess() ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(response.isSuccess() ? "Success" : "Error");
        alert.setHeaderText(null);
        alert.setContentText(response.getMessage());
        alert.showAndWait();
    }

    public void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showDetailsDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void showWarningDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.getDialogPane().setPrefWidth(500);
        alert.showAndWait();
    }

    public VBox createFormField(String labelText, String placeholder) {
        VBox fieldBox = new VBox(3);
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #34495e;"); // Smaller font

        TextField textField = new TextField();
        textField.setPromptText(placeholder);
        textField.setStyle("-fx-padding: 6px 8px; -fx-font-size: 12px; -fx-border-color: #bdc3c7; -fx-border-radius: 4px; -fx-background-radius: 4px;"); // Reduced padding and font size
        textField.setPrefHeight(28); // Reduced from default height

        fieldBox.getChildren().addAll(label, textField);
        return fieldBox;
    }


    public VBox createInfoSection(String title, String color) {
        VBox section = new VBox(5);
        section.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 8px; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-border-color: " + color + "; -fx-border-width: 1px 1px 1px 4px;");

        Label sectionTitle = new Label(title);
        sectionTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        section.getChildren().add(sectionTitle);

        return section;
    }

    public VBox createComboBoxField(String labelText, String promptText) {
        VBox fieldBox = new VBox(3);

        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e; -fx-font-size: 12px;");

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPromptText(promptText);
        comboBox.setPrefWidth(340);
        comboBox.setStyle("-fx-font-size: 12px; -fx-padding: 6px; -fx-border-color: #ddd; -fx-border-radius: 4px; -fx-background-radius: 4px;");

        fieldBox.getChildren().addAll(label, comboBox);
        return fieldBox;
    }

    public VBox createDatePickerField(String labelText) {
        VBox fieldBox = new VBox(3);

        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e; -fx-font-size: 12px;");

        DatePicker datePicker = new DatePicker();
        datePicker.setPrefWidth(340);

        fieldBox.getChildren().addAll(label, datePicker);
        return fieldBox;
    }

    public VBox createWarningBox(String warningText) {
        VBox warningBox = new VBox(5);
        warningBox.setStyle("-fx-background-color: #fff3cd; -fx-padding: 10px; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-border-color: #ffeaa7; -fx-border-width: 1px;");

        Label warningLabel = new Label(warningText);
        warningLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #856404; -fx-wrap-text: true;");
        warningLabel.setWrapText(true);
        warningLabel.setMaxWidth(320);

        warningBox.getChildren().addAll(warningLabel);
        return warningBox;
    }

    public VBox createStyledSection(String backgroundColor, String borderColor) {
        VBox section = new VBox(8);
        section.setStyle(String.format("-fx-background-color: %s; -fx-padding: 12px; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-border-color: %s; -fx-border-width: 1px;",
                backgroundColor, borderColor));
        return section;
    }

    public Button createStyledButton(String text, String backgroundColor, int width) {
        Button button = new Button(text);
        button.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8px 16px; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-cursor: hand;",
                backgroundColor));
        button.setPrefWidth(width);
        return button;
    }
}
