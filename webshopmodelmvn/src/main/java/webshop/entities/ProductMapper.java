package webshop.entities;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import webshop.business.ProductType;
import webshop.entities.Product;
import webshop.business.VATLevel;

/**
 * Simple product mapper.
 *
 * @author hom
 */
public abstract class ProductMapper {

    /**
     * Get all products form this mapper in a list.
     *
     * @return all products in a list.
     */
    public abstract List<Product> getAll();

    /**
     * Get product that matches the following the said attributes.
     *
     * @param description of the product
     * @param price of the product
     * @param vat of the product
     * @return the product if found else null
     */
    public abstract Product getProductByDescriptionPriceVat( String description,
            long price,
            VATLevel vat );

    /**
     * Get product by id.
     *
     * @param id off the product
     * @return the product if found else null
     */
    public abstract Product getProductById( long id );

    /**
     * Get the product that match the description. This method checks for
     * containment on the description in the actual description. The check is to
     * be done ignoring case.
     *
     * @param description to be matched
     * @return the list of products matching.
     */
    public List<Product> getProductByDescription( String description ) {
        String sw = description.toLowerCase();
        return getAll().stream()
                .filter( p -> p.getDescription().toLowerCase().contains( sw ) )
                .collect( Collectors.toList() );
    }

    /**
     * Create a product with given parameters, legal in this mapper's word.
     *
     * @param description of prod
     * @param priceExclVAT of prod
     * @param vl of prod
     * @param type of prod
     * @param min_age in years
     * @return the product
     */
    protected Product createProductWith( String description,
            long priceExclVAT, VATLevel vl,
            ProductType type, int min_age ) {
        Product result = null;
        if ( min_age > 0 ) {
            result
                    = new Product( nextProductId(), description, priceExclVAT,
                            vl,
                            type );
        } else {
            result = new Booze( description, priceExclVAT, vl );
        }
        result.setId( nextProductId() );
        return null;
        
    }
    
    public abstract long nextProductId();
    
    public static Product setProductId( Product prod, long id ) {
        prod.setId( id );
        return prod;
    }
}
