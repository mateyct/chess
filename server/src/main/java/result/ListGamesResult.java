package result;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public class ListGamesResult extends Result {
    private final Collection<GameMetadata> games;

    public ListGamesResult(Collection<GameData> gameList) {
        super(null);
        this.games = new ArrayList<>();
        for (GameData game : gameList) {
            this.games.add(new GameMetadata(
                    game.gameId(),
                    game.whiteUsername(),
                    game.blackUsername(),
                    game.gameName()
            ));
        }
    }

    public Collection<GameMetadata> getGames() {
        return games;
    }

    public record GameMetadata(int gameID, String whiteUsername, String blackUsername,
                                String gameName) {
    }
}
