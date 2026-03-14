package client;

import chess.*;
import request.CreateGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.CreateGameResult;
import result.ListGamesResult;
import server.ServerFacade;
import ui.EscapeSequences;

import java.util.Scanner;

public class ClientMain {
    private final Scanner scan;
    private final ServerFacade serverFacade;

    public static void main(String[] args) {
        ClientMain client = new ClientMain();
        client.mainLoop();
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

    private void mainLoop() {
        boolean loop = true;
        while (loop) {
            if (serverFacade.signedIn()) {
                loggedInLoop();
            }
            else {
                loop = loggedOutLoop();
            }
        }
        System.out.println("Goodbye!");
    }

    private boolean loggedOutLoop() {
        boolean loop = true;
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
                loop = false;
            }
        }
        return loop;
    }

    private void loggedInLoop() {
        String prompt = """
            1: Create Game
            2: List Games
            3: Join Game
            4: Observe Game
            5: Help
            6: Logout""";
        switch (getIntInput(prompt, 6)) {
            case 1 -> {
                createGame();
            }
            case 2 -> {
                listGames();
            }
            case 3 -> {
                System.out.println("Join game");
            }
            case 4 -> {
                System.out.println("Observe game");
            }
            case 5 -> {
                String help = """
                    ----Help-------------------------------------
                    |  Create Game - create a new chess game     |
                    |  List Game - get a list of all games       |
                    |  Join Game - join an existing game         |
                    |  Observe - watch a current game            |
                    |  Help - print help dialogue                |
                    |  Logout - logout from your account         |
                    ---------------------------------------------""";
                clearPrint(help);
            }
            case 6 -> {
                serverFacade.logout();
            }
        }
    }

    private void login() {
        String username = getStringInput("Input username: ");
        String password = getStringInput("Input password: ");
        serverFacade.login(new LoginRequest(username, password));
    }

    private void register() {
        String username = getStringInput("Input username: ");
        String password = getStringInput("Input password: ");
        String email = getStringInput("Input email: ");
        serverFacade.register(new RegisterRequest(username, password, email));
    }

    private void createGame() {
        String gameName = getStringInput("Input game name");
        CreateGameRequest request = new CreateGameRequest(gameName);
        CreateGameResult result = serverFacade.createGame(request);
        System.out.println("Game successfully created.");
        System.out.println("Game ID: " + result.getGameID());
    }

    private void listGames() {
        ListGamesResult result = serverFacade.listGames();
        StringBuilder listString = new StringBuilder();
        for (ListGamesResult.GameMetadata data : result.getGames()) {
            listString.append("ID: ").append(data.gameID()).append("; ");
            listString.append("Game Name: ").append(data.gameName()).append("; ");
            String whiteUsername = data.whiteUsername() == null ? "[EMPTY]" : data.whiteUsername();
            String blackUsername = data.blackUsername() == null ? "[EMPTY]" : data.blackUsername();
            listString.append("White Username: ").append(whiteUsername).append("; ");
            listString.append("Black Username: ").append(blackUsername).append("; ");
            listString.append("\n");
        }
        System.out.println(listString);
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
