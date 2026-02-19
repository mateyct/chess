package server;

import exception.ResponseException;
import io.javalin.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        Handlers handlers = new Handlers();

        javalin.post("/user", handlers::registerHandler);
        javalin.post("/session", handlers::loginHandler);
        // use lambda to separate auth handling
        javalin.delete("/session", ctx -> {
            handlers.authorizeHandler(ctx);
            handlers.logoutHandler(ctx);
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
