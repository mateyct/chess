package server.websocket;

import exception.ResponseException;
import jakarta.websocket.*;
import util.JSONTranslator;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketCommunicator extends Endpoint {
    private NotificationHandler notificationHandler;
    private Session session;
    private JSONTranslator jsonTranslator;

    public WebSocketCommunicator(String url, NotificationHandler notificationHandler) throws ResponseException {
        this.notificationHandler = notificationHandler;
        jsonTranslator = new JSONTranslator();
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler((MessageHandler.Whole<String>) message -> {
                ServerMessage serverMsg = jsonTranslator.translateObject(message, ServerMessage.class);
                notificationHandler.notify(serverMsg);
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

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}
