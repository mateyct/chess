package client;

import chess.*;

import java.util.Scanner;

public class ClientMain {
    private Scanner scan;

    public static void main(String[] args) {
        ClientMain client = new ClientMain();
        client.loggedOutLoop();
    }

    public ClientMain() {
        scan = new Scanner(System.in);
        System.out.println("Welcome! It's time to play chess.");
    }

    private void loggedOutLoop() {
        boolean loop = true;
        while (loop) {
            String prompt = """
                1: Login
                2: Register
                3: Help
                4: Quit""";
            switch (getIntInput(prompt, 4)) {
                case 1 -> {
                    System.out.println("Login");
                }
                case 2 -> {
                    System.out.println("Register");
                }
                case 3 -> {
                    System.out.println(prompt);
                }
                case 4 -> {
                    System.out.println("Goodbye!");
                    loop = false;
                }
            }
        }
    }

    private int getIntInput(String prompt, int range) {
        System.out.println(prompt);
        int choice = 0;
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

    private static void drawTestBoard() {
        ClientChessBoard board = new ClientChessBoard();
        ChessGame game = new ChessGame();
        ChessMove move = new ChessMove(
            new ChessPosition(2, 1),
            new ChessPosition(3, 1),
            null
        );
        try {
            game.makeMove(move);
        } catch (InvalidMoveException e) {
            System.out.println("That's unfortunate.");
        }
        board.draw(game.getBoard(), false);
        board.draw(game.getBoard(), true);
    }
}
