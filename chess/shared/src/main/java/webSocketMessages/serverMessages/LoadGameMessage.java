package webSocketMessages.serverMessages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage {
    ChessGame game;
    public LoadGameMessage(ChessGame game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }
    public ChessGame game() {
        return game;
    }
}
