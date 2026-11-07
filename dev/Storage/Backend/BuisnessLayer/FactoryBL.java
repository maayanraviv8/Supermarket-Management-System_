package Storage.Backend.BuisnessLayer;

import Storage.Backend.DataServiceLayer.FactoryDAL;
import Storage.Backend.BuisnessLayer.StorageFacade;

public class FactoryBL {
    FactoryDAL factoryDal;
    StorageFacade storage;

    public FactoryBL() {
        factoryDal = new FactoryDAL();
        storage = new StorageFacade();
    }

    public StorageFacade getStorage() {
        return storage;
    }
    public boolean LoadData() throws Exception {
        return storage.LoadData();
    }

    public boolean DeleteData() {
        return factoryDal.DeleteData();
    }
}
