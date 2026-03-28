package ui;

import exception.ResponseException;
import server.websocket.NotificationHandler;
import server.websocket.WebSocketCommunicator;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

public class GameplayCLI implements NotificationHandler {

    private String authToken;
    private WebSocketCommunicator ws;
    private int gameID;

    public GameplayCLI(String url, String authToken, int gameID) throws ResponseException {
        this.authToken = authToken;
        this.ws = new WebSocketCommunicator(url, this);
        this.gameID = gameID;
        this.ws.connect(authToken, gameID);
    }

    @Override
    public void notify(ServerMessage message) {
        // do stuff here
    }
}
