package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import exception.InvalidCredentialsException;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;
import util.StringUtility;

import java.util.UUID;

public class UserService {
    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public RegisterResult register(RegisterRequest request) throws ResponseException {
        try {
            if (StringUtility.checkInvalidString(request.username()) ||
                StringUtility.checkInvalidString(request.password()) ||
                StringUtility.checkInvalidString(request.email())
            ) {
                throw new BadRequestException("Incorrect fields, requires: username, password, and email");
            }
            UserData existingUser = userDAO.getUser(request.username());
            if (existingUser != null) {
                throw new AlreadyTakenException("Username " + request.username() + " is already taken.");
            }
            String hashedPassword = BCrypt.hashpw(request.password(), BCrypt.gensalt());
            UserData newUser = new UserData(request.username(), hashedPassword, request.email());
            userDAO.createUser(newUser);
            AuthData auth = newAuth(request.username());
            return new RegisterResult(auth.username(), auth.authToken());
        } catch (DataAccessException ex) {
            throw new ResponseException(ex.getMessage(), 500);
        }
    }

    public LoginResult login(LoginRequest request) throws ResponseException {
        try {
            if (StringUtility.checkInvalidString(request.username()) ||
                StringUtility.checkInvalidString(request.password())
            ) {
                throw new BadRequestException("Incorrect fields, requires: username and password");
            }
            UserData user = userDAO.getUser(request.username());
            if (user == null || !BCrypt.checkpw(request.password(), user.password())) {
                throw new InvalidCredentialsException("Invalid username or password.");
            }
            AuthData auth = newAuth(user.username());
            return new LoginResult(auth.username(), auth.authToken());
        } catch (DataAccessException ex) {
            throw new ResponseException(ex.getMessage(), 500);
        }
    }

    public LogoutResult logout(LogoutRequest request) throws ResponseException {
        try {
            if (request.authToken() == null) {
                throw new InvalidCredentialsException("Missing authentication token.");
            }
            authDAO.removeAuth(request.authToken());
            return new LogoutResult();
        } catch (DataAccessException ex) {
            throw new ResponseException(ex.getMessage(), 500);
        }
    }

    public String authorize(String authToken) throws ResponseException {
        try {
            if (authToken == null) {
                throw new InvalidCredentialsException("Missing authentication token.");
            }
            AuthData authData = authDAO.getAuth(authToken);
            if (authData == null) {
                throw new InvalidCredentialsException("Invalid authentication token.");
            }
            return authData.username();
        } catch (DataAccessException ex) {
            throw new ResponseException(ex.getMessage(), 500);
        }
    }

    private AuthData newAuth(String username) throws DataAccessException {
        AuthData auth = new AuthData(UUID.randomUUID().toString(), username);
        authDAO.addAuth(auth);
        return auth;
    }
}
