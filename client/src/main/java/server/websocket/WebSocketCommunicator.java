package server.websocket;

import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.Session;

public class WebSocketCommunicator extends Endpoint {
    private NotificationHandler notificationHandler;

    public WebSocketCommunicator(NotificationHandler notificationHandler) {
        this.notificationHandler = notificationHandler;
    }

    // implement methods here

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}
