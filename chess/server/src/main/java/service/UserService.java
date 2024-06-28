package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.UserData;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
    public UserData register(UserData userData) throws DataAccessException {
        return dataAccess.createUser(userData);
    }
}
