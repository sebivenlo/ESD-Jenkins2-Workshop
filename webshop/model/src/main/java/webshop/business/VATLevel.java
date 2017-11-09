package webshop.business;

/**
 * The VATLevel of an Item. The VATLevel is expressed as a fraction with
 * numeration and denominator to get exact computations with money.
 *
 * @author hom
 */
public enum VATLevel {

    /**
     * For some products VAT is 0.
     */
    NONE( 0, 100 ) {

                @Override
                public long computeVAT( long price ) {
                    return 0;
                }

                @Override
                public long computeVATPartInPrice( long price ) {
                    return 0;
                }
            },
    /**
     * Low Dutch VAT percentage.
     */
    LOW( 6, 100 ),
    /**
     * High Dutch VAT percentage.
     */
    HIGH( 19, 100 );
    private final long numerator;
    private final long denominator;

    /**
     * Get the VAT level as Percentage.
     *
     * @return the percentage.
     */
    public double getPercentage() {
        return ( 100 * numerator ) / denominator;
    }

    /**
     * Set up numerator and denominator.
     *
     * @param numerator of fraction in calculation
     * @param denominator of fraction in calculation
     */
    private VATLevel( long numerator, long denominator ) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    /**
     * Compute the VAT amount for a given price. Use this method to get the fee
     * to add to a product with a net (VAT-less) price.
     *
     * @param price for which the VAT is computed.
     * @return the VAT fee.
     */
    public long computeVAT( long price ) {
        return ( price * numerator ) / denominator;
    }

    /**
     * Compute the net, VAT less price of the final price. Use this method to
     * get the price without VAT (business price), computed from the Consumer
     * price.
     *
     * @param price gross price value for this computation
     * @return the amount of cents IN the price.
     */
    public long computePriceWithoutVAT( long price ) {
        return ( denominator * price ) / ( denominator + numerator );
    }

    /**
     * Compute the difference between price and net or VATLess Price.
     *
     * @param price gross price value for this computation
     * @return the amount of cents VAT.
     */
    public long computeVATPartInPrice( long price ) {
        return price - computePriceWithoutVAT( price );
    }

    /**
     * Helper to map database values to vat levels.
     *
     * @param v one of H, L or N
     * @return HIHG, LOW or NONE
     */
    public static VATLevel shortStringToVatLevel( String v ) {
        switch ( v ) {
            default:
                if ( "N".equals( v ) ) {
                    return NONE;
                }
                if ( "L".equals( v ) ) {
                    return LOW;
                }
                if ( "H".equals( v ) ) {
                    return HIGH;
                }
                return NONE;
        }
    }

    /**
     * Get the short database name for this vat level.
     *
     * @return one of H, L or N
     */
    public String dbName() {
        return this.toString().substring( 0, 1 );
    }

    /**
     * Calculate the consumer price for a price with this VAT level.
     * @param price the price
     * @return the price plus VAT.
     */
    public long consumerPrice( long price ) {
        return price + computeVAT( price );
    }
}
