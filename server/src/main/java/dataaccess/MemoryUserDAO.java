package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryUserDAO implements UserDAO  {
    private final Collection<UserData> userCollection;

    public MemoryUserDAO() {
        userCollection = new ArrayList<>();
    }

    @Override
    public void clear() {
        userCollection.clear();
    }

    @Override
    public void createUser(UserData userData) {
        userCollection.add(userData);
    }

    @Override
    public UserData getUser(String username) {
        for (UserData data : userCollection) {
            if (username.equals(data.username())) {
                return data;
            }
        }
        return null;
    }
}
