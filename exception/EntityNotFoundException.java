package exception;

public class EntityNotFoundException extends Exception {
    public EntityNotFoundException(String message) {
        super(message);
    }
    
    public EntityNotFoundException(String entityType, String id) {
        super(String.format("%s with ID %s not found", entityType, id));
    }
}
