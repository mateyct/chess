package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import exception.BadGameDataException;
import exception.ResponseException;
import model.GameData;
import request.CreateGameRequest;
import request.CreateGameResult;
import util.StringUtility;

public class GameService {
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public CreateGameResult createGame(CreateGameRequest request) throws ResponseException {
        if (StringUtility.checkInvalidString(request.gameName())) {
            throw new BadGameDataException("Missing game name.");
        }
        int gameIndex = gameDAO.gameCount();
        GameData gameData = new GameData(
                gameIndex,
                null,
                null,
                request.gameName(),
                new ChessGame()
        );
        gameDAO.createGame(gameData);
        return new CreateGameResult(gameIndex);
    }
}
