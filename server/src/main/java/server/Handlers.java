package server;

import com.google.gson.Gson;
import dataaccess.*;
import exception.AlreadyTakenException;
import io.javalin.http.Context;
import request.RegisterRequest;
import request.RegisterResult;
import request.Result;
import service.UserService;

import java.lang.reflect.Type;

public class Handlers {
    private final AuthDAO authDAO;
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final UserService userService;

    public Handlers() {
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
        userService = new UserService(authDAO, userDAO);
    }

    public void registerHandler(Context ctx) {
        RegisterRequest request = deserialize(ctx.body(), RegisterRequest.class);
        RegisterResult result = userService.register(request);
        ctx.json(serialize(result));
    }

    private <T> T deserialize(String json, Type type) {
        Gson gson = new Gson();
        return gson.fromJson(json, type);
    }

    private String serialize(Result result) {
        Gson gson = new Gson();
        return gson.toJson(result);
    }

    public void exceptionHandler(AlreadyTakenException e, Context ctx) {
        Result result = new Result("Error: " + e.getMessage());
        String json = serialize(result);
        ctx.status(e.getStatusCode());
        ctx.json(json);
    }

}
