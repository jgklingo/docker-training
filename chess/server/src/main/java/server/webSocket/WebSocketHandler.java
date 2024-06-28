package server.webSocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.AuthService;
import service.ClearService;
import service.GameService;
import service.UserService;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {
    private final AuthService authService;
    private final ClearService clearService;
    private final GameService gameService;
    private final UserService userService;
    private final ConnectionManager connections = new ConnectionManager();

    public WebSocketHandler(AuthService authService, ClearService clearService, GameService gameService, UserService userService) {
        this.authService = authService;
        this.clearService = clearService;
        this.gameService = gameService;
        this.userService = userService;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer(session, message);
            case JOIN_OBSERVER -> joinObserver(session, message);
            case LEAVE -> leave(session, message);
            case RESIGN -> resign(session, message);
            case MAKE_MOVE -> makeMove(session, message);
        }
    }

    public void joinPlayer(Session session, String message) throws IOException {
        JoinPlayerCommand joinPlayerCommand = new Gson().fromJson(message, JoinPlayerCommand.class);
        try {
            AuthData authData = authService.checkAuth(joinPlayerCommand.getAuthString());
            String username = authData.username();
            GameData gameData = gameService.listGames().get(joinPlayerCommand.gameID);
            if (gameData == null) {
                throw new DataAccessException("Invalid game ID");
            }

            if ((joinPlayerCommand.playerColor == ChessGame.TeamColor.WHITE && !Objects.equals(gameData.whiteUsername(), username))
                    || (joinPlayerCommand.playerColor == ChessGame.TeamColor.BLACK && !Objects.equals(gameData.blackUsername(), username))) {
                throw new DataAccessException("Spot is already taken.");
            }

            connections.add(username, session, joinPlayerCommand.gameID());

            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData.game());
            connections.whisper(username, loadGameMessage);

            NotificationMessage notificationMessage = new NotificationMessage(
                    "%s has joined the game as the %s player.".formatted(username, joinPlayerCommand.playerColor));
            connections.broadcast(username, notificationMessage, joinPlayerCommand.gameID());
        } catch (DataAccessException e) {
            exceptionParser(e, session);
        }
    }
    public void joinObserver(Session session, String message) throws IOException {
        JoinObserverCommand joinObserverCommand = new Gson().fromJson(message, JoinObserverCommand.class);
        try {
            AuthData authData = authService.checkAuth(joinObserverCommand.getAuthString());
            String username = authData.username();
            GameData gameData = gameService.listGames().get(joinObserverCommand.gameID);
            if (gameData == null) {
                throw new DataAccessException("Invalid game ID");
            }

            connections.add(username, session, joinObserverCommand.gameID());

            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData.game());
            connections.whisper(username, loadGameMessage);

            NotificationMessage notificationMessage = new NotificationMessage(
                    "%s has joined the game as an observer.".formatted(username));
            connections.broadcast(username, notificationMessage, joinObserverCommand.gameID());
        } catch (DataAccessException e) {
            exceptionParser(e, session);
        }
    }
    public void leave(Session session, String message) throws IOException {
        try {
            LeaveCommand leaveCommand = new Gson().fromJson(message, LeaveCommand.class);
            AuthData authData = authService.checkAuth(leaveCommand.getAuthString());
            String username = authData.username();
            NotificationMessage notificationMessage = new NotificationMessage(
                    "%s has left the game.".formatted(username));
            connections.broadcast(username, notificationMessage, leaveCommand.gameID());
            connections.remove(username);
        } catch (DataAccessException e) {
            exceptionParser(e, session);
        }
    }
    public void resign(Session session, String message) throws IOException {
        try {
            ResignCommand resignCommand = new Gson().fromJson(message, ResignCommand.class);
            AuthData authData = authService.checkAuth(resignCommand.getAuthString());
            String username = authData.username();
            if (!(Objects.equals(username, gameService.getGame(resignCommand.gameID()).blackUsername())
                    || Objects.equals(username, gameService.getGame(resignCommand.gameID()).whiteUsername()))) {
                throw new DataAccessException("Must be a player to resign.");
            }
            ChessGame updatedGame = gameService.getGame(resignCommand.gameID()).game();
            if (updatedGame.gameOver) {
                throw new DataAccessException("Game has already ended.");
            }
            updatedGame.gameOver = true;
            gameService.updateGame(resignCommand.gameID(), new Gson().toJson(updatedGame));
            NotificationMessage notificationMessage = new NotificationMessage(
                    "%s has resigned the game.".formatted(username));
            connections.broadcast(null, notificationMessage, resignCommand.gameID());
            //  connections.broadcast(username, new LoadGameMessage(updatedGame));
        } catch (DataAccessException e) {
            exceptionParser(e, session);
        }
    }
    private void makeMove(Session session, String message) throws IOException {
        try {
            MakeMoveCommand makeMoveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
            AuthData authData = authService.checkAuth(makeMoveCommand.getAuthString());
            GameData gameData = gameService.getGame(makeMoveCommand.gameID());
            ChessGame chessGame = gameData.game();
            ChessMove move = makeMoveCommand.chessMove();

            if (chessGame.getBoard().getPiece(move.getStartPosition()).getTeamColor() == ChessGame.TeamColor.WHITE && !Objects.equals(gameData.whiteUsername(), authData.username())) {
                throw new InvalidMoveException();
            }
            if (chessGame.getBoard().getPiece(move.getStartPosition()).getTeamColor() == ChessGame.TeamColor.BLACK && !Objects.equals(gameData.blackUsername(), authData.username())) {
                throw new InvalidMoveException();
            }

            String endGameMessage = chessGame.makeMove(move);

            gameService.updateGame(makeMoveCommand.gameID(), new Gson().toJson(chessGame));
            connections.broadcast(null, new LoadGameMessage(chessGame), makeMoveCommand.gameID());
            NotificationMessage notificationMessage = new NotificationMessage(
                    "%s made the following move: %s".formatted(authData.username(), move));
            connections.broadcast(authData.username(), notificationMessage, makeMoveCommand.gameID());

            if (endGameMessage != null) {
                notificationMessage = new NotificationMessage(endGameMessage);
                connections.broadcast(null, notificationMessage, makeMoveCommand.gameID());
            }
        } catch (DataAccessException | InvalidMoveException e) {
            exceptionParser(e, session);
        }
    }

    private void exceptionParser(Throwable e, Session session) throws IOException {
        ErrorMessage errorMessage = new ErrorMessage("Error: " + e.getMessage());
        session.getRemote().sendString(new Gson().toJson(errorMessage));
    }
}
