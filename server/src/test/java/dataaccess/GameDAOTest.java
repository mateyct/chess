package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import model.GameData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTest {
    GameDAO dao;
    Gson gson;

    @BeforeAll
    static void init() {
        assertDoesNotThrow(DatabaseManager::createDatabase);
    }

    @BeforeEach
    void setUp() {
        gson = new Gson();
        assertDoesNotThrow(() -> {
            dao = new DatabaseGameDAO();
            String statement = "TRUNCATE game";
            DatabaseManager.executeUpdate(statement);
        });
    }

    private int insertGame(
            String gameName,
            String whiteUsername,
            String blackUsername,
            ChessGame game
    ) {
        try {
            String statement = "INSERT INTO game (blackUsername, whiteUsername, gameName, game)" +
                    "VALUES (?, ?, ?, ?)";
            return DatabaseManager.executeUpdate(
                    statement,
                    blackUsername,
                    whiteUsername,
                    gameName,
                    gson.toJson(game)
            );
        } catch (DataAccessException e) {
            fail("Error occurred setting up data in database");
        }
        return -1;
    }

    @Test
    void testGetGame() {
        String testName = "testGame";
        String whiteUsername = "testWhite";
        String blackUsername = "testBlack";
        ChessGame game = new ChessGame();
        int gameId = insertGame(testName, whiteUsername, blackUsername, game);
        assertDoesNotThrow(() -> {
            GameData dbGame = dao.getGame(gameId);
            assertEquals(testName, dbGame.gameName());
            assertEquals(whiteUsername, dbGame.whiteUsername());
            assertEquals(blackUsername, dbGame.blackUsername());
            assertEquals(game, dbGame.game());
        });
    }

    @Test
    void testGetGameInvalid() {
        assertDoesNotThrow(() -> {
            GameData dbGame = dao.getGame(1);
            assertNull(dbGame);
        });
    }

    @Test
    void testCreateGame() {
        String testName = "testGame1";
        String whiteUsername = "testWhite1";
        String blackUsername = "testBlack1";
        ChessGame game = new ChessGame();
        assertDoesNotThrow(() -> {
            GameData dbGame = dao.getGame(1);
            assertNull(dbGame);
            dao.createGame(new GameData(
                    1,
                    whiteUsername,
                    blackUsername,
                    testName,
                    game
            ));
            dbGame = dao.getGame(1);
            assertEquals(testName, dbGame.gameName());
            assertEquals(whiteUsername, dbGame.whiteUsername());
            assertEquals(blackUsername, dbGame.blackUsername());
            assertEquals(game, dbGame.game());
        });
    }

    @Test
    void testCreateGameInvalidInput() {
        String whiteUsername = "testWhite1";
        String blackUsername = "testBlack1";
        ChessGame game = new ChessGame();
        assertDoesNotThrow(() -> {
            GameData dbGame = dao.getGame(1);
            assertNull(dbGame);
        });
        assertThrows(DataAccessException.class, () -> {
            dao.createGame(new GameData(
                1,
                whiteUsername,
                blackUsername,
                null,
                game
            ));
        });
        assertDoesNotThrow(() -> {
            GameData dbGame = dao.getGame(1);
            assertNull(dbGame);
        });
    }

    @Test
    void testListGames() {
        GameData game1 = new GameData(
                1,
                "white1",
                "black1",
                "game1",
                new ChessGame()
        );
        GameData game2 = new GameData(
                2,
                "white2",
                "black2",
                "game2",
                new ChessGame()
        );
        assertDoesNotThrow(() -> {
            dao.createGame(game1);
            dao.createGame(game2);
            Collection<GameData> dbGames = dao.getGames();
            assertEquals(2, dbGames.size());
        });
    }

    @Test
    void testListGamesEmpty() {
        assertDoesNotThrow(() -> {
            Collection<GameData> dbGames = dao.getGames();
            assertEquals(0, dbGames.size());
        });
    }

    @Test
    void testUpdateGame() {
        GameData game = new GameData(
                1,
                "white1",
                "black1",
                "game1",
                new ChessGame()
        );
        ChessMove move = new ChessMove(
                new ChessPosition(2, 1),
                new ChessPosition(3, 1),
                null
        );
        assertDoesNotThrow(() -> {
            int id = dao.createGame(game);
            GameData newGame = new GameData(
                    id,
                    "new white 1",
                    game.blackUsername(),
                    "game1",
                    new ChessGame()
            );
            newGame.game().makeMove(move);
            GameData dbGame = dao.getGame(id);
            assertNotEquals(newGame, dbGame);
            dao.updateGame(id, newGame);
            dbGame = dao.getGame(id);
            assertEquals(newGame, dbGame);
        });
    }

    @Test
    void testUpdateGameInvalidInput() {
        GameData game = new GameData(
                1,
                "white1",
                "black1",
                "game1",
                new ChessGame()
        );
        assertThrows(DataAccessException.class, () -> {
            int id = dao.createGame(game);
            GameData invalidGame = new GameData(
                id,
                game.whiteUsername(),
                game.blackUsername(),
                null,
                new ChessGame()
            );
            dao.updateGame(id, invalidGame);
        });
    }

    private int getTableSize() {
        String statement = "SELECT COUNT(*) FROM game";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        catch (DataAccessException | SQLException e) {
            fail("Error interacting with the database");
        }
        return -1;
    }

    @Test
    void testClear() {
        GameData game1 = new GameData(
                1,
                "white1",
                "black1",
                "game1",
                new ChessGame()
        );
        GameData game2 = new GameData(
                2,
                "white2",
                "black2",
                "game2",
                new ChessGame()
        );
        assertDoesNotThrow(() -> {
            assertEquals(0, getTableSize());
            dao.createGame(game1);
            dao.createGame(game2);
            assertEquals(2, getTableSize());
            dao.clear();
            assertEquals(0, getTableSize());

        });
    }
}
