package webshop.persistence.inmemory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import webshop.entities.Product;
import webshop.business.ProductContainer;
import webshop.business.ProductQuantity;
import webshop.business.NegativeQuantityException;
import webshop.business.NoSuchProductException;
import webshop.entities.ProductContainerMapper;

/**
 * Product container that can be used as shopping cart and inventory, which both
 * contain (product, quantity) tuples. This Container uses a List of
 * ProductQuantity objects to store its content. To be able to support the
 * concept of owner, customer or session, the ProductContainerMapper has an
 * owner field, which is copied into the ProductQuantity owner field.
 *
 * Invariant: The product quantities in this container and its partner
 * (Inventory and Cart as example) stay in balance. As long as no sale has taken
 * place, the products are contained in either container and the overall sum of
 * products over all products is constant.
 *
 * The container supports transfer of products to a partner container and adding
 * and removing of products to this container. Adding is done when the container
 * is filled without partner, removing when the contents of the container is
 * sold to a customer (typically the cart).
 *
 *
 * @author Pieter van den Hombergh / Richard van den Ham
 */
public abstract class IMProductContainer extends ProductContainerMapper
        implements Serializable,
                   Iterable<ProductQuantity> {

    /**
     * Owner of this container.
     */
    protected long owner = 0;
    private final List<ProductQuantity> list = new ArrayList<>();

    /**
     * Add an amount of products of kind 'product' to this container. The amount
     * to be added must be non-negative.
     *
     * @param product to change quantity for.
     * @param quantityToAdd the difference in amount
     * @return a reference to the ProductQuantity tuple added or modified by
     * this operation or null of the operation results in a quantity of zero.
     * @throws IllegalArgumentException when the delta exceeds the current
     * quantity.
     */
    @Override
    public ProductQuantity merge( Product product, int quantityToAdd ) {
        if ( 0 > quantityToAdd ) {
            throw new IllegalArgumentException( "Negative quantity not allowed" );
        }
        return merge( new ProductQuantity( product, quantityToAdd ) );
    }

    /**
     * Merge the given ProductQuantity tuple into this container. Merging is
     * updating or adding a ProductQuantity in this container with the amount
     * specified in the tuple. Because ProductQuantity is guaranteed to only
     * have non-negative quantities, the effect of a merge is always at least
     * the same quantity as before. Passing a null as parameter is a
     * no-operation returning null. Passing a ProductQuantity with zero quantity
     * is a no-operation returning the result of
     * <code>find(ProductQuantity.getProduct())</code>.
     *
     * Hint to the implementor: Make sure you do not return the tuple in this
     * container, since that could be changed unmanaged, breaking the
     * responsibility of this container.
     *
     * @param pq the tuple of (product,quantity) to be merged.
     * @return the new (product,quantity) tuple of the product in this
     * container.
     */
    @Override
    public ProductQuantity merge( ProductQuantity pq ) {
        if ( null == pq ) {
            return null;
        }
        ProductQuantity have = find( pq.getProduct() );
        if ( null == have ) {
            have = new ProductQuantity( pq, getOwner() );
            this.list.add( have );
        } else {
            have.changeQuantity( pq.getQuantity() );
        }
        this.notifyObservers();
        return new ProductQuantity( have );
    }

    @Override
    public ProductContainer transferTo( ProductContainer target,
            Product product,
            int quantity ) {
        target.merge( this.take( product, quantity ) );
        return this;
    }

    @Override
    public ProductQuantity transferAllTo( ProductContainer target,
            Product product ) {
        return target.merge( this.takeAll( product ) );
    }

    /**
     * Takes or removes quantity products of this container. If the requested
     * quantity exceeds the available quantity in this container, a
     * NegativeQuantityException exception is thrown. Note that when the
     * container has the product registered, but the current quantity is zero,
     * it will react with throwing a NegativQuantityException on any positive,
     * non-zero, requested quantity. By convention, taking 0 products is a
     * no-operation.
     *
     * The implementor must ensure that no references to ProductQuantities in
     * this container leak out, because that would break the responsibility of
     * the container to keep exact numbers of the products contained. The tests
     * should verify that.
     *
     * @param product the product to take
     * @param quantity of the product
     * @return the taken product quantity
     * @throws IllegalArgumentException when quantity is less 0
     * @throws NoSuchProductException if the requested product is not contained
     * in this container
     * @throws NegativeQuantityException if the requested quantity exceeds the
     * available quantity
     */
    @Override
    public ProductQuantity take( Product product, int quantity ) {

        if ( 0 > quantity ) {
            throw new IllegalArgumentException(
                    "You cannot take negative quantities" );
        }
        ProductQuantity have = find( product );
        if ( null == have ) {
            throw new NoSuchProductException( "This container has no " + product );
        }

        // Negative QuantityException is thrown by changeQuantity method in case
        // not enough products are available.
        have.changeQuantity( -quantity );
        this.notifyObservers();
        return new ProductQuantity( product, quantity );
    }

    /**
     * Removes all (remaining) products of type product from this container. The
     * quantity for the product is set to zero for the product. The 'zero'
     * ProductQuantity is NOT purged.
     *
     * @param product to remove
     *
     * @return a copy of the ProductQuantity removed.
     * @throws NoSuchProductException when it is not there.
     */
    @Override
    public ProductQuantity takeAll( Product product ) {
        ProductQuantity have = find( product );
        if ( have != null ) {
            return take( have.getProduct(), have.getQuantity() );
        } else {
            return null;
        }
    }

    /**
     * Delete all ProductQuantity tuples that have quantity 0. The implementor
     * is reminded that the for loop cannot be used, since modifying the list
     * while looping over it will result in a ConcurrentModificationException.
     *
     * @return this container
     */
    @Override
    public IMProductContainer purge() {

        Iterator<ProductQuantity> itr = this.iterator();
        while ( itr.hasNext() ) {
            ProductQuantity pq = itr.next();
            if ( 0 == pq.getQuantity() ) {
                itr.remove();
            }
        }
        this.notifyObservers();
        return this;

    }

    /**
     * Get the content as list of product, quantity tuples.
     *
     * @return the list.
     */
    @Override
    public List<ProductQuantity> getContents() {
        return list;
    }

    /**
     * Search (and maybe find) the product quantity tuple for a product.
     *
     * @param product to find
     * @return a ProductQuantity tuple if found or null if not.
     */
    @Override
    public ProductQuantity find( final Product product ) {

        return list.stream().
                filter( pq -> pq.getProduct().
                        equals( product ) ).
                findFirst().
                orElse( null );
    }

    /**
     * Get the amount of a specific product contained in this container.
     *
     * @param product to test for
     * @return the count for the product, if available
     * @throws NoSuchProductException if not available in this container.
     */
    @Override
    public int count( Product product ) {

        ProductQuantity found = find( product );

        if ( null == found ) {
            throw new NoSuchProductException( "Product "
                    + product.getDescription() + " not in cart." );
        }
        return found.getQuantity();
    }

    /**
     * Check if a product is contained. Note that this does not mean the count
     * != 0, as in "the product is in stock".
     *
     * @param product to test for
     * @return true if the product is listed in this container.
     */
    @Override
    public boolean contains( Product product ) {
        return null != find( product );
    }

    /**
     * Compute the sum of all quantities.
     *
     * @return the total item count.
     */
    @Override
    public int itemCount() {
        // Must iterate over list.
        return this.list.stream().
                mapToInt( ProductQuantity::getQuantity ).
                sum();
    }

    /**
     * Get the iterator for this container.
     *
     * @return the Iterator.
     */
    @Override
    public Iterator<ProductQuantity> iterator() {
        return list.iterator();
    }

    /**
     * Get the owner id of this container
     *
     * @return the owner field.
     */
    @Override
    public long getOwner() {
        return owner;
    }

    /**
     * Set the owner id of this container.
     *
     * @param owner
     */
    @Override
    public void setOwner( long owner ) {
        this.owner = owner;
    }

    /**
     * Remove all contents of this container.
     */
    @Override
    public void empty() {
        list.clear();
    }

    /**
     * Default implementation does not make new numbers.
     *
     * @return 0
     */
    public long nextOwnerId() {
        return 0;
    }

}
