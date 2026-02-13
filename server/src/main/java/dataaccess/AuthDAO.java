package dataaccess;

import model.AuthData;

public interface AuthDAO {
    public void clear();

    public AuthData getAuth(String authToken);

    public void removeAuth(String authToken);

    public void addAuth(AuthData authData);
}
