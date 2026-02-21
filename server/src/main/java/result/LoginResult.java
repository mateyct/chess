package result;

public class LoginResult extends Result {
    private final String username;
    private final String authToken;
    public LoginResult(String username, String authToken) {
        super(null);
        this.username = username;
        this.authToken = authToken;
    }

    public String getUsername() {
        return username;
    }

    public String getAuthToken() {
        return authToken;
    }
}
