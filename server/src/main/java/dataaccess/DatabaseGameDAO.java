package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class DatabaseGameDAO implements GameDAO {
    @Override
    public void clear() throws DataAccessException {
        String statement = "TRUNCATE game";
        DatabaseManager.executeUpdate(statement);
    }

    @Override
    public int createGame(GameData gameData) throws DataAccessException {
        return 0;
    }

    @Override
    public GameData getGame(int id) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT id, whiteUsername, blackUsername, gameName, game FROM game WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ChessGame chessGame = deserializeGame(rs.getString(5));
                        return new GameData(
                            rs.getInt(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4),
                            chessGame
                        );
                    }
                }
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException("Error getting game: " + ex.getMessage());
        }
        return null;
    }

    @Override
    public Collection<GameData> getGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void updateGame(int gameId, GameData gameData) throws DataAccessException {

    }

    @Override
    public int gameCount() throws DataAccessException {
        return 0;
    }

    private static final String createStatement = """
            CREATE TABLE IF NOT EXISTS game (
                `id` int PRIMARY KEY NOT NULL AUTO_INCREMENT,
                `whiteUsername` VARCHAR(256) NOT NULL,
                `blackUsername` VARCHAR(256) NOT NULL,
                `gameName` VARCHAR(256) NOT NULL,
                `game` TEXT NOT NULL
            )
            """;

    private void configureDatabase() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(createStatement)) {
                ps.execute();
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException("Error configuring game table: " + ex.getMessage());
        }
    }

    private ChessGame deserializeGame(String gameString) {
        Gson gson = new Gson();
        return gson.fromJson(gameString, ChessGame.class);
    }
}
