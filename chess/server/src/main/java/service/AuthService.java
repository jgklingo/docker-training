package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class AuthService {
    private final DataAccess dataAccess;

    public AuthService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData createAuth(UserData userData) throws DataAccessException {
        AuthData authData = new AuthData(UUID.randomUUID().toString(), userData.username());
        return dataAccess.createAuth(authData);
    }

    public AuthData login(UserData userData) throws DataAccessException {
        AuthData authData;
        dataAccess.checkUser(userData);
        authData = createAuth(userData);
        return authData;
    }

    public void logout(String authToken) throws DataAccessException {
        dataAccess.checkAuth(authToken);
        dataAccess.deleteAuth(authToken);
    }

    public AuthData checkAuth (String authToken) throws DataAccessException {
        return dataAccess.checkAuth(authToken);
    }
}
