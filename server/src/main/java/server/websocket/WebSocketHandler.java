package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import exception.BadGameDataException;
import exception.InvalidCredentialsException;
import exception.ResponseException;
import io.javalin.websocket.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.JSONTranslator;
import websocket.commands.GameConnectionRole;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorServerMessage;
import websocket.messages.LoadGameServerMessage;
import websocket.messages.NotificationServerMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Map;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections;
    private final JSONTranslator translator;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        connections = new ConnectionManager();
        translator = new JSONTranslator();
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) {
        System.out.println("Websocket connected!");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {}

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) {
        try {
            try {
                UserGameCommand command = translator.translateObject(ctx.message(), UserGameCommand.class);
                switch (command.getCommandType()) {
                    case CONNECT -> connect(command, ctx.session);
                    case LEAVE -> leave(command, ctx.session);
                    case RESIGN -> resign(command);
                    case MAKE_MOVE -> {
                        MakeMoveCommand moveCommand = translator.translateObject(ctx.message(), MakeMoveCommand.class);
                        makeMove(moveCommand, ctx.session);
                    }
                }
            } catch (DataAccessException e) {
                ServerMessage msg = new ErrorServerMessage("Unexpected server error.");
                connections.messageSession(ctx.session, msg);
            } catch (ResponseException e) {
                ServerMessage msg = new ErrorServerMessage(e.getMessage());
                connections.messageSession(ctx.session, msg);

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

    private void handlePlayerLeaving(GameConnectionRole role, GameData gameData) throws DataAccessException {
        GameData updatedGame = null;
        if (role == GameConnectionRole.BLACK_PLAYER) {
            updatedGame = new GameData(
                gameData.gameId(),
                gameData.whiteUsername(),
                null,
                gameData.gameName(),
                gameData.game()
            );
        }
        if (role == GameConnectionRole.WHITE_PLAYER) {
            updatedGame = new GameData(
                gameData.gameId(),
                null,
                gameData.blackUsername(),
                gameData.gameName(),
                gameData.game()
            );
        }
        if (updatedGame != null) {
            gameDAO.updateGame(updatedGame.gameId(), updatedGame);
        }
    }

    private void connect(UserGameCommand command, Session session) throws IOException, DataAccessException, ResponseException {
        String username = getUserByAuth(command.getAuthToken());
        GameData game = getGameByID(command.getGameID());
        GameConnectionRole connectionType = getGameRole(game, username);
        connections.addSession(command.getGameID(), session);
        ServerMessage msg = new NotificationServerMessage(username + " joined the game as " + connectionType.name() + ".");
        connections.broadcast(command.getGameID(), session, msg);
        connections.messageSession(session, new LoadGameServerMessage(game.game()));
    }

    private void leave(UserGameCommand command, Session session) throws IOException, DataAccessException, ResponseException {
        String username = getUserByAuth(command.getAuthToken());
        GameData game = getGameByID(command.getGameID());
        GameConnectionRole connectionRole = getGameRole(game, username);
        connections.removeSession(command.getGameID(), session);
        handlePlayerLeaving(connectionRole, game);
        ServerMessage msg = new NotificationServerMessage(username + " (" + connectionRole.name() + ") left the game");
        connections.broadcast(command.getGameID(), session, msg);
        session.close();
    }

    private void resign(UserGameCommand command) throws IOException, DataAccessException, ResponseException {
        String username = getUserByAuth(command.getAuthToken());
        GameData game = getGameByID(command.getGameID());
        GameConnectionRole connectionRole = getGameRole(game, username);
        if (connectionRole == GameConnectionRole.OBSERVER) {
            throw new ResponseException("Only players can resign the game", 400);
        }
        if (game.game().getGameOver()){
            throw new ResponseException("Cannot resign twice, game is over", 400);
        }
        game.game().setGameOver();
        gameDAO.updateGame(game.gameId(), game);
        ServerMessage msg = new NotificationServerMessage(username + " resigned the game, game is over.");
        connections.broadcast(game.gameId(), null, msg);
    }

    private void sendMoveMessage(ChessMove move, Session session, int gameID, String username) throws IOException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ServerMessage moveNotification = new NotificationServerMessage(
            username + " moved piece from " + startPosition.getRow() +
                Constants.POSITION_LETTER_MAP.get(startPosition.getColumn()) +
                " to " + endPosition.getRow() + Constants.POSITION_LETTER_MAP.get(endPosition.getColumn()) + "."
        );
        connections.broadcast(gameID, session, moveNotification);
    }

    private void sendStatusMessage(GameData gameData, ChessGame.TeamColor color) throws IOException {
        String username = color == ChessGame.TeamColor.BLACK ? gameData.blackUsername() : gameData.whiteUsername();
        if (gameData.game().isInCheckmate(color)) {
            ServerMessage msg = new NotificationServerMessage(username + " is in checkmate.");
            connections.broadcast(gameData.gameId(), null, msg);
            return;
        }
        if (gameData.game().isInStalemate(color)) {
            ServerMessage msg = new NotificationServerMessage(username + " is in stalemate.");
            connections.broadcast(gameData.gameId(), null, msg);
            return;
        }
        if (gameData.game().isInCheck(color)) {
            ServerMessage msg = new NotificationServerMessage(username + " is in check.");
            connections.broadcast(gameData.gameId(), null, msg);
        }
    }

    private void makeMove(MakeMoveCommand command, Session session) throws IOException, DataAccessException, ResponseException {
        String username = getUserByAuth(command.getAuthToken());
        GameData game = getGameByID(command.getGameID());
        GameConnectionRole connectionRole = getGameRole(game, username);
        if (connectionRole == GameConnectionRole.OBSERVER) {
            ServerMessage msg = new ErrorServerMessage("Observers cannot make moves");
            connections.messageSession(session, msg);
            return;
        }
        ChessGame chessGame = game.game();
        if (chessGame.getGameOver()) {
            ServerMessage msg = new ErrorServerMessage("Game is over, no more moves can be made.");
            connections.messageSession(session, msg);
            return;
        }
        ChessGame.TeamColor userColor = connectionRole == GameConnectionRole.BLACK_PLAYER ?
            ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
        ChessMove move = command.getMove();
        if (!chessGame.validMove(move, userColor)) {
            ServerMessage msg = new ErrorServerMessage("Cannot move piece that isn't your color");
            connections.messageSession(session, msg);
            return;
        }
        try {
            chessGame.makeMove(move);
        } catch (InvalidMoveException e) {ServerMessage msg = new ErrorServerMessage("Invalid Move");
            connections.messageSession(session, msg);
            return;
        }
        gameDAO.updateGame(game.gameId(), game);
        ServerMessage loadGameMessage = new LoadGameServerMessage(game.game());
        connections.broadcast(game.gameId(), null, loadGameMessage);
        // send message notifying of move
        sendMoveMessage(move, session, game.gameId(), username);
        sendStatusMessage(game, ChessGame.TeamColor.BLACK);
        sendStatusMessage(game, ChessGame.TeamColor.WHITE);
    }
}
