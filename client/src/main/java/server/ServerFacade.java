package server;

import chess.ChessGame;
import model.GameData;
import request.*;
import result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerFacade {
    // methods are currently stubbed
    private String authToken;
    private int port;
    private Map<Integer, Integer> gameIDMap;

    public ServerFacade(int port) {
        this.port = port;
    }

    public LoginResult login(LoginRequest request) {
        authToken = request.password() + request.hashCode();
        return new LoginResult(request.username(), request.password() + request.hashCode());
    }

    public RegisterResult register(RegisterRequest request) {
        authToken = request.password() + request.email();
        return new RegisterResult(request.username(), request.password() + request.email());
    }

    public void logout() {
        if (!signedIn()) {
            throw new RuntimeException("Cannot make request, not logged in.");
        }
        authToken = null;
    }

    public ListGamesResult listGames() {
        if (!signedIn()) {
            throw new RuntimeException("Cannot make request, not logged in.");
        }
        ArrayList<GameData> games = new ArrayList<>();
        gameIDMap = new HashMap<>();
        games.add(new GameData(
            2,
            "Dave",
            "Mark",
            "Coolest Game",
            new ChessGame()
        ));
        games.add(new GameData(
            3,
            null,
            null,
            "Empty Game",
            new ChessGame()
        ));
        for (int i = 1; i <= games.size(); i++) {
            GameData currentGame = games.get(i - 1);
            gameIDMap.put(i, currentGame.gameId());
        }
        return new ListGamesResult(games);
    }

    public CreateGameResult createGame(CreateGameRequest request) {
        if (!signedIn()) {
            throw new RuntimeException("Cannot make request, not logged in.");
        }
        return new CreateGameResult(1);
    }

    public boolean joinGame(JoinGameRequest request) {
        if (!signedIn()) {
            throw new RuntimeException("Cannot make request, not logged in.");
        }
        return gameIDMap != null;
    }

    public boolean signedIn() {
        return authToken != null && !authToken.isEmpty();
    }
}
