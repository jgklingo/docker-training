package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;

public class ClearService {
    private final DataAccess dataAccess;

    public ClearService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void deleteDB() throws DataAccessException {
        dataAccess.deleteDB();
    }
}
