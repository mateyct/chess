package exception;

public class AlreadyTakenException extends RuntimeException {
    int statusCode;
    public AlreadyTakenException(String message) {
        super(message);
        statusCode = 403;
    }
    public AlreadyTakenException(String message, Throwable ex) {
        super(message, ex);
        statusCode = 403;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
