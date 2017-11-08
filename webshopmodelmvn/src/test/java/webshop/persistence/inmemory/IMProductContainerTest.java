package webshop.persistence.inmemory;

import java.util.List;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import webshop.entities.Product;
import webshop.business.ProductContainer;
import webshop.business.ProductQuantity;
import webshop.business.VATLevel;
import webshop.business.NegativeQuantityException;
import webshop.business.NoSuchProductException;
import static webshop.business.ProductType.BOOK;
import static webshop.business.ProductType.FOOD;
import static webshop.business.ProductType.LUXURY;
import static webshop.entities.TestProducts.*;
import webshop.business.WebshopException;
import webshop.entities.ProductMapper;
import static webshop.persistence.WebshopFactoryConfigurator.*;

/**
 * Test the product container.
 *
 * The tester/student is advised to first fully read all the documentation in
 * this class file. He/she may do so best by generating the javadoc for this
 * project, which will also include the documentation to the test, in this
 * document. The system under test (IMProductContainer) has no grading
 * information, but the correct functioning of that class is part of the grades
 * you can obtain by working test driven.
 *
 * Where you see MAX in the grading documentation, read is as WEIGHT.
 *
 *
 * @author hom
 */
//@Ignore
public class IMProductContainerTest {

    private ProductContainer cart;
//    private Product book, pricelessProduct, porsche;
    private ProductContainer inventory;

    @Before
    public void setUp() throws Exception {

        ProductMapper pm = CONFIGURATOR.getFactory( MEMORY_CONFIG )
                .getProductMapper();

        inventory = new IMInventory( pm );
        cart = new IMCart();
    }

    @After
    public void tearDown() throws Exception {
        cart = null;
    }

    /**
     * Test method for 'shop.IMProductContainer.addProducts(Product, int)'.
     * Tests if:
     * <ul>
     * <li>Null is accepted and no-op</li>
     * <li>an item with qty 0 can be added, to register the product in the
     * container.</li>
     * <li>the correct (same) product is added.</li>
     * <li>The correct quantity is added.</li>
     * <li>Works when container 1 does or 2 does not have the product
     * beforehand.</li>
     * </ul>
     *
     */
    @Test
    public final void testMerge_Product_int() {

        assertNull( "null in, null out", cart.merge( null ) );
        ProductQuantity pq = cart.merge( book, 0 );
        assertSame( "is it the same book", book, pq.getProduct() );
        assertEquals( "is the count correct ", 0, pq.getQuantity() );
        assertEquals( "adding nothing should result in nothing", 0,
                cart.itemCount() );

        // add
        pq = cart.merge( book, 1 );
        assertSame( "is it the same book", book, pq.getProduct() );
        assertEquals( "is the count correct ", 1, pq.getQuantity() );
        assertEquals( " one ", 1, cart.itemCount() );
        assertTrue( "has product", cart.contains( book ) );

        pq = cart.merge( pricelessProduct, 10 );
        assertEquals( " eleven ", 11, cart.itemCount() );

    }

    /**
     * Test if returned ProductQuantity is correct. Test
     * <ul>
     * <li>The correct product is contained in pq.</li>
     * <li>The contained ProductQuantity is a copy, not the same
     * ProductQuantity, but otherwise equals</li>
     * <li>The correct qty is returned in both container, total and pq.</li>
     * </ul>
     *
     * @throws NoSuchProductException when there is no such product in this
     * container.
     * @throws NegativeQuantityException when you try to cheat;-))
     */
    @Test
    public final void testMerge_ProductQuantity() throws
            NegativeQuantityException {

        ProductQuantity pq1 = new ProductQuantity( book, 1 );
        ProductQuantity r1 = cart.merge( pq1 );

        assertTrue( "product is there", cart.contains( book ) );
        assertNotSame( "Container should have created a copy", r1, pq1 );
        assertEquals( "not same but equals", pq1, r1 );
        assertEquals( "total count", 1, cart.itemCount() );
        assertTrue( "and has the product", cart.contains( book ) );

        ProductQuantity pq2 = new ProductQuantity( book, 11 );
        ProductQuantity r2 = cart.merge( book, 11 );
        assertNotSame( "Container should have created a copy", r2, pq2 );
        assertEquals( "not same but equal product", book,
                r2.getProduct() );
        assertEquals( "not same and correct count", 11 + 1, r2.getQuantity() );
        assertTrue( "and has the product", cart.contains( book ) );
        assertEquals( " twelve ", 11 + 1, cart.itemCount() );

    }

    @Test( expected = IllegalArgumentException.class )
    public void testMerg_NegativeWithException() {
        inventory.merge( porsche, -1 );
    }

