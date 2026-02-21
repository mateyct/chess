package request;

public class CreateGameResult extends Result {
    private final String gameId;
    public CreateGameResult(String gameId) {
        super(null);
        this.gameId = gameId;
    }

    public String getGameId() {
        return gameId;
    }
}
