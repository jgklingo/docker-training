package server;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.GameDataList;
import model.UserData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ServerFacade {
    private final String serverUrl;
    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData register(UserData userData) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, null, userData, AuthData.class);
    }
    public AuthData login(UserData userData) throws ResponseException {
        var path = "/session";
        return this.makeRequest("POST", path, null, userData, AuthData.class);
    }
    public void logout(String authToken) throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, authToken, null, null);
    }
    public ArrayList<GameData> listGames(String authToken) throws ResponseException {
        var path = "/game";
        var gameList = this.makeRequest("GET", path, authToken, null, GameDataList.class);
        return gameList.games();
    }
    public GameData createGame(String authToken, String gameName) throws ResponseException {
        var path = "/game";
        return this.makeRequest("POST", path, authToken, Collections.singletonMap("gameName", gameName), GameData.class);
    }
    public void joinGame(String authToken, String playerColor, Integer gameID) throws ResponseException {
        var path = "/game";
        HashMap<String, String> request = new HashMap<>();
        request.put("playerColor", playerColor);
        request.put("gameID", gameID.toString());
        this.makeRequest("PUT", path, authToken, request, null);
    }
    public void clear() throws ResponseException {
        this.makeRequest("DELETE", "/db", null, null, null);
    }


    private <T> T makeRequest(String method, String path, String authToken, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeAuthHeader(authToken, http);
            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            if (ex.getClass() != ResponseException.class) {
                throw new ResponseException(500, ex.getMessage());
            } else {
                throw new ResponseException(((ResponseException) ex).statusCode(), ex.getMessage());
            }
        }
    }
    private static void writeAuthHeader(String authToken, HttpURLConnection http) {
        if (authToken != null) {
            http.setRequestProperty("authorization", authToken);
        }
    }
    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }
    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        var message = http.getResponseMessage();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status + " - " + message);
        }
    }
    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }
    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
