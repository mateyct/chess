package service;

import dataaccess.*;
import exception.BadGameDataException;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.CreateGameRequest;
import result.CreateGameResult;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    AuthDAO authDAO;
    GameDAO gameDAO;
    GameService service;

    @BeforeEach
    void setUp() {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        service = new GameService(authDAO, gameDAO);
    }

    @Test
    void testCreateGame() {
        String gameName = "Super Game";
        String gameName2 = "Super Game 2";
        assertDoesNotThrow(() -> {
            CreateGameResult result = service.createGame(new CreateGameRequest(gameName));
            CreateGameResult result2 = service.createGame(new CreateGameRequest(gameName2));
            assertEquals(1, result.getGameID());
            assertEquals(2, result2.getGameID());
            GameData gameData = gameDAO.getGame(1);
            assertEquals(gameName, gameData.gameName());
            GameData gameData2 = gameDAO.getGame(2);
            assertEquals(gameName2, gameData2.gameName());
        });
    }


    @Test
    void testCreateGameBadName() {
        String gameName = "";
        assertThrows(BadGameDataException.class, () -> {
            service.createGame(new CreateGameRequest(gameName));
        });
    }
}