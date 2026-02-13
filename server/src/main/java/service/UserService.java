package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import exception.AlreadyTakenException;
import model.AuthData;
import model.UserData;
import request.RegisterRequest;
import request.RegisterResult;

import java.util.UUID;

public class UserService {
    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public RegisterResult register(RegisterRequest request) {
        UserData existingUser = userDAO.getUser(request.username());
        if (existingUser != null) {
            throw new AlreadyTakenException("Username " + request.username() + " is already taken.");
        }
        UserData newUser = new UserData(request.username(), request.password(), request.email());
        userDAO.createUser(newUser);
        AuthData auth = newAuth(request.username());
        return new RegisterResult(auth.username(), auth.authToken());
    }

    private AuthData newAuth(String username) {
        AuthData auth = new AuthData(UUID.randomUUID().toString(), username);
        authDAO.addAuth(auth);
        return auth;
    }
}
