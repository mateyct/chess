package service;

import dataaccess.*;
import exception.AlreadyTakenException;
import exception.InvalidCredentialsException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.*;

import java.rmi.AlreadyBoundException;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    UserDAO userDAO;
    AuthDAO authDAO;
    UserService service;

    @BeforeEach
    void setUp() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        service = new UserService(authDAO, userDAO);
    }

    // positive test register
    @Test
    void testRegisterUser() {
        // assert correct starting state
        String user = "user";
        assertNull(userDAO.getUser(user));
        // run function
        UserData userData = new UserData(user, user, "user@email.com");
        assertDoesNotThrow(() -> {
            RegisterResult result = service.register(new RegisterRequest(user, user, "user@email.com"));
            AuthData authData = authDAO.getAuth(result.getAuthToken());
            assertEquals(user, authData.username());
        });
        // test worked
        assertEquals(userData, userDAO.getUser(user));
    }

    // negative test register
    @Test
    void testFailRegisterDuplicate() {
        // setup starting state
        String user = "user";
        UserData userData = new UserData(user, user, "user@email.com");
        userDAO.createUser(userData);
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
        userDAO.createUser(userData);
        // run function
        assertDoesNotThrow(() -> {
            LoginResult result = service.login(new LoginRequest(user, user));
            AuthData authData = authDAO.getAuth(result.getAuthToken());
            assertEquals(user, authData.username());
        });
        assertEquals(userData, userDAO.getUser(user));
    }

    @Test
    void testInvalidLoginUser() {
        // set up starting state
        String user = "user";
        String fakeUser = "user-fake";
        UserData userData = new UserData(user, user, "user@email.com");
        userDAO.createUser(userData);
        // run function
        assertThrows(InvalidCredentialsException.class, () -> {
            service.login(new LoginRequest(fakeUser, user));
        });
    }

    @Test
    void testLogoutUser() {
        String authToken = "test-token";
        AuthData auth = new AuthData(authToken, "user");
        authDAO.addAuth(auth);
        assertNotNull(authDAO.getAuth(authToken));
        assertDoesNotThrow(() -> {
            service.logout(new LogoutRequest(authToken));
        });
        assertNull(authDAO.getAuth(authToken));
    }

    @Test
    void testInvalidLogoutUser() {
        String authToken = "test-token";
        assertThrows(InvalidCredentialsException.class, () -> {
            service.logout(new LogoutRequest(null));
        });
    }
}