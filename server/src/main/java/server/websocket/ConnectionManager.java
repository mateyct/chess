package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import sharedutil.JSONTranslator;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConnectionManager {
    private final Map<Integer, Set<Session>> sessionMap;
    private final JSONTranslator translator;

    public ConnectionManager() {
        sessionMap = new HashMap<>();
        translator = new JSONTranslator();
    }

    public void addSession(int gameID, Session session) {
        if (!sessionMap.containsKey(gameID)) {
            sessionMap.put(gameID, new HashSet<>());
        }
        sessionMap.get(gameID).add(session);
    }

    public void removeSession(int gameID, Session session) {
        if (!sessionMap.containsKey(gameID)) {
            return;
        }
        sessionMap.get(gameID).remove(session);
        if (sessionMap.get(gameID).isEmpty()) {
            sessionMap.remove(gameID);
        }
    }

    public void broadcast(int gameID, Session excludeSession, ServerMessage message) throws IOException {
        if (!sessionMap.containsKey(gameID)) {
            return;
        }
        String msg = translator.toJson(message);
        for (Session session : sessionMap.get(gameID)) {
            if (session.isOpen()) {
                if (!session.equals(excludeSession)) {
                    session.getRemote().sendString(msg);
                }
            }
        }
    }

    public void messageSession(Session session, ServerMessage message) throws IOException {
        String msg = translator.toJson(message);
        if (session.isOpen()) {
            session.getRemote().sendString(msg);
        }
    }
}
