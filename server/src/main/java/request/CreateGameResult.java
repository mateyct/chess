package request;

public class CreateGameResult extends Result {
    private final int gameId;
    public CreateGameResult(int gameId) {
        super(null);
        this.gameId = gameId;
    }

    public int getGameId() {
        return gameId;
    }
}
