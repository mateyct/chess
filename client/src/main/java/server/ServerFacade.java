package server;

import exception.ResponseException;
import request.*;
import result.*;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class ServerFacade {
    // methods are currently stubbed
    private String authToken;
    private Map<Integer, Integer> gameIDMap;
    private final JSONTranslator translator;
    private final ClientCommunicator clientCommunicator;

    public ServerFacade(int port) {
        translator = new JSONTranslator();
        clientCommunicator = new ClientCommunicator("localhost", port);
    }

    public void login(LoginRequest request) throws ResponseException {
        var response = clientCommunicator.post("/session", request, null);
        LoginResult result = handleResponse(response, LoginResult.class);
        authToken = result.getAuthToken();
    }

    public void register(RegisterRequest request) throws ResponseException {
        var response = clientCommunicator.post("/user", request, null);
        RegisterResult result = handleResponse(response, RegisterResult.class);
        authToken = result.getAuthToken();
    }

    public void logout() throws ResponseException {
        if (!signedIn()) {
            throw new RuntimeException("Cannot make request, not logged in.");
        }
        var response = clientCommunicator.delete("/session", authToken);
        handleResponse(response, LogoutResult.class);
        authToken = null;
    }

    public ListGamesResult listGames() throws ResponseException {
        if (!signedIn()) {
            throw new RuntimeException("Cannot make request, not logged in.");
        }
        gameIDMap = new HashMap<>();
        HttpResponse<String> response = clientCommunicator.get("/game", authToken);
        ListGamesResult gameList = handleResponse(response, ListGamesResult.class);
        int i = 1;
        for (ListGamesResult.GameMetadata game : gameList.getGames()) {
            gameIDMap.put(i, game.gameID());
            i++;
        }
        return gameList;
    }

    public CreateGameResult createGame(CreateGameRequest request) throws ResponseException {
        if (!signedIn()) {
            throw new RuntimeException("Cannot make request, not logged in.");
        }
        var response = clientCommunicator.post("/game", request, authToken);
        return handleResponse(response, CreateGameResult.class);
    }

    public void joinGame(int gameID, String playerColor) throws ResponseException {
        if (!signedIn()) {
            throw new RuntimeException("Cannot make request, not logged in.");
        }
        JoinGameRequest request = new JoinGameRequest(playerColor, gameID, null);
        var response = clientCommunicator.put("/game", request, authToken);
        handleResponse(response, JoinGameResult.class);
    }

    public int getGameCount() {
        if (gameIDMap == null) {
            return 0;
        }
        return gameIDMap.size();
    }

    public boolean signedIn() {
        return authToken != null && !authToken.isEmpty();
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return translator.translateObject(response.body(), responseClass);
        }
        var body = response.body();
        if (body != null) {
            throw translator.translateException(response);
        }

        throw new ResponseException("An unexpected error occurred", 500);
    }
}