    /**
     * Test the correct exception when trying negative value.
     * IllegalArgumentException should be thrown.
     */
    @Test
    @SuppressWarnings( "UseSpecificCatch" )
    public final void testTakeNegative() {

        try {
            cart.take( book, -5 );
            fail( "Exception not thrown" );
        } catch ( IllegalArgumentException iae ) {
            assertTrue( "The world is still Okay!", true );
        } catch ( Throwable wrong ) {
            fail( "Wrong exception thrown" );
        }
    }

    /**
     * Test the correct exception when trying to take an unavailable product.
     */
    @Test
    @SuppressWarnings( { "BroadCatchBlock", "TooBroadCatch" } )
    public final void testTakeUnavailableProduct() {
        try {
            cart.take( porsche, 5 );
            fail( "\"One can always hope\"" );
        } catch ( NoSuchProductException ncpe ) {
            // the world is ok.
            assertTrue( "The world is still Okay!", true );
        } catch ( Throwable wrong ) {
            fail( "Wrong exception thrown" );
        }
    }

    /**
     * Test the correct exception when trying to take too many.
     */
    @Test
    public final void testTakeTooMany() {
        try {
            cart.merge( book, 2 );
            cart.take( book, 5 );
            fail( "Exception not thrown" );
        } catch ( NegativeQuantityException nce ) {
            assertTrue( "The world is still Okay!", true );
        } catch ( Throwable wrong ) {
            fail( "Wrong exception thrown" );
        }
    }

    /**
     * Test method for 'shop.IMProductContainer.takeProducts(Product, int)'.
     * Success scenario. Test if takes works, the correct ProductQuantity is
     * returned.
     */
    @Test
    public final void testTakeProduct() throws WebshopException {

        int testAmount = 20;
        ProductQuantity pq = cart.merge( porsche, testAmount ); // feel rich
        assertSame( "Is it real?", porsche, pq.getProduct() );
        assertEquals( testAmount, cart.count( porsche ) );
        assertEquals( testAmount, cart.itemCount() );

        // now  take some out
        int takeAmount = 19;
        pq = cart.take( porsche, takeAmount ); // take some out
        assertSame( "Is it real?", porsche, pq.getProduct() );
        assertEquals( takeAmount, pq.getQuantity() );
        assertEquals( testAmount - takeAmount, cart.count( porsche ) );
        assertEquals( testAmount - takeAmount, cart.itemCount() );
    }

    /**
     * Test of takeAll method, of class IMProductContainer.
     */
    @Test
    public void testTakeAll() throws WebshopException {

        ProductQuantity pq0 = new ProductQuantity( porsche, 2 );
        ProductQuantity pq1 = cart.merge( pq0 );
        ProductQuantity pq2 = cart.takeAll( porsche );
        assertEquals( "zero left", 0, cart.count( porsche ) );
        assertNotSame( "not same but copies step 1.", pq1, pq2 );
        assertEquals( "not same but copies step 2.", pq1, pq2 );
        assertNotSame( "not same but copies step 1.", pq0, pq2 );
        assertEquals( "not same but copies step 2.", pq0, pq2 );

        ProductQuantity pq3 = cart.takeAll( book );
        assertNull( "should have produced null or exception", pq3 );

    }

    /**
     * Test of purge method, of class IMProductContainer.
     */
    @Test
    public void testPurge() throws WebshopException {

        ProductQuantity p0 = new ProductQuantity( porsche, 1 );
        cart.merge( p0 ); // to drive home in
        cart.merge( pricelessProduct, 10 ); // loads of free stuff
        cart.merge( book, 100 ); // good book for all students
        assertEquals( 1 + 10 + 100, cart.itemCount() );
        cart.take( porsche, 1 );
        assertEquals( 0 + 10 + 100, cart.itemCount() );
        ProductQuantity p = cart.find( porsche );
        assertNotNull( p );
        assertTrue( cart.contains( porsche ) );
        assertSame( porsche, p.getProduct() );
        assertEquals( 0, cart.count( porsche ) );
        cart.purge();
        assertFalse( "goner", cart.contains( porsche ) );

    }

    /**
     * Test of drainTo method, of class IMProductContainer.
     */
    @Test
    public void testDrainTo() {

        ProductQuantity p0 = new ProductQuantity( porsche, 1 );
        cart.merge( p0 ); // to drive home in
        cart.merge( pricelessProduct, 10 ); // loads of free stuff
        cart.merge( book, 100 ); // good book for all students

        IMProductContainer cart2 = new IMCart();
        cart.drainTo( cart2 );
        ProductQuantity pq1 = cart2.find( porsche );
        assertEquals( 1, pq1.getQuantity() );
        assertSame( porsche, pq1.getProduct() );

        ProductQuantity pq2 = cart2.find( pricelessProduct );
        assertEquals( 10, pq2.getQuantity() );
        assertSame( pricelessProduct, pq2.getProduct() );

        ProductQuantity pq3 = cart2.find( book );
        assertEquals( 100, pq3.getQuantity() );
        assertSame( book, pq3.getProduct() );

    }

