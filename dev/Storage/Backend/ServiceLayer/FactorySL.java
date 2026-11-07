package Storage.Backend.ServiceLayer;


import Storage.Backend.BuisnessLayer.FactoryBL;
import Storage.Backend.BuisnessLayer.StorageFacade;

public class FactorySL {
    private FactoryBL factoryBL;
    private StorageService storageService;

    public FactorySL() {
        factoryBL = new FactoryBL();
        storageService =new StorageService(factoryBL.getStorage());
    }

    public StorageService getStorageService() {
        return storageService;
    }
    public boolean LoadData() throws Exception {
        return factoryBL.LoadData();
    }

    public boolean DeleteData() {
        return factoryBL.DeleteData();
    }
}
