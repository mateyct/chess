package server;

import exception.ResponseException;
import sharedutil.JSONTranslator;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;

public class ClientCommunicator {
    private final String url;
    private final int port;
    private final HttpClient client;
    private final JSONTranslator translator;

    private static final Duration TIMEOUT = java.time.Duration.ofMillis(5000);

    public ClientCommunicator(String url, int port) {
        this.url = url;
        this.port = port;
        client = HttpClient.newHttpClient();
        translator = new JSONTranslator();
    }

    public HttpResponse<String> get(String path, String auth) throws ResponseException {
        String urlString;
        if (port > 0) {
            urlString = String.format(Locale.getDefault(), "%s:%d%s", url, port, path);
        }
        else {
            urlString = String.format(Locale.getDefault(), "%s%s", url, path);
        }
        try {
            HttpRequest request = HttpRequest.newBuilder(new URI(urlString))
                .timeout(TIMEOUT)
                .GET()
                .header("Authorization", auth)
                .build();

            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new ResponseException(e.getMessage(), 0);
        }
    }

    public HttpResponse<String> post(String path, Object body, String auth) throws ResponseException {
        String urlString;
        if (port > 0) {
            urlString = String.format(Locale.getDefault(), "%s:%d%s", url, port, path);
        }
        else {
            urlString = String.format(Locale.getDefault(), "%s%s", url, path);
        }
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder(new URI(urlString))
                .timeout(TIMEOUT)
                .POST(HttpRequest.BodyPublishers.ofString(translator.toJson(body)));
            if (auth != null && !auth.isEmpty()) {
                builder = builder.header("Authorization", auth);
            }
            HttpRequest request = builder.build();

            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new ResponseException(e.getMessage(), 0);
        }
    }

    public HttpResponse<String> delete(String path, String auth) throws ResponseException {
        String urlString;
        if (port > 0) {
            urlString = String.format(Locale.getDefault(), "%s:%d%s", url, port, path);
        }
        else {
            urlString = String.format(Locale.getDefault(), "%s%s", url, path);
        }
        try {
            HttpRequest request = HttpRequest.newBuilder(new URI(urlString))
                .timeout(TIMEOUT)
                .DELETE()
                .header("Authorization", auth)
                .build();

            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new ResponseException(e.getMessage(), 0);
        }
    }

    public HttpResponse<String> put(String path, Object body, String auth) throws ResponseException {
        String urlString;
        if (port > 0) {
            urlString = String.format(Locale.getDefault(), "%s:%d%s", url, port, path);
        }
        else {
            urlString = String.format(Locale.getDefault(), "%s%s", url, path);
        }
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder(new URI(urlString))
                .timeout(TIMEOUT)
                .PUT(HttpRequest.BodyPublishers.ofString(translator.toJson(body)));
            if (auth != null && !auth.isEmpty()) {
                builder = builder.header("Authorization", auth);
            }
            HttpRequest request = builder.build();

            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new ResponseException(e.getMessage(), 0);
        }
    }
}
