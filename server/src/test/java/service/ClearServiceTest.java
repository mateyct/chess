package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {
    UserDAO userDAO;
    GameDAO gameDAO;
    AuthDAO authDAO;
    ClearService service;

    @BeforeEach
    void setUp() {
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        service = new ClearService(authDAO, userDAO, gameDAO);
    }

    @Test
    void testClear() {
        // set up clear
        String user = "user";
        userDAO.createUser(new UserData(user, user, "user@email.com"));
        String authToken = "auth-token";
        authDAO.addAuth(new AuthData(authToken, user));
        ChessGame game = new ChessGame();
        gameDAO.createGame(new GameData(123, user, "user-2", "Cool Game", game));
        service.clear();
        // test that it worked
        assertNull(userDAO.getUser(user));
        assertNull(authDAO.getAuth(authToken));
        assertEquals(0, gameDAO.getGames().size());
    }
}