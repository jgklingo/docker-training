package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashMap;

public interface DataAccess {
    UserData createUser(UserData userData) throws DataAccessException;
    AuthData createAuth(AuthData authData) throws DataAccessException;
    void checkUser(UserData userData) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    AuthData checkAuth(String authToken) throws DataAccessException;
    HashMap<Integer, GameData> getGames() throws DataAccessException;
    GameData newGame(String name) throws DataAccessException;
    void addPlayer(String username, String playerColor, Integer gameID) throws DataAccessException;
    void deleteDB() throws DataAccessException;
    void updateGame(Integer gameID, String game) throws DataAccessException;
    GameData getGame(Integer gameID) throws DataAccessException;
}
