package dataAccess;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception {
    private final Integer statusCode;

    public DataAccessException(String message) {
        super(message);
        this.statusCode = 0;
    }
    public DataAccessException(String message, Integer statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
    public Integer statusCode() {
        return this.statusCode;
    }
}
