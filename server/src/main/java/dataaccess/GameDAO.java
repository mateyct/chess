package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    public void clear();

    public void createGame(GameData gameData);

    public GameData getGame(int id);

    public Collection<GameData> getGames();
}
