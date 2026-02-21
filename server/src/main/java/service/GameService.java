package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import request.CreateGameRequest;
import request.CreateGameResult;

public class GameService {
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

//    public CreateGameResult createGame(CreateGameRequest request) {
//
//    }
}
