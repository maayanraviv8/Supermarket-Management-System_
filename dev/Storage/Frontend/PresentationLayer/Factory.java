package Storage.Frontend.PresentationLayer;

import Storage.Backend.ServiceLayer.FactorySL;
import Storage.Backend.ServiceLayer.StorageService;

public class Factory {


        private FactorySL factorySL;
        private StorageService storageService;

        public Factory(){
            factorySL = new FactorySL();
            storageService= factorySL.getStorageService();
        }

        public StorageService getStorageService() {
            return storageService;
        }

        public boolean loadData() {
            try {
                return factorySL.LoadData();
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return false;
        }

        public boolean DeleteData() {
            return factorySL.DeleteData();
        }
    }
