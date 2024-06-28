package webSocketMessages.userCommands;

public class ResignCommand extends UserGameCommand {
    Integer gameID;
    public ResignCommand(String authToken, Integer gameID) {
        super(authToken);
        commandType = CommandType.RESIGN;
        this.gameID = gameID;
    }
    public Integer gameID() {
        return gameID;
    }
}
