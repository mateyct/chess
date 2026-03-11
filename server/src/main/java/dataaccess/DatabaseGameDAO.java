package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class DatabaseGameDAO implements GameDAO {

    public DatabaseGameDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void clear() throws DataAccessException {
        String statement = "TRUNCATE game";
        DatabaseManager.executeUpdate(statement);
    }

    @Override
    public int createGame(GameData gameData) throws DataAccessException {
        String statement = "INSERT INTO game (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        return DatabaseManager.executeUpdate(
            statement,
            gameData.whiteUsername(),
            gameData.blackUsername(),
            gameData.gameName(),
            serializeGame(gameData.game())
        );
    }

    @Override
    public GameData getGame(int id) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT id, whiteUsername, blackUsername, gameName, game FROM game WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error getting game: " + ex.getMessage());
        }
        return null;
    }

    private GameData readGame(ResultSet resultSet) throws SQLException {
        ChessGame chessGame = deserializeGame(resultSet.getString(5));
        return new GameData(
            resultSet.getInt(1),
            resultSet.getString(2),
            resultSet.getString(3),
            resultSet.getString(4),
            chessGame
        );
    }

    @Override
    public Collection<GameData> getGames() throws DataAccessException {
        ArrayList<GameData> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT id, whiteUsername, blackUsername, gameName, game FROM game";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        list.add(readGame(rs));
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error getting game list: " + ex.getMessage());
        }
        return list;
    }

    @Override
    public void updateGame(int gameId, GameData gameData) throws DataAccessException {
        String statement = """
            UPDATE game
            SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ?
            WHERE id = ?
            """;
        DatabaseManager.executeUpdate(
            statement,
            gameData.whiteUsername(),
            gameData.blackUsername(),
            gameData.gameName(),
            serializeGame(gameData.game()),
            gameId
        );
    }

    private static final String CREATE_STATEMENT = """
        CREATE TABLE IF NOT EXISTS game (
            `id` int PRIMARY KEY NOT NULL AUTO_INCREMENT,
            `whiteUsername` VARCHAR(256),
            `blackUsername` VARCHAR(256),
            `gameName` VARCHAR(256) NOT NULL,
            `game` TEXT NOT NULL
        )
        """;

    private void configureDatabase() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(CREATE_STATEMENT)) {
                ps.execute();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error configuring game table: " + ex.getMessage());
        }
    }

    private ChessGame deserializeGame(String gameString) {
        Gson gson = new Gson();
        return gson.fromJson(gameString, ChessGame.class);
    }

    private String serializeGame(ChessGame game) {
        Gson gson = new Gson();
        return gson.toJson(game);
    }
}
