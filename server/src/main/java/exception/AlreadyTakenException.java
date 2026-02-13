package exception;

public class AlreadyTakenException extends ResponseException {
    public AlreadyTakenException(String message) {
        super(message, 403);
    }
    public AlreadyTakenException(String message, Throwable ex) {
        super(message, ex, 403);
    }
}
