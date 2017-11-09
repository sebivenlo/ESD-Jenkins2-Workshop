package webshop.business;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import static java.util.Collections.EMPTY_LIST;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

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

    private static final long serialVersionUID = 3L;
    private long number = 0;
    private static PriceReductionCalculator DEFAULT_CALCULATOR
            = new DefaultReduction();
    private PriceReductionCalculator calculator = DEFAULT_CALCULATOR;
    private List<InvoiceLine> lines = null;
    private LocalDate invoiceDate;
    private final Map<VATLevel, Long> vatMap = new EnumMap<>( VATLevel.class );
    private long finalSalesPrice;
    private long priceReduction;
    private LocalDate customerBirthday;

    // not saved in serialized form.
    private transient ProductContainer cart;
    // not saved in serialize form and restored to false, so no
    // cart can be reattached.
    private transient boolean cartCanBeSet = true;

    /**
     * Create a new Invoice.
     *
     */
    public Invoice() {
    }

    /**
     * Set the cart for this invoice. This may only be done once per lifetime of
     * this object.
     *
     * @param cart for which the invoice is updated.
     * @return this invoice.
     */
    public Invoice setCart( ProductContainer cart ) {
        if ( cartCanBeSet ) {
            this.cart = cart;
            this.lines = mapCartToLines( cart );
            this.invoiceDate = LocalDate.now();
            attachToCart();
            cartCanBeSet = false;
        } else {
            new IllegalStateException( "can be set only once" );
        }
        return this;
    }

    /**
     * Ctor to load product back from a persistence layer.
     *
     * @param invoiceId id
     * @param invoiceDate date
     * @param lines the lines in this invoice.
     */
    public Invoice( long invoiceId, LocalDate invoiceDate,
            List<InvoiceLine> lines ) {
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

    public List<InvoiceLine> getLines() {
        return this.lines;

    }

    //<editor-fold defaultstate="expanded" desc="T06_B; __STUDENT_ID__ ;WEIGHT 1">
    /**
     * Get the product/quantity lines of the invoice. The use of lambda
     * expressions is mandatory.
     *
     * <p>
     * <b>Approach</b>: Stream content product container (cart) as product
     * quantities, map these to invoice lines using invoice line constructor and
     * collect those into a list. Return this as result list. (Replace the
     * given return value.)</p>
     *
     * @return the list of lines.
     */
    final List<InvoiceLine> mapCartToLines( ProductContainer cart ) {
        // stream pqs from cart.
        // map pq to invoice line with new, then collect.
        //Start Solution::replacewith:://TODO T06_B impl Invoice.mapToCartLines
        Stream<ProductQuantity> pqs = cart.getContents().
                stream();
        return pqs.map( p -> new InvoiceLine( this.number, p ) ).
                collect( Collectors.toList() );
        //End Solution::replacewith::return EMPTY_LIST;
    }
    //</editor-fold>

    /**
     * Get the the final price of this invoice, the amount payable or end user
     * price. The implementor should remember that the prices in the products
     * include VAT. The use of a lambda expression is mandatory.
     *
     * @return the sum to pay
     */
    public long getTotalPriceIncludingVAT() {
        long sum = getLines()
                .stream()
                .mapToLong( InvoiceLine::getConsumerPrice )
                .sum();
        return sum;
    }

    //<editor-fold defaultstate="expanded" desc="T04_B; __STUDENT_ID__ ;WEIGHT 1">
    /**
     * Get the the final business to business price of this invoice, the amount
     * payable between companies. The price without VAT price. In the exam you
     * must use a for loop.
     *
     * @return the sum to pay
     */
    public long getTotalPriceExcludingVAT() {
        long sum = 0;
        //Start Solution::replacewith:://TODO T04_B implement getTotalPriceExcludingVat using for-loop
        for ( InvoiceLine il : getLines() ) {
            sum += il.getConsumerPrice() - il.getVat();
        }
        //End Solution::replacewith::
        return sum;
    }
    //</editor-fold>

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
        if ( cart != null ) {
            long value = getLines().
                    stream().
                    filter( l -> forLevel == l.getProduct().
                            getVatLevel() ).
                    mapToLong( l -> l.getVat() ).
                    sum();
            vatMap.put( forLevel, value );
        }
        return vatMap.get( forLevel );
    }

    /**
     * Return a special (typically reduced) price for this cart. This method has
     * as side effect that this.priceReduction and this.totalSalesPrice are
     * updated to be persisted.
     *
     * @return the special price.
     */
    public long getFinalPrice() {
        priceReduction = getPriceReduction();
        finalSalesPrice = getTotalPriceIncludingVAT() - priceReduction;
        return finalSalesPrice;
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

    /**
     * Get the price reduction. Computes the price reduction if there is a cart,
     * otherwise returns the saved value.
     *
     * @return the priceReduction.
     */
    public long getPriceReduction() {
        if ( null == cart ) {
            return this.priceReduction;
        } else {
            return priceReduction = calculator.getReduction( this.cart );
        }
    }

    /**
     * To be able to restore an invoice from persistence layer.
     *
     * @param priceReduction
     */
    public void setPriceReduction( long priceReduction ) {
        this.priceReduction = priceReduction;
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

    public LocalDate getCustomerBirthDay() {
        return this.customerBirthday;
    }

    public void setCustomerBirthDay( LocalDate bd ) {
        this.customerBirthday = bd;
    }

    /**
     * Check if the cart contains booze.
     *
     * @return true if check needed.
     */
    public boolean isAgeCheckRequired() {
        if ( cart != null ) {
            return cart.getContents().stream().anyMatch(
                    pq -> pq.getProduct() instanceof RequiresAgeCheck );
        }
        return false;
    }

    /**
     * Check required age for the contents of the cart. Birthday null should
     * return false. Otherwise the age check of all products in the cart that
     * require and age check should be execute. All age checks executed should
     * say Okay.
     *
     * @param birthDay to check for
     * @return the check result
     */
    public boolean isAgeOk( LocalDate birthDay ) {
        if ( cart != null ) {
            return cart.getContents().stream()
                    .allMatch(
                            pq
                            -> !( pq.getProduct() instanceof RequiresAgeCheck )
                            || ( ( RequiresAgeCheck ) pq.getProduct() ).ageOk(
                                    birthDay ) );
        }
        return false;
    }

    public boolean isAgeOk() {
        return isAgeOk( customerBirthday );
    }

    /**
     * Default special price is not very special; It return reduction 0L.
     */
    static class DefaultReduction implements PriceReductionCalculator {

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
            return false;
        }
        final Invoice other = ( Invoice ) obj;
        if ( this.number != other.number ) {
            return false;
        }
        if ( !this.invoiceDate.equals( other.invoiceDate ) ) {
            return false;
        }
        if ( !this.lines.equals( other.lines ) ) {
            return false;
        }
        return true;
    }

    /**
     * For debugging only
     *
     * @return a string for debugging purposes. Do not assume that this string
     * has any meaning to the business. In particular, do not parse it.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder( "Invoice{" );
        sb.append( "number=" + number + ",\n calculator=" + calculator
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

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate( LocalDate invoiceDate ) {
        this.invoiceDate = invoiceDate;
    }

    public long getFinalSalesPrice() {
        return finalSalesPrice;
    }

    public void setFinalSalesPrice( long finalSalesPrice ) {
        this.finalSalesPrice = finalSalesPrice;
    }

    public long getLowVatValue() {
        return vatMap.get( VATLevel.LOW );
    }

    public void setLowVatValue( long lowVatValue ) {
        this.vatMap.put( VATLevel.LOW, lowVatValue );
    }

    public long getHighVatValue() {
        return vatMap.get( VATLevel.HIGH );
    }

    public void setHighVatValue( long highVatValue ) {
        this.vatMap.put( VATLevel.HIGH, highVatValue );
    }

    public LocalDate getCustomerBirthday() {
        return customerBirthday;
    }

    public void setCustomerBirthday( LocalDate customerBirthday ) {
        this.customerBirthday = customerBirthday;
    }

}
