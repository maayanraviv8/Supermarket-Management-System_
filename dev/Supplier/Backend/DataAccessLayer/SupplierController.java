package Supplier.Backend.DataAccessLayer;

import Supplier.Backend.domainLayer.Contact;
import Supplier.Backend.domainLayer.Payment;
import Supplier.Backend.DTO.AgreementDTO;
import Supplier.Backend.DTO.OrderDTO;
import Supplier.Backend.DTO.ProductDTO;
import Supplier.Backend.DTO.SupplierDTO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SupplierController {
    private final String connectionString;

    public SupplierController() {
        Path path = Paths.get(System.getProperty("user.dir"), "DB", "SuperLee.db");

        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            throw new RuntimeException("Failed to create DB directory", e);
        }

        this.connectionString = "jdbc:sqlite:" + path.toString().replace("\\", "/");
    }

    public SupplierController(String dbUrl) {
        this.connectionString = dbUrl;
    }

    public void addSupplierDB(SupplierDTO dto) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("""
            INSERT OR IGNORE INTO Suppliers (supplierId, name, supplyDay, address, bankAccount, isDelivery, paymentMethod, paymentTerm)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?);
        """);
            stmt.setLong(1, dto.getSupplierId());
            stmt.setString(2, dto.getName());
            stmt.setInt(3, dto.getSupplyDay());
            stmt.setString(4, dto.getAddress());
            stmt.setString(5, dto.getBankAccount());
            stmt.setBoolean(6, dto.isDelivery());
            stmt.setString(7, dto.getPayment().getPaymentMethod().toString());
            stmt.setString(8, dto.getPayment().getPaymentTerm().toString());
            stmt.executeUpdate();
            addContactDB(dto.getContacts(), dto.getSupplierId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void addContactDB(List<Contact> contacts, long supplierId) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("""
            INSERT OR IGNORE INTO Contacts (supplierId, contactName, phoneNumber)
            VALUES (?, ?, ?);
        """);
            for (Contact contact : contacts) {
                stmt.setLong(1, supplierId);
                stmt.setString(2, contact.getName());
                stmt.setString(3, contact.getPhone());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void addContactDB(Contact contact, long supplierId) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("""
            INSERT OR IGNORE INTO Contacts (supplierId, contactName, phoneNumber)
            VALUES (?, ?, ?);
        """);
            stmt.setLong(1, supplierId);
            stmt.setString(2, contact.getName());
            stmt.setString(3, contact.getPhone());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void addAgreementDB(AgreementDTO agreement, long supplierId) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("""
            INSERT OR IGNORE INTO Agreements (supplierId, agreementId, phoneNumber)
            VALUES (?, ?, ?);
        """);
            stmt.setLong(1, supplierId);
            stmt.setLong(2, agreement.getAgreementId());
            stmt.setString(3, agreement.getPhoneNumber());
            stmt.executeUpdate();
            addProductsDB(agreement.getProducts(), supplierId, agreement.getAgreementId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void addProductsDB(Map<String, ProductDTO> products, long supplierId, int agreementId) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("""
            INSERT OR IGNORE INTO Products (supplierId, agreementId, catalogNumber, productName, manufacturer, cost, discount, quantityForDiscount, measurementType, category0, category1, category2, expirationDate)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
        """);
            for (ProductDTO product : products.values()) {
                stmt.setLong(1, supplierId);
                stmt.setInt(2, agreementId);
                stmt.setString(3, product.getCatalogNumber());
                stmt.setString(4, product.getName());
                stmt.setString(5, product.getManufacturer());
                stmt.setDouble(6, product.getCost());
                stmt.setInt(7, product.getDiscount());
                stmt.setInt(8, product.getQuantityForDiscount());
                stmt.setString(9, product.getMeasurementType().toString());
                stmt.setString(10, product.getCategory0());
                stmt.setString(11, product.getCategory1());
                stmt.setString(12, product.getCategory2());
                stmt.setLong(13, product.getExpirationDate());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void addProductsDB(ProductDTO products, long supplierId, int agreementId){
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("""
            INSERT OR IGNORE INTO Products (supplierId, agreementId, catalogNumber, productName, manufacturer, cost, discount, quantityForDiscount, measurementType, category0, category1, category2, expirationDate)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
        """);
            stmt.setLong(1, supplierId);
            stmt.setInt(2, agreementId);
            stmt.setString(3, products.getCatalogNumber());
            stmt.setString(4, products.getName());
            stmt.setString(5, products.getManufacturer());
            stmt.setDouble(6, products.getCost());
            stmt.setInt(7, products.getDiscount());
            stmt.setInt(8, products.getQuantityForDiscount());
            stmt.setString(9, products.getMeasurementType().toString());
            stmt.setString(10, products.getCategory0());
            stmt.setString(11, products.getCategory1());
            stmt.setString(12, products.getCategory2());
            stmt.setLong(13, products.getExpirationDate());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void addOrderDB(OrderDTO order, long supplierId) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("""
            INSERT OR IGNORE INTO Orders (supplierId, orderId, orderDate, orderStatus, phoneNumber)
            VALUES (?, ?, ?, ?, ?);
        """);
            stmt.setLong(1, supplierId);
            stmt.setInt(2, order.getOrderId());
            stmt.setString(3, order.getOrderDate().toString());
            stmt.setString(4, order.getStatus());
            stmt.setString(5, order.getPhoneNumber());
            stmt.executeUpdate();
            addProductOrderDataDB(order.getProducts(), supplierId, order.getOrderId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void addProductOrderDataDB(Map<String, OrderDTO.ProductEntry> products, long supplierId, int orderId) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("""
            INSERT OR IGNORE INTO ProductOrderData (supplierId, orderId, catalogNumber, quantity, discount)
            VALUES (?, ?, ?, ?, ?);
        """);
            for (Map.Entry<String, OrderDTO.ProductEntry> entry : products.entrySet()) {
                String catalogNumber = entry.getKey();
                int quantity = entry.getValue().getQuantity();
                int discount = entry.getValue().getDiscount();
                stmt.setLong(1, supplierId);
                stmt.setInt(2, orderId);
                stmt.setString(3, catalogNumber);
                stmt.setInt(4, quantity);
                stmt.setInt(5, discount);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateSupplierName(long supplierId, String newName) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("""
            UPDATE Suppliers
            SET name = ?
            WHERE supplierId = ?;
        """);
            stmt.setString(1, newName);
            stmt.setLong(2, supplierId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateSupplierAddress(long supplierId, String newAddress) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("""
                        UPDATE Suppliers
                        SET address = ?
                        WHERE supplierId = ?;
                    """);
            stmt.setString(1, newAddress);
            stmt.setLong(2, supplierId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateSupplierBankAccount(long supplierId, String newBankAccount) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("""
                        UPDATE Suppliers
                        SET bankAccount = ?
                        WHERE supplierId = ?;
                    """);
            stmt.setString(1, newBankAccount);
            stmt.setLong(2, supplierId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateSupplierPaymentMethod(long supplierId, String newPaymentMethod) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("""
                        UPDATE Suppliers
                        SET paymentMethod = ?
                        WHERE supplierId = ?;
                    """);
            stmt.setString(1, newPaymentMethod);
            stmt.setLong(2, supplierId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateSupplierPaymentTerm(long supplierId, String newPaymentTerm) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("""
                        UPDATE Suppliers
                        SET paymentTerm = ?
                        WHERE supplierId = ?;
                    """);
            stmt.setString(1, newPaymentTerm);
            stmt.setLong(2, supplierId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateSupplierDelivery(long supplierId, boolean isDelivery) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("""
                        UPDATE Suppliers
                        SET isDelivery = ?
                        WHERE supplierId = ?;
                    """);
            stmt.setBoolean(1, isDelivery);
            stmt.setLong(2, supplierId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateSupplierSupplyDay(long supplierId, int newSupplyDay) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("""
                        UPDATE Suppliers
                        SET supplyDay = ?
                        WHERE supplierId = ?;
                    """);
            stmt.setInt(1, newSupplyDay);
            stmt.setLong(2, supplierId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateAgreementPhoneNumber(long supplierId, int agreementId, String newPhoneNumber) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("""
                        UPDATE Agreements
                        SET phoneNumber = ?
                        WHERE supplierId = ? AND agreementId = ?;
                    """);
            stmt.setString(1, newPhoneNumber);
            stmt.setLong(2, supplierId);
            stmt.setInt(3, agreementId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateProductCost(long supplierId, int agreementId, String catalogNumber, double newCost) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("""
                        UPDATE Products
                        SET cost = ?
                        WHERE supplierId = ? AND agreementId = ? AND catalogNumber = ?;
                    """);
            stmt.setDouble(1, newCost);
            stmt.setLong(2, supplierId);
            stmt.setInt(3, agreementId);
            stmt.setString(4, catalogNumber);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateProductDiscount(long supplierId, int agreementId, String catalogNumber, int newDiscount) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("""
                        UPDATE Products
                        SET discount = ?
                        WHERE supplierId = ? AND agreementId = ? AND catalogNumber = ?;
                    """);
            stmt.setInt(1, newDiscount);
            stmt.setLong(2, supplierId);
            stmt.setInt(3, agreementId);
            stmt.setString(4, catalogNumber);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateProductQuantityForDiscount(long supplierId, int agreementId, String catalogNumber, int newQuantityForDiscount) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("""
                        UPDATE Products
                        SET quantityForDiscount = ?
                        WHERE supplierId = ? AND agreementId = ? AND catalogNumber = ?;
                    """);
            stmt.setInt(1, newQuantityForDiscount);
            stmt.setLong(2, supplierId);
            stmt.setInt(3, agreementId);
            stmt.setString(4, catalogNumber);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateOrderProductDiscount(long supplierId, int orderID, String catalogNumber, int newDiscount) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("""
            UPDATE ProductOrderData
            SET discount = ?
            WHERE supplierId = ? AND orderId = ? AND catalogNumber = ?;
        """);
            stmt.setInt(1, newDiscount);
            stmt.setLong(2, supplierId);
            stmt.setInt(3, orderID);
            stmt.setString(4, catalogNumber);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateOrderStatus(long supplierId, int orderId, String newStatus) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("""
            UPDATE Orders
            SET orderStatus = ?
            WHERE supplierId = ? AND orderId = ?;
        """);
            stmt.setString(1, newStatus);
            stmt.setLong(2, supplierId);
            stmt.setInt(3, orderId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateOrderPhoneNumber(long supplierId, int orderId, String newPhoneNumber) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("""
            UPDATE Orders
            SET phoneNumber = ?
            WHERE supplierId = ? AND orderId = ?;
        """);
            stmt.setString(1, newPhoneNumber);
            stmt.setLong(2, supplierId);
            stmt.setInt(3, orderId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void deleteSupplier(long supplierId) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {

            PreparedStatement stmt1 = conn.prepareStatement("""
            DELETE FROM ProductOrderData
            WHERE supplierId = ?;
        """);
            stmt1.setLong(1, supplierId);
            stmt1.executeUpdate();

            PreparedStatement stmt2 = conn.prepareStatement("""
            DELETE FROM Products
            WHERE supplierId = ?;
        """);
            stmt2.setLong(1, supplierId);
            stmt2.executeUpdate();

            PreparedStatement stmt3 = conn.prepareStatement("""
            DELETE FROM Agreements
            WHERE supplierId = ?;
        """);
            stmt3.setLong(1, supplierId);
            stmt3.executeUpdate();

            PreparedStatement stmt4 = conn.prepareStatement("""
            DELETE FROM Contacts
            WHERE supplierId = ?;
        """);
            stmt4.setLong(1, supplierId);
            stmt4.executeUpdate();

            PreparedStatement stmt5 = conn.prepareStatement("""
            DELETE FROM Orders
            WHERE supplierId = ?;
        """);
            stmt5.setLong(1, supplierId);
            stmt5.executeUpdate();

            PreparedStatement stmt6 = conn.prepareStatement("""
            DELETE FROM Suppliers
            WHERE supplierId = ?;
        """);
            stmt6.setLong(1, supplierId);
            stmt6.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteAgreement(long supplierId, int agreementId) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("""
            DELETE FROM Agreements
            WHERE supplierId = ? AND agreementId = ?;
        """);
            stmt.setLong(1, supplierId);
            stmt.setInt(2, agreementId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteProduct(long supplierId, String catalogNumber) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("""
                DELETE FROM Products
                WHERE supplierId = ? AND catalogNumber = ?;
                """);
            stmt.setLong(1, supplierId);
            stmt.setString(2, catalogNumber);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteOrder(long supplierId, int orderId) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("""
            DELETE FROM Orders
            WHERE supplierId = ? AND orderId = ?;
            """);
            stmt.setLong(1, supplierId);
            stmt.setInt(2, orderId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean deleteAllData() {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM ProductOrderData;");
            stmt.executeUpdate("DELETE FROM Products;");
            stmt.executeUpdate("DELETE FROM Contacts;");
            stmt.executeUpdate("DELETE FROM Orders;");
            stmt.executeUpdate("DELETE FROM Agreements;");
            stmt.executeUpdate("DELETE FROM Suppliers;");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public SupplierDTO uploadSupplier(long supplierId) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("""
            SELECT * FROM Suppliers
            WHERE supplierId = ?;
        """);
            stmt.setLong(1, supplierId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;

                String name = rs.getString("name");
                int supplyDay = rs.getInt("supplyDay");
                String address = rs.getString("address");
                String bankAccount = rs.getString("bankAccount");
                boolean isDelivery = rs.getBoolean("isDelivery");
                String paymentMethod = rs.getString("paymentMethod");
                String paymentTerm = rs.getString("paymentTerm");

                List<Contact> contacts = uploadContacts(supplierId);
                if (contacts == null) {
                    contacts = List.of();
                }
                List<AgreementDTO> agreements = uploadAgreements(supplierId);
                if (agreements == null) {
                    agreements = List.of();
                }
                List<OrderDTO> orders = uploadOrders(supplierId);
                if (orders == null) {
                    orders = List.of();
                }
                return new SupplierDTO(name, supplierId, bankAccount, supplyDay,
                        Payment.PaymentMethod.valueOf(paymentMethod),
                        Payment.PaymentTerm.valueOf(paymentTerm), address, isDelivery, contacts, orders, agreements, getMaxOrderId(supplierId)+1, getMaxAgreementId(supplierId)+1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Contact> uploadContacts(long supplierId) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("""
            SELECT * FROM Contacts
            WHERE supplierId = ?;
        """);
            stmt.setLong(1, supplierId);

            try (ResultSet rs = stmt.executeQuery()) {
                List<Contact> contacts = new ArrayList<>();
                while (rs.next()) {
                    String contactName = rs.getString("contactName");
                    String phoneNumber = rs.getString("phoneNumber");
                    contacts.add(new Contact(contactName, phoneNumber));
                }
                return contacts;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<AgreementDTO> uploadAgreements(long supplierId) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("""
            SELECT * FROM Agreements
            WHERE supplierId = ?;
        """);
            stmt.setLong(1, supplierId);

            try (ResultSet rs = stmt.executeQuery()) {
                List<AgreementDTO> agreements = new ArrayList<>();
                while (rs.next()) {
                    int agreementId = rs.getInt("agreementId");
                    String phoneNumber = rs.getString("phoneNumber");
                    Map<String, ProductDTO> products = uploadProducts(supplierId, agreementId);

                    agreements.add(new AgreementDTO(products, phoneNumber, agreementId));
                }
                return agreements;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Map<String, ProductDTO> uploadProducts(long supplierId, int agreementId) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("""
            SELECT * FROM Products
            WHERE supplierId = ? AND agreementId = ?;
        """);
            stmt.setLong(1, supplierId);
            stmt.setInt(2, agreementId);

            try (ResultSet rs = stmt.executeQuery()) {
                Map<String, ProductDTO> products = new java.util.HashMap<>();
                while (rs.next()) {
                    String catalogNumber = rs.getString("catalogNumber");
                    String name = rs.getString("productName");
                    String manufacturer = rs.getString("manufacturer");
                    double cost = rs.getDouble("cost");
                    int discount = rs.getInt("discount");
                    int quantityForDiscount = rs.getInt("quantityForDiscount");
                    String measurementType = rs.getString("measurementType");
                    String category0 = rs.getString("category0");
                    String category1 = rs.getString("category1");
                    String category2 = rs.getString("category2");
                    int expirationDate = (int) rs.getLong("expirationDate");

                    products.put(catalogNumber, new ProductDTO(name, catalogNumber,manufacturer, cost, discount,
                            quantityForDiscount, measurementType, category0, category1, category2, expirationDate));
                }
                return products;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<OrderDTO> uploadOrders(long supplierId) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("""
            SELECT * FROM Orders
            WHERE supplierId = ?;
        """);
            stmt.setLong(1, supplierId);

            try (ResultSet rs = stmt.executeQuery()) {
                List<OrderDTO> orders = new ArrayList<>();
                while (rs.next()) {
                    int orderId = rs.getInt("orderId");
                    String orderDate = rs.getString("orderDate");
                    String orderStatus = rs.getString("orderStatus");
                    String phoneNumber = rs.getString("phoneNumber");
                    LocalDate orderDateParsed = LocalDate.parse(orderDate);
                    Map<String, OrderDTO.ProductEntry> products = uploadProductOrderData(supplierId, orderId);
                    orders.add(new OrderDTO(orderDateParsed, products, phoneNumber, orderId, orderStatus));
                    //System.out.println("Order ID = " + orderId + ", Products = " + products.size());
                }
                return orders;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Map<String, OrderDTO.ProductEntry> uploadProductOrderData(long supplierId, int orderId) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("""
            SELECT * FROM ProductOrderData
            WHERE supplierId = ? AND orderId = ?;
        """);
            stmt.setLong(1, supplierId);
            stmt.setInt(2, orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                Map<String, OrderDTO.ProductEntry> products = new java.util.HashMap<>();
                while (rs.next()) {
                    String catalogNumber = rs.getString("catalogNumber");
                    int quantity = rs.getInt("quantity");
                    int discount = rs.getInt("discount");
                    products.put(catalogNumber, new OrderDTO.ProductEntry(quantity, discount));
                }
                return products;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public List<Long> getAllSupplierIds() {
            List<Long> ids = new ArrayList<>();
            try (Connection conn = DriverManager.getConnection(connectionString)) {
                PreparedStatement stmt = conn.prepareStatement("""
            SELECT supplierId FROM Suppliers;
        """);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    ids.add(rs.getLong("supplierId"));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ids;

    }

    public int getMaxOrderId(long supplierId) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("""
            SELECT MAX(orderId) AS maxId FROM Orders WHERE supplierId = ?;
        """);
            stmt.setLong(1, supplierId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("maxId");
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public int getMaxAgreementId(long supplierId) {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("""
            SELECT MAX(agreementId) AS maxId FROM Agreements WHERE supplierId = ?;
        """);
            stmt.setLong(1, supplierId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("maxId");
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
