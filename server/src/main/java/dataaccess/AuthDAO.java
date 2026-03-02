package dataaccess;

import model.AuthData;

public interface AuthDAO {
    public void clear() throws DataAccessException;

    public AuthData getAuth(String authToken) throws DataAccessException;

    public void removeAuth(String authToken) throws DataAccessException;

    public void addAuth(AuthData authData) throws DataAccessException;
}
