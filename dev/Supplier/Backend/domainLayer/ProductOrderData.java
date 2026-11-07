package Supplier.Backend.domainLayer;

public class ProductOrderData {
    private int quantity;
    private int discount;
    private Product product;

    public ProductOrderData(int quantity, Product product) {
        this.quantity = quantity;
        this.discount = 0;
        this.product = product;
    }
    public ProductOrderData(int quantity, Product product, int discount) {
        this.quantity = quantity;
        this.discount = 0;
        this.product = product;
        this.discount = discount;
    }

    public int getQuantity() {
        return quantity;
    }
    public int getDiscount() {
        return discount;
    }
    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public double totalPricePerProduct() {
        if(discount ==0){
            if (product.getQuantityForDiscount() > quantity){
                return product.getCost() * quantity;
            }
            else{
                double firstDisc =  (100.0 - product.getDiscount()) / 100.0;
                return  product.getCost() * firstDisc * quantity;
            }
        }
        else{
            if (product.getQuantityForDiscount() > quantity){
                double secondDisc = (100.0 - discount) / 100.0;
                return product.getCost() * secondDisc * quantity;
            }
            else{
                double firstDisc =  (100.0 - product.getDiscount()) / 100.0;
                double secondDisc = (100.0 - discount) / 100.0;
                return product.getCost() * firstDisc * secondDisc * quantity;
            }
        }
    }
    public Product getProduct() {
        return product;
    }

    public String toString() {
        return "Product:" +
                " Product Catalog Number=" + product.getCatalogNumber() +
                ", Product Name=" + product.getName() +
                ", quantity=" + quantity +
                ", Price In Catalog=" + product.getCost() +
                ", discount=" + totalDiscount() +
                ", final price="+ totalPricePerProduct();
    }

    private String totalDiscount(){
        if(discount ==0){
            if(product.getQuantityForDiscount() > quantity){
                return "0";
            }
            else{
                return ""+product.getDiscount()+"%";
            }
        }
        else{
            if(product.getQuantityForDiscount() > quantity){
                return ""+discount+"%";
            }
            else{
                return product.getDiscount()+"% with extra discount of "+discount+"%";
            }
        }
    }



}


