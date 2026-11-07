import PresentationLayer.CLI.CLIPres;
import PresentationLayer.GUI.GUIPres;
import PresentationLayer.GUI.LogInPageGUI;
import Supplier.SingeltonScanner;
import java.util.Scanner;

public class StartMain {
    public static void main(String[] args) throws Exception {
        Scanner scanner = SingeltonScanner.getInstance();
        if (args[0].equals("CLI")) {
            CLI();
        }
        else if (true || args[0].equals("GUI")) {
        GUI(args,scanner);
        }
        else
            System.out.println("UI invalid");
    }

    public static void CLI() throws Exception {
        CLIPres cliPres = new CLIPres();
        cliPres.start();
    }

    public static void GUI(String[] args, Scanner scanner) throws Exception {
        javafx.application.Application.launch(LogInPageGUI.class, args);
    }

}