package client;

import chess.*;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;
import server.ServerFacade;
import ui.EscapeSequences;

import java.util.Scanner;

public class ClientMain {
    private final Scanner scan;
    private final ServerFacade serverFacade;

    public static void main(String[] args) {
        ClientMain client = new ClientMain();
        client.loggedOutLoop();
        client.closeScanner();
    }

    public ClientMain() {
        scan = new Scanner(System.in);
        serverFacade = new ServerFacade(0);
        System.out.println("Welcome! It's time to play chess.");
    }

    public void closeScanner() {
        scan.close();
    }

    private void clearPrint(String string) {
        System.out.println(EscapeSequences.ERASE_SCREEN + string);
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
                    login();
                }
                case 2 -> {
                    register();
                }
                case 3 -> {
                    String help = """
                        ----Help-------------------------------------
                        |  Login - login to the chess server account |
                        |  Register - create chess server account    |
                        |  Help - print help dialogue                |
                        |  Quit - exit the program                   |
                        ---------------------------------------------""";
                    clearPrint(help);
                }
                case 4 -> {
                    System.out.println("Goodbye!");
                    loop = false;
                }
            }
        }
    }

    private boolean login() {
        String username = getStringInput("Input username: ");
        String password = getStringInput("Input password: ");
        LoginResult result = serverFacade.login(new LoginRequest(username, password));
        return result.getAuthToken() != null && !result.getAuthToken().isEmpty();
    }

    private boolean register() {
        String username = getStringInput("Input username: ");
        String password = getStringInput("Input password: ");
        String email = getStringInput("Input email: ");
        RegisterResult result = serverFacade.register(new RegisterRequest(username, password, email));
        return result.getAuthToken() != null && !result.getAuthToken().isEmpty();
    }

    private int getIntInput(String prompt, int range) {
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

    private String getStringInput(String prompt) {
        System.out.println(prompt);
        while (true) {
            String input = scan.nextLine();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Input cannot be empty.");
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
