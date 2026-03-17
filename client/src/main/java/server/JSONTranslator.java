package server;

import com.google.gson.Gson;
import exception.ResponseException;

import java.lang.reflect.Type;

public class JSONTranslator {
    private final Gson gson;

    public JSONTranslator() {
        gson = new Gson();
    }

    public ResponseException translateException(String body) {
        return gson.fromJson(body, ResponseException.class);
    }

    public Object translateObject(String body, Type type){
        return gson.fromJson(body, type);
    }
}
