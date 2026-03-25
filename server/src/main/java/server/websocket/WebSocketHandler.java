package server.websocket;

import chess.ChessGame;
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
import websocket.commands.GameConnectionRole;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorServerMessage;
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

    private String getUserByAuth(String authToken) throws ResponseException, DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new InvalidCredentialsException("Connected user is not authenticated.");
        }
        return authData.username();
    }

    private GameData getGameByID(int gameID) throws DataAccessException, ResponseException {
        GameData game = gameDAO.getGame(gameID);
        if (game == null) {
            throw new BadGameDataException("Provided game ID is not valid");
        }
        return game;
    }

    private GameConnectionRole getGameRole(GameData game, String username) {
        if (username.equals(game.whiteUsername())) {
            return GameConnectionRole.WHITE_PLAYER;
        }
        if (username.equals(game.blackUsername())) {
            return GameConnectionRole.BLACK_PLAYER;
        }
        return GameConnectionRole.OBSERVER;
    }

    private void connect(UserGameCommand command, Session session) throws IOException {
        try {
            String username = getUserByAuth(command.getAuthToken());
            GameData game = getGameByID(command.getGameID());
            GameConnectionRole connectionType = getGameRole(game, username);
            connections.addSession(command.getGameID(), session);
            ServerMessage msg = new NotificationServerMessage(username + " joined the game as " + connectionType.name() + ".");
            connections.broadcast(command.getGameID(), session, msg);
            session.getRemote().sendString(translator.toJson(game.game()));
        } catch (DataAccessException e) {
            ServerMessage msg = new ErrorServerMessage("Unexpected server error.");
            connections.broadcast(command.getGameID(), session, msg);
        } catch (ResponseException e) {
            ServerMessage msg = new ErrorServerMessage(e.getMessage());
            connections.broadcast(command.getGameID(), session, msg);
        }
    }

    private void leave(UserGameCommand command) {

    }

    private void resign(UserGameCommand command) {

    }

    private void makeMove(UserGameCommand command) {

    }
}
