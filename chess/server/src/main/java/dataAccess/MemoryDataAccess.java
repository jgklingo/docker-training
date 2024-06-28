package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashMap;
import java.util.Objects;

public class MemoryDataAccess implements DataAccess{
    private int gameID = 1;
    final private HashMap<String, UserData> users = new HashMap<>();
    final private HashMap<Integer, GameData> games = new HashMap<>();
    final private HashMap<String, AuthData> authTokens = new HashMap<>();

    public UserData createUser(UserData userData) throws DataAccessException {
        if (userData.username() == null || userData.password() == null || userData.email() == null) {
            throw new DataAccessException("Error: bad request", 400);
        }
        userData = new UserData(userData.username(), userData.password(), userData.email());
        if (users.containsKey(userData.username())) {
            throw new DataAccessException("Error: already taken", 403);
        }
        users.put(userData.username(), userData);
        return userData;
    }

    public AuthData createAuth(AuthData authData) {
        authData = new AuthData(authData.authToken(), authData.username());
        authTokens.put(authData.authToken(), authData);
        return authData;
    }

    public void checkUser(UserData userData) throws DataAccessException {
        UserData record = users.get(userData.username());
        if (record == null || !Objects.equals(userData.password(), record.password())) {
            throw new DataAccessException("Error: unauthorized", 401);
        }
    }

    public void deleteAuth(String authToken) {
        authTokens.remove(authToken);
    }

    public AuthData checkAuth(String authToken) throws DataAccessException {
        if (!authTokens.containsKey(authToken)) {
            throw new DataAccessException("Error: unauthorized", 401);
        }
        return authTokens.get(authToken);
    }

    public HashMap<Integer, GameData> getGames() {
        return games;
    }

    public GameData newGame(String name) throws DataAccessException {
        if (name == null || name.isEmpty()) {
            throw new DataAccessException("Error: bad request", 400);
        }
        int id = gameID++;
        var game = new GameData(id, null, null, name, null);
        games.put(id, game);
        return game;
    }

    public void addPlayer(String username, String playerColor, Integer gameID) throws DataAccessException{
        var game = games.get(gameID);
        if (game == null) {
            throw new DataAccessException("Error: bad request", 400);
        }
        if (Objects.equals(playerColor, "BLACK")) {
            if (game.blackUsername() != null) {
                throw new DataAccessException("Error: already taken", 403);
            }
            games.put(game.gameID(),
                    new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game()));
        } else if (Objects.equals(playerColor, "WHITE")) {
            if (game.whiteUsername() != null) {
                throw new DataAccessException("Error: already taken", 403);
            }
            games.put(game.gameID(),
                    new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game()));
        }
    }

    public void deleteDB() {
        users.clear();
        games.clear();
        authTokens.clear();
    }

    public void updateGame(Integer gameID, String game) throws DataAccessException {

    }

    public GameData getGame(Integer gameID) throws DataAccessException {
        return null;
    }
}
