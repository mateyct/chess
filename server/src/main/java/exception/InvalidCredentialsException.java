package exception;

public class InvalidCredentialsException extends ResponseException {
    public InvalidCredentialsException(String message) {
        super(message, 401);
    }

    public InvalidCredentialsException(String message, Throwable ex) {
        super(message, ex, 401);
    }
}
