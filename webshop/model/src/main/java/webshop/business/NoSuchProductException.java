package webshop.business;

/**
 * Thrown on retrieval of non existing item.
 * Specific exception. Extends RuntimeException to make it an unchecked
 * exception.
 *
 * @author Pieter van den Hombergh
 *
 */
public class NoSuchProductException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NoSuchProductException( String m ) {
        super( m );
    }

}
