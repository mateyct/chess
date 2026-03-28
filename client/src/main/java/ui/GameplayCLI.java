package ui;

import exception.ResponseException;
import server.websocket.NotificationHandler;
import server.websocket.WebSocketCommunicator;
import websocket.commands.GameConnectionRole;
import websocket.messages.ErrorServerMessage;
import websocket.messages.LoadGameServerMessage;
import websocket.messages.NotificationServerMessage;

public class GameplayCLI implements NotificationHandler {

    private String authToken;
    private WebSocketCommunicator ws;
    private int gameID;
    private GameConnectionRole gameRole;

    public GameplayCLI(String url, String authToken, int gameID, int port, GameConnectionRole role) throws ResponseException {
        this.authToken = authToken;
        this.ws = new WebSocketCommunicator(url, port, this);
        this.gameID = gameID;
        this.ws.connect(authToken, gameID);
        this.gameRole = role;
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
        chessDrawer.draw(message.getGame().getBoard(), gameRole == GameConnectionRole.BLACK_PLAYER);
    }
}
