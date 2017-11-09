package webshop.business;

/**
 * Thrown on underflow impeding condition of container.
 * @author Pieter van den Hombergh
 *
 */
public class NegativeQuantityException extends RuntimeException {

    /**
     * Implementation serial number.
     */
    private static final long serialVersionUID = 1L;

    public NegativeQuantityException(String m) {
        super(m);
    }
}
