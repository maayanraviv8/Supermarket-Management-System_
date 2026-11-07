package Supplier.Backend.domainLayer;

import Supplier.Backend.serviceLayer.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SupplierSystem implements ExternalSupplierSystem {
    private Map<String, Product> catalog = new HashMap<>();

    public SupplierSystem(){}

    public Message updateCatalog(List<Product> products){
        for(Product product : products){
            if(catalog.containsKey(product.getCatalogNumber())){
                return new Message<>(false, "Product "+product.getCatalogNumber()+" already exists in catalog");
            }
        }
        for(Product product : products){
            catalog.put(product.getCatalogNumber(), product);
        }
        return new Message<>(true, "Catalog updated successfully");
    }

    public Message updateCatalog(Product product){
        if(catalog.containsKey(product.getCatalogNumber())){
            return new Message<>(false, "Product "+product.getCatalogNumber()+" already exists in catalog");
        }
        catalog.put(product.getCatalogNumber(), product);
        return new Message<>(true, "Catalog updated successfully");
    }

    public Message removeProductFromCatalog(Product product){
        if(!catalog.containsKey(product.getCatalogNumber())){
            return new Message<>(false, "Product "+product.getCatalogNumber()+" does not exist in catalog");
        }
        catalog.remove(product.getCatalogNumber());
        return new Message<>(true, "Product removed from catalog");
    }

    public Product getProduct(String catalogNumber){
        return catalog.get(catalogNumber);
    }

    public List<Product> getAllProductsInCatalog(){
        return new ArrayList<>(catalog.values());
    }

    public void deleteAgreement(List<Product> products){
        for(Product product : products){
            catalog.remove(product.getCatalogNumber());
        }
    }



}
