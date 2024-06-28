package chess;

/**
 * Indicates an invalid move was made in a game
 */
public class InvalidMoveException extends Exception {
    final String message = "Invalid move";
    public InvalidMoveException() {}

    @Override
    public String getMessage() {
        return message;
    }
}
