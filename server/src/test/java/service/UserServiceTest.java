package service;

import dataaccess.*;
import exception.AlreadyTakenException;
import exception.InvalidCredentialsException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import result.LoginResult;
import result.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    UserDAO userDAO;
    AuthDAO authDAO;
    UserService service;

    @BeforeAll
    static void init() {
        assertDoesNotThrow(DatabaseManager::createDatabase);
    }

    @BeforeEach
    void setUp() {
        assertDoesNotThrow(() -> {
            userDAO = new DatabaseUserDAO();
            authDAO = new DatabaseAuthDAO();
            service = new UserService(authDAO, userDAO);
            userDAO.clear();
            authDAO.clear();
        });
    }

    // positive test register
    @Test
    void testRegisterUser() {
        // assert correct starting state
        String user = "user";
        // run function
        UserData userData = new UserData(user, user, "user@email.com");
        assertDoesNotThrow(() -> {
            assertNull(userDAO.getUser(user));
            RegisterResult result = service.register(new RegisterRequest(user, user, "user@email.com"));
            AuthData authData = authDAO.getAuth(result.getAuthToken());
            assertEquals(user, authData.username());
            // test worked
            UserData dbUser = userDAO.getUser(user);
            assertEquals(userData.username(), dbUser.username());
            assertEquals(userData.email(), dbUser.email());
        });
    }

    // negative test register
    @Test
    void testFailRegisterDuplicate() {
        // setup starting state
        String user = "user";
        UserData userData = new UserData(user, user, "user@email.com");
        assertDoesNotThrow(() -> userDAO.createUser(userData));
        // run function
        assertThrows(AlreadyTakenException.class, () -> {
            service.register(new RegisterRequest(user, user, "user@email.com"));
        });
    }

    // positive test login
    @Test
    void testLoginUser() {
        // set up starting state
        String user = "user";
        UserData userData = new UserData(user, user, "user@email.com");
        // run function
        assertDoesNotThrow(() -> {
            service.register(new RegisterRequest(user, user, "user@email.com"));
            LoginResult result = service.login(new LoginRequest(user, user));
            AuthData authData = authDAO.getAuth(result.getAuthToken());
            assertEquals(user, authData.username());
            UserData dbUser = userDAO.getUser(user);
            assertEquals(userData.username(), dbUser.username());
            assertEquals(userData.email(), dbUser.email());
        });
    }

    @Test
    void testInvalidLoginUser() {
        // set up starting state
        String user = "user";
        String fakeUser = "user-fake";
        UserData userData = new UserData(user, user, "user@email.com");
        assertDoesNotThrow(() -> userDAO.createUser(userData));
        // run function
        assertThrows(InvalidCredentialsException.class, () -> {
            service.login(new LoginRequest(fakeUser, user));
        });
    }

    @Test
    void testLogoutUser() {
        String authToken = "test-token";
        AuthData auth = new AuthData(authToken, "user");
        assertDoesNotThrow(() -> {
            authDAO.addAuth(auth);
            assertNotNull(authDAO.getAuth(authToken));
            service.logout(new LogoutRequest(authToken));
            assertNull(authDAO.getAuth(authToken));
        });
    }

    @Test
    void testInvalidLogoutUser() {
        String authToken = "test-token";
        assertThrows(InvalidCredentialsException.class, () -> {
            service.logout(new LogoutRequest(null));
        });
    }

    @Test
    void testAuthorize() {
        AuthData authData = new AuthData("test-token", "Dave");
        assertDoesNotThrow(() -> {
            authDAO.addAuth(authData);
            String retrievedUser = service.authorize(authData.authToken());
            assertEquals(authData.username(), retrievedUser);
        });
    }

    @Test
    void testAuthorizeNonexistentAuth() {
        assertThrows(InvalidCredentialsException.class, () -> {
            service.authorize("fake-token");
        });
    }
}