package webSocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    ServerMessageHandler serverMessageHandler;
    public WebSocketFacade(String url, ServerMessageHandler serverMessageHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            this.serverMessageHandler = serverMessageHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
                        var loadGameMessage = new Gson().fromJson(message, LoadGameMessage.class);
                        serverMessageHandler.notify(loadGameMessage);
                    } else if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
                        var errorMessage = new Gson().fromJson(message, ErrorMessage.class);
                        serverMessageHandler.notify(errorMessage);
                    } else {
                        serverMessageHandler.notify(serverMessage);
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @OnOpen
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void joinPlayer(String authToken, Integer gameID, ChessGame.TeamColor teamColor) throws ResponseException {
        try {
            var command = new JoinPlayerCommand(authToken, gameID, teamColor);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void joinObserver(String authToken, Integer gameID) throws ResponseException {
        try {
            var command = new JoinObserverCommand(authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void leave(String authToken, Integer gameID) throws ResponseException {
        try {
            var command = new LeaveCommand(authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void resign(String authToken, Integer gameID) throws ResponseException {
        try {
            var command = new ResignCommand(authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void makeMove(String authToken, Integer gameID, ChessMove chessMove) throws ResponseException {
        try {
            var command = new MakeMoveCommand(authToken, gameID, chessMove);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
}
