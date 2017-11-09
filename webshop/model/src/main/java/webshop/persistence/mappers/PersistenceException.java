package webshop.persistence.mappers;

/**
 * Persistence exceptions are the exceptions that the business can deal with.
 * @author hvd
 */
public class PersistenceException extends RuntimeException {

    public PersistenceException( String message ) {
        super( message );
    }

    public PersistenceException( String message, Throwable cause ) {
        super( message, cause );
    }
    
    
}
