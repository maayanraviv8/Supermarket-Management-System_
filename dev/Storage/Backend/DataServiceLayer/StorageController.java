package Storage.Backend.DataServiceLayer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StorageController {
    private final String _connectionString;


    public StorageController() {
        String baseDir = System.getProperty("user.dir");
        Path path = Paths.get(baseDir, "DB", "SuperLee.db").toAbsolutePath();
        this._connectionString = "jdbc:sqlite:" + path.toString().replace("\\", "/");
    }

    public void AddProduct(ProductDAO p) throws SQLException {
        try (Connection conn = DriverManager.getConnection(_connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO ProductsInWareHouse (Id, name, category,sub_category,sub_sub_category,cost_price,discount,min_quantity_to_alert,selling_price,quantity,manufacture,days_for_expired,locationInStore)\n" +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?);");
            stmt.setInt(1, p.getId());
            stmt.setString(2, p.getName());
            stmt.setString(3, p.getCategory());
            stmt.setString(4, p.getSub_category());
            stmt.setString(5, p.getSub_sub_category());
            stmt.setDouble(6, p.getCost_price());
            stmt.setInt(7, p.getDiscount());
            stmt.setInt(8, p.getMin_quantity_to_alert());
            stmt.setDouble(9, p.getSelling_price());
            stmt.setInt(10, p.getQuantity());
            stmt.setString(11, p.getManufacture());
            stmt.setInt(12, p.getDays_for_expired());
            stmt.setString(13,String.valueOf(p.getLocationInStore()));
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void DeleteProduct(ProductDAO p, int quantity) throws SQLException {
        if (quantity > 0) {
            try (Connection conn = DriverManager.getConnection(_connectionString)) {
                PreparedStatement stmt = conn.prepareStatement("UPDATE ProductsInStore SET quantity = ? WHERE id=? AND days_for_expired=?");
                stmt.setInt(1, quantity);
                stmt.setInt(2, p.getId());
                stmt.setInt(3, p.getDays_for_expired());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    else{
            try (Connection conn = DriverManager.getConnection(_connectionString)) {
                PreparedStatement stmt2 = conn.prepareStatement("DELETE FROM ProductsInStore WHERE id=? AND days_for_expired=?");
                stmt2.setInt(1, p.getId());
                stmt2.setInt(2, p.getDays_for_expired());
                stmt2.executeUpdate();
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void MoveProduct(ProductDAO p,int ammountToMove, boolean isNew) throws SQLException {
            if(isNew) {
                try (Connection conn = DriverManager.getConnection(_connectionString)) {
                    PreparedStatement stmt = conn.prepareStatement("INSERT INTO ProductsInStore (Id, name, category,sub_category,sub_sub_category,cost_price,discount,min_quantity_to_alert,selling_price,quantity,manufacture,days_for_expired,locationInStore)\n" +
                            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?);");
                    stmt.setInt(1, p.getId());
                    stmt.setString(2, p.getName());
                    stmt.setString(3, p.getCategory());
                    stmt.setString(4, p.getSub_category());
                    stmt.setString(5, p.getSub_sub_category());
                    stmt.setDouble(6, p.getCost_price());
                    stmt.setInt(7, p.getDiscount());
                    stmt.setInt(8, p.getMin_quantity_to_alert());
                    stmt.setDouble(9, p.getSelling_price());
                    stmt.setInt(10, ammountToMove);
                    stmt.setString(11, p.getManufacture());
                    stmt.setInt(12, p.getDays_for_expired());
                    stmt.setString(13,String.valueOf(p.getLocationInStore()));
                    stmt.executeUpdate();

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            else{
                try (Connection conn = DriverManager.getConnection(_connectionString)) {
                    PreparedStatement stmt = conn.prepareStatement("UPDATE ProductsInStore SET quantity= quantity + ? WHERE id=? AND days_for_expired =?");
                    stmt.setInt(1,ammountToMove);
                    stmt.setInt(2, p.getId());
                    stmt.setInt(3, p.getDays_for_expired());
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if(ammountToMove == p.getQuantity()) {
                try (Connection conn = DriverManager.getConnection(_connectionString)) {
                    PreparedStatement stmt = conn.prepareStatement("DELETE FROM ProductsInWareHouse WHERE id=? AND days_for_expired=?");
                    stmt.setInt(1, p.getId());
                    stmt.setInt(2, p.getDays_for_expired());
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        else{
            try (Connection conn = DriverManager.getConnection(_connectionString)) {
                PreparedStatement stmt = conn.prepareStatement("UPDATE ProductsInWareHouse SET quantity=? WHERE id=? AND days_for_expired =?");
                stmt.setInt(1, p.getQuantity()-ammountToMove);
                stmt.setInt(2, p.getId());
                stmt.setInt(3, p.getDays_for_expired());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void SetDiscountInWareHouse(ProductDAO productToSetDiscount, int discountToSet){
        try (Connection conn = DriverManager.getConnection(_connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE ProductsInWareHouse SET discount=? WHERE id=?");
            stmt.setInt(1, discountToSet);
            stmt.setInt(2, productToSetDiscount.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void SetDiscountInStore(ProductDAO productToSetDiscount, int discountToSet){
        try (Connection conn = DriverManager.getConnection(_connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE ProductsInStore SET discount=? WHERE id=?");
            stmt.setInt(1, discountToSet);
            stmt.setInt(2, productToSetDiscount.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void SetDamagedFromStore(ProductDAO p, int quantity, boolean isNew) throws SQLException {
        if(isNew){
            try (Connection conn = DriverManager.getConnection(_connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Damaged (Id, name, category,sub_category,sub_sub_category,cost_price,discount,min_quantity_to_alert,selling_price,quantity,manufacture,days_for_expired)\n" +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?);");
            stmt.setInt(1, p.getId());
            stmt.setString(2, p.getName());
            stmt.setString(3, p.getCategory());
            stmt.setString(4, p.getSub_category());
            stmt.setString(5, p.getSub_sub_category());
            stmt.setDouble(6, p.getCost_price());
            stmt.setInt(7, p.getDiscount());
            stmt.setInt(8, p.getMin_quantity_to_alert());
            stmt.setDouble(9, p.getSelling_price());
            stmt.setInt(10, p.getQuantity());
            stmt.setString(11, p.getManufacture());
            stmt.setInt(12, p.getDays_for_expired());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        }
        else{
            try (Connection conn = DriverManager.getConnection(_connectionString)) {
                PreparedStatement stmt = conn.prepareStatement("UPDATE Damaged SET quantity= quantity +? WHERE id=? AND days_for_expired =?");
                stmt.setInt(1,quantity);
                stmt.setInt(2, p.getId());
                stmt.setInt(3, p.getDays_for_expired());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if(quantity == p.getQuantity()) {
            try (Connection conn = DriverManager.getConnection(_connectionString)) {
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM ProductsInStore WHERE id=? AND days_for_expired=?");
                stmt.setInt(1, p.getId());
                stmt.setInt(2, p.getDays_for_expired());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            try (Connection conn = DriverManager.getConnection(_connectionString)) {
                PreparedStatement stmt = conn.prepareStatement("UPDATE ProductsInStore SET quantity=? WHERE id=? AND days_for_expired =?");
                stmt.setInt(1, p.getQuantity()-quantity);
                stmt.setInt(2, p.getId());
                stmt.setInt(3, p.getDays_for_expired());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void SetDamagedFromWareHouse(ProductDAO p, int quantity,boolean isNew) throws SQLException {
        if(isNew){
            try (Connection conn = DriverManager.getConnection(_connectionString)) {
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO Damaged (Id, name, category,sub_category,sub_sub_category,cost_price,discount,min_quantity_to_alert,selling_price,quantity,manufacture,days_for_expired)\n" +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?);");
                stmt.setInt(1, p.getId());
                stmt.setString(2, p.getName());
                stmt.setString(3, p.getCategory());
                stmt.setString(4, p.getSub_category());
                stmt.setString(5, p.getSub_sub_category());
                stmt.setDouble(6, p.getCost_price());
                stmt.setInt(7, p.getDiscount());
                stmt.setInt(8, p.getMin_quantity_to_alert());
                stmt.setDouble(9, p.getSelling_price());
                stmt.setInt(10, p.getQuantity());
                stmt.setString(11, p.getManufacture());
                stmt.setInt(12, p.getDays_for_expired());
                stmt.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            try (Connection conn = DriverManager.getConnection(_connectionString)) {
                PreparedStatement stmt = conn.prepareStatement("UPDATE Damaged SET quantity = quantity + ? WHERE id=? AND days_for_expired =?");
                stmt.setInt(1,quantity);
                stmt.setInt(2, p.getId());
                stmt.setInt(3, p.getDays_for_expired());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if(quantity == p.getQuantity()) {
            try (Connection conn = DriverManager.getConnection(_connectionString)) {
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM ProductsInWareHouse WHERE id=? AND days_for_expired=?");
                stmt.setInt(1, p.getId());
                stmt.setInt(2, p.getDays_for_expired());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            try (Connection conn = DriverManager.getConnection(_connectionString)) {
                PreparedStatement stmt = conn.prepareStatement("UPDATE ProductsInWareHouse SET quantity=? WHERE id=? AND days_for_expired =?");
                stmt.setInt(1, p.getQuantity()-quantity);
                stmt.setInt(2, p.getId());
                stmt.setInt(3, p.getDays_for_expired());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void SetExpiredFromStore(ProductDAO p,boolean isNew) throws SQLException {
        if(isNew){
            try (Connection conn = DriverManager.getConnection(_connectionString)) {
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO Expired (Id, name, quantity)\n" +
                        "VALUES (?,?,?);");
                stmt.setInt(1, p.getId());
                stmt.setString(2, p.getName());
                stmt.setInt(3, p.getQuantity());
                stmt.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
        else{
            try (Connection conn = DriverManager.getConnection(_connectionString)) {
                PreparedStatement stmt = conn.prepareStatement("UPDATE Expired SET quantity+=? WHERE id=?");
                stmt.setInt(1, p.getId());
                stmt.setInt(2, p.getQuantity());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        try (Connection conn = DriverManager.getConnection(_connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM ProductsInStore WHERE id=? AND days_for_expired=0");
            stmt.setInt(1, p.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void SetExpiredFromWareHouse(ProductDAO p,boolean isNew) throws SQLException {
        if(isNew){
        try (Connection conn = DriverManager.getConnection(_connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Expired (Id, name, quantity)\n" +
                    "VALUES (?,?,?);");
            stmt.setInt(1, p.getId());
            stmt.setString(2, p.getName());
            stmt.setInt(3, p.getQuantity());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        }
        else{
            try (Connection conn = DriverManager.getConnection(_connectionString)) {
                PreparedStatement stmt = conn.prepareStatement("UPDATE Expired SET quantity+=? WHERE id=?");
                stmt.setInt(1, p.getId());
                stmt.setInt(2, p.getQuantity());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        try (Connection conn = DriverManager.getConnection(_connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM ProductsInWareHouse WHERE id=? AND days_for_expired=0");
            stmt.setInt(1, p.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void SetProductQuantityInWareHouse(ProductDAO p, int quantityToSet){
        try (Connection conn = DriverManager.getConnection(_connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE ProductsInWareHouse SET quantity=? WHERE id=? AND days_for_expired=?");
            stmt.setInt(1, quantityToSet);
            stmt.setInt(2, p.getId());
            stmt.setInt(3, p.getDays_for_expired());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<Integer,List<ProductDAO>> LoadExpiredProducts(){
        try (Connection conn = DriverManager.getConnection(_connectionString)) {
            Map<Integer,List<ProductDAO>> products = new HashMap<>();
            String query = "SELECT * FROM Expired";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    ProductDAO prod = new ProductDAO(rs.getString("name"),
                            rs.getString("category"),
                            rs.getString("sub_category"),
                            rs.getString("sub_sub_category"),
                            Double.parseDouble(rs.getString("cost_price")),
                            Integer.parseInt(rs.getString("discount")),
                            Integer.parseInt(rs.getString("min_quantity_to_alert")),
                            Double.parseDouble(rs.getString("selling_price")),
                            Integer.parseInt(rs.getString("Id")),
                            rs.getString("manufacture"),
                            Integer.parseInt(rs.getString("days_for_expired")),
                            Integer.parseInt(rs.getString("quantity")));
                    if(!products.containsKey(prod.getId())){
                        products.put(prod.getId(),new ArrayList<>());
                    }
                    products.get(prod.getId()).add(prod);
                }
            }
            return products;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<Integer,List<ProductDAO>>  LoadDamagedProducts(){
        try (Connection conn = DriverManager.getConnection(_connectionString)) {
            Map<Integer,List<ProductDAO>> products = new HashMap<>();
            String query = "SELECT * FROM Damaged";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    ProductDAO prod = new ProductDAO(rs.getString("name"),
                            rs.getString("category"),
                            rs.getString("sub_category"),
                            rs.getString("sub_sub_category"),
                            Double.parseDouble(rs.getString("cost_price")),
                            Integer.parseInt(rs.getString("discount")),
                            Integer.parseInt(rs.getString("min_quantity_to_alert")),
                            Double.parseDouble(rs.getString("selling_price")),
                            Integer.parseInt(rs.getString("Id")),
                            rs.getString("manufacture"),
                            Integer.parseInt(rs.getString("days_for_expired")),
                            Integer.parseInt(rs.getString("quantity")));
                    if(!products.containsKey(prod.getId())){
                        products.put(prod.getId(),new ArrayList<>());
                    }
                    products.get(prod.getId()).add(prod);
                }
            }
            return products;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<Integer,List<ProductDAO>>  LoadProductsToStore(){
        try (Connection conn = DriverManager.getConnection(_connectionString)) {
            Map<Integer,List<ProductDAO>> products = new HashMap<>();
            String query = "SELECT * FROM ProductsInStore";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    ProductDAO prod = new ProductDAO(rs.getString("name"),
                            rs.getString("category"),rs.getString("sub_category"),
                            rs.getString("sub_sub_category"),
                            Double.parseDouble(rs.getString("cost_price")),
                            Integer.parseInt(rs.getString("discount")),
                            Integer.parseInt(rs.getString("min_quantity_to_alert")),
                            Double.parseDouble(rs.getString("selling_price")),
                            Integer.parseInt(rs.getString("Id")),
                            rs.getString("manufacture"),
                            Integer.parseInt(rs.getString("days_for_expired")),
                            Integer.parseInt(rs.getString("quantity")));
                    if(!products.containsKey(prod.getId())){
                        products.put(prod.getId(),new ArrayList<>());
                    }
                    products.get(prod.getId()).add(prod);
                }
            }
            return products;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<Integer,List<ProductDAO>> LoadProductsToWareHouse(){
        try (Connection conn = DriverManager.getConnection(_connectionString)) {
            Map<Integer,List<ProductDAO>> products = new HashMap<>();
            String query = "SELECT * FROM ProductsInWareHouse";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    ProductDAO prod = new ProductDAO(rs.getString("name"),
                            rs.getString("category"),rs.getString("sub_category"),
                            rs.getString("sub_sub_category"),
                            Double.parseDouble(rs.getString("cost_price")),
                            Integer.parseInt(rs.getString("discount")),
                            Integer.parseInt(rs.getString("min_quantity_to_alert")),
                            Double.parseDouble(rs.getString("selling_price")),
                            Integer.parseInt(rs.getString("Id")),
                            rs.getString("manufacture"),
                            Integer.parseInt(rs.getString("days_for_expired")),
                            Integer.parseInt(rs.getString("quantity")));

                    if(!products.containsKey(prod.getId())){
                        products.put(prod.getId(),new ArrayList<>());
                    }
                    products.get(prod.getId()).add(prod);
                }
            }
            return products;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Map<String,Integer> LoadIds(){
        try (Connection conn = DriverManager.getConnection(_connectionString)) {
            Map<String,Integer> ids = new HashMap<>();
            String query = "SELECT * FROM Ids";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    ids.put(rs.getString("StringId"),rs.getInt("IntegerId"));
                }
            }
            return ids;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void AddId(String strId, int id) {
        try (Connection conn = DriverManager.getConnection(_connectionString)) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Ids (StringId,IntegerId)" +
                    " VALUES (?,?)");
                 stmt.setString(1, strId);
                 stmt.setInt(2, id);
                 stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


//    public void AddNewProductIdToStore(int id){}

//    public void AddNewProductIdToWareHouse(int id){}

//    public void AddDamagedId(int Id){}

//    public void AddExpiredId(int Id){}
}

