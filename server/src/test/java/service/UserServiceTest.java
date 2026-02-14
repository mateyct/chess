package service;

import dataaccess.*;
import exception.AlreadyTakenException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.RegisterRequest;
import request.RegisterResult;

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

    // positive test
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

    // negative test
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
}