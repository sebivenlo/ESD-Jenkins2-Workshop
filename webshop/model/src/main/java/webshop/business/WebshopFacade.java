package webshop.business;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;
import javax.annotation.Resource;
import webshop.persistence.mappers.AbstractWebshopFactory;
import webshop.persistence.mappers.InvoiceMapper;

/**
 * Represents a webshop session from the business perspective.
 *
 * @author hvd
 */
public class WebshopFacade {

    public static final long INVENTORY_OWNER_ID = 0;

    @Resource
    private ProductContainer inventory;
    @Resource
    private ProductContainer cart;
    private String discountCode = "";
    private volatile Invoice invoice;
    public static final String BONUS = "BONUS";
    public static final String VAT_FREE_FILM = "NO VAT ON FILMS";
    public static final String DOUBLE_CRIPS = "CRISPS";

    /**
     * Constructor DI.
     *
     * @param factory
     * @throws WebshopException database problem
     */
    public WebshopFacade( AbstractWebshopFactory factory ) throws
            WebshopException {

        try {
            System.out.println( "new facade" );
            this.factory = factory;
            inventory = factory.getInventory();
            cart = factory.createCart();
            invoice = new Invoice().setCart( cart );
        } catch ( Exception e ) {
            throw new WebshopException(
                    "Problem occured, please contact system administrator", e );
        }
    }

    private final AbstractWebshopFactory factory;

    /**
     * Get the session id.
     *
     * @return the session id
     */
    public int getSessionID() {
        return ( int ) cart.getOwner();
    }

    /**
     * Adds a product to the cart. Adds quantity 1 of this product to the cart.
     *
     * @param product to add
     * @throws WebshopException
     */
    public void addToCart( Product product ) throws WebshopException {
        addToCart( product, 1 );
    }

    /**
     * Transfers amount of product from inventory to cart. Possible runtime
     * exceptions are wrapped into a WebshopException.
     *
     * @param product to transfer to the cart.
     * @param quantity to take (positive) from inventory
     * @throws WebshopException Wrapper for runtime exceptions
     */
    public void addToCart( Product product, int quantity )
            throws WebshopException {

        try {
            inventory.transferTo( cart, product, quantity );
        } catch ( RuntimeException ex ) {
            throw new WebshopException( "Add not possible", ex );
        }
    }

    /**
     * Transfers amount of product from cart to inventory. Transfers amount of
     * product and purges cart afterwards. Possible runtime exceptions are
     * wrapped into a WebshopException.
     *
     * @param product to put back to inventory.
     * @param quantity to take (positive) from cart
     * @throws WebshopException Wrapper for runtime exceptions
     */
    public void removeFromCart( Product product, int quantity ) throws
            WebshopException {

        try {
            cart.transferTo( inventory, product, quantity );
            cart.purge();
        } catch ( RuntimeException ex ) {
            throw new WebshopException(
                    "Not possible to remove pq from cart, see cause", ex );
        }

    }

    /**
     * Transfers complete amount of product from cart back to inventory.
     * Transfers complete amount of requested product and purges cart
     * afterwards. Possible runtime exceptions are wrapped into a
     * WebshopException.
     *
     * @param product concerned
     * @throws WebshopException Wrapper for runtime exceptions.
     */
    public void removeFromCart( Product product )
            throws WebshopException {

        try {
            cart.transferAllTo( inventory, product );
            cart.purge();
        } catch ( RuntimeException ex ) {
            throw new WebshopException(
                    "Not possible to remove product from cart", ex );
        }

    }

    /**
     * Get the inventory.
     *
     * @return the inventory
     */
    public ProductContainer getInventory() {
        return inventory;
    }

    /**
     * Produce and get an invoice.
     *
     * @return the invoice
     */
    public Invoice getInvoice() {
        if ( null == invoice ) {
            invoice = new Invoice().setCart( cart );
        }
        return invoice;
    }

    /**
     * Get the cart.
     *
     * @return the cart.
     */
    public ProductContainer getCart() {
        return cart;
    }

    /**
     * Activate a discount code for this purchase. Activating the code switches
     * the special price strategy. If the String 'BONUS' is received, a
     * BonusDiscount special price strategy is activated.
     *
     */
    public void activateDiscountCode() {
        switch ( discountCode ) {
            case BONUS:
                invoice.setPriceReductionCalculator( new BonusDiscount() );
                break;
            case VAT_FREE_FILM:
                invoice.setPriceReductionCalculator( new NoVatOnFilms() );
                break;
            case DOUBLE_CRIPS:
                invoice.setPriceReductionCalculator(
                        new TwoForOnePrice( "Crisps" ) );
                break;
            default:
                invoice.setPriceReductionCalculator( null );
                break;
        }
    }

