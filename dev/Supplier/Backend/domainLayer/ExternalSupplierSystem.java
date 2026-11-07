package Supplier.Backend.domainLayer;

import java.util.List;

public interface ExternalSupplierSystem {
    Product getProduct(String catalogNumber);
    List<Product> getAllProductsInCatalog();
}
