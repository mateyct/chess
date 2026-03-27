package websocket.messages;

public class ErrorServerMessage extends ServerMessage {
    private final String errorMessage;
    public ErrorServerMessage(String msg) {
        super(ServerMessageType.ERROR);
        errorMessage = "Error: " + msg;
    }

    public String getMessage() {
        return errorMessage;
    }
}
