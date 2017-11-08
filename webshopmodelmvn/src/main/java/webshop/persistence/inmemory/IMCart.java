package webshop.persistence.inmemory;

/**
 * Empty implementation of IMProductContainer. To make purpose of
 * IMProductContainer object clear, cart in this case.
 *
 * @author hom
 */
public final class IMCart extends IMProductContainer {

    private static long ownerSequence = 1;

    /**
     * Cart gets a sequence number.
     *
     * @return next number
     */
    @Override
    public long nextOwnerId() {
        return ownerSequence++;
    }

    /**
     * Create a new cart with unique id.
     */
    public IMCart() {
        owner = nextOwnerId();
    }

}
