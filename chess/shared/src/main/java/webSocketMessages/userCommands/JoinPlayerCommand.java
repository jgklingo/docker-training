package webSocketMessages.userCommands;

import chess.ChessGame;

public class JoinPlayerCommand extends JoinObserverCommand {
    public ChessGame.TeamColor playerColor;

    public JoinPlayerCommand(String authToken, Integer gameID, ChessGame.TeamColor playerColor) {
        super(authToken, gameID);
        this.playerColor = playerColor;
        this.commandType = CommandType.JOIN_PLAYER;
    }
}
