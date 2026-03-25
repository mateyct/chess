package websocket.messages;

public class ErrorServerMessage extends ServerMessage {
    private final String message;
    public ErrorServerMessage(String msg) {
        super(ServerMessageType.ERROR);
        message = "Error: " + msg;
    }

    public String getMessage() {
        return message;
    }
}
