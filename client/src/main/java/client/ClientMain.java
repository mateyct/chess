package client;

import chess.*;
import exception.ResponseException;
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
            try {
                if (serverFacade.signedIn()) {
                    loggedInLoop();
                } else {
                    loop = loggedOutLoop();
                }
            } catch (ResponseException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println("Goodbye!");
    }

    private boolean loggedOutLoop() throws ResponseException {
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

    private void loggedInLoop() throws ResponseException {
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
                joinGame();
            }
            case 4 -> {
                observeGame();
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

    private void login() throws ResponseException {
        String username = getStringInput("Input username: ");
        String password = getStringInput("Input password: ");
        serverFacade.login(new LoginRequest(username, password));
    }

    private void register() throws ResponseException {
        String username = getStringInput("Input username: ");
        String password = getStringInput("Input password: ");
        String email = getStringInput("Input email: ");
        serverFacade.register(new RegisterRequest(username, password, email));
    }

    private void createGame() throws ResponseException {
        String gameName = getStringInput("Input game name");
        CreateGameRequest request = new CreateGameRequest(gameName);
        CreateGameResult result = serverFacade.createGame(request);
        System.out.println("Game successfully created.");
        System.out.println("Game ID: " + result.getGameID());
    }

    private void listGames() throws ResponseException {
        ListGamesResult result = serverFacade.listGames();
        StringBuilder listString = new StringBuilder();
        int i = 1;
        for (ListGamesResult.GameMetadata data : result.getGames()) {
            listString.append("ID: ").append(i).append("; ");
            listString.append("Game Name: ").append(data.gameName()).append("; ");
            String whiteUsername = data.whiteUsername() == null ? "[EMPTY]" : data.whiteUsername();
            String blackUsername = data.blackUsername() == null ? "[EMPTY]" : data.blackUsername();
            listString.append("White Username: ").append(whiteUsername).append("; ");
            listString.append("Black Username: ").append(blackUsername).append("; ");
            listString.append("\n");
            i++;
        }
        System.out.println(listString);
    }

    private void joinGame() throws ResponseException {
        int gameCount = serverFacade.getGameCount();
        if (gameCount == 0) {
            System.out.println("Please list games before joining.");
            return;
        }
        int gameID = getIntInput(
            "Enter gameID of the game you want to play:",
            gameCount
        );
        int colorChoice = getIntInput("""
            Which color do you want to play?:
            1. WHITE
            2. BLACK""",
            2);
        String color = colorChoice == 1 ? "WHITE" : "BLACK";
        serverFacade.joinGame(gameID, color);
        drawTestBoard();
    }

    private void observeGame() throws ResponseException {
        drawTestBoard();
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
    }
}
