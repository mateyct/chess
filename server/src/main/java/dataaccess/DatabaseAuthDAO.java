package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseAuthDAO implements AuthDAO {
    @Override
    public void clear() throws DataAccessException {
        String statement = "TRUNCATE auth";
        DatabaseManager.executeUpdate(statement);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT token, username FROM auth WHERE token = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet resultSet = ps.executeQuery()) {
                    if (resultSet.next()) {
                        String username = resultSet.getString("username");
                        String dbAuthToken = resultSet.getString("token");
                        return new AuthData(dbAuthToken, username);
                    }
                }
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException("Error getting auth: " + ex.getMessage());
        }
        return null;
    }

    @Override
    public void removeAuth(String authToken) throws DataAccessException {

    }

    @Override
    public void addAuth(AuthData authData) throws DataAccessException {

    }

    private static final String createStatement = """
        CREATE TABLE IF NOT EXISTS user (
            `id` int PRIMARY KEY NOT NULL AUTO_INCREMENT,
            `token` TEXT NOT NULL,
            `username` VARCHAR(256) NOT NULL,
        )
    """;

    private void configureDatabase() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(createStatement)) {
                preparedStatement.execute();
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database for users." + ex.getMessage(), ex);
        }
    }
}
