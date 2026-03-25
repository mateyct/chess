package server.websocket;

import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;
import util.JSONTranslator;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationServerMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final JSONTranslator translator = new JSONTranslator();

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) {
        System.out.println("Websocket connected!");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("WebSocket closed");
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) {
        try {
            UserGameCommand command = translator.translateObject(ctx.message(), UserGameCommand.class);
            if (command.getCommandType() == UserGameCommand.CommandType.CONNECT) {
                connections.addSession(command.getGameID(), ctx.session);
                ServerMessage msg = new NotificationServerMessage(command.getAuthToken() + " joined the game.");
                connections.broadcast(command.getGameID(), ctx.session, msg);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
