package webSocketMessages.userCommands;

public class LeaveCommand extends UserGameCommand {
    Integer gameID;
    public LeaveCommand(String authToken, Integer gameID) {
        super(authToken);
        commandType = CommandType.LEAVE;
        this.gameID = gameID;
    }
    public Integer gameID() {return this.gameID;}
}
