package server.websocket;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.BadGameDataException;
import exception.InvalidCredentialsException;
import exception.ResponseException;
import io.javalin.websocket.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import util.JSONTranslator;
import websocket.commands.ConnectionType;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationServerMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections;
    private final JSONTranslator translator;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
        connections = new ConnectionManager();
        translator = new JSONTranslator();
    }

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

    private ConnectionType getGameRole(int gameID, String authToken) throws ResponseException, DataAccessException {
        GameData game = gameDAO.getGame(gameID);
        AuthData authData = authDAO.getAuth(authToken);
        if (game == null) {
            throw new BadGameDataException("Provided game ID is not valid");
        }
        if (authData == null) {
            throw new InvalidCredentialsException("Connected user is not authenticated.");
        }
        if (authData.username().equals(game.whiteUsername())) {
            return ConnectionType.WHITE_PLAYER;
        }
        if (authData.username().equals(game.blackUsername())) {
            return ConnectionType.BLACK_PLAYER;
        }
        return ConnectionType.OBSERVER;
    }

    private void connect(UserGameCommand command, Session session) throws IOException {
        try {
            ConnectionType connectionType = getGameRole(command.getGameID(), command.getAuthToken());
            connections.addSession(command.getGameID(), session);
            ServerMessage msg = new NotificationServerMessage(command.getAuthToken() + " joined the game.");
            connections.broadcast(command.getGameID(), session, msg);
        } catch (DataAccessException e) {
            // send error to client
        } catch (ResponseException e) {
            // send error to client
        }
    }

    private void leave(UserGameCommand command) {

    }

    private void resign(UserGameCommand command) {

    }

    private void makeMove(UserGameCommand command) {

    }
}
