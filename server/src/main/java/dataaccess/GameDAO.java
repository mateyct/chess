package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    public void clear();

    public void createGame(GameData gameData);

    public GameData getGame(int id) throws DataAccessException;

    public Collection<GameData> getGames();

    public void addUserToGame(String username, String playerColor, int gameId) throws DataAccessException;
}
