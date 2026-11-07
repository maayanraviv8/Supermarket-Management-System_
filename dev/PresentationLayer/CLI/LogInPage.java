package PresentationLayer.CLI;


import HRandDelivery.Utils.UserInput;
import HRandDelivery.Utils.UserOutput;

public class LogInPage {
    private String openingText;
    private UserInput userInput;
    private UserOutput userOutput;

    public LogInPage() {
        this.userInput = UserInput.getInstance();
        this.userOutput = UserOutput.getInstance();
        this.openingText = "Welcome to the Super-Lee Management System :)";
    }

    public String display() {

        userOutput.print(openingText);

        userOutput.print("Please enter your Employee Id / Supplier Id: ");
        String loginDetails = userInput.getUserInput();

        return loginDetails;
    }
}
