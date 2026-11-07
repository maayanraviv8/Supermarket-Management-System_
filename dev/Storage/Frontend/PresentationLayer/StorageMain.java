package Frontend.PresentationLayer;

import Storage.Backend.ServiceLayer.Response;
import Storage.Backend.ServiceLayer.StorageService;
import Storage.Frontend.PresentationLayer.Factory;
import Supplier.SingeltonScanner;
import java.util.List;
import java.util.Scanner;

public class StorageMain {
    Scanner in = SingeltonScanner.getInstance();
    String line;
    Factory factory;
    StorageService storage;

    public StorageMain() {
        factory = new Factory();
        storage = factory.getStorageService();
    }

    public boolean setup() {
        return factory.loadData();
    }

    public boolean reset() {
        return factory.DeleteData();
    }

    public void handleAddProduct() {
        System.out.println("Enter product name, category, sub category, sub-sub category, price, selling price, id, manufacture, quantity and number of days to expired");
        String line = in.nextLine();
        System.out.println(line);
        Response res = storage.AddNewProduct(line);
        System.out.println("Add product " + res.toString());
    }

    public void handleDeleteProduct() {
        System.out.println("Enter product id and quantity");
        String line = in.nextLine();
        System.out.println(line);
        Response res = storage.DeleteProduct(line);
        System.out.println("Delete product " + res.toString());
    }

    public void handleMoveProduct() {
        System.out.println("Enter product id, how many products you want to move");
        String line = in.nextLine();
        Response res = storage.MoveProduct(line);
        System.out.println("Move product " + res.toString());
    }

    public void handleDamagedReport() {
        Response res = storage.ReportAboutDamaged();
        if (res.getErrorMessage() == null) {
            List<String> products = (List<String>) res.getReturnValue();
            if (!products.isEmpty()) {
                System.out.println("Damaged Report:");
                for (String str : products)
                    System.out.println(str);
            } else {
                System.out.println("No damaged products to report");
            }
        } else {
            System.out.println(res.getErrorMessage());
        }
    }

    public void handleExpiredReport() {
        Response res = storage.ReportAboutExpired();
        if (res.getErrorMessage() == null) {
            List<String> products = (List<String>) res.getReturnValue();
            if (!products.isEmpty()) {
                System.out.println("Expired Report:");
                for (String str : products)
                    System.out.println(str);
            } else {
                System.out.println("No expired products to report");
            }
        } else {
            System.out.println("Report Expired failed " + res.getErrorMessage());
        }
    }

    public void handleApplySaleByCategory() {
        System.out.println("Enter category, how much discount you want to apply");
        String line = in.nextLine();
        System.out.println(line);
        Response res = storage.ApplySaleByCategory(line);
        System.out.println("Apply category sales " + res.toString());
    }

    public void handleApplySaleByProduct() {
        System.out.println("Enter product id, how much discount you want to apply");
        String line = in.nextLine();
        System.out.println(line);
        Response res = storage.ApplySaleByProductId(line);
        System.out.println("Apply product sales " + res.toString());
    }

    public void handleAddDamaged() {
        System.out.println("Enter product id and quantity");
        String line = in.nextLine();
        System.out.println(line);
        Response res = storage.AddDamagedProduct(line);
        System.out.println("Add damaged " + res.toString());
    }

    public void handleAddExpired() {
        Response res = storage.AddExpiredProduct();
        System.out.println("Add expired products to expired list was " + res.toString());
    }

    public void handleCategoryReport() {
        System.out.println("Enter category");
        String line = in.nextLine();
        Response res = storage.ReportAboutCategory(line);
        if (res.getErrorMessage() == null) {
            String product = res.getReturnValue().toString();
            if (!product.isEmpty()) {
                System.out.println("Category: " + line + " Report:");
                System.out.println(product);
            } else {
                System.out.println("No category to report");
            }
        } else {
            System.out.println("Report Category failed " + res.getErrorMessage());
        }
    }

    public void handleSubCategoryReport() {
        System.out.println("Enter sub category");
        String line = in.nextLine();
        Response res = storage.ReportAboutSubCategory(line);
        if (res.getErrorMessage() == null) {
            String product = res.getReturnValue().toString();
            if (!product.isEmpty()) {
                System.out.println("Sub Category: " + line + " Report:");
                System.out.println(product);
            } else {
                System.out.println("No sub category to report");
            }
        } else {
            System.out.println("Report Category failed " + res.getErrorMessage());
        }
    }

    public void handleSubSubCategoryReport() {
        System.out.println("Enter sub sub category");
        String line = in.nextLine();
        Response res = storage.ReportAboutSubSubCategory(line);
        if (res.getErrorMessage() == null) {
            String product = res.getReturnValue().toString();
            if (!product.isEmpty()) {
                System.out.println("Sub Sub Category: " + line + " Report:");
                System.out.println(product);
            } else {
                System.out.println("No sub sub category to report");
            }
        } else {
            System.out.println("Report Category failed " + res.getErrorMessage());
        }
    }

    public void handleAnnualOrder() {
        Response res = storage.AnnualOrder();
        System.out.println("CheckAnnualOrder " + res.toString());
    }
}

