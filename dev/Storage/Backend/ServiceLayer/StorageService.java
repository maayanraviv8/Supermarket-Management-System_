package Storage.Backend.ServiceLayer;
import Storage.Backend.BuisnessLayer.*;

import java.util.List;

public class StorageService {
    StorageFacade storage;
    public StorageService(StorageFacade storage) {
        this.storage = storage;
    }

        public Response AddNewProduct(String line)
    {   line=line.trim();
        String[] product = line.split(" ");
        Response res;
        try
        {
            this.storage.AddNewProductToWareHouse(product[0],product[1],product[2],product[3],Double.parseDouble(product[4])
                    ,Double.parseDouble(product[5]),Integer.parseInt(product[6]),product[7],Integer.parseInt(product[8]),Integer.parseInt(product[9]));
            res = new Response(null,true);
        }
        catch(Exception ex)
        {
            res= new Response(ex.getMessage());
        }
        return res;
    }

    public Response DeleteProduct(String line) {
        Response res;
        String[] id=line.split(" ");
        try
        {
            this.storage.DeleteProduct(Integer.parseInt(id[0]),Integer.parseInt(id[1]));
            res = new Response(null,true);
        }
        catch(Exception ex)
        {
            res= new Response(ex.getMessage());
        }
        return res;
    }


    public Response MoveProduct(String line) {
        Response res;
        line=line.trim();
        String[] product = line.split(" ");
        try
        {
            this.storage.MoveProduct(Integer.parseInt(product[0]),Integer.parseInt(product[1]));
            res = new Response(null,true);
        }
        catch(Exception ex)
        {
            res= new Response(ex.getMessage());
        }
        return res;
    }


    public Response ReportAboutDamaged() {
        Response res;
        try
        {
            List<String> products=this.storage.ReportAboutDamaged();
            res = new Response(products,true);
        }
        catch(Exception ex)
        {
            res= new Response(ex.getMessage());
        }
        return res;
    }


    public  Response ReportAboutExpired() {
        Response res;
        try
        {
            List<String> products=this.storage.ReportAboutExpired();
            res = new Response(products,true);
        }
        catch(Exception ex)
        {
            res= new Response(ex.getMessage());
        }
        return res;
    }


    public  Response ApplySaleByCategory(String line) {
        Response res;
        line=line.trim();
        String[] product = line.split(" ");
        try
        {
            this.storage.ApplySaleByCategory(product[0],Integer.parseInt(product[1]) );
            res = new Response(null,true);
        }
        catch(Exception ex)
        {
            res= new Response(ex.getMessage());
        }
        return res;
    }


    public  Response ApplySaleByProductId(String line) {
        Response res;
        line=line.trim();
        String[] product = line.split(" ");
        try
        {
            this.storage.ApplySaleByProductId(Integer.parseInt(product[0]),Integer.parseInt(product[1]));
            res = new Response(null,true);
        }
        catch(Exception ex)
        {
            res= new Response(ex.getMessage());
        }
        return res;
    }

    public Response AddDamagedProduct(String line) {
        Response res;
        line=line.trim();
        String[] product = line.split(" ");
        try
        {
            this.storage.AddDamagedProduct(Integer.parseInt(product[0]),Integer.parseInt(product[1]));
            res = new Response(null,true);
        }
        catch(Exception ex)
        {
            res= new Response(ex.getMessage());
        }
        return res;
    }
    public Response AddExpiredProduct() {
        Response res;
        try
        {
            this.storage.AddExpiredProduct();
            res = new Response(null,true);
        }
        catch(Exception ex)
        {
            res= new Response(ex.getMessage());
        }
        return res;
    }
    public Response ReportAboutCategory(String line) {
        Response res;
        try
        {
            String products=this.storage.ReportAboutCategory(line);
            res = new Response(products,true);
        }
        catch(Exception ex)
        {
            res= new Response(ex.getMessage());
        }
        return res;
    }
    public Response ReportAboutSubCategory(String line) {
        Response res;
        try
        {
            String products=this.storage.ReportAboutSubCategory(line);
            res = new Response(products,true);
        }
        catch(Exception ex)
        {
            res= new Response(ex.getMessage());
        }
        return res;
    }
    public Response ReportAboutSubSubCategory(String line) {
        Response res;
        try
        {
            String products=this.storage.ReportAboutSubSubCategory(line);
            res = new Response(products,true);
        }
        catch(Exception ex)
        {
            res= new Response(ex.getMessage());
        }
        return res;
    }

    public  Response AnnualOrder() {
        Response res=new Response();
        try
        {
            res = new Response(null,this.storage.AnnualOrder());
        }
        catch(Exception ex)
        {
            res= new Response(ex.getMessage());
        }
        return res;
    }
    public Response NewDay() {
        Response res;
        try
        {
            this.storage.newDay();
            res = new Response(null,true);
        }
        catch(Exception ex)
        {
            res= new Response(ex.getMessage());
        }
        return res;
    }
}
