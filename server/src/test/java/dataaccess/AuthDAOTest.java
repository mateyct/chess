package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthDAOTest {
    AuthDAO dao;

    @BeforeAll
    static void init() {
        assertDoesNotThrow(DatabaseManager::createDatabase);
    }

    @BeforeEach
    void setUp() {
        assertDoesNotThrow(() -> {
            dao = new DatabaseAuthDAO();
            String statement = "TRUNCATE auth";
            DatabaseManager.executeUpdate(statement);
        });
    }

    private void insertAuth(String username, String token) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "INSERT INTO auth (username, token) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                ps.setString(2, token);
                ps.execute();
            }
        } catch (SQLException | DataAccessException e) {
            fail("Error occurred setting up data in database");
        }
    }

    @Test
    void testGetAuth() {
        // set up data in database
        String username = "User1";
        String token = "Token1";
        insertAuth(username, token);
        // run the test and assertions
        assertDoesNotThrow(() -> {
            AuthData dbAuth = dao.getAuth(token);
            assertEquals(username, dbAuth.username());
            assertEquals(token, dbAuth.authToken());
        });
    }

    @Test
    void testGetAuthMissing() {
        assertDoesNotThrow(() -> {
            AuthData dbAuth = dao.getAuth("No exist");
            assertNull(dbAuth);
        });
    }

    @Test
    void testAddAuth() {
        String username = "User2";
        String token = "Token2";
        assertDoesNotThrow(() -> {
            // ensure auth isn't in database
            AuthData dbAuth = dao.getAuth(token);
            assertNull(dbAuth);
            // add the auth
            dao.addAuth(new AuthData(token, username));
            // test to see if it was added
            dbAuth = dao.getAuth(token);
            assertEquals(username, dbAuth.username());
            assertEquals(token, dbAuth.authToken());
        });
    }


    @Test
    void testAddAuthInvalidUsername() {
        String username = null;
        String token = "Token3";
        assertThrows(DataAccessException.class, () -> {
            dao.addAuth(new AuthData(token, username));
        });
    }

    @Test
    void testRemoveAuth() {
        String username = "User4";
        String token = "Token4";
        assertDoesNotThrow(() -> {
            dao.addAuth(new AuthData(token, username));
            // test to see if it was added
            AuthData dbAuth = dao.getAuth(token);
            assertEquals(username, dbAuth.username());
            assertEquals(token, dbAuth.authToken());
            // remove auth
            dao.removeAuth(token);
            dbAuth = dao.getAuth(token);
            assertNull(dbAuth);
        });
    }

    @Test
    void testRemoveAuthInvalidToken() {
        String username = "User5";
        String token = "Token5";
        assertDoesNotThrow(() -> {
            dao.addAuth(new AuthData(token, username));
            // remove auth
            dao.removeAuth(null);
            // should have done absolutely nothing
            AuthData dbAuth = dao.getAuth(token);
            assertNotNull(dbAuth);
        });
    }

    private int getTableSize() {
        String statement = "SELECT COUNT(*) FROM auth";
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
        AuthData auth1 = new AuthData("auth1", "user1");
        AuthData auth2 = new AuthData("auth2", "user2");
        // insert data into database
        assertDoesNotThrow(() -> {
            dao.addAuth(auth1);
            dao.addAuth(auth2);
        });
        // check size
        assertEquals(2, getTableSize());
        // clear and check size again
        assertDoesNotThrow(dao::clear);
        assertEquals(0, getTableSize());
    }
}
