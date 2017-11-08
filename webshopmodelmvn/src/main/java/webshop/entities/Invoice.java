package webshop.entities;

import java.io.Serializable;
import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import webshop.business.ContainerObserver;
import webshop.business.PriceReductionCalculator;
import webshop.business.ProductContainer;
import webshop.business.ProductQuantity;
import webshop.business.VATLevel;

/**
 * Invoice of a purchase. The Invoice specifies product, quantity of product,
 * vat level and price.
 *
 * In this exercise you should strive to use lambda expressions. It 2015 now,
 * java 8 is default.
 *
 * @author hom
 */
public class Invoice implements ContainerObserver, Serializable {

    private static final long serialVersionUID = 1L;
    private long number =0;
    private static PriceReductionCalculator DEFAULT_CALCULATOR
            = new DefaultReduction();
    private PriceReductionCalculator calculator = DEFAULT_CALCULATOR;
    private List<InvoiceLine> lines = null;
    private Date invoiceDate;
    // not saved in serialized form.
    private transient ProductContainer cart;

    /**
     * Create an Invoice for a cart (content).
     *
     * @param cart for which the invoice is created.
     */
    public Invoice( ProductContainer cart ) {
        this.cart = cart;
        this.lines = mapCartToLines( cart );
        this.invoiceDate = new java.sql.Date(System.currentTimeMillis());
        attachToCart();
    }

    public Invoice( long invoiceId, Date invoiceDate, List<InvoiceLine> lines) {
        this.cart = null;
        this.number = invoiceId;
        this.invoiceDate = invoiceDate;
        this.lines = lines;
    }

    /**
     * Every invoice needs one.
     *
     * @return the number
     */
    public long getNumber() {
        return number;
    }

    /**
     * Set the number.
     *
     * @param number for this invoice
     */
    public void setNumber( long number ) {
        this.number = number;
    }

    /**
     * Get the product/quantity lines of the invoice. The use of lambda
     * expressions is mandatory.
     *
     * <p>
     * <b>Approach</b>: Stream content product container (cart) as product
     * quantities, map these to invoice lines using invoice line constructor and
     * collect those into the result list. Return this result list. (Replace the
     * given return value.)</p>
     *
     * @return the list of lines.
     */
    public List<InvoiceLine> getLines() {
        return this.lines;

    }
    //</editor-fold>

    //<editor-fold defaultstate="expanded" desc="T01_B; __STUDENT_ID__ ;WEIGHT 1">
    public final List<InvoiceLine> mapCartToLines( ProductContainer cart ) {
        // stream pqs from cart.
        //Start Solution::replacewith:://TODO T01_B impl Invoice.getLines
        Stream<ProductQuantity> pqs = cart.getContents().
                stream();
        // map pq to invoice line with new, then collect.
        return pqs.map( p -> new InvoiceLine( this.number, p ) ).
                collect( Collectors.toList() );
        //End Solution::replacewith::return Collections.EMPTY_LIST;

    }

    //<editor-fold defaultstate="expanded" desc="T02_B; __STUDENT_ID__ ;WEIGHT 1">
    /**
     * Get the the final price of this invoice, the amount payable or end user
     * price. The implementor should remember that the prices in the products
     * include VAT. The use of a lambda expression is mandatory.
     *
     * @return the sum to pay
     */
    public long getTotalPriceIncludingVAT() {
        //Start Solution::replacewith:://TODO T02_B impl Invoice.getTotalPriceOncluding
        long sum = getLines()
                .stream()
                .mapToLong( InvoiceLine::getConsumerPrice )
                .sum();
        return sum;
        //End Solution::replacewith::return 0L;
    }
    //</editor-fold>

    /**
     * Get the the final business to business price of this invoice, the amount
     * payable between companies. The price without VAT. price.
     *
     * @return the sum to pay
     */
    public long getTotalPriceExcludingVAT() {
        long sum = 0;
        for ( InvoiceLine il : getLines() ) {
            sum += il.getConsumerPrice() - il.getVat();
        }
        return sum;
    }

