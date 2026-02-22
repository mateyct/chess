package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    public void clear();

    public int createGame(GameData gameData);

    public GameData getGame(int id) throws DataAccessException;

    public Collection<GameData> getGames();

    public void updateGame(int gameId, GameData gameData) throws DataAccessException;

    public int gameCount();
}
