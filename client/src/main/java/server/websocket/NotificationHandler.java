package server.websocket;

import websocket.messages.ErrorServerMessage;
import websocket.messages.LoadGameServerMessage;
import websocket.messages.NotificationServerMessage;

public interface NotificationHandler {
    void handleNotification(NotificationServerMessage message);

    void handleError(ErrorServerMessage message);

    void handleLoadGame(LoadGameServerMessage message);
}
