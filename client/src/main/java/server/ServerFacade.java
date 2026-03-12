package server;

import chess.ChessGame;
import model.GameData;
import request.*;
import result.*;

import java.util.ArrayList;

public class ServerFacade {
    // methods are currently stubbed
    private String authToken;
    private int port;

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
        if (authToken.isEmpty()) {
            throw new RuntimeException("Cannot make request, not logged in.");
        }
    }

    public ListGamesResult listGames() {
        if (authToken.isEmpty()) {
            throw new RuntimeException("Cannot make request, not logged in.");
        }
        ArrayList<GameData> games = new ArrayList<>();
        games.add(new GameData(
            3,
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
        return new ListGamesResult(games);
    }

    public CreateGameResult createGame(CreateGameRequest request) {
        if (authToken.isEmpty()) {
            throw new RuntimeException("Cannot make request, not logged in.");
        }
        return new CreateGameResult(1);
    }

    public void joinGame(JoinGameRequest request) {
        if (authToken.isEmpty()) {
            throw new RuntimeException("Cannot make request, not logged in.");
        }
    }
}
