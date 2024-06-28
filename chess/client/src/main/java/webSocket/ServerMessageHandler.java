package webSocket;

import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.ServerMessage;

public interface ServerMessageHandler {
    void notify(ServerMessage serverMessage);
    void notify(ErrorMessage errorMessage);
    void notify(LoadGameMessage loadGameMessage);
}
