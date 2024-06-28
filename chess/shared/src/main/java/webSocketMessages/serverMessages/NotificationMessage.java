package webSocketMessages.serverMessages;

public class NotificationMessage extends  ServerMessage {
    public NotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }
}
