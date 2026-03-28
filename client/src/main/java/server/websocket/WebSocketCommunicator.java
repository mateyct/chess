package server.websocket;

import exception.ResponseException;
import jakarta.websocket.*;
import util.JSONTranslator;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorServerMessage;
import websocket.messages.LoadGameServerMessage;
import websocket.messages.NotificationServerMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketCommunicator extends Endpoint {
    private final NotificationHandler notificationHandler;
    private Session session;
    private JSONTranslator jsonTranslator;

    public WebSocketCommunicator(String url, int port, NotificationHandler notificationHandler) throws ResponseException {
        this.notificationHandler = notificationHandler;
        jsonTranslator = new JSONTranslator();
        try {
            url = url.replace("http", "ws");
            url += ":" + port;
            URI socketURI = new URI(url + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMsg = jsonTranslator.translateObject(message, ServerMessage.class);
                    switch (serverMsg.getServerMessageType()) {
                        case NOTIFICATION -> {
                            NotificationServerMessage msg = jsonTranslator.translateObject(
                                message,
                                NotificationServerMessage.class
                            );
                            notificationHandler.handleNotification(msg);
                        }
                        case ERROR -> {
                            ErrorServerMessage msg = jsonTranslator.translateObject(
                                message,
                                ErrorServerMessage.class
                            );
                            notificationHandler.handleError(msg);
                        }
                        case LOAD_GAME -> {
                            LoadGameServerMessage msg = jsonTranslator.translateObject(
                                message,
                                LoadGameServerMessage.class
                            );
                            notificationHandler.handleLoadGame(msg);
                        }
                    }
                }
            });

        } catch (DeploymentException | IOException | URISyntaxException e) {
            throw new ResponseException("Error connecting to server.", 500);
        }
    }

    public void connect(String authToken, int gameID) throws ResponseException {
        UserGameCommand connectCmd = new UserGameCommand(
            UserGameCommand.CommandType.CONNECT,
            authToken,
            gameID
        );
        try {
            this.session.getBasicRemote().sendText(jsonTranslator.toJson(connectCmd));
        } catch (IOException e) {
            throw new ResponseException("Error joining game", 500);
        }
    }

    public void leave(String authToken, int gameID) throws ResponseException {
        UserGameCommand leaveCmd = new UserGameCommand(
            UserGameCommand.CommandType.LEAVE,
            authToken,
            gameID
        );
        try {
            this.session.getBasicRemote().sendText(jsonTranslator.toJson(leaveCmd));
        } catch (IOException e) {
            throw new ResponseException("Error leaving game", 500);
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("connected to ws");
    }
}
