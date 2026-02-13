package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MemoryGameDAO implements GameDAO {
    private final Collection<GameData> gameCollection;

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
    public GameData getGame(int id) {
        for (GameData game : gameCollection) {
            if (id == game.gameId()) {
                return game;
            }
        }
        return null;
    }

    @Override
    public Collection<GameData> getGames() {
        return gameCollection;
    }
}
