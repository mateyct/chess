package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

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
    public int createGame(GameData gameData) {
        int id = gameCount() + 1;
        gameCollection.add(new GameData(
                id,
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName(),
                gameData.game()
        ));
        return id;
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
    public void updateGame(int gameId, GameData gameData) throws DataAccessException {
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
        gameCollection.set(gameIndex, gameData);
    }

    @Override
    public int gameCount() {
        return gameCollection.size();
    }
}
