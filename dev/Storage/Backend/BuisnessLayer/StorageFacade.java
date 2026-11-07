package Storage.Backend.BuisnessLayer;

import Storage.Backend.DataServiceLayer.ProductDAO;
import Storage.Backend.DataServiceLayer.StorageController;
import Supplier.Backend.domainLayer.Product;
import Supplier.Backend.domainLayer.Supplier;
import Supplier.Backend.DTO.OrderDTO;
import Supplier.Backend.serviceLayer.SupplierService;

import javax.swing.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StorageFacade {

    private Map<Integer,List<ProductBL>> products_in_store;
    private Map<Integer,List<ProductBL>> products_in_warehouse;
    private List<String> gotdiscount;
    private Map<Integer,List<ProductBL>> expired_products;
    private Map<Integer,List<ProductBL>> damaged_products;
    private String location = "Beer Sheva";
    private Map<String,Integer> productsIds;
    private StorageController controller;
    private SupplierService supplierService;

    public StorageFacade() {
        this.products_in_store=new HashMap<>();
        this.products_in_warehouse=new HashMap<>();
        this.expired_products=new HashMap<>();
        this.damaged_products=new HashMap<>();
        this.gotdiscount=new ArrayList<>();
        this.productsIds=new HashMap<>();
        this.controller=new StorageController();
        supplierService=SupplierService.getInstance();
    }

    public String AddNewProductToWareHouse(String name, String category, String sub_category, String sub_sub_category,
                                           double price, double selling_price, int Id, String manufacture,int quantity, int daysforexpired) throws Exception{
        int discount =0;
        int min_quantity_to_alert=quantity*3/10;

        if(!products_in_warehouse.containsKey(Integer.valueOf(Id))) {
            products_in_warehouse.put(Integer.valueOf(Id), new ArrayList<ProductBL>());
        }
        else{
            discount = products_in_warehouse.get(Integer.valueOf(Id)).getFirst().getDiscount();
        }
        ProductBL prodToAdd = new ProductBL(name,category,sub_category,sub_sub_category,price,discount,min_quantity_to_alert,selling_price,Id,manufacture,daysforexpired,quantity);
        boolean isOldExpiry = false;
        for(ProductBL prod :  products_in_warehouse.get(Integer.valueOf(Id))){
            if(prod.getDays_for_expired()==daysforexpired){
                isOldExpiry = true;
                prod.setQuantity(prod.getQuantity() + quantity);
                prod.getProduct_dao().setQuantity(prod.getQuantity());
            }
        }
        if(!isOldExpiry){
            products_in_warehouse.get(Integer.valueOf(Id)).add(prodToAdd);
            prodToAdd.getProduct_dao().AddProduct();
        }

        String strId=name+"_"+manufacture+"_"
                +category+"_"+sub_category+"_"+sub_sub_category;
        if(!productsIds.containsKey(strId)) {
            productsIds.put(name + "_" + manufacture + "_"
                    + category + "_" + sub_category + "_" + sub_sub_category, Id);
            controller.AddId(strId,Id);
        }
        return "Products added to warehouse";
    }

    public String DeleteProduct(int id, int quantity) throws Exception {
        int storeQty = 0;
        int warehouseQty = 0;
        if(products_in_store.containsKey(Integer.valueOf(id))) {
            for (ProductBL p : products_in_store.get(Integer.valueOf(id))) {
                storeQty += p.getQuantity();
            }
        }
        if(products_in_warehouse.containsKey(Integer.valueOf(Integer.valueOf(id)))) {
            for (ProductBL p : products_in_warehouse.get(Integer.valueOf(id))) {
                warehouseQty += p.getQuantity();
            }
        }
        int totalQty = storeQty + warehouseQty;

        if (totalQty < quantity) {
            throw new Exception("Product not found or insufficient quantity");
        }

        // Move from warehouse if store has less than required
        if (storeQty < quantity) {
            int neededFromWarehouse = quantity - storeQty;
            MoveProduct(id, neededFromWarehouse);
        }

        String name=products_in_store.get(Integer.valueOf(id)).get(0).getName();

        // Now delete from store
        ProductBL prodToDelete = products_in_store.get(Integer.valueOf(id)).getFirst();
        while(quantity>0){
            if (prodToDelete.getQuantity()<=quantity) {
                prodToDelete.getProduct_dao().DeleteProduct(0);
                quantity = quantity - (prodToDelete.getQuantity());
                products_in_store.get(Integer.valueOf(id)).removeFirst();
                if(!products_in_store.get(id).isEmpty()) {
                    prodToDelete = products_in_store.get(Integer.valueOf(id)).getFirst();
                }
            }
            else{
                int quanAfterDelete = prodToDelete.getQuantity() - quantity;
                prodToDelete.setQuantity(quanAfterDelete);
                prodToDelete.getProduct_dao().DeleteProduct(quanAfterDelete);
                quantity =0;
            }
        }

        // Clean up warehouse list if empty
        if (products_in_warehouse.containsKey(Integer.valueOf(id)) && products_in_warehouse.get(Integer.valueOf(id)).isEmpty()) {
            //products_in_warehouse.remove(Integer.valueOf(id));
        }

        // Clean up store list if empty
        if (products_in_store.containsKey(Integer.valueOf(id)) && products_in_store.get(Integer.valueOf(id)).isEmpty()) {
            if(products_in_warehouse.containsKey(Integer.valueOf(id))&&!products_in_warehouse.get(Integer.valueOf(id)).isEmpty()) {
                MoveProduct(id, 1);
            }
        }
        if (isOrderNeeded(Integer.valueOf(id))){
            MakeOrderWhenEmpty(products_in_store.get(Integer.valueOf(id)).getFirst(), products_in_store.get(Integer.valueOf(id)).getFirst().getQuantity());
            showPopup("More " + products_in_store.get(Integer.valueOf(id)).getFirst().getName()+"s are being ordered");
        }

        return "Product deleted";
    }
    public String MoveProduct(int id, int quantity) throws Exception {
        int wareHouseQTY = 0;
        if(!products_in_warehouse.containsKey(Integer.valueOf(id))){
            throw new Exception("Product not found in warehouse");
        }
        for (ProductBL p : products_in_warehouse.get(Integer.valueOf(id))) {
                wareHouseQTY +=p.getQuantity();
        }
        if(wareHouseQTY<quantity) {
            throw new Exception("Product not found in this amount in warehouse, there are only " + products_in_warehouse.get(Integer.valueOf(id)).size() + " of it");
        }
        else{
            if(!products_in_store.containsKey(Integer.valueOf(id))){
                products_in_store.put(Integer.valueOf(id), new ArrayList<>());
            }
            boolean isExisting = false;
            ProductBL theOneExisting = null;
            for (int i = 0; i < quantity; i++) {
                ProductBL prodToMove = products_in_warehouse.get(Integer.valueOf(id)).getFirst();
                while (quantity > 0) {
                    for (ProductBL p : products_in_store.get(Integer.valueOf(id))) {
                        if (prodToMove.getDays_for_expired() == p.getDays_for_expired()) {
                            isExisting = true;
                            theOneExisting = p;
                        }
                    }
                    if (prodToMove.getQuantity() < quantity) {
                        quantity = quantity - (prodToMove.getQuantity());
                        if (!isExisting) {
                            prodToMove.getProduct_dao().MoveProduct(prodToMove.getQuantity(), true);
                            products_in_store.get(Integer.valueOf(id)).add(products_in_warehouse.get(Integer.valueOf(id)).removeFirst());
                        } else {
                            prodToMove.getProduct_dao().MoveProduct(prodToMove.getQuantity(), false);
                            for (ProductBL p : products_in_store.get(Integer.valueOf(id))) {
                                if (p.getDays_for_expired() == theOneExisting.getDays_for_expired()) {
                                    p.setQuantity(p.getQuantity() + prodToMove.getQuantity());
                                }
                            }
                            products_in_warehouse.get(id).removeFirst();

                        }
                        if (!products_in_warehouse.get(id).isEmpty()) {
                            prodToMove = products_in_warehouse.get(Integer.valueOf(id)).getFirst();
                        }
                        isExisting = false;
                        theOneExisting = null;
                    } else {
                        for (ProductBL p : products_in_store.get(Integer.valueOf(id))) {
                            if (prodToMove.getDays_for_expired() == p.getDays_for_expired()) {
                                isExisting = true;
                                theOneExisting = p;
                            }
                        }
                        if (!isExisting) {
                            prodToMove.getProduct_dao().MoveProduct(quantity, true);
                            products_in_store.get(Integer.valueOf(id)).addFirst(new ProductBL((products_in_warehouse.get(Integer.valueOf(id)).getFirst())));
                            products_in_store.get(Integer.valueOf(id)).getFirst().setQuantity(quantity);
                            products_in_store.get(Integer.valueOf(id)).getFirst().getProduct_dao().Persist();
                            products_in_store.get(Integer.valueOf(id)).getFirst().getProduct_dao().Persist();
                        } else {
                            prodToMove.getProduct_dao().MoveProduct(quantity, false);
                            for (ProductBL p : products_in_store.get(Integer.valueOf(id))) {
                                if (p.getDays_for_expired() == theOneExisting.getDays_for_expired()) {
                                    p.setQuantity(p.getQuantity() + quantity);
                                }
                            }
                        }
                        prodToMove.setQuantity(prodToMove.getQuantity()-quantity);
                        quantity= 0;
                    }
                }
            }
        }
        if (isOrderNeeded(Integer.valueOf(id))){            MakeOrderWhenEmpty(products_in_store.get(Integer.valueOf(id)).getFirst(), products_in_store.get(Integer.valueOf(id)).getFirst().getQuantity());
            showPopup("More " + products_in_store.get(Integer.valueOf(id)).getFirst().getName()+"s are being ordered");
        }
        return "Product moved";
    }
    public List<String> ReportAboutDamaged() throws Exception {
        List<String> damaged_products_to_report=new ArrayList<>();
        for(Map.Entry<Integer,List<ProductBL>> entry : damaged_products.entrySet()) {
            String s = entry.getValue().getFirst().toString() + ", id: " + entry.getKey() + ", quantity: " + entry.getValue().size();
            damaged_products_to_report.addFirst(s);
        }
        if(damaged_products_to_report.isEmpty()){
            throw new Exception("No damaged products as for now");
        }
        return damaged_products_to_report;
    }
    public List<String> ReportAboutExpired() {
        List<String> expired_products_to_report=new ArrayList<>();
        for(Map.Entry<Integer,List<ProductBL>> entry : expired_products.entrySet()) {
            String s = entry.getValue().getFirst().toString()+", id: "+entry.getKey()+", quantity: "+entry.getValue().size();
            expired_products_to_report.add(s);
        }
        return expired_products_to_report;
    }
    public String ApplySaleByCategory(String category, int discount) throws Exception {
        int counter=0;
        if(!gotdiscount.contains(category)) {
            for (Map.Entry<Integer, List<ProductBL>> entry : products_in_store.entrySet()) {
                if (!entry.getValue().isEmpty() && entry.getValue().getFirst().getCategory().equals(category)) {
                    entry.getValue().getFirst().getProduct_dao().SetDiscountInStore(discount);
                    for (ProductBL product : entry.getValue()) {
                        product.setDiscount(discount);
                        counter++;
                    }
                }
            }
            for (Map.Entry<Integer, List<ProductBL>> entry : products_in_warehouse.entrySet()) {
                if (!entry.getValue().isEmpty() && entry.getValue().getFirst().getCategory().equals(category)) {
                    entry.getValue().getFirst().getProduct_dao().SetDiscountInWareHouse(discount);
                    for (ProductBL product : entry.getValue()) {
                        product.setDiscount(discount);
                        counter++;
                    }
                }
            }
            if(counter>0) {
                gotdiscount.add(category);
                return "Discount applied for " + category + " category";
            }
            else
                throw new Exception("Category does not exist");
        }
        else
            throw new Exception(category + " already got discount");
    }
    public String ApplySaleByProductId(int id, int discount)throws Exception {
        if(!products_in_warehouse.containsKey(Integer.valueOf(id))) {
            if(!products_in_store.containsKey(Integer.valueOf(id))) {
                throw new Exception("Product does not exist in the storage");
            }
            else{
                for(ProductBL product : products_in_store.get(Integer.valueOf(id))) {
                    product.setDiscount(discount);
                    product.getProduct_dao().SetDiscountInStore(discount);
                }
            }
        }
        else if(!products_in_store.containsKey(Integer.valueOf(id))){
            for(ProductBL product : products_in_warehouse.get(Integer.valueOf(id))) {
                product.setDiscount(discount);
                product.getProduct_dao().SetDiscountInWareHouse(discount);
            }
        }
        else{
            for(ProductBL product : products_in_warehouse.get(Integer.valueOf(id))) {
                product.getProduct_dao().SetDiscountInWareHouse(discount);
                product.setDiscount(discount);
            }
            for(ProductBL product : products_in_warehouse.get(Integer.valueOf(id))) {
                product.getProduct_dao().SetDiscountInWareHouse(discount);
                product.setDiscount(discount);

            }
        }
        return "Discount applied for product with id number:  " + id;
    }
    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public String AddDamagedProduct(int id,int quantity) throws Exception {
        int storeQty = 0;
        int warehouseQty = 0;
        if(products_in_store.containsKey(Integer.valueOf(id))) {
            for (ProductBL p : products_in_store.get(Integer.valueOf(id))) {
                storeQty += p.getQuantity();
            }
        }
        if(products_in_warehouse.containsKey(Integer.valueOf(id))) {
            for (ProductBL p : products_in_warehouse.get(Integer.valueOf(id))) {
                warehouseQty += p.getQuantity();
            }
        }
        int totalQty = storeQty + warehouseQty;

        if (totalQty < quantity) {
            throw new Exception("Product not found or insufficient quantity");
        }
        if(!damaged_products.containsKey(id)){
            damaged_products.put(id,new ArrayList<>());
        }

        while(quantity > 0 && storeQty > 0 ) {
            boolean isExsiting = false;
            ProductBL prodDamaged = products_in_store.get(id).getLast();
            if(!damaged_products.get(id).isEmpty()) {
                for (ProductBL product : damaged_products.get(id)) {
                    if (product.getDays_for_expired() == prodDamaged.getDays_for_expired()) {
                        isExsiting = true;
                    }
                }
            }
            if(prodDamaged.getQuantity()<=quantity){
                if(!isExsiting) {
                    prodDamaged.getProduct_dao().SetDamagedFromStore(prodDamaged.getQuantity(),true);
                    damaged_products.get(id).add(prodDamaged);
                }
                else{
                    for(ProductBL product : damaged_products.get(id)) {
                        if(product.getDays_for_expired()==prodDamaged.getDays_for_expired()) {
                            product.setQuantity(product.getQuantity()+prodDamaged.getQuantity());
                            prodDamaged.getProduct_dao().SetDamagedFromStore(prodDamaged.getQuantity(),false);
                        }
                    }
                }
                storeQty-=storeQty-prodDamaged.getQuantity();
                quantity-=quantity-prodDamaged.getQuantity();
                products_in_store.get(id).removeLast();
            }
            else if(prodDamaged.getQuantity()>quantity){
              if(!isExsiting) {
                    prodDamaged.getProduct_dao().SetDamagedFromStore(quantity,true);
                    prodDamaged.setQuantity(prodDamaged.getQuantity()-quantity);
                    damaged_products.get(id).add(prodDamaged);
                    for(ProductBL product : damaged_products.get(id)) {
                        if(product.getDays_for_expired() == prodDamaged.getDays_for_expired()) {
                            product.setQuantity(quantity);
                        }
                    }
                    quantity = 0;
                }
                else{
                    for(ProductBL product : damaged_products.get(id)) {
                        if (product.getDays_for_expired()==prodDamaged.getDays_for_expired()) {
                            product.setQuantity(product.getQuantity()+quantity);
                            prodDamaged.setQuantity(prodDamaged.getQuantity()-quantity);
                        }
                    }
                    quantity-=0;
                }
            }
        }
        if(quantity > 0) {
            while(quantity > 0) {
                boolean isExsiting = false;
                ProductBL prodDamaged = products_in_warehouse.get(id).getLast();
                for(ProductBL product : damaged_products.get(id)) {
                    if(product.getDays_for_expired()==prodDamaged.getDays_for_expired()) {
                        isExsiting = true;
                    }
                }
                if(prodDamaged.getQuantity()<=quantity){
                    if(!isExsiting) {
                        prodDamaged.getProduct_dao().SetDamagedFromWareHouse(prodDamaged.getQuantity(),true);
                        damaged_products.get(id).add(prodDamaged);
                    }
                    else{
                        for(ProductBL product : damaged_products.get(id)) {
                            if(product.getDays_for_expired()==prodDamaged.getDays_for_expired()) {
                                product.setQuantity(product.getQuantity()+prodDamaged.getQuantity());
                                prodDamaged.getProduct_dao().SetDamagedFromWareHouse(prodDamaged.getQuantity(),false);
                            }
                        }
                    }
                    storeQty-=storeQty-prodDamaged.getQuantity();
                    quantity-=quantity-prodDamaged.getQuantity();
                    products_in_warehouse.get(id).removeLast();
                }
                else if(prodDamaged.getQuantity()>quantity){
                    if(!isExsiting) {
                        prodDamaged.getProduct_dao().SetDamagedFromWareHouse(quantity,true);
                        prodDamaged.setQuantity(prodDamaged.getQuantity()-quantity);
                        damaged_products.get(id).add(prodDamaged);
                        for(ProductBL product : damaged_products.get(id)) {
                            if(product.getDays_for_expired() == prodDamaged.getDays_for_expired()) {
                                product.setQuantity(quantity);
                            }
                        }

                    }
                    else{
                        for(ProductBL product : damaged_products.get(id)) {
                            if (product.getDays_for_expired()==prodDamaged.getDays_for_expired()) {
                                product.setQuantity(product.getQuantity()+quantity);
                                prodDamaged.setQuantity(prodDamaged.getQuantity()-quantity);
                            }
                        }
                        quantity-=0;
                    }
                }
            }
        }
        if (isOrderNeeded(Integer.valueOf(id))){            MakeOrderWhenEmpty(products_in_store.get(Integer.valueOf(id)).getFirst(), products_in_store.get(Integer.valueOf(id)).getFirst().getQuantity());
            showPopup("More " + products_in_store.get(Integer.valueOf(id)).getFirst().getName()+"s are being ordered");
        }
        return "Product added to damaged product";
    }
    public String AddExpiredProduct() throws Exception {
        for(Map.Entry<Integer,List<ProductBL>> entry : products_in_warehouse.entrySet()) {
            for(ProductBL product : entry.getValue()) {
                if(product.getDays_for_expired()==0) {
                    if(expired_products.containsKey(entry.getKey())){
                        product.getProduct_dao().SetExpiredFromWareHouse(false);
                        expired_products.get(entry.getKey()).getFirst().setQuantity(expired_products.get(entry.getKey()).getFirst().getQuantity()+product.getQuantity());
                    }else{
                        expired_products.put(entry.getKey(),new ArrayList<>());
                        expired_products.get(entry.getKey()).add(product);
                        product.getProduct_dao().SetExpiredFromWareHouse(true);
                    }
                }
            }
        }
        for(Map.Entry<Integer,List<ProductBL>> entry : products_in_store.entrySet()) {
            for(ProductBL product : entry.getValue()) {
                if(product.getDays_for_expired()==0) {
                    if(expired_products.containsKey(entry.getKey())){
                        product.getProduct_dao().SetExpiredFromStore(false);
                        expired_products.get(entry.getKey()).getFirst().setQuantity(expired_products.get(entry.getKey()).getFirst().getQuantity()+product.getQuantity());
                    }else{
                        expired_products.put(entry.getKey(),new ArrayList<>());
                        expired_products.get(entry.getKey()).add(product);
                        product.getProduct_dao().SetExpiredFromStore(true);
                    }
                }
            }
            int id = entry.getKey();
            if (isOrderNeeded(Integer.valueOf(id))){                MakeOrderWhenEmpty(products_in_store.get(Integer.valueOf(id)).getFirst(), products_in_store.get(Integer.valueOf(id)).getFirst().getQuantity());
                showPopup("More " + products_in_store.get(Integer.valueOf(id)).getFirst().getName()+"s are being ordered");
            }
        }
        return "Expired products added to expired products list";
        }

    private void showPopup(String message) {
        JOptionPane.showMessageDialog(null, message, "Alert", JOptionPane.WARNING_MESSAGE);
    }
    public String ReportAboutCategory(String category) {
        StringBuilder report = new StringBuilder();
        for(Map.Entry<Integer, List<ProductBL>> entry : products_in_warehouse.entrySet()) {
            if (!entry.getValue().isEmpty() && entry.getValue().getFirst().getCategory().equals(category)) {
                int prodQuantity = entry.getValue().size();
                if (products_in_store.containsKey(entry.getKey())) {
                    prodQuantity += products_in_warehouse.get(entry.getKey()).size();
                }

                report.append("Product: ").append(entry.getValue().getFirst().getName()).append(" Quantity: ").append(prodQuantity).append("\n");
            }
        }
        return report.toString();
    }
    public String ReportAboutSubCategory(String sub_category) {
        StringBuilder report = new StringBuilder();
        for(Map.Entry<Integer, List<ProductBL>> entry : products_in_warehouse.entrySet()) {
            if (!entry.getValue().isEmpty() && entry.getValue().getFirst().getSub_category().equals(sub_category)) {
                int prodQuantity = entry.getValue().size();
                if (products_in_store.containsKey(entry.getKey())) {
                    prodQuantity += products_in_warehouse.get(entry.getKey()).size();
                }

                report.append("Product: ").append(entry.getValue().getFirst().getName()).append(" Quantity: ").append(prodQuantity).append("\n");
            }
        }
        return report.toString();
    }
    public String ReportAboutSubSubCategory(String sub_sub_category) {
        StringBuilder report = new StringBuilder();
        for(Map.Entry<Integer, List<ProductBL>> entry : products_in_warehouse.entrySet()) {
            if (!entry.getValue().isEmpty() && entry.getValue().getFirst().getSub_sub_category().equals(sub_sub_category)) {
                int prodQuantity = entry.getValue().size();
                if (products_in_store.containsKey(entry.getKey())) {
                    prodQuantity += products_in_warehouse.get(entry.getKey()).size();
                }

                report.append("Product: ").append(entry.getValue().getFirst().getName()).append(" Quantity: ").append(prodQuantity).append("\n");
            }
        }
        return report.toString();
    }
    private void ExpiryCheckAndUpdate() throws Exception{
        int numToExpire = 0;
        for(Map.Entry<Integer, List<ProductBL>> entry : products_in_store.entrySet()){
            for(ProductBL prod : entry.getValue()){
                    prod.setDays_for_expired(prod.getDays_for_expired()-1);
            }

            if(products_in_warehouse.containsKey(entry.getKey())){
                for(ProductBL prod : products_in_warehouse.get(entry.getKey())){
                        prod.setDays_for_expired(prod.getDays_for_expired()-1);

                }
            }
            AddExpiredProduct();
        }
    }
    private boolean isOrderNeeded(int id){
        int quantityOverAll = 0;
        int minQuantToAlert = 1;
        boolean ans =false;
        if(products_in_store.containsKey(id)){
            minQuantToAlert = products_in_store.get(id).getFirst().getMin_quantity_to_alert();
            for(ProductBL p : products_in_store.get(id)){
                quantityOverAll += p.getQuantity();
            }
            if (products_in_warehouse.containsKey(id)){
                for(ProductBL p : products_in_warehouse.get(id)){
                    quantityOverAll += p.getQuantity();
                }
            }

        }
        if(minQuantToAlert==quantityOverAll){
            ans = true;
        }
        return  ans;
    }
    public void newDay() throws Exception{
        Runnable dailyTask = () -> {
                try {
                    ExpiryCheckAndUpdate();
                    AnnualOrder();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
        };
        LocalTime targetTime = LocalTime.of(8, 0); // 8:00 AM
        long initialDelay = Duration.between(LocalTime.now(), targetTime).toSeconds();
        if (initialDelay < 0) {
            initialDelay += 24 * 60 * 60; // if it's already past target time today, schedule for tomorrow
        }

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(dailyTask, initialDelay, 24 * 60 * 60, TimeUnit.SECONDS);
    }
    synchronized public boolean AnnualOrder() throws Exception {
        Map<Long, Supplier> suppliers=supplierService.getAllSuppliers();
        List<ProductBL> stockupdatedExist=new ArrayList<>();
        List<Product> stockupdatedNonExist=new ArrayList<>();
        for(Map.Entry<Long,Supplier> supplier: suppliers.entrySet()){
            //if(supplier.getValue().getSupplyDay() +1 == LocalDate.now().getDayOfWeek().getValue()) {
            if(supplier.getValue().getSupplyDay()==1 || supplier.getValue().getSupplyDay()==0){
                Map<String, Integer> products = new HashMap<>();
                for (Product p : supplier.getValue().getAllProductsInCatalog()) {
                    String prod = p.getName() + "_" + p.getManufacturer() + "_"
                            + p.getCategory()[0] + "_" + p.getCategory()[1] + "_" + p.getCategory()[2];
                    Integer product_Bl_Id = productsIds.getOrDefault(prod, null);
                    if (product_Bl_Id != null) {
                        ProductBL tempProduct = products_in_warehouse.get(product_Bl_Id).getFirst();
                        products.put(prod, tempProduct.min_quantity_to_alert + 10);
                        stockupdatedExist.add(tempProduct);
                    } else {
                        products.put(prod, 10);
                        stockupdatedNonExist.add(p);
                        Random random = new Random();
                        int number = random.nextInt(1000 - 300 + 1) + 300;
                        controller.AddId(prod,number);
                        productsIds.put(prod,number);
                    }
                }
                if (!products.isEmpty()) {
                    OrderDTO order = new OrderDTO(LocalDate.now(), products, generateIsraeliPhoneNumber());
                    if (supplierService.addOrderFromStorage(order, supplier.getKey()).isSuccess()) {
                        for (ProductBL p : stockupdatedExist) {
                            AddNewProductToWareHouse(p.name, p.category, p.sub_category, p.sub_sub_category, p.cost_price
                                    , p.selling_price, p.Id, p.manufacture, 10 + p.min_quantity_to_alert, p.days_for_expired);
                        }
                        for (Product p : stockupdatedNonExist) {
                            String getprod = p.getName() + "_" + p.getManufacturer() + "_"
                                    + p.getCategory()[0] + "_" + p.getCategory()[1] + "_" + p.getCategory()[2];
                            Integer id = productsIds.get(getprod);
                            AddNewProductToWareHouse(p.getName(), p.getCategory()[0], p.getCategory()[1], p.getCategory()[2],
                                    p.getCost(), p.getDiscount() * p.getCost(), id
                                    , p.getManufacturer(), 10, p.getExpirationDate());
                        }
                    }
                }
            }
            stockupdatedExist.clear();
            stockupdatedNonExist.clear();
        }
        return true;
    }
    private static String generateIsraeliPhoneNumber() {
        Random rand = new Random();

        // Valid Israeli mobile prefixes
        String[] prefixes = {
                "050", "051", "052", "053", "054", "055", "056", "057", "058", "059"
        };

        // Choose a random prefix
        String prefix = prefixes[rand.nextInt(prefixes.length)];

        // Generate the remaining 7 digits
        StringBuilder phone = new StringBuilder(prefix);
        for (int i = 0; i < 7; i++) {
            phone.append(rand.nextInt(10)); // 0–9
        }

        return phone.toString();
    }
    public void MakeOrderWhenEmpty(ProductBL p, int quantity) throws Exception {
        Map<String, Integer> products = new HashMap<>();
        String prod = p.getName() + "_" + p.getManufacture() + "_"
        + p.getCategory() + "_" + p.getSub_category() + "_" + p.getSub_sub_category();
        products.put(prod, quantity + 10);

        OrderDTO order = new OrderDTO(LocalDate.now(), products, generateIsraeliPhoneNumber());
        if (supplierService.addOrder(order).isSuccess()){
            AddNewProductToWareHouse(p.getName(), p.getCategory(),p.getSub_category(),p.getSub_sub_category(),p.getCost_price(),p.getSelling_price(),p.getId(),p.getManufacture(),quantity+10,10);
        }


    }

    public boolean LoadData() {
        try {
            for (Map.Entry<Integer, List<ProductDAO>> products : controller.LoadProductsToWareHouse().entrySet()) {
                List<ProductBL> toInsert = new ArrayList<>();
                for (ProductDAO p : products.getValue()) {
                    p.Persist();
                    toInsert.add((new ProductBL(p)));


                }
                products_in_warehouse.put(toInsert.getFirst().getId(), toInsert);
            }
            for (Map.Entry<Integer, List<ProductDAO>> products : controller.LoadProductsToStore().entrySet()) {
                List<ProductBL> toInsert = new ArrayList<>();
                for (ProductDAO p : products.getValue()) {
                    p.Persist();
                    toInsert.add((new ProductBL(p)));
                }
                products_in_store.put(toInsert.getFirst().getId(), toInsert);
            }
            for (Map.Entry<Integer, List<ProductDAO>> products : controller.LoadDamagedProducts().entrySet()) {
                List<ProductBL> toInsert = new ArrayList<>();
                for (ProductDAO p : products.getValue()) {
                    p.Persist();
                    toInsert.add((new ProductBL(p)));
                }
                damaged_products.put(toInsert.getFirst().getId(), toInsert);
            }
            for (Map.Entry<Integer, List<ProductDAO>> products : controller.LoadExpiredProducts().entrySet()) {
                List<ProductBL> toInsert = new ArrayList<>();
                for (ProductDAO p : products.getValue()) {
                    p.Persist();
                    toInsert.add((new ProductBL(p)));
                }
                expired_products.put(toInsert.getFirst().getId(), toInsert);
            }
            productsIds = controller.LoadIds();
            return true;
        }
       catch (Exception e) {
            return false;
       }
    }

}