    /**
     * Get the summed value for a vat level. This enables the invoice to return
     * the total amount of VAT of products in the cart having the provided
     * VATLevel. Extra points for lambda with filter expression.
     *
     * @param forLevel HIGH or LOW, NONE does not have to be computed.
     * @return the amount of money to the vat level. If there are no products
     * with the provided VATLevel in the cart, 0 is returned as amount.
     */
    public long getVATValue( VATLevel forLevel ) {
        return getLines().
                stream().
                filter( l -> forLevel == l.getProduct().
                        getVatLevel() ).
                mapToLong( l -> l.getVat() ).
                sum();
    }

    /**
     * Return a special (typically reduced) price for this cart.
     *
     * @return the special price.
     */
    public long getSpecialPrice() {
        return getTotalPriceIncludingVAT() - getPriceReduction();
    }

    /**
     * Set a price reduction calculator.
     *
     * @param cp the calculator to set.
     */
    public void setPriceReductionCalculator( PriceReductionCalculator cp ) {
        if ( null != cp ) {
            this.calculator = cp;
        } else {
            this.calculator = DEFAULT_CALCULATOR;
        }
    }

    private long getPriceReduction() {
        return calculator.getReduction( this.cart );
    }

    /**
     * Recompute this invoice.
     *
     * @param container
     */
    @Override
    public void update( ProductContainer container ) {
        System.out.println( "invoice update" );
        this.lines = mapCartToLines( cart );
    }

    /**
     * Default special price is not very special; It return reduction 0L.
     */
    public static class DefaultReduction implements PriceReductionCalculator {

        private static final long serialVersionUID = 1L;

        @Override
        public long getReduction( ProductContainer invoice ) {
            return 0L;
        }
    }

    /**
     * Get the active special price calculator.
     *
     * @return the calculator
     */
    public PriceReductionCalculator getCalculator() {
        return calculator;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + ( int ) ( this.number ^ ( this.number >>> 32 ) );
        hash = 43 * hash + Objects.hashCode( this.calculator );
        hash = 43 * hash + Objects.hashCode( this.lines );
        hash = 43 * hash + Objects.hashCode( this.invoiceDate );
        return hash;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            System.out.println( "Fails on class" );
            return false;
        }
        final Invoice other = ( Invoice ) obj;
        if ( this.number != other.number ) {
            System.out.println( "Fails on number" );
            return false;
        }
        if ( !this.lines.equals( other.lines ) ) {
            System.out.println( "Fails on lines" );
            System.out.println( "this.lines.getClass() = " + this.lines.
                    getClass() );
            System.out.println( "other.lines.getClass() = " + other.lines.
                    getClass() );
            System.out.println( "this.lines.size() = " + this.lines.size() );
            System.out.println( "other.lines.size() = " + this.lines.size() );
            return false;
        }
        if ( this.invoiceDate.equals( other.invoiceDate ) ) {
            System.out.println( "Fails on date" );

            return false;
        }
        return true;
    }

    /**
     * For debugging only
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder( "Invoice{" );
        sb.append( "number=" + number +  ",\n calculator=" + calculator
                + ",\n invoiceDate=" + invoiceDate + "\nlines:---\n\t" );
        sb.append( lines.stream().
                map( x -> x.toString() ).
                collect( Collectors
                        .joining( "\n\t" ) ) );
        sb.append( "\n----}" );
        return sb.toString();
    }

    /**
     * Connect this invoice to a product container, so that is may observe it.
     *
     */
    public final void attachToCart() {
        if ( this.cart != null ) {
            this.cart.addObserver( this );
        }
    }

    /**
     * Connect this invoice to a product container, so that is may observe it.
     *
     */
    public void detachFromCart() {
        if ( this.cart != null ) {
            this.cart.deleteObserver( this );
        }
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate( Date invoiceDate ) {
        this.invoiceDate = invoiceDate;
    }
    
}
