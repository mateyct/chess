package ui;

import server.ServerFacade;
import server.websocket.NotificationHandler;
import server.websocket.WebSocketCommunicator;
import websocket.messages.ServerMessage;

public class GameplayCLI implements NotificationHandler {

    private ServerFacade serverFacade;
    private WebSocketCommunicator ws;

    public GameplayCLI(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
        this.ws = new WebSocketCommunicator(this);
    }

    @Override
    public void notify(ServerMessage message) {
        // do stuff here
    }
}
