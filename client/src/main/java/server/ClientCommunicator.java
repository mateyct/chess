package server;

import exception.ResponseException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;

public class ClientCommunicator {
    private final String hostname;
    private final int port;
    private final HttpClient client;

    private static final Duration TIMEOUT = java.time.Duration.ofMillis(5000);

    public ClientCommunicator(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        client = HttpClient.newHttpClient();
    }

    public String get(String path) throws ResponseException {
        String urlString = String.format(Locale.getDefault(), "http://%s:%d%s", hostname, port, path);
        try {
            HttpRequest request = HttpRequest.newBuilder(new URI(urlString))
                .timeout(TIMEOUT)
                .GET()
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return response.body();
            }
            return "";
        } catch (Exception e) {
            throw new ResponseException(e.getMessage(), 0);
        }
    }
}
