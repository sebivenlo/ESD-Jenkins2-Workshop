package webshop.persistence.pgdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import webshop.business.Booze;
import webshop.business.Product;
import webshop.business.ProductType;
import webshop.business.VATLevel;
import webshop.persistence.mappers.ProductMapper;

/**
 *
 * @author hom
 */
public class PGDBProductMapper implements
        ProductMapper {

    private Map<Long, Product> products;

    /**
     * Get all Products from the product table.
     *
     * @return list of products
     */
    @Override
    public List<Product> getAll() {
        validate();
        List<Product> list = new ArrayList<>();
        list.addAll( products.values() );
        return list;
    }

    private void validate() {
        if ( null == products ) {
            products = new HashMap<>();
            String query = "select * from products where ? order by product_id";
            try ( QueryHelper qh = new QueryHelper();
                    ResultSetWrapper rs = qh.doSelectWrapped( query, true ); ) {
                while ( rs.next() ) {
                    Product p = rsToObject( rs );
                    System.out.println( " found p = " + p );
                    products.put( p.getId(), p );
                }
            }
        }
    }

    /**
     * Factory method to create a Product from result set attributes.
     *
     * @param rs result set to use
     * @return a product.
     */
    static Product rsToObject( ResultSetWrapper rs ) {
        long id = nextProductId( rs );
        String description = rs.getString( "description" );
        int price = rs.getInt( "price" );
        VATLevel vl = VATLevel.shortStringToVatLevel( rs.getString(
                "vat_level" ) );
        short min_age = rs.getShort( "min_age" );
        ProductType type = ProductType.valueOf( rs.getString( "product_type" ) );
        Product product = ProductMapper.classifyProduct( id, min_age );
        product.setDescription( description );
        product.setPriceExclVAT( price );
        product.setVatLevel( vl );
        product.setProductType( type );
        return product;

    }

    public static long nextProductId( ResultSetWrapper rs ) {
        long id = rs.getInt( "product_id" );
        return id;
    }

    @Override
    public Product getProductById( long id ) {
        validate();
        Product prod = products.get( id );
        if ( null != prod ) {
            return prod;
        }
        String query = "select * from products where product_id =?";
        prod = getProductByAttributes( query, id );
        products.putIfAbsent( id, prod );
        return prod;
    }

    /**
     * Get the first product that matches the query and args. Through its
     * implementation, the result of this method depends on the ordering in the
     * result set, typically determined by the query string.
     *
     * @param query the query
     * @param args to the query
     * @return first object from result set.
     */
    private Product getProductByAttributes( String query, Object... args ) {
        Product p = null;
        try ( QueryHelper qh = new QueryHelper(); ) {
            ResultSetWrapper rs = qh.doSelectWrapped( query, args );
            if ( rs.next() ) {
                p = rsToObject( rs );
                products.put( p.getId(), p );
            }
        }
        return p;
    }

    /**
     * Find first product matching attributes.
     *
     * @param description of product
     * @param price of product
     * @param vat VATLevel of product
     * @return the product or null if not available.
     */
    @Override
    public Product getProductByDescriptionPriceVat( String description,
            long price,
            VATLevel vat ) {
        validate();
        Product result = products.values()
                .stream()
                .filter(
                        p -> p.getDescription().equals( description )
                        && p.getPriceExclVAT() == price
                        && p.getVatLevel().equals( vat ) )
                .findFirst()
                .orElse( null );
        if ( result != null ) {
            return result;
        }
        return getProductByAttributes(
                "select * from products where description=? and"
                + " price = ? and vat_level = ?",
                description, price, vat.dbName() );
    }

    @Override
    public long nextProductId() {
        return new QueryHelper().getNextValue( "products_product_id_seq" );
    }
}
