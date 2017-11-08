package webshop.entities;

import java.util.concurrent.CopyOnWriteArrayList;
import webshop.business.ContainerObserver;
import webshop.business.ProductContainer;
import webshop.business.ProductQuantity;

/**
 * This provides a default implementation for drainTo. It works in all known
 * derived classes, but probably a pure db implementation can do better,
 * delegating the operation in one transactional query or stored procedure.
 *
 * @author hom
 */
public abstract class ProductContainerMapper implements ProductContainer {

    protected final CopyOnWriteArrayList<ContainerObserver> observers
            = new CopyOnWriteArrayList<>();

    /**
     * Transfer all ProductQantities in this container to a target container.
     * Post condition: this container is purged. Use case: Abandoned cart.<br/>
     * Hints to the implementor:
     * <ol>
     * <li>The implementor is reminded that the for loop cannot be used, since
     * modifying the list while looping over it will result in a
     * ConcurrentModificationException. </li>
     * <li>The operation is a transfer of all pqs from this to target, followed
     * by a purge operation. This WOULD allow the for loop approach.
     * </li></ol>
     *
     * @param target to flush to
     * @return this container, which will be empty.
     */
    @Override
    public ProductContainer drainTo( ProductContainer target ) {
        for ( ProductQuantity pq : this ) {
            target.merge( this.takeAll( pq.getProduct() ) );
        }
        this.purge();
        return this;
    }

    @Override
    public void deleteObserver( ContainerObserver obs ) {
        observers.remove( obs );
    }

    @Override
    public void addObserver( ContainerObserver obs ) {
        observers.addIfAbsent( obs );
    }

    /**
     * Notify all observers of this container.
     */
    protected void notifyObservers() {
        System.out
                .println( "notify observers of " + getClass().getSimpleName() );
        for ( ContainerObserver obs : this.observers ) {
            obs.update( this );
        }
    }

}
