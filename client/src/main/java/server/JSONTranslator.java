package server;

import com.google.gson.Gson;
import exception.ResponseException;
import result.Result;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.HashMap;

public class JSONTranslator {
    private final Gson gson;

    public JSONTranslator() {
        gson = new Gson();
    }

    public ResponseException translateException(HttpResponse<String> response) {
        Result result = gson.fromJson(response.body(), Result.class);
        int statusCode = response.statusCode();
        String message = result.getMessage();
        return new ResponseException(message, statusCode);
    }

    public Object translateObject(String body, Type type){
        return gson.fromJson(body, type);
    }

    public String toJson(Object body) {
        return gson.toJson(body);
    }
}
