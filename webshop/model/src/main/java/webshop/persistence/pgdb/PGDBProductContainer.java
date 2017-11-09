package webshop.persistence.pgdb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import webshop.business.Product;
import webshop.business.ProductContainer;
import webshop.business.ProductQuantity;
import webshop.business.NegativeQuantityException;
import webshop.business.NoSuchProductException;
import webshop.persistence.mappers.PersistenceException;
import webshop.persistence.mappers.ProductContainerMapper;

/**
 *
 * @author hom
 */
public abstract class PGDBProductContainer extends ProductContainerMapper
        implements Serializable,
                   NamedTable {

    private static final long serialVersionUID = 1L;

    protected long owner_id = 0;

    PGDBProductContainer() {
    }

    @Override
    public boolean contains( Product product ) {
        return null != find( product );
    }

    @Override
    public int count( Product product ) {
        int result = 0;
        ProductQuantity pq = find( product );
        if ( null != pq ) {
            result = pq.getQuantity();
        }
        return result;
    }

    @Override
    public ProductQuantity find( Product product ) {
        System.out.println( "find = " + product );
        if ( null == product ) {
            return null;
        }
        getContents();
        ProductQuantity result = contents.stream()
                .filter( pq -> product.equals( pq.getProduct() ) )
                .findFirst()
                .orElse( null );
        return result;
    }

    private volatile List<ProductQuantity> contents = null;
    private volatile int itemCount = 0;

    /**
     * Get the pq of this container as tuples as list. To speed up things, the
     * result is cached. This cache is to be dropped by calling
     * <code>this.invalidateCache()</code>,on insert, update or delete. This
     * implementation in fact maintains two caches: a list of product quantities
     * and a map of product to product quantity.
     *
     * @return the productQuatities as list.
     */
    @Override
    public final List<ProductQuantity> getContents() {
        if ( null != contents ) {
            return contents;
        }
        System.out.println( "get contents" );
        String query = String.format(
                "select * from %s join products using(product_id) "
                + " where  owner_session=? order by product_id",
                getTableName() );
        List<ProductQuantity> result = new ArrayList<>();
        try ( QueryHelper helper = new QueryHelper();
                ResultSetWrapper rs = helper.doSelectWrapped( query,
                        getOwner() ); ) {
            itemCount = 0;
            while ( rs.next() ) {
                Product p = PGDBProductMapper.rsToObject( rs );
                ProductQuantity pq = createProductQuantity( p, rs );
                itemCount += pq.getQuantity();
                result.add( pq );
            }
        }
        // save content and return result
        return contents = result;
    }

    /**
     * Count all product in this container. Sums all quantities in this
     * container
     *
     * @return the sum the product quantities in this container.
     */
    @Override
    public int itemCount() {
        return itemCount;
    }

    @Override
    public ProductQuantity merge( Product product, int quantityToAdd ) {
        ProductQuantity pq;
        try ( QueryHelper helper = new QueryHelper() ) {

            pq = merge( helper, product, quantityToAdd );

        }
        return pq;
    }

    /**
     * Transactional version of merge. This does the main database work and does
     * it as one transaction. The query helper helps to ensure that the database
     * actions take place as one transaction. It commits on success and rolls
     * back on any exception. Exceptions are wrapped in non checked exceptions.
     * <br>Hint to the implementor: use the query helper. Study API and use
     * appropriate methods. For the select statement, use a method that returns
     * ResultSetWrapper as result set, which deals with all exceptions.
     *
     * @param helper the query helper
     * @param product to merge
     * @param quantityToAdd obvious
     * @return the merged quantity now in the database for this container.
     */
    ProductQuantity merge( final QueryHelper helper,
            Product product, int quantityToAdd ) {
        String tableName = getTableName();
        ProductQuantity have = null;

        String query1 = String.format(
                "insert into %s (product_id,owner_session) select ?,? "
                + "where not exists "
                + "(select 1 from %s where product_id =? and owner_session=?)",
                tableName, tableName );
        String query2 = String.format(
                "update %s set quantity= quantity+?,date_update=default "
                + "where product_id = ? and owner_session=?",
                tableName );
        String query3 = String.format(
                "select quantity from %s "
                + "where product_id=? and owner_session=?",
                tableName );
        long pid = product.getId();
        long me = getOwner(); // merge make the products owned by this product container.

        helper.doInsert( query1, pid, me, pid, me );
        helper.doUpdate( query2, quantityToAdd, pid, me );
        ResultSetWrapper rs = helper.doSelectWrapped( query3, pid, me );
        int qty;
        if ( rs.next() ) {
            // if we succeed contains is true
            qty = rs.getInt( "quantity" );
            if ( null == ( have = find( product ) ) ) {
                have = new ProductQuantity( product, qty, me );
                contents.add( have );
                itemCount += qty;
            } else {
                have.setQuantity( qty );
            }
        }
        notifyObservers();
        return have;
    }
    //</editor-fold>

    @Override
    public ProductQuantity merge( ProductQuantity pq ) {
        return merge( pq.getProduct(), pq.getQuantity() );
    }

    /**
     * Implementation detail: must query db.
     *
     * @return this
     */
    @Override
    public ProductContainer purge() {
        System.out.println( "Purge called" );
        String query = String.format(
                "delete from %s where quantity=0 and owner_session=?",
                getTableName() );

        try ( QueryHelper helper = new QueryHelper() ) {
            helper.doDelete( query, getOwner() );
        }
        // refresh local view
        contents = null;
        getContents();
        notifyObservers();
        return this;
    }

    /**
     * Take a product quantity. Most important is to check preconditions and
     * throw exceptions when a precondition is not met
     *
     * @param product to take
     * @param quantity to take
     * @return the product quantity take
     * @throws IllegalArgumentException on negative amount
     * @throws NoSuchProductException when product not available in this
     * container
     */
    @Override
    public ProductQuantity take( Product product, int quantity ) {

        if ( 0 > quantity ) {
            throw new IllegalArgumentException( "cannot take negative qty" );
        }

        ProductQuantity from = find( product );
        if ( null == from ) {
            throw new NoSuchProductException( "I have no " + product );
        }

        if ( quantity > from.getQuantity() ) {
            throw new NegativeQuantityException(
                    "cannot take more then avialable" );
        }

        ProductQuantity pqResult = null;
        try ( QueryHelper helper = new QueryHelper() ) {

            pqResult = take( helper, from, quantity );
        }

        return pqResult;
    }

    /**
     * Take, using the exception preserving route. The rules that might trigger
     * business exceptions such as quantity check and negative quantity check,
     * have already been done.
     *
     * @param from product quantity to take from.
     * @param quantity in quantities
     * @return the new product quantity
     * @throws Exception when something bad happens
     */
    ProductQuantity take( QueryHelper helper, ProductQuantity from, int quantity ) {
        String tableName = getTableName();
        long pid = from.getProductId();
        ProductQuantity pq = null;

        String updateQuery = String.format(
                "update %s  set quantity = quantity-? "
                + "where product_id =? and owner_session=?",
                tableName );

        helper.doUpdate( updateQuery, quantity, pid, getOwner() );
        from.changeQuantity( -quantity );
        itemCount -= quantity;
        pq = new ProductQuantity( from.getProduct(), quantity );

        notifyObservers();
        return pq;
    }

    /**
     * Takes all products from this container and returns the result as a
     * productQuantity.
     *
     * @param product of the product.
     * @return the productQuantity if the product is known in this container,
     * else null.
     */
    @Override
    public ProductQuantity takeAll( Product product ) {
        ProductQuantity pq = find( product );
        if ( null != pq ) {
            int qtyToTake = pq.getQuantity();
            System.out.println( "takeall qtyToTake = " + qtyToTake );
            ProductQuantity result = take( product, qtyToTake );
            System.out.println( "takeAll result = " + result );
            return result;
        }
        return null;

    }

    /**
     * Using a query helper make the operations take followed by merge
     * transactional. Use query helper
     *
     * @param target container
     * @param product to transfer
     * @param quantity to transfer
     * @return this container
     */
    @Override
    public ProductContainer transferTo( ProductContainer target,
            Product product, int quantity
    ) {
        if ( target == this ) {
            return this; // for the trivial case.
        }
        if ( !( target instanceof PGDBProductContainer ) ) {
            throw new PersistenceException(
                    "Transfer only in the family" );
        }
        ProductQuantity have = find( product );
        if ( null == have ) {
            throw new NoSuchProductException( "Not here to take from " );
        }
        try ( QueryHelper helper = new QueryHelper() ) {

            PGDBProductContainer dbTarget
                    = ( PGDBProductContainer ) target;
            ProductQuantity tPQ = this.take( helper, have, quantity );
            dbTarget.merge( helper, tPQ.getProduct(), tPQ.getQuantity() );
        }
        return this;
    }

    @Override
    public ProductQuantity transferAllTo( ProductContainer target,
            Product product
    ) {
        return target.merge( takeAll( product ) );
    }

    @Override
    public void empty() {
        String query = String.
                format( "delete from %s where owner_session=?",
                        getTableName() );
        try ( QueryHelper helper = new QueryHelper() ) {
            helper.doDelete( query, getOwner() );
            if ( null != contents ) {
                contents.clear();
            }
            itemCount = 0;
        }
    }

    @Override
    public long getOwner() {
        return owner_id;
    }

    @Override
    public void setOwner( long owner ) {
        owner_id = owner;
    }

    static ProductQuantity createProductQuantity( Product p, ResultSetWrapper rs ) {
        int quantity = rs.getInt( "quantity" );
        long owner = rs.getLong( "owner_session" );
        ProductQuantity result = new ProductQuantity( p, quantity, owner );
        result.setDateCreation( rs.getTimestamp( "date_creation" ) );
        result.setDateUpdate( rs.getTimestamp( "date_update" ) );
        return result;
    }

    @Override
    public Iterator<ProductQuantity> iterator() {
        return getContents().iterator();
    }

}
