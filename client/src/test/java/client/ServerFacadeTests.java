package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import request.*;
import result.CreateGameResult;
import server.Server;
import server.ServerFacade;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    static int port;

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    void reset() {
        assertDoesNotThrow(() -> {
            var client = HttpClient.newHttpClient();
            String urlString = String.format(Locale.getDefault(), "%s:%d%s", "http://localhost", port, "/db");
            HttpRequest request = HttpRequest.newBuilder(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(5000))
                .DELETE()
                .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
            client.close();
        });
        facade = new ServerFacade("http://localhost", port);
    }

    @Test
    void testRegisterValid() {
        RegisterRequest request = new RegisterRequest("User", "Pass", "Email");
        assertFalse(facade.signedIn());
        assertDoesNotThrow(() -> {
            facade.register(request);
        });
        assertTrue(facade.signedIn());
    }

    @Test
    void testRegisterInvalid() {
        RegisterRequest request = new RegisterRequest("User", "Pass", "Email");
        assertFalse(facade.signedIn());
        assertDoesNotThrow(() -> {
            facade.register(request);
        });
        assertTrue(facade.signedIn());
        assertThrows(ResponseException.class, () -> {
            facade.register(request);
        });
    }

    @Test
    void testLoginValid() {
        RegisterRequest registerRequest = new RegisterRequest("User", "Pass", "Email");
        assertFalse(facade.signedIn());
        assertDoesNotThrow(() -> {
            facade.register(registerRequest);
        });
        assertTrue(facade.signedIn());
        LoginRequest loginRequest = new LoginRequest("User", "Pass");
        facade = new ServerFacade("http://localhost", port);
        assertFalse(facade.signedIn());
        assertDoesNotThrow(() -> {
            facade.login(loginRequest);
        });
        assertTrue(facade.signedIn());
    }

    @Test
    void testLoginInvalid() {
        LoginRequest request = new LoginRequest("User", "Pass");
        assertFalse(facade.signedIn());
        assertThrows(ResponseException.class, () -> {
            facade.login(request);
        });
    }

    @Test
    void testLogoutValid() {
        RegisterRequest request = new RegisterRequest("User", "Pass", "Email");
        assertFalse(facade.signedIn());
        assertDoesNotThrow(() -> {
            facade.register(request);
        });
        assertTrue(facade.signedIn());
        assertDoesNotThrow(() -> {
            facade.logout();
        });
        assertFalse(facade.signedIn());
    }

    @Test
    void testLogoutInvalid() {
        assertThrows(ResponseException.class, () -> {
            facade.logout();
        });
    }

    @Test
    void testListGamesValid() {
        RegisterRequest request = new RegisterRequest("User", "Pass", "Email");
        assertFalse(facade.signedIn());
        assertDoesNotThrow(() -> {
            facade.register(request);
        });
        assertTrue(facade.signedIn());
        assertDoesNotThrow(() -> {
            var games = facade.listGames();
            assertEquals(0, games.getGames().size());
        });
    }

    @Test
    void testListGamesInvalid() {
        assertThrows(ResponseException.class, () -> {
            var games = facade.listGames();
            assertEquals(0, games.getGames().size());
        });
    }

    @Test
    void testCreateGameValid() {
        RegisterRequest request = new RegisterRequest("User", "Pass", "Email");
        assertFalse(facade.signedIn());
        assertDoesNotThrow(() -> {
            facade.register(request);
        });
        assertTrue(facade.signedIn());
        CreateGameRequest createGameRequest = new CreateGameRequest("Epic Game");
        assertDoesNotThrow(() -> {
            CreateGameResult result = facade.createGame(createGameRequest);
            assertTrue(result.getGameID() > 0);
        });
    }

    @Test
    void testCreateGameInvalid() {
        RegisterRequest request = new RegisterRequest("User", "Pass", "Email");
        assertFalse(facade.signedIn());
        assertDoesNotThrow(() -> {
            facade.register(request);
        });
        assertTrue(facade.signedIn());
        CreateGameRequest createGameRequest = new CreateGameRequest(null);
        assertThrows(ResponseException.class, () -> {
            facade.createGame(createGameRequest);
        });
    }

    @Test
    void testJoinGameValid() {
        RegisterRequest request = new RegisterRequest("User", "Pass", "Email");
        assertFalse(facade.signedIn());
        assertDoesNotThrow(() -> {
            facade.register(request);
        });
        assertTrue(facade.signedIn());
        CreateGameRequest createGameRequest = new CreateGameRequest("Epic Game");
        assertDoesNotThrow(() -> {
            CreateGameResult result = facade.createGame(createGameRequest);
            assertTrue(result.getGameID() > 0);
        });
        assertDoesNotThrow(() -> {
            facade.listGames();
            facade.joinGame(1, "WHITE");
        });
    }

    @Test
    void testJoinGameInvalid() {
        testJoinGameValid();
        assertThrows(ResponseException.class, () -> {
            facade.joinGame(1, "WHITE");
        });
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

}
