package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import exception.ResponseException;
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

    private boolean checkInvalidString(String val) {
        return val == null || val.isEmpty();
    }

    public RegisterResult register(RegisterRequest request) throws ResponseException {
        if (checkInvalidString(request.username()) || checkInvalidString(request.password()) || checkInvalidString(request.email())) {
            throw new BadRequestException("Incorrect fields, requires: username, password, and email");
        }
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
