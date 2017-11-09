package webshop.business;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * One free if you have two of a named products.
 *
 * @author hom
 */
class TwoForOnePrice implements PriceReductionCalculator {

    private final String prodDescription;

    /**
     * Constructor taking a list of product names with a discount.
     *
     * @param prodDescription
     */
    public TwoForOnePrice( String prodDescription ) {
        this.prodDescription = prodDescription;
    }

    //<editor-fold defaultstate="expanded" desc="T02_B; __STUDENT_ID__ ;WEIGHT 1">
    /**
     * Get TwoForOnePrice sales price. This implementation should halve the
     * price of any pair of the named products.
     *
     * The algorithm: find all products quantities that have quantity > 1 and
     * have a description equal to the prodDescription. Sum the price of half
     * (integer calculation) that quantity. Example: If you have three bags
     * crisps, you get a reduction of one, because 3 % 2 = 1.
     *
     * @param cart the invoice for this calculation
     * @return the price to pay
     */
    @Override
    public long getReduction( ProductContainer cart ) {

        long reduction = 0;
        //Start Solution::replacewith:://TODO T02_B lambda for two for one price
        reduction = cart.getContents()
                .stream()
                .filter( pq -> pq.getProduct().getDescription()
                        .equalsIgnoreCase( prodDescription ) )
                .mapToLong( line -> ( ( line.getQuantity() / 2 ) * line
                        .getProduct().getConsumerPrice() ) )
                .sum();
        //End Solution
        return reduction;
    }
    //</editor-fold>
}