    /**
     * Test of getContents method, of class IMProductContainer. Get the list, go
     * through it to see if it contains the correct things, that is, equal but
     * not same PruductQuantities. Also test if it contains no more or less then
     * expected. The test implementor is advised to use the isPQInlist( List,
     * Product, int ) method provided in this class. Note that simple equals
     * test of the list will not suffice because the list type might differ from
     * the type the tester chooses and still should give correct results.
     */
    @Test
    public void testGetContents() {
        ProductQuantity pq1 = new ProductQuantity( porsche, 1 );
        ProductQuantity pq2 = new ProductQuantity( pricelessProduct, 10 );
        ProductQuantity pq3 = new ProductQuantity( book, 100 );

        cart.merge( pq1 );
        cart.merge( pq2 );
        cart.merge( pq3 );

        assertEquals( 1 + 10 + 100, cart.itemCount() );

        List<ProductQuantity> result = cart.getContents();
        assertEquals( 3, result.size() );
        assertTrue( "not in list", isPQInlist( result, porsche, 1 ) );
    }

    /**
     * Test of find method, of class IMProductContainer.
     */
    @Test
    public void testFind() {
        ProductQuantity firstAttempt = cart.find( porsche );
        assertNull( "have non null ", firstAttempt );
        cart.merge( book, 2 );
        ProductQuantity b = cart.find( book );
        assertSame( book, b.getProduct() );
        assertEquals( 2, b.getQuantity() );
    }

    /**
     * Test of contains method, of class IMProductContainer.
     */
    @Test
    public void testContains() {
        boolean firstAttempt = cart.contains( porsche );
        assertFalse( "should not ", firstAttempt );
        cart.merge( book, 2 );
        boolean b = cart.contains( book );
        assertTrue( b );
    }

    /**
     * Test method for 'shop.IMProductContainer.count(Product)'. Test for
     * success (product contained) and failed (exception because product is not
     * available.
     */
    @Test
    public final void testCount_Product() {
        cart.merge( porsche, 15 ); // must be garage
        assertEquals( " must be garage ", 15, cart.count( porsche ) );

        // exception trigger
        try {
            cart.count( book );
            fail( "should have exception" );
        } catch ( NoSuchProductException nspe ) {
            assertTrue( "die welt is heil", true );
        } catch ( Throwable wrong ) {
            fail( "got wrong exception" );
        }
    }

    /**
     * Test of itemCount method, of class IMProductContainer.
     */
    @Test
    public void testItemCount() {
        assertEquals( 0, cart.itemCount() );
        cart.merge( book, 2 );
        assertEquals( 2, cart.itemCount() );
    }

    /**
     * Helper to verify that a list contains PQ with correct values.
     *
     * @param result list
     * @param product to test
     * @param quantity to test
     * @return true if product contained and with correct quantity, false
     * otherwise.
     */
    boolean isPQInlist( List<ProductQuantity> result, Product product,
            int quantity ) {
        for ( ProductQuantity inResult : result ) {
            if ( product == inResult.getProduct() ) {
                if ( quantity == inResult.getQuantity() ) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * SetGet Owner to satisfy coverage.
     */
    @Test
    public void testSetGetOwner() {
        long owner = 762319;
        IMProductContainer c = new IMProductContainer() {
        };
        c.setOwner( owner );
        assertEquals( owner, c.getOwner() );
    }

    /**
     * Test of transferTo method, of class IMProductContainer. Ensure that
     * operation is transactional. For the in memory container it is sufficient
     * to test if nothing bad happens when the requested quantity does not meet
     * its conditions.
     *
     * @throws java.lang.Exception not expected
     */
    @Test
    public void testTransferTo_Conditions_Ok() throws Exception {
        System.out.println( "transferTo" );
        Product product = book;
        // clean up
        int sourceCount = 200;
        inventory.merge( product, sourceCount );
        int quantity = 10;
        ProductContainer source
                = inventory.transferTo( cart, product, quantity );
        assertEquals( "should return source ", inventory, source );
        assertEquals( "qty in reciver", 10, cart.count( product ) );
        assertEquals( "qty in source", sourceCount - quantity, inventory.count(
                product ) );
        assertSame( product, cart.find( product ).getProduct() );
    }

    /**
     * Test the empty method.
     */
    @Test
    public void testEmpty() {
        Product product = book;
        // clean up
        int sourceCount = 200;
        cart.merge( product, sourceCount );
        cart.empty();
        assertTrue( cart.getContents().isEmpty() );
    }
}
