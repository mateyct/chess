package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
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
        facade = new ServerFacade(port);
    }

    @BeforeEach
    void reset() {
        assertDoesNotThrow(() -> {
            var client = HttpClient.newHttpClient();
            String urlString = String.format(Locale.getDefault(), "http://%s:%d%s", "localhost", port, "/db");
            HttpRequest request = HttpRequest.newBuilder(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(5000))
                .DELETE()
                .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
            client.close();
        });
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
        facade = new ServerFacade(port);
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

    @AfterAll
    static void stopServer() {
        server.stop();
    }

}
