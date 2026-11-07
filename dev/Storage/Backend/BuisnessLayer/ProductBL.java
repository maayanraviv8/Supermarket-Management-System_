package Storage.Backend.BuisnessLayer;

import Storage.Backend.DataServiceLayer.ProductDAO;

import java.util.Date;

public class ProductBL {
    protected String name;
    protected String category;
    protected String sub_category;
    protected String sub_sub_category;
    protected double cost_price;
    protected int discount;
    protected int min_quantity_to_alert;
    protected double selling_price;
    protected int Id;
    protected String manufacture;
    protected int days_for_expired;
    protected ProductDAO product_dao;
    protected int quantity;
    protected Character locationInStore;

    public ProductBL(String name, String category, String sub_category, String sub_sub_category,
                     double price, int discount, int min_quantity_to_alert, double selling_price, int id, String manufacture, int days_for_expired, int quantity) {


        this.product_dao=new ProductDAO(name,category,sub_category,sub_sub_category,price,discount,min_quantity_to_alert,selling_price,id,manufacture,days_for_expired,quantity);
        this.name = name;
        this.category = category;
        this.sub_category = sub_category;
        this.sub_sub_category = sub_sub_category;
        this.cost_price = price;
        this.discount = discount;
        this.min_quantity_to_alert = min_quantity_to_alert;
        this.selling_price = selling_price;
        this.Id = id;
        this.manufacture = manufacture;
        this.days_for_expired = days_for_expired;
        this.quantity = quantity;
        this.locationInStore = category.charAt(0);
    }
    public ProductBL(ProductDAO product_dao) {
        this.product_dao=product_dao;
        this.name=product_dao.getName();
        this.category=product_dao.getCategory();
        this.sub_category=product_dao.getSub_category();
        this.sub_sub_category=product_dao.getSub_sub_category();
        this.cost_price=product_dao.getCost_price();
        this.discount=product_dao.getDiscount();
        this.min_quantity_to_alert=product_dao.getMin_quantity_to_alert();
        this.selling_price=product_dao.getSelling_price();
        this.Id=product_dao.getId();
        this.manufacture=product_dao.getManufacture();
        this.days_for_expired=product_dao.getDays_for_expired();
        this.quantity=product_dao.getQuantity();
        this.locationInStore = category.charAt(0);
    }
    public ProductBL(ProductBL other){
        this.product_dao=new ProductDAO(other.getName(),other.getCategory(),other.getSub_category(),other.getSub_sub_category(),other.getCost_price(),other.getDiscount(),other.getMin_quantity_to_alert(),other.getSelling_price(), other.getId(), other.getManufacture(),other.getDays_for_expired(), other.getQuantity());
        this.name = other.getName();
        this.category = other.getCategory();
        this.sub_category = other.getSub_category();
        this.sub_sub_category = other.getSub_sub_category();
        this.cost_price = other.getCost_price();
        this.discount = other.getDiscount();
        this.min_quantity_to_alert = other.getMin_quantity_to_alert();
        this.selling_price = other.getSelling_price();
        this.Id = other.getId();
        this.manufacture = other.getManufacture();
        this.days_for_expired = other.getDays_for_expired();
        this.quantity = other.getQuantity();
        this.locationInStore = category.charAt(0);
    }
    protected void setName(String name){
        this.name = name;
        this.product_dao.setName(name);
    }
    protected void setCategory(String category){
        this.category = category;
        this.product_dao.setCategory(category);
    }
    public void setQuantity(int quantity) throws Exception {
        this.quantity = quantity;
        this.product_dao.setQuantity(quantity);
    }
    public int getQuantity(){
        return this.quantity;
    }
    protected void setSub_category(String sub_category){
        this.sub_category = sub_category;
        this.product_dao.setSub_category(sub_category);
    }
    protected void setSub_sub_category(String sub_sub_category){
        this.sub_sub_category = sub_sub_category;
        this.product_dao.setSub_sub_category(sub_sub_category);
    }
    protected void setCost_price(double cost_price){
        this.cost_price = cost_price;
        this.product_dao.setCost_price(cost_price);
    }
    public void setDiscount(int discount){
        this.discount = discount;
        this.product_dao.setDiscount(discount);
    }
    protected void setMin_quantity_to_alert(int min_quantity_to_alert){
        this.min_quantity_to_alert = min_quantity_to_alert;
        this.product_dao.setMin_quantity_to_alert(min_quantity_to_alert);
    }
    public void setDays_for_expired(int days_for_expired) {
        this.days_for_expired = days_for_expired;
        this.product_dao.setDays_for_expired(days_for_expired);
    }
    protected void setSelling_price(double selling_price){
        this.selling_price = selling_price;
        this.product_dao.setSelling_price(selling_price);
    }
    protected void setId(int Id){
        this.Id = Id;
        this.product_dao.setId(Id);
    }
    protected void setManufacture(String manufacture){
        this.manufacture = manufacture;
        this.product_dao.setManufacture(manufacture);
    }
    public String getName() {
        return name;
    }
    public String getCategory() {
        return category;
    }
    protected String getSub_category() {
        return sub_category;
    }
    protected String getSub_sub_category() {
        return sub_sub_category;
    }
    protected double getCost_price() {
        return cost_price;
    }
    protected int getDiscount() {
        return discount;
    }
    protected int getMin_quantity_to_alert() {
        return min_quantity_to_alert;
    }
    protected double getSelling_price() {
        return selling_price;
    }
    protected int getId() {
        return Id;
    }
    protected String getManufacture() {
        return manufacture;
    }
    public int getDays_for_expired() {
        return days_for_expired;
    }
    public ProductDAO getProduct_dao() {
        return product_dao;
    }
    protected Character getLocationInStore() {
        return locationInStore;
    }

    @Override
    public String toString() {
        return  "name= " + name +
                ", category= " + category +
                ", manufactor= " + manufacture;
    }
}
