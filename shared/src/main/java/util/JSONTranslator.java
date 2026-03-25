package util;

import com.google.gson.Gson;
import exception.ResponseException;
import result.Result;

import java.net.http.HttpResponse;

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

    public <T> T translateObject(String body, Class<T> type){
        return gson.fromJson(body, type);
    }

    public String toJson(Object body) {
        return gson.toJson(body);
    }
}
