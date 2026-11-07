package HRandDelivery.Utils;

public class UserOutput {
    // This Singleton class is responsible for displaying output to the user.

    private static UserOutput instance;

    public static UserOutput getInstance() {
        if (instance == null) {
            instance = new UserOutput();
        }
        return instance;
    }

    public void print(String message) {
        System.out.println(message);
    }
}
