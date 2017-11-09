package webshop.business;

/**
 * Application specific exception.
 * @author hvd
 */
public class WebshopException extends Exception {

    /**
     * Adds a message.
     * @param message for this exception
     */
    public WebshopException(String message) {
        super(message);
    }
    
    /**
     * Wrap cause.
     * @param cause root cause 
     */
    public WebshopException(Throwable cause) {
        super(cause);
    }

    /**
     * Allows cause wrapping.
     * @param message to convey
     * @param cause of this exception
     */
    public WebshopException( String message, Throwable cause ) {
        super( message, cause );
    }
    
}
