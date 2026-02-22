package service;

import chess.ChessGame;
import dataaccess.*;
import exception.BadGameDataException;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.JoinGameResult;
import result.ListGamesResult;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    GameDAO gameDAO;
    GameService service;

    @BeforeEach
    void setUp() {
        gameDAO = new MemoryGameDAO();
        service = new GameService(gameDAO);
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

    @Test
    void testListGames() {
        GameData game1 = new GameData(1, "Dave", "Bill", "Cool game", new ChessGame());
        GameData game2 = new GameData(2, "Max", null, "Coolest game", new ChessGame());
        gameDAO.createGame(game1);
        gameDAO.createGame(game2);
        ListGamesResult result = service.listGames();
        assertInstanceOf(ArrayList.class, result.getGames());
        ArrayList<ListGamesResult.GameMetadata> games = (ArrayList<ListGamesResult.GameMetadata>)result.getGames();
        assertEquals(game1.gameId(), games.getFirst().gameID());
        assertEquals(game1.whiteUsername(), games.getFirst().whiteUsername());
        assertEquals(game1.blackUsername(), games.getFirst().blackUsername());
        assertEquals(game1.gameName(), games.getFirst().gameName());
        assertEquals(game2.gameId(), games.get(1).gameID());
        assertEquals(game2.whiteUsername(), games.get(1).whiteUsername());
        assertEquals(game2.blackUsername(), games.get(1).blackUsername());
        assertEquals(game2.gameName(), games.get(1).gameName());
    }

    @Test
    void testEmptyListGames() {
        ListGamesResult result = service.listGames();
        assertEquals(0, result.getGames().size());
    }

    @Test
    void testJoinGame() {
        GameData existingGame = new GameData(1, "Marv", null, "Cool", new ChessGame());
        gameDAO.createGame(existingGame);

        JoinGameRequest request = new JoinGameRequest("BLACK", 1, "Dave");
        assertDoesNotThrow(() -> {
            service.joinGame(request);
            GameData changedGame = gameDAO.getGame(1);
            assertEquals(request.username(), changedGame.blackUsername());
        });
    }

    @Test
    void testJoinInvalidSpot() {
        GameData existingGame = new GameData(1, "Marv", null, "Cool", new ChessGame());
        gameDAO.createGame(existingGame);

        JoinGameRequest request = new JoinGameRequest(null, 1, "Dave");
        assertThrows(BadGameDataException.class, () -> {
            service.joinGame(request);
        });
    }
}
