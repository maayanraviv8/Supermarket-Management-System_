package Storage.Backend.DataServiceLayer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class FactoryDAL {
    String url;
    // SQL for creating all tables
    String createProductsInWareHouse = """
                CREATE TABLE IF NOT EXISTS ProductsInWareHouse (
                    Id INTEGER PRIMARY KEY,
                    name TEXT NOT NULL,
                    category TEXT,
                    sub_category TEXT,
                    sub_sub_category TEXT,
                    cost_price REAL,
                    discount INTEGER,
                    min_quantity_to_alert INTEGER,
                    selling_price REAL,
                    quantity INTEGER,
                    manufacture TEXT,
                    locationInStore TEXT,
                    days_for_expired INTEGER
                );
                """;
    String createProductsInStore = """
                CREATE TABLE IF NOT EXISTS ProductsInStore (
                    Id INTEGER PRIMARY KEY,
                    name TEXT NOT NULL,
                    category TEXT,
                    sub_category TEXT,
                    sub_sub_category TEXT,
                    cost_price REAL,
                    discount INTEGER,
                    min_quantity_to_alert INTEGER,
                    selling_price REAL,
                    quantity INTEGER,
                    manufacture TEXT,
                    locationInStore TEXT,
                    days_for_expired INTEGER
                );
                """;

    String createDamaged = """
                CREATE TABLE IF NOT EXISTS Damaged (
                    Id INTEGER PRIMARY KEY,
                    days_for_expired INTEGER,
                    name TEXT NOT NULL,
                    category TEXT,
                    sub_category TEXT,
                    sub_sub_category TEXT,
                    cost_price REAL,
                    discount INTEGER,
                    min_quantity_to_alert INTEGER,
                    selling_price REAL,
                    quantity INTEGER,
                    manufacture TEXT
                );
                """;
    String createExpired = """
                CREATE TABLE IF NOT EXISTS Expired (
                    Id INTEGER PRIMARY KEY,
                    name TEXT NOT NULL,
                    quantity INTEGER
                );
                """;
    String createProductsids = """
                CREATE TABLE IF NOT EXISTS Ids (
                    StringId String PRIMARY KEY,
                    IntegerId INTEGER
                );
                """;


    public FactoryDAL() {
        Path path = Paths.get(System.getProperty("user.dir"), "DB", "SuperLee.db");
        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            throw new RuntimeException("Failed to create DB directory", e);
        }

        url = "jdbc:sqlite:" + path.toString().replace("\\", "/");

        CreateTables();
    }


        public void CreateTables(){
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            stmt.execute(createProductsInWareHouse);
            stmt.execute(createProductsInStore);
            stmt.execute(createDamaged);
            stmt.execute(createExpired);
            stmt.execute(createProductsids);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean DeleteData() {
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            stmt.execute("DELETE FROM ProductsInWareHouse;");
            stmt.execute("DELETE FROM ProductsInStore;");
            stmt.execute("DELETE FROM Damaged;");
            stmt.execute("DELETE FROM Expired;");
            stmt.execute("DELETE FROM Ids;");
            return true;

        } catch (Exception e) {
            return false;
        }
    }

}
