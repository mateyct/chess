package result;

public class CreateGameResult extends Result {
    private final int gameID;
    public CreateGameResult(int gameID) {
        super(null);
        this.gameID = gameID;
    }

    public int getGameID() {
        return gameID;
    }
}
