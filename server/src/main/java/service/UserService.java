package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import exception.InvalidCredentialsException;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import request.*;

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

    public LoginResult login(LoginRequest request) throws ResponseException {
        if (checkInvalidString(request.username()) || checkInvalidString(request.password())) {
            throw new BadRequestException("Incorrect fields, requires: username and password");
        }
        UserData user = userDAO.getUser(request.username());
        if (user == null || !user.password().equals(request.password())) {
            throw new InvalidCredentialsException("Invalid username or password.");
        }
        AuthData auth = newAuth(user.username());
        return new LoginResult(auth.username(), auth.authToken());
    }

    public LogoutResult logout(LogoutRequest request) throws InvalidCredentialsException {
        if (request.authToken() == null) {
            throw new InvalidCredentialsException("Missing authentication token.");
        }
        authDAO.removeAuth(request.authToken());
        return new LogoutResult();
    }

    public void authorize(String authToken) throws ResponseException {
        if (authToken == null) {
            throw new InvalidCredentialsException("Missing authentication token.");
        }
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new InvalidCredentialsException("Invalid authentication token.");
        }
    }

    private AuthData newAuth(String username) {
        AuthData auth = new AuthData(UUID.randomUUID().toString(), username);
        authDAO.addAuth(auth);
        return auth;
    }
}
