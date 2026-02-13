package exception;

public class ResponseException extends Exception {
    private final int statusCode;

    public ResponseException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
    public ResponseException(String message, Throwable ex, int statusCode) {
        super(message, ex);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
