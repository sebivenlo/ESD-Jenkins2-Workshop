package webshop.persistence.inmemory;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import webshop.business.Booze;
import webshop.business.Product;
import webshop.business.ProductType;
import webshop.business.VATLevel;
import webshop.persistence.mappers.ProductMapper;
import static webshop.utils.Utils.readFile;

/**
 * This Mapper is read only.
 *
 * @author hom
 */
public enum IMProductMapper implements ProductMapper {

    INSTANCE;
    private long nextProductId = 0;

    public static final String DEFAULT_PRODUCT_FILE = "resources/products.csv";
    private final List<Product> products;

    /**
     * Create a product list from file. This is the public constructor.
     */
    private IMProductMapper() {
        this( DEFAULT_PRODUCT_FILE );
    }

    /**
     * Private constructor to be able to force a load error and cover the
     * exception catching. Purpose: test coverage.
     *
     * @param productsFileName
     */
    IMProductMapper( String productsFileName ) {

        products = new ArrayList<>();
        List<String> allLines = readFile( productsFileName );
        int lineNr = 1;
        for ( String line : allLines ) {
            if ( !line.trim().isEmpty() ) {
                //System.out.println( "product line = " + line );
                String[] attributes = line.trim().split( "\\s*;\\s*" );
                try {
                    // ignored value
                    long id = Long.parseLong( attributes[ 0 ].trim() );
                    String description = attributes[ 1 ].trim();
                    long price = Long.parseLong( attributes[ 2 ].trim() );
                    VATLevel vl = VATLevel.shortStringToVatLevel(
                            attributes[ 3 ].trim() );
                    ProductType t = ProductType
                            .valueOf( attributes[ 4 ].trim() );
                    int minAge = Integer.parseInt( attributes[ 5 ].trim() );
                    Product product = null;
                    id = nextProductId();
                    product = ProductMapper.classifyProduct( id, minAge );
                    product.setDescription( description );
                    product.setPriceExclVAT( price );
                    product.setVatLevel( vl );
                    product.setProductType( t );
                    products.add( product );
                } catch ( RuntimeException rte ) {
                    Logger.getLogger( IMProductMapper.class.getName() ).log(
                            Level.WARNING,
                            "something wrong with line " + lineNr + ": \""
                            + line
                            + "\"", rte );
                }
                lineNr++;
            }
        }
    }

    /**
     * Returns a copy of the list, so no one can change list, keeping it read
     * only.
     *
     * @return the products in a list.
     */
    @Override
    public List<Product> getAll() {
        List<Product> result = new ArrayList<>();
        result.addAll( products );
        return result;
    }

    /**
     * Iterates over list and returns matching product else null
     *
     * @param description of the product
     * @param price of the product
     * @param vat of the product
     * @return the product
     */
    @Override
    public Product getProductByDescriptionPriceVat( String description,
            long price, VATLevel vat ) {
        Product product = null;
        for ( Product p : products ) {
            if ( p.getDescription().equals( description )
                    && p.getPriceExclVAT() == price
                    && p.getVatLevel() == vat ) {
                product = p;
                break;
            }
        }
        return product;
    }

    @Override
    public Product getProductById( long id ) {
        Product product = null;
        for ( Product p : products ) {
            if ( p.getId() == id ) {
                product = p;
                break;
            }
        }
        return product;

    }

    /**
     * Print all products t a Printstream.
     *
     * @param out PrintStream
     */
    public void printProducts( PrintStream out ) {
        getAll().forEach( ( x ) -> out.println( x ) );
    }

    @Override
    public long nextProductId() {
        return ++nextProductId;
    }

    long lastProductId() {
        return nextProductId;
    }
}
