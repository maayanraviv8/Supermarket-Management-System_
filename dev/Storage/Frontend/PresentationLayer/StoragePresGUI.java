package Storage.Frontend.PresentationLayer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import Storage.Backend.ServiceLayer.Response;
import Storage.Backend.ServiceLayer.StorageService;

import java.util.List;
import java.util.Optional;

public class StoragePresGUI {
    private static StoragePresGUI instance=null;
    private Factory factory;
    private StorageService storage;
    private Stage primarystage;
    private StoragePresGUI(Stage primaryStage) {
        factory = new Factory();
        this.storage = factory.getStorageService();
        this.primarystage = primaryStage;
    }
    public static StoragePresGUI getInstance(Stage primaryStage) {
        if(instance==null) {
            instance = new StoragePresGUI(primaryStage);
        }
        return instance;
    }
    private Stage createDialogStage(String title) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(title);
        dialogStage.setMinWidth(400);
        dialogStage.setMinHeight(300);
        return dialogStage;
    }

    private void showInputDialog(String title, String[] labels, java.util.function.Function<String[], Response> callback) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setResizable(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        TextField[] fields = new TextField[labels.length];
        for (int i = 0; i < labels.length; i++) {
            Label label = new Label(labels[i] + ":");
            fields[i] = new TextField();
            fields[i].setPromptText(labels[i]);
            grid.add(label, 0, i);
            grid.add(fields[i], 1, i);
        }

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Disable OK button until all fields are filled
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].getText().trim().isEmpty()) {
                    event.consume(); // prevent dialog from closing
                    showInfo("Validation Error", "Please fill the field: " + labels[i]);
                    return;
                }
            }
        });

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String[] inputs = new String[fields.length];
            for (int i = 0; i < fields.length; i++) {
                inputs[i] = fields[i].getText().trim();
            }

            Response res = callback.apply(inputs);
            showInfo(title + " Result", formatResponse(res));
        }
        // If Cancel is pressed or closed, we do nothing (as expected)
    }


    private String formatResponse(Response res) {
        if (res.getErrorMessage() != null)
            return "Error: " + res.getErrorMessage();
        else if (res.getReturnValue() != null) {
            Object val = res.getReturnValue();
            if (val instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> list = (List<String>) val;
                return String.join("\n", list);
            }
            return val.toString();
        }
        else
            return "Operation completed successfully.";
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);

        if (content.length() > 300 || content.contains("\n")) {
            TextArea textArea = new TextArea(content);
            textArea.setWrapText(true);
            textArea.setEditable(false);
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            alert.getDialogPane().setContent(textArea);
            alert.setResizable(true);
            alert.getDialogPane().setPrefSize(600, 400);
        } else {
            alert.setContentText(content);
        }
        alert.showAndWait();
    }

    public void handleAddProduct() {
        showInputDialog("Add Product",
                new String[]{"Name", "Category", "Sub Category", "Sub Sub Category", "Price", "Selling Price", "ID", "Manufacture", "Quantity", "Days to Expire"},
                inputs -> storage.AddNewProduct(String.join(" ", inputs)));
    }

    public void handleDeleteProduct() {
        showInputDialog("Delete Product",
                new String[]{"Product ID", "Quantity"},
                inputs -> storage.DeleteProduct(String.join(" ", inputs)));
    }

    public void handleMoveProduct() {
        showInputDialog("Move Product",
                new String[]{"Product ID", "Quantity"},
                inputs -> storage.MoveProduct(String.join(" ", inputs)));
    }

    public void handleAddDamaged() {
        showInputDialog("Add Damaged",
                new String[]{"Product ID", "Quantity"},
                inputs -> storage.AddDamagedProduct(String.join(" ", inputs)));
    }

    public void handleAddExpired() {
        Response res = storage.AddExpiredProduct();
        showInfo("Add Expired", formatResponse(res));
    }

    public void handleDamagedReport() {
        Response res = storage.ReportAboutDamaged();
        showInfo("Damaged Report", formatResponse(res));
    }

    public void handleExpiredReport() {
        Response res = storage.ReportAboutExpired();
        showInfo("Expired Report", formatResponse(res));
    }

    public void handleApplySaleByCategory() {
        showInputDialog("Apply Sale by Category",
                new String[]{"Category", "Discount %"},
                inputs -> storage.ApplySaleByCategory(String.join(" ", inputs)));
    }

    public void handleApplySaleByProduct() {
        showInputDialog("Apply Sale by Product",
                new String[]{"Product ID", "Discount %"},
                inputs -> storage.ApplySaleByProductId(String.join(" ", inputs)));
    }

    public void handleCategoryReport() {
        showInputDialog("Category Report",
                new String[]{"Category"},
                inputs -> {
                    Response res = storage.ReportAboutCategory(inputs[0]);
                    showInfo("Category Report", formatResponse(res));
                    return res;
                });
    }

    public void handleSubCategoryReport() {
        showInputDialog("Sub Category Report",
                new String[]{"Sub Category"},
                inputs -> {
                    Response res = storage.ReportAboutSubCategory(inputs[0]);
                    showInfo("Sub Category Report", formatResponse(res));
                    return res;
                });
    }

    public void handleSubSubCategoryReport() {
        showInputDialog("Sub Sub Category Report",
                new String[]{"Sub Sub Category"},
                inputs -> {
                    Response res = storage.ReportAboutSubSubCategory(inputs[0]);
                    showInfo("Sub Sub Category Report", formatResponse(res));
                    return res;
                });
    }

    public void handleAnnualOrder() {
        Response res = storage.AnnualOrder();
        showInfo("Annual Order", formatResponse(res));
    }

    public boolean setup() {
        return factory.loadData();
    }

    public boolean reset() {
        return factory.DeleteData();
    }
}
