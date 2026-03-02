package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import result.ClearResult;

public class ClearService {
    private final AuthDAO authDAO;
    private final UserDAO userDAO;
    private final GameDAO gameDAO;

    public ClearService(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
    }

    public ClearResult clear() throws ResponseException {
        try {
            authDAO.clear();
            userDAO.clear();
            gameDAO.clear();
        } catch (DataAccessException e) {
            throw new ResponseException(e.getMessage(), 500);
        }
        return new ClearResult();
    }
}
