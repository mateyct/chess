package ui;

import java.util.Scanner;

public class CLIUtils {
    public static int getIntInput(String prompt, int range, Scanner scan) {
        System.out.println(prompt);
        int choice;
        while (true) {
            try {
                String input = scan.nextLine().strip();
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                choice = 0;
            }
            if (choice >= 1 && choice <= range) {
                return choice;
            }
            System.out.println("Invalid choice provided. Please choose a valid option");
        }
    }

    public static String getStringInput(String prompt, Scanner scan) {
        System.out.println(prompt);
        while (true) {
            String input = scan.nextLine();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Input cannot be empty.");
        }
    }
}
