package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    public void clear() throws DataAccessException;

    public int createGame(GameData gameData) throws DataAccessException;

    public GameData getGame(int id) throws DataAccessException;

    public Collection<GameData> getGames() throws DataAccessException;

    public void updateGame(int gameId, GameData gameData) throws DataAccessException;

    public int gameCount() throws DataAccessException;
}
