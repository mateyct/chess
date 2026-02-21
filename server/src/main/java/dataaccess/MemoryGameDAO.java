package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MemoryGameDAO implements GameDAO {
    private final ArrayList<GameData> gameCollection;

    public MemoryGameDAO() {
        gameCollection = new ArrayList<>();
    }

    @Override
    public void clear() {
        gameCollection.clear();
    }

    @Override
    public void createGame(GameData gameData) {
        gameCollection.add(gameData);
    }

    @Override
    public GameData getGame(int id) throws DataAccessException {
        for (GameData game : gameCollection) {
            if (id == game.gameId()) {
                return game;
            }
        }
        throw new DataAccessException("Game with id " + id + " doesn't exist");
    }

    @Override
    public Collection<GameData> getGames() {
        return gameCollection;
    }

    @Override
    public void addUserToGame(String username, String playerColor, int gameId) throws DataAccessException {
        GameData currentGame = null;
        // verify game is still there
        int gameIndex;
        for (gameIndex = 0; gameIndex < gameCollection.size(); gameIndex++) {
            if (gameCollection.get(gameIndex).gameId() == gameId) {
                currentGame = gameCollection.get(gameIndex);
                break;
            }
        }
        if (currentGame == null) {
            throw new DataAccessException("Game with id " + gameId + "doesn't exist.");
        }
        if (playerColor.equals(ChessGame.TeamColor.BLACK.name())) {
            gameCollection.set(gameIndex, new GameData(
                    gameId,
                    currentGame.whiteUsername(),
                    username,
                    currentGame.gameName(),
                    currentGame.game()
            ));
        }
        else {
            gameCollection.set(gameIndex, new GameData(
                    gameId,
                    username,
                    currentGame.blackUsername(),
                    currentGame.gameName(),
                    currentGame.game()
            ));
        }
    }

    @Override
    public int gameCount() {
        return gameCollection.size();
    }
}
