package exception;

public class BadGameDataException extends ResponseException {
    public BadGameDataException(String message) {
        super(message, 400);
    }

    public BadGameDataException(String message, Throwable ex) {
        super(message, ex, 400);
    }
}
