package exception;

public class BadRequestException extends ResponseException {
    public BadRequestException(String message) {
        super(message, 400);
    }

    public BadRequestException(String message, Throwable ex) {
        super(message, ex, 400);
    }
}
