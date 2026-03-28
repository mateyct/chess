package ui;

import chess.ChessGame;
import exception.ResponseException;
import server.websocket.NotificationHandler;
import server.websocket.WebSocketCommunicator;
import websocket.commands.GameConnectionRole;
import websocket.messages.ErrorServerMessage;
import websocket.messages.LoadGameServerMessage;
import websocket.messages.NotificationServerMessage;

import java.util.Scanner;

public class GameplayCLI implements NotificationHandler {

    private String authToken;
    private WebSocketCommunicator ws;
    private int gameID;
    private GameConnectionRole gameRole;
    private Scanner scan;

    private ChessGame chessGame;

    public GameplayCLI(String url, String authToken, int gameID, int port, GameConnectionRole role) throws ResponseException {
        this.authToken = authToken;
        this.ws = new WebSocketCommunicator(url, port, this);
        this.gameID = gameID;
        this.ws.connect(authToken, gameID);
        this.gameRole = role;
        scan = new Scanner(System.in);
        gameplayLoop();
        chessGame = null;
    }

    @Override
    public void handleNotification(NotificationServerMessage message) {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + message.getMessage() + EscapeSequences.RESET_TEXT_COLOR);
    }

    @Override
    public void handleError(ErrorServerMessage message) {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + message.getMessage() + EscapeSequences.RESET_TEXT_COLOR);
    }

    @Override
    public void handleLoadGame(LoadGameServerMessage message) {
        ClientChessBoard chessDrawer = new ClientChessBoard();
        chessGame = message.getGame();
        chessDrawer.draw(chessGame.getBoard(), gameRole == GameConnectionRole.BLACK_PLAYER);
    }

    private void gameplayLoop() throws ResponseException {
        boolean loop = true;
        while (loop) {
            String prompt = """
            1: Make Move
            2: Redraw Board
            3: Highlight Legal Moves
            4: Help
            5: Leave
            6: Resign""";
            switch (CLIUtils.getIntInput(prompt, 6, scan)) {
                case 2 -> redrawBoard();
                case 5 -> {
                    loop = false;
                    leaveGame();
                }
            }
        }
    }

    private void leaveGame() throws ResponseException {
        ws.leave(authToken, gameID);
    }

    private void redrawBoard() {
        if (chessGame == null) {
            return;
        }
        ClientChessBoard chessDrawer = new ClientChessBoard();
        chessDrawer.draw(chessGame.getBoard(), gameRole == GameConnectionRole.BLACK_PLAYER);
    }
}
