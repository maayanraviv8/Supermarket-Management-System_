package Supplier;

import java.util.Scanner;

public class SingeltonScanner {
    private static final Scanner INSTANCE = new Scanner(System.in);

    private SingeltonScanner() {
        // private constructor to prevent instantiation
    }

    public static Scanner getInstance() {
        return INSTANCE;
    }
}
