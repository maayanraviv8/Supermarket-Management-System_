package HRandDelivery.Utils;

import java.util.Scanner;

public class UserInput {
    // This Singleton class is responsible for handling user input
    // It can include methods for reading input from the console, validating input, etc.

    private static UserInput instance;
    Scanner input;

    private UserInput() {
        input = new Scanner(System.in);
    }

    public static UserInput getInstance() {
        if (instance == null) {
            instance = new UserInput();
        }
        return instance;
    }

    public String getUserInput() {
        return input.nextLine();
    }
}
