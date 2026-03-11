package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
            String statement = "TRUNCATE user";
            DatabaseManager.executeUpdate(statement);
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
        } catch (SQLException | DataAccessException e) {
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

    @Test
    void testCreateUser() {
        String username = "User2";
        String password = "Pass2";
        String email = "email2@email.com";
        assertDoesNotThrow(() -> {
            // ensure user isn't in database
            UserData dbUser = dao.getUser(username);
            assertNull(dbUser);
            // add the user
            dao.createUser(new UserData(username, password, email));
            // test to see if it was added
            dbUser = dao.getUser(username);
            assertEquals(username, dbUser.username());
            assertEquals(password, dbUser.password());
            assertEquals(email, dbUser.email());
        });
    }

    @Test
    void testCreateUserDuplicate() {
        String username = "User3";
        String password = "Pass3";
        String email = "email3@email.com";
        assertDoesNotThrow(() -> {
            // add the user
            dao.createUser(new UserData(username, password, email));
            // test to see if it was added
            UserData dbUser = dao.getUser(username);
            assertEquals(username, dbUser.username());
            assertEquals(password, dbUser.password());
            assertEquals(email, dbUser.email());
        });
        assertThrows(DataAccessException.class, () -> {
            // try creating again with same username
            dao.createUser(new UserData(username, "bad", "bad"));
        });
        assertDoesNotThrow(() -> {
            // ensure the user data is from the first one
            UserData dbUser = dao.getUser(username);
            assertEquals(username, dbUser.username());
            assertEquals(password, dbUser.password());
            assertEquals(email, dbUser.email());
        });
    }

    private int getTableSize() {
        String statement = "SELECT COUNT(*) FROM user";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (DataAccessException | SQLException e) {
            fail("Error interacting with the database");
        }
        return -1;
    }

    @Test
    void testClear() {
        UserData user1 = new UserData("user1", "pass1", "email1");
        UserData user2 = new UserData("user2", "pass2", "email2");
        // insert data into database
        assertDoesNotThrow(() -> {
            dao.createUser(user1);
            dao.createUser(user2);
        });
        // check size
        assertEquals(2, getTableSize());
        // clear and check size again
        assertDoesNotThrow(dao::clear);
        assertEquals(0, getTableSize());
    }
}
