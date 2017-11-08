package webshop.entities;

import java.io.Serializable;
import java.util.Objects;
import webshop.business.ProductQuantity;
import webshop.business.VATLevel;

/**
 * Invoice line specifies one product quantity, its price and vat.
 *
 * @author hom
 */
public class InvoiceLine implements Serializable {


    private final long id = 0;
    private final Product product;
    private final int quantity;
    private final long invoiceNumber;
    private final long consumerPrice;

    /**
     * Create an invoice line for a ProductQuantity.
     *
     *
     * @param inv for invoice; null is allowed for tests.
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
        return "id\t" + id + "\n\tproduct\t" + product + "\n\tquantity\t" + quantity
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
     * See {@link java.lang.Object#hashCode()}, adapted to prevent recursion into invoice.
     * 
     * @return the hash code including the invoice number, not the invoice itself.
     */
    @Override
    public int hashCode() {
        int hash = 5;
        //hash = 41 * hash + (int) ( this.id ^ ( this.id >>> 32 ) );
        hash = 41 * hash + Objects.hashCode( this.product );
        hash = 41 * hash + this.quantity;
        hash = 41 * hash + Objects.hashCode( this.invoiceNumber );
        hash = 41 * hash + (int) ( this.consumerPrice ^ ( this.consumerPrice >>> 32 ) );
        return hash;
    }

    /**
     * See {@link java.lang.Object#equals()}, adapted to prevent recursion into invoice.
     * 
     * @return equality value, including the invoice number, not the invoice itself.
     */
    @Override
    public boolean equals( Object obj ) {
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final InvoiceLine other = (InvoiceLine) obj;
        if ( this.id != other.id ) {
            return false;
        }
        if ( !Objects.equals( this.product, other.product ) ) {
            return false;
        }
        if ( this.quantity != other.quantity ) {
            return false;
        }
        // modified from generated code.
        if ( this.invoiceNumber != other.invoiceNumber  ) {
            return false;
        }
        if ( this.consumerPrice != other.consumerPrice ) {
            return false;
        }
        return true;
    }
    
    
}