    /**
     * Submit the order. The cart contents is deleted from database. Sales and
     * invoice tables should normally be updated, but that is not implemented.
     * Post: new empty cart, invoice = new Invoice(cart), discountCode to empty
     * string.
     */
    public void submitOrder() {
        InvoiceMapper im = factory.getInvoiceMapper();
        im.save( invoice );
        cart.empty();
        cart = factory.createCart();
        invoice = new Invoice().setCart( cart );
        discountCode = "";
        for ( long l : im.getInvoiceIDs() ) {
            System.out.println( "l = " + l );
        }
    }

    /**
     * (Temporary) Method to generate invoice for display.
     *
     * @return invoice data.
     */
    public String getInvoiceData() {

        StringBuilder builder = new StringBuilder();
        DecimalFormat df = new DecimalFormat( "0.00" );

        Date date = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat( "dd MMM yyyy" );
        builder.append( "Date: " ).append( dateFormatter.format( date ) ).
                append( "\n" );

        builder.append( "SessionId: " ).append( getSessionID() ).append( "\n" );

        builder.append(
                "============================================\n"
                + "qty prd   Description            VAT     PRC\n" );

        for ( InvoiceLine line : invoice.getLines() ) {
            builder.append( String
                    .format( "%3d (%2d) %-20.20s %6.2f %7.2f%n",
                            line.getQuantity(),
                            line.getProduct().getId(),
                            truncateAfter( line.getProduct().getDescription(),
                                    20 ),
                            line.getVat() / 100.0,
                            line.getConsumerPrice() / 100.0 )
            );
        }

        builder.append( "\n" );
        if ( getInvoice().isAgeCheckRequired() ) {
            builder.append( "************** AGE CHECK REQUIRED **********\n" ).
                    append( "customer b.d. " )
                    .append( Objects.toString( invoice.getCustomerBirthDay() ) )
                    .append( invoice.isAgeOk( invoice.getCustomerBirthDay() )
                                     ? " Ok" : " Where is daddy?" )
                    .append( "\n"
                            + "************** AGE CHECK REQUIRED **********\n" );

        }
        builder.append( "========= All Prices are in EURO ===========\n" );
        builder.append( String.format(
                "TOTAL PRICE                          %7.2f%n"
                + "VAT LOW                              %7.2f%n"
                + "VAT HIGH                             %7.2f%n",
                invoice.getTotalPriceIncludingVAT() / 100.0,
                invoice.getVATValue( VATLevel.LOW ) / 100.0,
                invoice.getVATValue( VATLevel.HIGH ) / 100.0 ) );
        if ( invoice.getPriceReduction() != 0L ) {
            builder.append( String.format(
                    "SPECIAL PRICE                        %7.2f%n",
                    invoice.getFinalPrice() / 100.0 ) );
            builder.append( String.format(
                    "YOUR ADVANTAGE WITH BONUS            %7.2f%n",
                    ( invoice.getTotalPriceIncludingVAT() - invoice
                    .getFinalPrice() ) / 100.0 ) );
        }

        return builder.toString();
    }

    /**
     * Return all goods in the cart unsold to the inventory. Drains cart
     * contents (back) to Inventory. Creates new cart. Sets invoice new
     * Invoice(cart).
     *
     */
    public void abandonCart() {

        cart.drainTo( inventory );
        invoice.detachFromCart();
        cart = factory.createCart();
        invoice = new Invoice().setCart( cart );
        discountCode = "";
    }

    public int getActiveCarts() {
        return 1;
    }

    /**
     * Truncate long string to max after characters and appends ellipses if
     * changed.
     *
     * @param in to truncate
     * @param after size
     * @return shorter string of same string if truncation is not required.
     */
    static String truncateAfter( String in, int after ) {
        if ( in.length() < after - 1 ) {
            return in;
        }
        return in.substring( 0, after - 1 ) + "\u2026";
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode( String discountCode ) {
        this.discountCode = discountCode;
    }

    public boolean isAgeCheckRequired() {
        return invoice.isAgeCheckRequired();
    }

    public LocalDate getCustomerBirthDay() {
        return invoice.getCustomerBirthDay();
    }

    public void setCustomerBirthDay( LocalDate bd ) {
        invoice.setCustomerBirthDay( bd );
    }

    public boolean isAgeOk() {
        return invoice.isAgeOk();
    }
}
