package server;

import com.google.gson.Gson;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.SQLDataAccess;
import model.*;
import server.webSocket.WebSocketHandler;
import service.AuthService;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Server {
    private final AuthService authService;
    private final ClearService clearService;
    private final GameService gameService;
    private final UserService userService;
    private final WebSocketHandler webSocketHandler;

    public Server() {
        DataAccess dataAccess;
        try {
            dataAccess = new SQLDataAccess();
        } catch (Throwable ex) {
            throw new RuntimeException("SQL database failed to initialize: %s".formatted(ex.getMessage()));
        }

        this.authService = new AuthService(dataAccess);
        this.clearService = new ClearService(dataAccess);
        this.gameService = new GameService(dataAccess);
        this.userService = new UserService(dataAccess);
        this.webSocketHandler = new WebSocketHandler(authService, clearService, gameService, userService);
    }
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/connect", webSocketHandler);

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.exception(DataAccessException.class, this::exceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private void exceptionHandler(DataAccessException ex, Request req, Response res) {
        res.status(ex.statusCode());
    }

    private Object register(Request req, Response res) {
        try {
            var userData = new Gson().fromJson(req.body(), UserData.class);
            userData = userService.register(userData);
            AuthData authData = authService.createAuth(userData);
            return new Gson().toJson(authData);
        } catch (DataAccessException e) {
            return exceptionParser(e, res);
        }
    }

    private Object login(Request req, Response res) {
        try {
            var userData = new Gson().fromJson(req.body(), UserData.class);
            AuthData authData = authService.login(userData);
            return new Gson().toJson(authData);
        } catch (DataAccessException e) {
            return exceptionParser(e, res);
        }
    }

    private Object logout(Request req, Response res) {
        try {
            String authToken = req.headers("authorization");
            authService.checkAuth(authToken);
            authService.logout(authToken);
            return "";
        } catch (DataAccessException e) {
            return exceptionParser(e, res);
        }
    }

    private Object listGames(Request req, Response res) {
        try {
            String authToken = req.headers("authorization");
            authService.checkAuth(authToken);
            var games = gameService.listGames();

            var gameListJson = new HashMap<String, ArrayList<GameData>>();
            gameListJson.put("games", new ArrayList<>());
            gameListJson.get("games").addAll(games.values());

            return new Gson().toJson(gameListJson);
        } catch (DataAccessException e) {
            return exceptionParser(e, res);
        }
    }

    private Object createGame(Request req, Response res) {
        try {
            var name = (String) new Gson().fromJson(req.body(), HashMap.class).get("gameName");
            String authToken = req.headers("authorization");
            authService.checkAuth(authToken);
            GameData gameData = gameService.createGame(name);
            return new Gson().toJson(gameData);
        } catch (DataAccessException e) {
            return exceptionParser(e, res);
        }
    }

    private Object joinGame(Request req, Response res) {
        try {
            String authToken = req.headers("authorization");
            var requestBody = new Gson().fromJson(req.body(), HashMap.class);
            var playerColor = (String) requestBody.get("playerColor");
            var gameID = String.valueOf(requestBody.get("gameID"));
            if (authToken != null) {
                AuthData authData = authService.checkAuth(authToken);
                gameService.joinGame(authData.username(), playerColor, (int) Double.parseDouble(gameID));
            } else {
                gameService.joinGame(null, playerColor, Integer.parseInt(gameID));
            }
        } catch (DataAccessException e) {
            return exceptionParser(e, res);
        }
        return "";
    }

    private Object clear(Request req, Response res) {
        try {
            clearService.deleteDB();
            return new Gson().toJson(Collections.singletonMap("Result", "Success"));
        } catch (DataAccessException e) {
            return exceptionParser(e, res);
        }
    }

    private String exceptionParser(DataAccessException e, Response res) {
        res.status(e.statusCode());
        return new Gson().toJson(new JsonResponse(e.getMessage()));
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
