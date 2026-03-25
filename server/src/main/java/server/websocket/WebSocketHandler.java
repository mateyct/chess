package server.websocket;

import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
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
            switch (command.getCommandType()) {
                case CONNECT -> connect(command, ctx.session);
                case LEAVE -> {}
                case RESIGN -> {}
                case MAKE_MOVE -> {}
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connect(UserGameCommand command, Session session) throws IOException {
        connections.addSession(command.getGameID(), session);
        ServerMessage msg = new NotificationServerMessage(command.getAuthToken() + " joined the game.");
        connections.broadcast(command.getGameID(), session, msg);
    }

    private void leave(UserGameCommand command) {

    }

    private void resign(UserGameCommand command) {

    }

    private void makeMove(UserGameCommand command) {

    }
}
