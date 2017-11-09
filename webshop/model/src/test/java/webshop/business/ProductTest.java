package webshop.business;

import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
import static webshop.business.ProductType.BD;
import static webshop.business.ProductType.BOOK;
import static webshop.business.ProductType.DVD;
import static webshop.business.ProductType.FOOD;
import static webshop.business.ProductType.LUXURY;
import static webshop.business.VATLevel.HIGH;
import static webshop.business.VATLevel.LOW;

/**
 * Simple setter getter test in one method. For coverage only.
 *
 * @author hom
 */
public class ProductTest {

    /**
     * Test of getId method, of class Product.
     */
    @Test
    public void testSettersAndGetters() {
        System.out.println( "setters and getters" );
        String name = "The Empire Strikes Back";

        long priceExclVAT = 2000l;
        VATLevel vat = HIGH;
        long id = 23;
        Product product
                = new Product( id, name, priceExclVAT, VATLevel.HIGH, BD );
        assertEquals( id, product.getId() );
        assertEquals( priceExclVAT, product.getPriceExclVAT() );
        assertEquals( vat, product.getVatLevel() );
        assertEquals( name, product.getDescription() );
        assertEquals( priceExclVAT + vat.computeVAT( priceExclVAT ), product
                .getConsumerPrice() );
        name = "Lord of The Rings";
        vat = LOW;
        priceExclVAT = 1800;
        product = new Product( id, name, priceExclVAT, VATLevel.LOW, FOOD );
        assertEquals( priceExclVAT, product.getPriceExclVAT() );
        assertEquals( priceExclVAT + vat.computeVAT( priceExclVAT ), product
                .getConsumerPrice() );
        assertEquals( vat, product.getVatLevel() );
        assertEquals( name, product.getDescription() );
    }

    @Test
    @SuppressWarnings( { "ObjectEqualsNull", "IncompatibleEquals" } )
    public void testEquals() {
        Product p1 = new Product( 1, "Test", 2000, VATLevel.HIGH, BD );
        Product p2 = new Product( 1, "Test", 2000, VATLevel.HIGH, BD );
        Product p3 = new Product( 2, "Test", 2000, VATLevel.HIGH, LUXURY );
        Product p4 = new Product( 1, "Test1", 2000, VATLevel.HIGH, BOOK );
        Product p5 = new Product( 1, "Test", 2001, VATLevel.HIGH, BD );
        Product p6 = new Product( 1, "Test", 2001, VATLevel.LOW, FOOD );

        assertTrue( p1.equals( p2 ) );
        assertFalse( p1.equals( p3 ) );
        assertFalse( p1.equals( p4 ) );
        assertFalse( p1.equals( p5 ) );
        assertFalse( p1.equals( p6 ) );
        assertFalse( p1.equals( null ) );
        assertFalse( p1.equals( "Hi" ) );

    }
}
