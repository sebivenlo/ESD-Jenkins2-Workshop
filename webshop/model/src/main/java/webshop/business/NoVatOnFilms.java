package webshop.business;

import static webshop.business.ProductType.BD;
import static webshop.business.ProductType.DVD;

/**
 * BounusDiscount is no VAT ion High vat products. Although
 *
 * @author hom
 */
class NoVatOnFilms implements PriceReductionCalculator {

    //<editor-fold defaultstate="expanded" desc="T05_B; __STUDENT_ID__ ;WEIGHT 1">
    /**
     * Get Bonus price reduction. This implementation sums up the vat value of
     * the selected products.
     *
     * This implementations takes both product types BD and DVD to be films and
     * therefor applicable for the reduction. <br/>
     *
     * <b>Implementation Approach</b><br/>: Define long reduction make a stream
     * of the invoice, filter the applicable product types, map to long by the
     * product's vat level times the quantity and sum the resulting stream of
     * long. The result is the requested reduction. Hint: see the {@link java.util.stream} package description.
     *
     * @param cart for this calculation
     * @return the price reduction
     */
    @Override
    public long getReduction( ProductContainer cart ) {
        long reduction = 0;
        //Start Solution::replacewith:://TODO T05_B compute reduction.
        reduction = cart.getContents().stream()
                .filter( pq -> pq.getProduct().getProductType() == BD || pq
                        .getProduct().getProductType() == DVD )
                .mapToLong( pq -> pq.getVat() )
                .sum();
        System.out.println( "reduction = " + reduction );
        //End Solution
        return reduction;
    }
    //</editor-fold>
}
