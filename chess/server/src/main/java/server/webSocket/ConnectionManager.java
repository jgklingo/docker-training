package server.webSocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    public void add(String username, Session session, Integer lobby) {
        var connection = new Connection(username, session, lobby);
        connections.put(username, connection);
    }
    public void remove(String username) {
        connections.remove(username);
    }
    public void broadcast(String excludeUsername, ServerMessage serverMessage, Integer lobby) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.username.equals(excludeUsername) && Objects.equals(c.lobby, lobby)) {
                    c.send(new Gson().toJson(serverMessage));
                }
            } else {
                removeList.add(c);
            }
        }

        for (var c : removeList) {
            remove(c.username);
        }
    }
    // used to send LoadGameMessage to one player
    public void whisper(String username, ServerMessage serverMessage) throws IOException {
        connections.get(username).send(new Gson().toJson(serverMessage));
    }
}
