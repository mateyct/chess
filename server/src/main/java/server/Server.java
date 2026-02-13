package server;

import exception.AlreadyTakenException;
import io.javalin.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        Handlers handlers = new Handlers();

        javalin.post("/user", handlers::registerHandler);

        javalin.exception(AlreadyTakenException.class, handlers::exceptionHandler);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
