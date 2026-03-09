package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {
    UserDAO dao;

    @BeforeAll
    static void init() {
        assertDoesNotThrow(DatabaseManager::createDatabase);
    }

    @BeforeEach
    void setUp() {
        assertDoesNotThrow(() -> {
            dao = new DatabaseUserDAO();
            dao.clear();
        });
    }

    private void insertUser(String username, String password, String email) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, email);
                ps.execute();
            }
        }
        catch (SQLException | DataAccessException e) {
            fail("Error occurred setting up data in database");
        }
    }

    @Test
    void testGetUser() {
        // set up data in database
        String username = "User1";
        String password = "Pass1";
        String email = "email1@email.com";
        insertUser(username, password, email);
        // run the test and assertions
        assertDoesNotThrow(() -> {
            UserData dbUser = dao.getUser(username);
            assertEquals(username, dbUser.username());
            assertEquals(password, dbUser.password());
            assertEquals(email, dbUser.email());
        });
    }

    @Test
    void testGetUserMissing() {
        String username = "No exist";
        assertDoesNotThrow(() -> {
            UserData dbUser = dao.getUser(username);
            assertNull(dbUser);
        });
    }
}
