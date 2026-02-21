package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import exception.BadGameDataException;
import exception.ResponseException;
import model.GameData;
import request.CreateGameRequest;
import result.CreateGameResult;
import util.StringUtility;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public CreateGameResult createGame(CreateGameRequest request) throws ResponseException {
        if (StringUtility.checkInvalidString(request.gameName())) {
            throw new BadGameDataException("Missing game name.");
        }
        GameData gameData = new GameData(
                0,
                null,
                null,
                request.gameName(),
                new ChessGame()
        );
        int id = gameDAO.createGame(gameData);
        return new CreateGameResult(id);
    }
}
