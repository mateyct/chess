package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import exception.AlreadyTakenException;
import exception.BadGameDataException;
import exception.ResponseException;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.JoinGameResult;
import result.ListGamesResult;
import util.StringUtility;

import java.util.Collection;

public class GameService {
    private final GameDAO gameDAO;

    public GameService(GameDAO gameDAO) {
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

    public ListGamesResult listGames() {
        Collection<GameData> gameList = gameDAO.getGames();
        return new ListGamesResult(gameList);
    }

    public JoinGameResult joinGame(JoinGameRequest request) throws ResponseException {
        if (StringUtility.checkInvalidString(request.playerColor())) {
            throw new BadGameDataException("Missing player color.");
        }
        boolean isBlack = request.playerColor().equals(ChessGame.TeamColor.BLACK.name());
        if ((!isBlack && !request.playerColor().equals(ChessGame.TeamColor.WHITE.name()))) {
            throw new BadGameDataException("Invalid player color. Must be WHITE or BLACK.");
        }
        if (StringUtility.checkInvalidString(request.username())) {
            throw new BadGameDataException("Error with username.");
        }
        if (request.gameID() == 0) {
            throw new BadGameDataException("Missing game ID.");
        }
        try {
            GameData game = gameDAO.getGame(request.gameID());
            game = assignUser(game, isBlack, request.username());
            gameDAO.updateGame(game.gameId(), game);
        } catch (DataAccessException e) {
            throw new BadGameDataException(e.getMessage());
        }
        return new JoinGameResult();
    }

    private GameData assignUser(GameData game, boolean isBlack, String username) throws AlreadyTakenException {
        if (isBlack) {
            if (game.blackUsername() != null) {
                throw new AlreadyTakenException("Black team already taken.");
            }
            return new GameData(game.gameId(),
                    game.whiteUsername(),
                    username,
                    game.gameName(),
                    game.game()
            );
        }
        if (game.whiteUsername() != null) {
            throw new AlreadyTakenException("White team already taken.");
        }
        return new GameData(game.gameId(),
                username,
                game.blackUsername(),
                game.gameName(),
                game.game()
        );
    }
}
