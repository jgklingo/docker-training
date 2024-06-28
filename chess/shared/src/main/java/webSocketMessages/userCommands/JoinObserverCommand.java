package webSocketMessages.userCommands;

public class JoinObserverCommand extends UserGameCommand {
    public Integer gameID;

    public JoinObserverCommand(String authToken, Integer gameID) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.JOIN_OBSERVER;
    }
    public Integer gameID() {
        return gameID;
    }
}
