package server;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import exception.ResponseException;
import io.javalin.*;
import server.websocket.WebSocketHandler;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // set up database
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException ex) {
            throw new RuntimeException("Failed to set up database: " + ex.getMessage());
        }

        Handlers handlers = new Handlers();
        WebSocketHandler wsHandler = handlers.getWebSocketHandler();

        javalin.post("/user", handlers::registerHandler);
        javalin.post("/session", handlers::loginHandler);
        // use lambda to separate auth handling
        javalin.delete("/session", ctx -> {
            handlers.authorizeHandler(ctx);
            handlers.logoutHandler(ctx);
        });

        javalin.before("/game", handlers::authorizeHandler);
        javalin.post("/game", handlers::createGameHandler);
        javalin.get("/game", handlers::listGamesHandler);
        javalin.put("/game", handlers::joinGameHandler);
        javalin.ws("/ws", ws -> {
            ws.onConnect(wsHandler);
            ws.onMessage(wsHandler);
            ws.onClose(wsHandler);
        });

        javalin.delete("/db", handlers::clearHandler);

        javalin.exception(ResponseException.class, handlers::responseExceptionHandler);
        javalin.exception(Exception.class, handlers::generalExceptionHandler);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
