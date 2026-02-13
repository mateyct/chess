package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryAuthDAO implements AuthDAO {
    private Collection<AuthData> authCollection;

    public MemoryAuthDAO() {
        authCollection = new ArrayList<>();
    }

    @Override
    public void clear() {
        authCollection.clear();
    }

    @Override
    public AuthData getAuth(String authToken) {
        for (AuthData data : authCollection) {
            if (authToken.equals(data.authToken())) {
                return data;
            }
        }
        return null;
    }

    @Override
    public void removeAuth(String authToken) {
        authCollection.removeIf(data -> data.authToken().equals(authToken));
    }
}
