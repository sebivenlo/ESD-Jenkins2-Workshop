package webshop.business;

import webshop.entities.Product;
import java.sql.Date;
import java.sql.Timestamp;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
import static webshop.entities.TestProducts.*;

/**
 * Use the test data please. They are there to make your life less miserable.
 *
 * @author hom
 */
public class ProductQuantityTest {

    public ProductQuantityTest() {
    }

    /**
     * Test of getProduct method, of class ProductQuantity.
     */
    @Test
    public void testGetProduct() {
        System.out.println( "getProduct" );
        ProductQuantity instance = new ProductQuantity( porsche, 1, 0 );
        Product expResult = porsche;
        Product result = instance.getProduct();
        assertSame( expResult, result );
    }

    /**
     * Test of getQuantity method, of class ProductQuantity.
     */
    @Test
    public void testGetQuantity() {
        System.out.println( "getQuantity" );
        ProductQuantity instance = new ProductQuantity( book, 1, 0 );
        int expResult = 1;
        int result = instance.getQuantity();
        assertEquals( expResult, result );
    }

    /**
     * Test of setQuantity method, of class ProductQuantity.
     */
    @Test
    public void testSetQuantity() {
        System.out.println( "setQuantity" );
        int quantity = 2;
        ProductQuantity instance = new ProductQuantity( book, 2, 0 );
        instance.setQuantity( quantity );
        assertEquals( "two book please", 2, instance.getQuantity() );
    }

    /**
     * Test of setQuantity method, of class ProductQuantity.
     */
    @Test( expected = NegativeQuantityException.class )
    public void testSetQuantity_Exception() {
        System.out.println( "setQuantity  exception" );
        ProductQuantity instance = new ProductQuantity( book, 1, 0 );
        instance.setQuantity( -1 );
    }

    /**
     * Test of changeQuantity method, of class ProductQuantity. Test positive.
     */
    @Test
    public void testChangeQuantity() {
        System.out.println( "changeQuantity" );
        int quantityDelta = -1;
        ProductQuantity instance = new ProductQuantity( book, 2, 0 );
        int expResult = 1;
        int result = instance.changeQuantity( quantityDelta );
        assertEquals( expResult, result );
    }

    /**
     * Test of changeQuantity method, of class ProductQuantity. Test positive.
     */
    @Test( expected = NegativeQuantityException.class )
    public void testChangeQuantity_Exception() {
        System.out.println( "changeQuantity" );
        int quantityDelta = -3;
        ProductQuantity instance = new ProductQuantity( book, 2, 0 );
        instance.changeQuantity( quantityDelta );
    }

    /**
     * Test of hashCode method, of class ProductQuantity.
     */
    @Test
    public void testHashCode() {
        System.out.println( "hashCode" );
        ProductQuantity instance = new ProductQuantity( book, 2, 0 );
        int expResult = 0;
        int result = instance.hashCode();
        assertNotSame( expResult, result );
    }

    /**
     * Test of equals method, of class ProductQuantity.
     */
    @Test
    @SuppressWarnings( { "IncompatibleEquals", "ObjectEqualsNull" } )
    public void testEquals() {
        System.out.println( "equals" );
        ProductQuantity instance = new ProductQuantity( book, 2, 0 );
        ProductQuantity clone = new ProductQuantity( instance );
        assertFalse( "equals null", instance.equals( null ) );
        assertFalse( "equals other type", instance.equals( "Hello" ) );
        assertFalse( "2 books is not a porsche", instance.equals(
                new ProductQuantity( porsche, 1, 0 ) ) );
        assertTrue( instance.equals( clone ) );
        // also tets the equals usecase in assert
        assertEquals( instance, clone );
        assertTrue( "self", instance.equals( instance ) );
        clone.changeQuantity( 1 ); // more books please
        assertFalse( instance.equals( clone ) );
    }

    /**
     * Test of toString method, of class ProductQuantity.
     */
    @Test
    public void testToString() {
        System.out.println( "toString" );
        ProductQuantity instance = new ProductQuantity( porsche, 1, 0 );
        assertNotNull( instance.toString() );
    }

    /**
     * Test of getDate_creation method, of class ProductQuantity.
     */
    @Test
    public void testSetGetDate_creation() {
        System.out.println( "setGetDate_creation" );
        ProductQuantity instance = new ProductQuantity( porsche, 1 );
        Timestamp now = new Timestamp( System.currentTimeMillis() );
        instance.setDateCreation( now );
        Timestamp result = instance.getDateCreation();
        assertEquals( now, result );
    }

    /**
     * Test of getDateUpdate method, of class ProductQuantity.
     */
    @Test
    public void testSetGetDate_update() {
        System.out.println( "getData_update" );
        ProductQuantity instance = new ProductQuantity( porsche, 6 );
        Timestamp now = new Timestamp( System.currentTimeMillis() );
        instance.setDateUpdate( now );
        Timestamp result = instance.getDateUpdate();
        assertEquals( now, result );
    }

    /**
     * Test of getOwner method, of class ProductQuantity.
     */
    @Test
    public void testSetGetOwner() {
        System.out.println( "getOwner" );
        long expResult = 12L;
        ProductQuantity instance = new ProductQuantity( porsche, 1, expResult );
        long result = instance.getOwner();
        assertEquals( expResult, result );

        expResult = 13l;
        instance.setOwner( expResult );
        result = instance.getOwner();
        assertEquals( expResult, result );
    }

}
