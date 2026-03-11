package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUserDAO implements UserDAO {

    public DatabaseUserDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void clear() throws DataAccessException {
        String statement = "TRUNCATE user";
        DatabaseManager.executeUpdate(statement);
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        String statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        DatabaseManager.executeUpdate(statement, userData.username(), userData.password(), userData.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT username, password, email FROM user WHERE username = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet results = ps.executeQuery()) {
                    if (results.next()) {
                        String dbUsername = results.getString("username");
                        String password = results.getString("password");
                        String email = results.getString("email");
                        return new UserData(dbUsername, password, email);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database for users.", ex);
        }
        return null;
    }

    private static final String createStatement = """
        CREATE TABLE IF NOT EXISTS user (
            `id` int PRIMARY KEY NOT NULL AUTO_INCREMENT,
            `username` VARCHAR(256) UNIQUE NOT NULL,
            `password` TEXT NOT NULL,
            `email` VARCHAR(256) NOT NULL
        )
        """;

    private void configureDatabase() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(createStatement)) {
                preparedStatement.execute();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database for users." + ex.getMessage(), ex);
        }
    }
}
