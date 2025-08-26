package exception;

public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String field, String message) {
        super(String.format("Validation error for %s: %s", field, message));
    }
    
    public ValidationException(String field, String message, Throwable cause) {
        super(String.format("Validation error for %s: %s", field, message), cause);
    }
}
