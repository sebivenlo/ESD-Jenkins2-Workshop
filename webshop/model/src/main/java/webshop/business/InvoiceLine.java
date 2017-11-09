package webshop.business;

import java.io.Serializable;
import java.util.Objects;

/**
 * Invoice line specifies one product quantity, its price and vat.
 *
 * @author hom
 */
public class InvoiceLine implements Serializable {

    private long id = 0;
    private Product product;
    private int quantity;
    private long invoiceNumber;
    private long consumerPrice;

    /**
     * Create an invoice line for a ProductQuantity.
     *
     *
     * @param invoiceNumber for invoice; null is allowed for tests.
     * @param pq and product quantity
     */
    public InvoiceLine( long invoiceNumber, ProductQuantity pq ) {
        this.invoiceNumber = invoiceNumber;
        this.product = pq.getProduct();
        this.quantity = pq.getQuantity();
        this.consumerPrice = quantity * getConsumerUnitPrice();
    }

    public long getVat() {
        return quantity * product.getVat();
    }

    public VATLevel getVatLevel() {
        return product.getVatLevel();
    }

    /**
     * The invoice this line is part of.
     *
     * @return the invoice.
     */
    public long getInvoiceNumber() {
        return invoiceNumber;
    }

    /**
     * The product of this line.
     *
     * @return the product.
     */
    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    /**
     * Get price of product*quantity including VAT.
     *
     * @return the price of product times quantity.
     */
    public long getConsumerPrice() {
        return consumerPrice;
    }

    @Override
    public String toString() {
        return "invoice line id\t" + id + "\n\tproduct\t" + product
                + "\n\tquantity\t"
                + quantity
                + "\n\tinvoiceId\t" + invoiceNumber + "\n\tsalesPrice\t"
                + consumerPrice
                + "\n\tvat\t"
                + getVat();
    }

    /**
     * Get price for one unit of product.
     *
     * @return the product unit price
     */
    public final long getConsumerUnitPrice() {
        return getVatLevel().consumerPrice( product.getPriceExclVAT() );
    }

    /**
     * See {@link java.lang.Object#hashCode()}, adapted to prevent recursion
     * into invoice.
     *
     * @return the hash code including the invoice number, not the invoice
     * itself.
     */
    @Override
    public int hashCode() {
        int hash = 5;
        //hash = 41 * hash + (int) ( this.id ^ ( this.id >>> 32 ) );
        hash = 41 * hash + Objects.hashCode( this.product );
        hash = 41 * hash + this.quantity;
        hash = 41 * hash + Objects.hashCode( this.invoiceNumber );
        hash = 41 * hash + ( int ) ( this.consumerPrice ^ ( this.consumerPrice
                >>> 32 ) );
        return hash;
    }

    /**
     * See {@link java.lang.Object#equals(Object)}, adapted to prevent recursion
     * into invoice.
     *
     * @param obj to check for equality with this.
     * @return equality value, including the invoice number, not the invoice
     * itself.
     */
    @Override
    public boolean equals( Object obj ) {
        if ( obj == null ) {
            return false;
        }
        // same is always equals
        if ( this == obj ) {
            return true;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final InvoiceLine other = ( InvoiceLine ) obj;
        if ( !Objects.deepEquals( this.product, other.product ) ) {
            return false;
        }
        if ( this.consumerPrice != other.consumerPrice ) {
            return false;
        }
        if ( this.quantity != other.quantity ) {
            return false;
        }
        if ( this.invoiceNumber != other.invoiceNumber ) {
            return false;
        }

        return this.id == other.id;
    }

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public void setProduct( Product product ) {
        this.product = product;
    }

    public void setQuantity( int quantity ) {
        this.quantity = quantity;
    }

    public void setInvoiceNumber( long invoiceNumber ) {
        this.invoiceNumber = invoiceNumber;
    }

    public void setConsumerPrice( long consumerPrice ) {
        this.consumerPrice = consumerPrice;
    }

}
