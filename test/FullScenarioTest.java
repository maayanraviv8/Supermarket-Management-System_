import Storage.Frontend.PresentationLayer.Factory;

import java.io.*;

public class FullScenarioTest {
    public static void main(String[] args) throws Exception {
        String[] inputLines = {
                "1","no","0","no","2","Y",
                // User registration
                "n", "tester pass123",

                // Add products
                "1", "Milk Dairy DairyA Shelf1 4.5 5.0 301 Tnuva 20 5",
                "1", "Bread Bakery BreadA Shelf2 3.0 3.5 302 Angel 15 4",
                "1", "Cheese Dairy DairyB Shelf3 6.0 6.5 303 Tara 10 3",
                "1", "Butter Dairy DairyC Shelf4 5.0 5.5 304 Tnuva 8 2",
                "1", "Yogurt Dairy DairyC Shelf4 2.0 2.5 305 Tara 12 1",

                // Delete/Buy
                "2", "302 5",
                "2", "305 2",
                "2", "999 1",

                // Move to store
                "3", "301 10",
                "3", "305 1",
                "3", "999 5",

                // Damaged report
                "4",
                "8", "305 1",
                "4",

                // Expired
                "5",
                "9",
                "5",

                // Sales
                "6", "Dairy 10",
                "6", "Bakery 20",
                "6", "NonExistent 15",

                "7", "301 15",
                "7", "304 5",
                "7", "999 20",

                // Add damaged
                "8", "303 2",
                "8", "305 1",
                "8", "999 1",

                // Expired
                "9",

                // Reports
                "10", "Dairy",
                "10", "Bakery",
                "10", "NonExistent",

                "11", "DairyA",
                "11", "DairyC",
                "11", "UnknownSubCat",

                "12", "Shelf1",
                "12", "Shelf4",
                "12", "InvalidShelf",

                "14",
                "13",
                // New user
                "n", "newuser 4567",
                "1", "Jam Pantry PantryA Shelf5 3.0 3.3 306 YadMordechai 6 2",
                "1", "Eggs Fridge EggA Shelf6 4.0 4.5 307 FarmerJoe 10 5",
                "1", "Juice Drinks JuiceA Shelf7 5.0 5.8 308 Prigat 8 3",

                // Invalid op
                "2", "306 -1",

                // Logout
                "13",

                // Wrong login
                "y", "tester wrongpass",

                // Correct login
                "y", "tester pass123",

                // Exit
                "14","15"
        };

        // Simulate input
        String input = String.join(System.lineSeparator(), inputLines);
        InputStream originalIn = System.in;

        try {
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            Factory factory = new Factory();
            //factory.DeleteData();  // Reset state
            StartMain.main(new String[]{"1"}); // Run UI
        } catch (Exception e) {
            System.out.println("⚠️ Exception occurred: " + e.getMessage());
        } finally {
            System.setIn(originalIn);
            System.out.println("✅ Full scenario test completed.");
        }
    }
}
