package client;

import chess.*;
import exception.ResponseException;
import request.CreateGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.ListGamesResult;
import server.ServerFacade;
import ui.ClientChessBoard;
import ui.EscapeSequences;
import ui.GameplayCLI;
import websocket.commands.GameConnectionRole;

import java.util.Scanner;

import static ui.CLIUtils.getIntInput;
import static ui.CLIUtils.getStringInput;

public class ClientMain {
    private final Scanner scan;
    private final ServerFacade serverFacade;
    private final String url;
    private final int port;

    public static void main(String[] args) {
        String url = "http://localhost";
        int port = 8080;
        if (args.length == 2) {
            url = args[0];
            port = Integer.parseInt(args[1]);
        }
        ClientMain client = new ClientMain(url, port);
        client.mainLoop();
        client.closeScanner();
    }

    public ClientMain(String url, int port) {
        scan = new Scanner(System.in);
        serverFacade = new ServerFacade(url, port);
        System.out.println("Welcome! It's time to play chess.");
        this.url = url;
        this.port = port;
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
        switch (getIntInput(prompt, 4, scan)) {
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
        switch (getIntInput(prompt, 6, scan)) {
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
        String username = getStringInput("Input username: ", scan);
        String password = getStringInput("Input password: ", scan);
        serverFacade.login(new LoginRequest(username, password));
    }

    private void register() throws ResponseException {
        String username = getStringInput("Input username: ", scan);
        String password = getStringInput("Input password: ", scan);
        String email = getStringInput("Input email: ", scan);
        serverFacade.register(new RegisterRequest(username, password, email));
    }

    private void createGame() throws ResponseException {
        String gameName = getStringInput("Input game name", scan);
        CreateGameRequest request = new CreateGameRequest(gameName);
        serverFacade.createGame(request);
        System.out.println("Game successfully created.");
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
        clearPrint(listString.toString());
    }

    private void joinGame() throws ResponseException {
        int gameCount = serverFacade.getGameCount();
        if (gameCount == 0) {
            System.out.println("Please list games before joining.");
            return;
        }
        int gameID = getIntInput(
            "Enter gameID of the game you want to play:",
            gameCount,
            scan
        );
        int colorChoice = getIntInput("""
            Which color do you want to play?:
            1. WHITE
            2. BLACK""",
            2,
            scan);
        String color = colorChoice == 1 ? "WHITE" : "BLACK";
        int serverGameID = serverFacade.joinGame(gameID, color);
        GameplayCLI gameplayCLI = new GameplayCLI(
            url,
            serverFacade.getAuthToken(),
            serverGameID,
            port,
            colorChoice == 1 ? GameConnectionRole.WHITE_PLAYER : GameConnectionRole.BLACK_PLAYER
        );
    }

    private void observeGame() throws ResponseException {
        drawTestBoard(false);
    }

    private static void drawTestBoard(boolean reversed) {
        ClientChessBoard board = new ClientChessBoard();
        ChessGame game = new ChessGame();
        board.draw(game.getBoard(), reversed);
    }
}
