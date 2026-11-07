package Supplier.Backend.DataAccessLayer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class FactoryDAL {
    String url;

    public FactoryDAL() {
        Path path = Paths.get(System.getProperty("user.dir"), "DB", "SuperLee.db");

        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            throw new RuntimeException("Failed to create DB directory", e);
        }

        url = "jdbc:sqlite:" + path.toString().replace("\\", "/");

        createTables();
    }

    String supplierTable = """
                CREATE TABLE IF NOT EXISTS Suppliers (
                    supplierId INTEGER PRIMARY KEY,
                    name TEXT NOT NULL,
                    supplyDay TEXT NOT NULL,
                    address TEXT NOT NULL,
                    bankAccount TEXT NOT NULL,
                    isDelivery BOOLEAN NOT NULL,
                    paymentMethod TEXT NOT NULL,
                    paymentTerm TEXT NOT NULL
                );
                """;
    String contactTable = """
                CREATE TABLE IF NOT EXISTS Contacts (
                    supplierId INTEGER,
                    contactName TEXT NOT NULL,
                    phoneNumber TEXT PRIMARY KEY,
                    FOREIGN KEY(supplierId) REFERENCES Suppliers(supplierId)
                );
                """;
    String agreementTable = """
                CREATE TABLE IF NOT EXISTS Agreements (
                    supplierId INTEGER,
                    agreementId INTEGER,
                    phoneNumber TEXT NOT NULL,
                    PRIMARY KEY(supplierId, agreementId),
                    FOREIGN KEY(supplierId) REFERENCES Suppliers(supplierId)
                );
                """;
    String productTable = """
                CREATE TABLE IF NOT EXISTS Products (
                    catalogNumber TEXT,
                    supplierId INTEGER NOT NULL,
                    agreementId INTEGER,
                    productName TEXT NOT NULL,
                    manufacturer TEXT NOT NULL,
                    cost REAL NOT NULL,
                    discount INTEGER NOT NULL,
                    quantityForDiscount INTEGER NOT NULL,
                    measurementType TEXT NOT NULL,
                    category0 TEXT NOT NULL,
                    category1 TEXT NOT NULL,
                    category2 TEXT NOT NULL,
                    expirationDate INTEGER NOT NULL,
                    PRIMARY KEY (supplierId, catalogNumber),
                    FOREIGN KEY(supplierId, agreementId) REFERENCES Agreements(supplierId, agreementId) ON DELETE SET NULL
                );
                """;
    String orderTable = """
                CREATE TABLE IF NOT EXISTS Orders (
                    orderId INTEGER PRIMARY KEY,
                    supplierId INTEGER NOT NULL,
                    orderDate TEXT NOT NULL,
                    orderStatus TEXT NOT NULL,
                    phoneNumber TEXT NOT NULL,
                    FOREIGN KEY(supplierId) REFERENCES Suppliers(supplierId)
                );
                """;
    String productOrderDataTable = """
                CREATE TABLE IF NOT EXISTS ProductOrderData (
                    orderId INTEGER NOT NULL,
                    catalogNumber TEXT NOT NULL,
                    supplierId INTEGER NOT NULL,
                    quantity INTEGER NOT NULL,
                    discount INTEGER NOT NULL,
                    PRIMARY KEY(orderId, supplierId, catalogNumber),
                    FOREIGN KEY(orderId) REFERENCES Orders(orderId),
                    FOREIGN KEY(supplierId, catalogNumber) REFERENCES Products(supplierId, catalogNumber)
                );
                """;

    public void createTables() {
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(supplierTable);
            stmt.execute(contactTable);
            stmt.execute(agreementTable);
            stmt.execute(productTable);
            stmt.execute(orderTable);
            stmt.execute(productOrderDataTable);
            System.out.println("All supplier tables created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void DeleteData() {
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM Suppliers;");
            stmt.execute("DELETE FROM Products;");
            stmt.execute("DELETE FROM Contacts;");
            stmt.execute("DELETE FROM Orders;");
            stmt.execute("DELETE FROM Agreements;");
            stmt.execute("DELETE FROM ProductOrderData;");
            System.out.println("All data deleted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUrl() {
        return url;
    }
//    public void insertExampleData() {
//        try (Connection conn = DriverManager.getConnection(url);
//             Statement stmt = conn.createStatement()) {
//            stmt.execute("INSERT INTO Supplier VALUES (1, 'תנובה', 'Sunday', 'תל אביב', '123456', 1, 'Bank Transfer', 'Net 30');");
//            stmt.execute("INSERT INTO Contact VALUES (1, 'רונית', '0501234567');");
//            stmt.execute("INSERT INTO Agreement VALUES (1, 1001, '0501234567');");
//            stmt.execute("INSERT INTO Product VALUES ('milk1', 1, 1001, 'חלב 1%', 'תנובה', 5.5, 10, 12, 'Litre', 'מוצרי חלב', 'חלב', '1 ליטר', 14);");
//            stmt.execute("INSERT INTO Order VALUES (5001, 1, '2025-05-25', 'Pending', '0501234567');");
//            stmt.execute("INSERT INTO ProductOrderData VALUES (5001, 'milk1', 1, 30, 10);");
//            System.out.println("Example data inserted.");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


}
