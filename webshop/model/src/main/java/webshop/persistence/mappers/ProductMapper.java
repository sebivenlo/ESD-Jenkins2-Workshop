package webshop.persistence.mappers;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import webshop.business.Booze;
import webshop.business.Product;
import webshop.business.VATLevel;

/**
 * Simple product mapper.
 *
 * @author hom
 */
public interface ProductMapper {

    /**
     * Get all products form this mapper in a list.
     *
     * @return all products in a list.
     */
    List<Product> getAll();

    /**
     * Get product that matches the following the said attributes.
     *
     * @param description of the product
     * @param price of the product
     * @param vat of the product
     * @return the product if found else null
     */
    Product getProductByDescriptionPriceVat( String description, long price,
            VATLevel vat );

    /**
     * Get product by id.
     *
     * @param id off the product
     * @return the product if found else null
     */
    Product getProductById( long id );

    /**
     * Get the product that match the description. This method checks for
     * containment on the description in the actual description. The check is to
     * be done ignoring case.
     *
     * @param description to be matched
     * @return the list of products matching.
     */
    default List<Product> getProductByDescription( String description ) {
        String sw = description.toLowerCase();
        return getAll().stream()
                .filter( p -> p.getDescription().toLowerCase().contains( sw ) )
                .collect( Collectors.toList() );
    }

    /**
     * Get the next id for a product mapped by this mapper.
     *
     * @return The next id.
     */
    long nextProductId();

    /**
     * Creates a classified product based on age. Returns and object of the
     * proper type. Booze if age check required with age ==18.
     *
     * @param id for the new product
     * @param minAge
     * @return an fitting product
     */
    static Product classifyProduct( long id, int minAge ) {
        Product prod;
        if ( minAge == 0 ) {
            prod = new Product( id );

        } else {
            prod = new Booze( id );
        }
        return prod;
    }

}
