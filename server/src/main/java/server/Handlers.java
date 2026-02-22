package server;

import com.google.gson.Gson;
import dataaccess.*;
import exception.ResponseException;
import io.javalin.http.Context;
import request.*;
import result.*;
import service.ClearService;
import service.GameService;
import service.UserService;

import java.lang.reflect.Type;

public class Handlers {
    private final AuthDAO authDAO;
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final UserService userService;
    private final ClearService clearService;
    private final GameService gameService;

    public Handlers() {
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
        userService = new UserService(authDAO, userDAO);
        clearService = new ClearService(authDAO, userDAO, gameDAO);
        gameService = new GameService(gameDAO);
    }

    public void registerHandler(Context ctx) throws ResponseException {
        RegisterRequest request = deserialize(ctx.body(), RegisterRequest.class);
        RegisterResult result = userService.register(request);
        ctx.json(serialize(result));
    }

    public void loginHandler(Context ctx) throws ResponseException {
        LoginRequest request = deserialize(ctx.body(), LoginRequest.class);
        LoginResult result = userService.login(request);
        ctx.json(serialize(result));
    }

    public void logoutHandler(Context ctx) throws ResponseException {
        LogoutRequest request = new LogoutRequest(ctx.header("Authorization"));
        LogoutResult result = userService.logout(request);
        ctx.json(serialize(result));
    }

    public void createGameHandler(Context ctx) throws ResponseException {
        CreateGameRequest request = deserialize(ctx.body(), CreateGameRequest.class);
        CreateGameResult result = gameService.createGame(request);
        ctx.json(serialize(result));
    }

    public void listGamesHandler(Context ctx) {
        ListGamesResult games = gameService.listGames();
        ctx.json(serialize(games));
    }

    public void joinGameHandler(Context ctx) throws ResponseException {
        JoinGameRequest request = deserialize(ctx.body(), JoinGameRequest.class);
        request = new JoinGameRequest(request.playerColor(), request.gameID(), ctx.attribute("user"));
        JoinGameResult result = gameService.joinGame(request);
        ctx.json(serialize(result));
    }

    public void authorizeHandler(Context ctx) throws ResponseException {
        String username = userService.authorize(ctx.header("Authorization"));
        ctx.attribute("user", username);
    }

    public void clearHandler(Context ctx) {
        ClearResult result = clearService.clear();
        String json = serialize(result);
        ctx.json(json);
    }

    private <T> T deserialize(String json, Type type) {
        Gson gson = new Gson();
        return gson.fromJson(json, type);
    }

    private String serialize(Result result) {
        Gson gson = new Gson();
        return gson.toJson(result);
    }

    public void responseExceptionHandler(ResponseException e, Context ctx) {
        Result result = new Result("Error: " + e.getMessage());
        String json = serialize(result);
        ctx.status(e.getStatusCode());
        ctx.json(json);
    }

    public void generalExceptionHandler(Exception e, Context ctx) {
        Result result = new Result("Error: " + e.getMessage());
        String json = serialize(result);
        ctx.status(500);
        ctx.json(json);
    }

}
