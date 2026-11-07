package Supplier.Backend.domainLayer;

import Supplier.Backend.DTO.ProductDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//productName + "_" + manufacturer + "_" + category1 + "_" + category2 + "_" + category3
public class SupermarketCatalog {
    private static SupermarketCatalog instance = null;
    private HashMap<String, List<SupplierProductEntry>> supermarketCatalog;
    private SupermarketCatalog() {
        supermarketCatalog = new HashMap<>();
    }
    public static SupermarketCatalog getInstance() {
        if (instance == null) {
            instance = new SupermarketCatalog();
        }
        return instance;
    }

    public void addProducts(long supplierId, ProductDTO product) {
        SupplierProductEntry prod = new SupplierProductEntry(supplierId, product.getCatalogNumber(), product.getCost(), product.getQuantityForDiscount(), product.getDiscount());
        supermarketCatalog.computeIfAbsent(product.getName()+"_"+product.getManufacturer()+"_"+product.getCategory()[0]+"_"+product.getCategory()[1]+"_"+product.getCategory()[2], k -> new ArrayList<>()).add(prod);
    }
    public void removeProducts(long supplierId, ProductDTO product) {
        List<SupplierProductEntry> entries = supermarketCatalog.get(product.getName()+"_"+product.getManufacturer()+"_"+product.getCategory()[0]+"_"+product.getCategory()[1]+"_"+product.getCategory()[2]);
        if (entries != null) {
            entries.removeIf(entry -> entry.getSupplierId() == supplierId);
            if (entries.isEmpty()) {
                supermarketCatalog.remove(product.getName()+"_"+product.getManufacturer()+"_"+product.getCategory()[0]+"_"+product.getCategory()[1]+"_"+product.getCategory()[2]);
            }
        }
    }
    public void updateDiscount(long supplierId, ProductDTO product, int discount) {
        List<SupplierProductEntry> entries = supermarketCatalog.get(product.getName()+"_"+product.getManufacturer()+"_"+product.getCategory()[0]+"_"+product.getCategory()[1]+"_"+product.getCategory()[2]);
        if (entries != null) {
            for (SupplierProductEntry entry : entries) {
                if (entry.getSupplierId() == supplierId) {
                    entry.setDiscount(discount);
                    break;
                }
            }
        }
    }
    public void updatePrice(long supplierId, ProductDTO product, double price) {
        List<SupplierProductEntry> entries = supermarketCatalog.get(product.getName()+"_"+product.getManufacturer()+"_"+product.getCategory()[0]+"_"+product.getCategory()[1]+"_"+product.getCategory()[2]);
        if (entries != null) {
            for (SupplierProductEntry entry : entries) {
                if (entry.getSupplierId() == supplierId) {
                    entry.setPricePerUnit(price);
                    break;
                }
            }
        }
    }
    public void updateQuantity(long supplierId, ProductDTO product, int quantity) {
        List<SupplierProductEntry> entries = supermarketCatalog.get(product.getName()+"_"+product.getManufacturer()+"_"+product.getCategory()[0]+"_"+product.getCategory()[1]+"_"+product.getCategory()[2]);
        if (entries != null) {
            for (SupplierProductEntry entry : entries) {
                if (entry.getSupplierId() == supplierId) {
                    entry.setQuantityForDiscount(quantity);
                    break;
                }
            }
        }
    }
    public void removeAllSupplierProducts(long supplierId) {
        supermarketCatalog.values().forEach(entries -> entries.removeIf(entry -> entry.getSupplierId() == supplierId));
        supermarketCatalog.values().removeIf(List::isEmpty);
    }

    public long getLowestPriceProduct(String productName, String manufacturer, String category1, String category2, String category3, int quantity) {
        List<SupplierProductEntry> entries = supermarketCatalog.get(productName+"_"+manufacturer+"_"+category1+"_"+category2+"_"+category3);
        if (entries != null) {
            SupplierProductEntry lowestPriceProduct = entries.get(0);
            for (SupplierProductEntry entry : entries) {
                if (entry.getTotalPrice(quantity) < lowestPriceProduct.getTotalPrice(quantity)) {
                    lowestPriceProduct = entry;
                }
            }
            return lowestPriceProduct.getSupplierId();
        }
        return -1;
    }
    public long getLowestPriceProduct(String product, int quantity) {
        List<SupplierProductEntry> entries = supermarketCatalog.get(product);
        if (entries != null) {
            SupplierProductEntry lowestPriceProduct = entries.get(0);
            for (SupplierProductEntry entry : entries) {
                if (entry.getTotalPrice(quantity) < lowestPriceProduct.getTotalPrice(quantity)) {
                    lowestPriceProduct = entry;
                }
            }
            return lowestPriceProduct.getSupplierId();
        }
        return -1;
    }
    public String catalogNumber(String category, long supplierID){
        List<SupplierProductEntry> entries = supermarketCatalog.get(category);
        if (entries != null) {
            for (SupplierProductEntry entry : entries) {
                if (entry.getSupplierId() == supplierID) {
                    return entry.getSupplierCatalogNumber();
                }
            }
        }
        return null;
    }
}
