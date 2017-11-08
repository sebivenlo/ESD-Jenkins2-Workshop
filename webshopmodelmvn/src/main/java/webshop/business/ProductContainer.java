package webshop.business;

import webshop.entities.Product;
import java.util.List;
import webshop.persistence.PersistenceException;

/**
 * Product container that can be used as shopping cart and inventory, which both
 * contain (product, quantity) tuples. This Container uses a List of
 * ProductQuantity objects to store its content. To be able to support the
 * concept of owner, customer or session, the ProductContainer has an owner
 * field, which is copied into the ProductQuantity owner field.
 * <br/>
 * Invariant: The product quantities in this container and its partner
 * (Inventory and Cart as example) stay in balance. As long as no sale has taken
 * place, the products are contained in either container and the overall sum of
 * products over all products is constant.<br>
 *
 * The container supports transfer of products to a partner container and adding
 * and removing of products to this container. Adding is done when the container
 * is filled without partner (thick of re-stocking the inventory), removing when
 * the contents of the container is sold to a customer (typically the cart).<br>
 *
 * Although the transfer of ProductQuantities from a source to a target
 * container can be thought of as the compound operation "take from source and
 * merge to target", it would not be transactional. To be able to make this
 * transfer transactional, the method <code>transferTo(ProductContainer target,
 * ProductQuantity pq)</code> should be used.
 *
 * @author Pieter van den Hombergh / Richard van den Ham
 */
public interface ProductContainer extends Iterable<ProductQuantity> {

    /**
     * Check if a product is contained. Note that this does not mean the count
     * != 0, as in "the product is in stock".
     *
     * @param product to test for
     * @return true if the product is listed in this container.
     * @throws PersistenceException something goes wrong in db world
     */
    boolean contains( Product product );

    /**
     * Get the amount of a specific product contained in this container.
     *
     * @param product to test for
     * @return the count for the product, if available
     * @throws NoSuchProductException if not available in this container.
     * @throws PersistenceException something goes wrong in db world
     */
    int count( Product product );

    /**
     * Transfer all ProductQantities in this container to a target container.
     * Use case: Abandoned cart. Hints to the implementor:
     * <ol>
     * <li>The implementor is reminded that the for loop cannot be used, since
     * modifying the list while looping over it will result in a
     * ConcurrentModificationException. </li>
     * <li>The operation is a transfer of all product quantities from this to
     * target, followed by a purge operation. This WOULD allow the for loop
     * approach.
     * </li></ol>
     *
     * @param target to flush to
     * @return this container, which will be empty.
     * @throws PersistenceException something goes wrong in db world
     */
    ProductContainer drainTo( ProductContainer target );

    /**
     * Remove all Products from this container.
     *
     * @throws PersistenceException something goes wrong in db world
     */
    void empty();

    /**
     * Search (and maybe find) the product quantity tuple for a product. Return
     * a reference to the product quantity for the requested in this container.
     * The reference should not be shared with other object, in particular not
     * with other product container instances or child class instances thereof.
     *
     * @param product to find
     * @return a ProductQuantity tuple if found or null if not.
     * @throws PersistenceException something goes wrong in db world
     */
    ProductQuantity find( Product product );

    /**
     * Get the content as list of product, quantity tuples.
     *
     * @return the list.
     * @throws PersistenceException something goes wrong in db world
     */
    List<ProductQuantity> getContents();

    /**
     *
     */
    default long getValueIncludingVat() {
        return getContents()
                .stream()
                .mapToLong( pq -> pq.getConsumerPrice() )
                .sum();
    }

    /**
     * Get the owner of this container.
     *
     * @return the owner id
     * @throws PersistenceException something goes wrong in db world
     */
    long getOwner();

    /**
     * Compute the sum of all quantities.
     *
     * @return the total item count.
     * @throws PersistenceException something goes wrong in db world *
     */
    int itemCount();

    /**
     * Add an amount of products of kind 'product' to this container. The amount
     * to be added must be non-negative.
     *
     * @param product to change quantity for.
     * @param quantityToAdd the difference in amount
     * @return a reference to the ProductQantity tuple added or modified by this
     * operation or null of the operation results in a quantity of zero.
     * @throws IllegalArgumentException when the delta exceeds the current
     * quantity.
     * @throws PersistenceException something goes wrong in db world
     */
    ProductQuantity merge( Product product, int quantityToAdd );

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
     * @throws PersistenceException something goes wrong in db world
     */
    ProductQuantity merge( ProductQuantity pq );

    /**
     * Delete all ProductQuantity tuples that have quantity 0. The implementor
     * is reminded that the for loop cannot be used, since modifying the list
     * while looping over it will result in a ConcurrentModificationException.
     *
     * @return this container
     * @throws PersistenceException something goes wrong in db world
     */
    ProductContainer purge();

    /**
     * Set the owner of this container.
     *
     * @param owner identifies container.
     */
    void setOwner( long owner );

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
     * @throws PersistenceException something goes wrong in db world
     */
    ProductQuantity take( Product product, int quantity ) throws
            NegativeQuantityException, NoSuchProductException,
            PersistenceException;

    /**
     * Removes all (remaining) products of type product from this container. The
     * quantity for the product is set to zero for the product. The 'zero'
     * ProductQuantity is NOT purged.
     *
     * @param product to remove
     * @return a copy of the ProductQuantity removed.
     * @throws NoSuchProductException when it is not there.
     * @throws PersistenceException something goes wrong in db world
     */
    ProductQuantity takeAll( Product product ) throws
            NoSuchProductException;

    /**
     * Transfer Products form this container to the target. Use this method in
     * preference to the the compound operation target.merge(this.take(...)),
     * because the later will not be transactional in all cases. The thrown
     * exception can be unwrapped to get the underlying cause of the failure.
     *
     * @param target receiver of transfer
     * @param product to transfer
     * @param quantity in this quantity
     * @return this container
     * @throws IllegalArgumentException when quantity is less 0
     * @throws NoSuchProductException if the requested product is not contained
     * in this container
     * @throws NegativeQuantityException if the requested quantity exceeds the
     * available quantity
     * @throws PersistenceException something goes wrong in db world
     */
    ProductContainer transferTo( ProductContainer target,
            Product product, int quantity );

    /**
     * Transfer all products form this container to the target. Use this method
     * in preference to the the compound operation target.merge(this.take(...)),
     * because the later will not be transactional in all cases. The thrown
     * exception can be unwrapped to get the underlying cause of the failure.
     *
     * @param target receiver of transfer
     * @param product to transfer
     * @return this container
     * @throws IllegalArgumentException when quantity is less 0
     * @throws NoSuchProductException if the requested product is not contained
     * in this container
     * @throws NegativeQuantityException if the requested quantity exceeds the
     * available quantity
     * @throws PersistenceException something goes wrong in db world
     */
    ProductQuantity transferAllTo( ProductContainer target,
            Product product );

    /**
     * Add a container observer. 
     * A Container can be made observable, so that
     * changes can be noticed. The default implementation does nothing, but has
     * been added because the database implementation does not require it.
     *
     * @param obs to add.
     */
    default void addObserver( ContainerObserver obs ) {

    }

    /**
     * delete a container observer. 
     * A Container can be made observable, so that
     * changes can be noticed. The default implementation does nothing, but has
     * been added because the database implementation does not require it.
     *
     * @param obs to remove.
     */
    default void deleteObserver( ContainerObserver obs ) {

    }
}
