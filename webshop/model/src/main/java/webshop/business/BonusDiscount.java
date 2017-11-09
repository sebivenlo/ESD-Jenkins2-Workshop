package webshop.business;

/**
 * BounusDiscount is 10% off.
 *
 * @author hom
 */
class BonusDiscount implements PriceReductionCalculator {

    /**
     * Get Bonus price reduction. This implementation returns 1/10 of the price.
     *
     * @param cart for this calculation
     * @return the price to pay
     */
    @Override
    public long getReduction( ProductContainer cart ) {
        // 10% discount
        return cart.getValueIncludingVat() / 10;
    }

}
