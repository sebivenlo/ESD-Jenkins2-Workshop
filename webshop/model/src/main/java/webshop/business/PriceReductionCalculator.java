/**
 * SpecialPriceCalculator.java. Author: Pieter van den Hombergh
 */
package webshop.business;

import java.io.Serializable;

/**
 * GoF Strategy interface. Allows to calculate price reductions in case of
 * special offers. The computation is typically done based on the contents of a
 * cart. The implementation should return 0L on a null input.
 *
 * @author Pieter van den Hombergh (hom)
 */
@FunctionalInterface
public interface PriceReductionCalculator extends Serializable {

    /**
     * Compute sales price for the content.
     *
     * @param invoice on the goods.
     * @return the price
     */
    long getReduction( ProductContainer invoice );
}
