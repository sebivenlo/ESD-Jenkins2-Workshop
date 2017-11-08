package webshop.business;

import webshop.entities.Product;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * Product-quantity for usage in inventory or web shop cart. This class ensures
 * that no negative quantity value can exist for the (product,quantity).
 * Rationale: and inventory cannot have a debt, nor can you have a negative
 * amount of physical objects/goods.
 *
 * @author hom / hvd
 */
public class ProductQuantity implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Product product;
    private int quantity;
    private long owner;
    private java.sql.Timestamp dateCreation;
    private java.sql.Timestamp dateUpdate;

    /**
     * Get date of this creation.
     *
     * @return the date
     */
    public Timestamp getDateCreation() {
        return dateCreation;
    }

    /**
     * Set date of this creation.
     *
     * @param genisis the date
     */
    public void setDateCreation( Timestamp genisis ) {
        this.dateCreation = genisis;
    }

    /**
     * Get date of this update.
     *
     * @return the date
     */
    public Timestamp getDateUpdate() {
        return dateUpdate;
    }

    /**
     * Set date of this update.
     *
     * @param aDate the date
     */
    public void setDateUpdate( Timestamp aDate ) {
        this.dateUpdate = aDate;
    }

    /**
     * Create a product quantity tuple.
     *
     * @param product to contain
     * @param quantity the amount
     * @param owner of this productQuantity
     * @throws NegativeQuantityException when negative number is tried
     */
    public ProductQuantity( Product product, int quantity, long owner )
            throws NegativeQuantityException {
                if ( quantity < 0 ) {
            throw new NegativeQuantityException(
                    "Illegal quantity, below zero"  );
        }

        this.product = product;
        long now = System.currentTimeMillis();
        this.dateCreation = new java.sql.Timestamp( now );
        this.dateUpdate = new java.sql.Timestamp( now );
        this.owner = owner;
        this.quantity = quantity;
    }

    /**
     * Create a copy of a product quantity. This is also known as a copy
     * constructor.
     *
     * @param orginal to copy.
     */
    public ProductQuantity( ProductQuantity orginal ) {
        this( orginal.product, orginal.quantity, orginal.owner );
    }

    /**
     * Create a product quantity for owner 0 (Inventory).
     *
     * @param product for this
     * @param quantity for this
     *
     */
    public ProductQuantity( Product product, int quantity )
            throws NegativeQuantityException {
        this( product, quantity, 0 );
    }

    /**
     * Create a product quantity for owner 0 (Inventory) from original.
     *
     * @param original for this
     * @param owner for this
     *
     */
    public ProductQuantity( ProductQuantity original, long owner ) {
        this( original );
        this.owner = owner;
    }

    /**
     * Get the product of this ProductQuantity.
     *
     * @return the product
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Get the quantity.
     *
     * @return the quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Set or Update the quantity.
     *
     * @param quantity new value
     * @throws NegativeQuantityException when you try to make a debt for this
     * container
     */
    public final void setQuantity( int quantity ) throws
            NegativeQuantityException {

        if ( quantity < 0 ) {
            throw new NegativeQuantityException(
                    "Product quantity would go below zero." );
        }

        this.quantity = quantity;
        updateUpdateTime();
    }

    private void updateUpdateTime() {
        this.dateUpdate.setTime( System.currentTimeMillis() );
    }

    /**
     * Change product quantity. Negative delta allowed to decrease quantity.
     *
     * @return the new quantity of this product in this container.
     * @param quantityDelta the amount to add or subtract (when negative)
     * @throws NegativeQuantityException when new quantity would become
     * negative.
     */
    public int changeQuantity( int quantityDelta ) {
        if ( 0 > this.quantity + quantityDelta ) {
            throw new NegativeQuantityException(
                    "insufficient quantity for transfer" );
        }

        setQuantity( quantity + quantityDelta );
        return quantity;
    }

    /**
     * This hash code does not consider the owner field.
     *
     * @return the hash code using Product and quantity.
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode( this.product );
        hash = 67 * hash + this.quantity;
        return hash;
    }

    /**
     * This equals method does not consider ownership.
     *
     * @param obj other ProductQuantity.
     * @return true if obj is ProductQuantity with same Product and Quantity.
     */
    @Override
    public boolean equals( Object obj ) {
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final ProductQuantity other = (ProductQuantity) obj;
        if ( !Objects.equals( this.product, other.product ) ) {
            return false;
        }
        return this.quantity == other.quantity;
    }

    /**
     * Get ownership.
     *
     * @return owner of this ProductQuantity
     */
    public long getOwner() {
        return owner;
    }

    /**
     * Set Ownership.
     *
     * @param owner new owner.
     */
    public void setOwner( long owner ) {
        this.owner = owner;
        updateUpdateTime();
    }

    public long getProductId() {
        return getProduct().getId();
    }

    @Override
    public String toString() {
        return "pq= " + quantity + " times " + product.toString();
    }

    /**
     * Get the consumer price for the contained product.
     *
     * @return the price
     */
    public long getConsumerPrice() {
        return quantity * product.getConsumerPrice();
    }

    /**
     * Get the vat amount for this product times quantity.
     *
     * @return the vat for this product quantity.
     */
    public long getVat() {
        return quantity * product.getVat();
    }
}
