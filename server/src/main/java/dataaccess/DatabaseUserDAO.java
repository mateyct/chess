package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseUserDAO implements UserDAO {

    public DatabaseUserDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void clear() {

    }

    @Override
    public void createUser(UserData userData) {

    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    private static final String createStatement = """
            CREATE TABLE IF NOT EXISTS user (
                `id` int NOT NULL AUTO_INCREMENT,
                `username` VARCHAR(256) NOT NULL,
                `password` TEXT NOT NULL,
                `email` VARCHAR(256) NOT NULL
            )
            """;

    private void configureDatabase() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(createStatement)) {
                preparedStatement.execute();
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database for users.", ex);
        }
    }
}
