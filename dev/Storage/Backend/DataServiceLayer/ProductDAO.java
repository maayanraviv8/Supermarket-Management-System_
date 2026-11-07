package Storage.Backend.DataServiceLayer;

import java.util.Date;

public class ProductDAO {
    protected String name;
    protected String category;
    protected String sub_category;
    protected String sub_sub_category;
    protected double cost_price;
    protected int discount;
    protected int min_quantity_to_alert;
    protected double selling_price;
    protected int quantity;
    protected int Id;
    protected String manufacture;
    protected int days_for_expired;
    protected Date expiry_date;
    protected boolean isPersist;
    protected StorageController controller;
    protected Character locationInStore;

    public ProductDAO(String name, String category, String sub_category, String sub_sub_category,
                      double price, int discount, int minQuantityToAlert, double selling_price, int id,
                      String manufacture, int daysForExpired, int quantity) {
        this.name = name;
        this.category = category;
        this.sub_category = sub_category;
        this.sub_sub_category = sub_sub_category;
        this.cost_price = price;
        this.discount = discount;
        this.min_quantity_to_alert = minQuantityToAlert;
        this.selling_price = selling_price;
        this.Id = id;
        this.quantity = quantity;
        this.manufacture = manufacture;
        this.days_for_expired = daysForExpired;
        this.isPersist = false;
        this.controller = new StorageController();
        this.locationInStore = category.charAt(0);
    }

    public void Persist() {
        this.isPersist = true;
    }
    public void AddProduct() throws Exception {
        if(!isPersist) {
            controller.AddProduct(this);
            this.isPersist = true;
        }
        else {
            throw new Exception("Product is already exist");
        }
    }
    public void DeleteProduct(int quantity) throws Exception {
        if(isPersist) {
            controller.DeleteProduct(this,quantity);
        }
        else {
            throw new Exception("Product is not exist");
        }
    }
    public void MoveProduct(int ammountToMove , boolean isNew) throws Exception {
        if (isPersist) {
            controller.MoveProduct(this, ammountToMove, isNew);
        }
        else {
            throw new Exception("Product does not exist");
        }
    }

    public void SetDiscountInWareHouse(int discountToSet) throws Exception {
        if (isPersist) {
            controller.SetDiscountInWareHouse(this,discountToSet);
        } else {
            throw new Exception("Unable To Set Discount In Warehouse");
        }
    }
    public void SetDiscountInStore (int discountToSet) throws Exception {
        if (isPersist) {
            controller.SetDiscountInStore(this,discountToSet);
        } else {
            throw new Exception("Unable To Set Discount In Store");
        }
    }
    public void SetDamagedFromStore (int quantity, boolean isNew) throws Exception {
        if (isPersist) {
            controller.SetDamagedFromStore(this,quantity,isNew);
        } else {
            throw new Exception("Unable To Set Damaged From Store");
        }
    }
    public void SetDamagedFromWareHouse (int quantity, boolean isNew) throws Exception {
        if (isPersist) {
            controller.SetDamagedFromWareHouse(this,quantity,isNew);
        } else {
            throw new Exception("Unable To Set Damaged From Warehouse");
        }
    }
    public void SetExpiredFromWareHouse (boolean isNew) throws Exception {
        if (isPersist) {
            controller.SetExpiredFromWareHouse(this,isNew);
        } else {
            throw new Exception("Unable To Set Expired From Warehouse");
        }
    }
    public void SetExpiredFromStore (boolean isNew) throws Exception {
        if (isPersist) {
            controller.SetExpiredFromStore(this,isNew);
        } else {
            throw new Exception("Unable To Set Expired From Store");
        }
    }
    public void setQuantity(int quantity) throws Exception {
        this.quantity = quantity;
        controller.SetProductQuantityInWareHouse(this,quantity);
    }

    public double getCost_price() {
        return cost_price;
    }

    public double getSelling_price() {
        return selling_price;
    }

    public int getDays_for_expired() {
        return days_for_expired;
    }

    public int getDiscount() {
        return discount;
    }

    public int getId() {
        return Id;
    }

    public int getMin_quantity_to_alert() {
        return min_quantity_to_alert;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getCategory() {
        return category;
    }

    public String getManufacture() {
        return manufacture;
    }

    public String getName() {
        return name;
    }

    public String getSub_category() {
        return sub_category;
    }

    public String getSub_sub_category() {
        return sub_sub_category;
    }

    public void setCost_price(double price) {
        this.cost_price = price;
    }
    public void setDiscount(int discount)  {
        this.discount = discount;
    }
    public void setMinQuantityToAlert(int minQuantityToAlert){
        this.min_quantity_to_alert = minQuantityToAlert;
    }
    public void setCategory(String category)  {
        this.category = category;
    }
    public void setManufacture(String manufacture)  {
        this.manufacture = manufacture;
    }
    public void setSub_category(String sub_category) {
        this.sub_category = sub_category;
    }
    public void setSub_sub_category(String sub_sub_category)  {
        this.sub_sub_category = sub_sub_category;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setMin_quantity_to_alert(int minQuantityToAlert)  {
        this.min_quantity_to_alert = minQuantityToAlert;
    }
    public void setSelling_price(double selling_price) {
        this.selling_price = selling_price;
    }

    public void setId(int id) {
        Id = id;
    }
    public void setDays_for_expired(int days_for_expired) {
        this.days_for_expired = days_for_expired;
    }

    public Character getLocationInStore() {
        return locationInStore;
    }
}
