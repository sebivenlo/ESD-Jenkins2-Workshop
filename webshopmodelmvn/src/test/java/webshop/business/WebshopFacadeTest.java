package webshop.business;

import webshop.entities.Invoice;
import webshop.entities.Product;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static webshop.business.ProductType.BD;
import static webshop.business.ProductType.BOOK;
import webshop.persistence.inmemory.IMCart;
import webshop.persistence.AbstractWebshopFactory;
import webshop.persistence.WebshopFactoryConfigurator;
import static webshop.entities.TestProducts.*;
/**
 *
 * @author hvd
 */
public class WebshopFacadeTest {

    WebshopFacade session;
    AbstractWebshopFactory factory
            = WebshopFactoryConfigurator.CONFIGURATOR.getFactory();

    public WebshopFacadeTest() {
    }

    @Before
    public void setUp() throws WebshopException {
        session = new WebshopFacade( factory );
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getSessionID method, of class WebshopFacade.
     */
    @Test
    public void testGetSessionID() throws WebshopException {
        System.out.println( "getSessionID" );
        int expResult = session.getSessionID() + 1;
        session = new WebshopFacade( factory );
        int result = session.getSessionID();
        assertEquals( expResult, result );
    }

    /**
     * Test of addToCart method, of class WebshopFacade.
     *
     * @throws WebshopException when inventory will not allow it.
     */
    @Test
    public void testAddToCart_Product() throws WebshopException {

        System.out.println( "addToCart" );

        ProductContainer inventory = session.getInventory();

        inventory.merge( book, 1 );
        session.addToCart( book );

        int expectedInInventory = 0;
        int expectedInCart = 1;

        assertEquals( "No (zero) interesting book in inventory",
                expectedInInventory, session.getInventory().count(
                        book ) );
        assertEquals( "One interesting book in cart", expectedInCart,
                session.getCart().count( book ) );

    }

    /**
     * Test of addToCart method, of class WebshopFacade. Remember to test all
     * relevant scenarios.
     *
     * @throws WebshopException
     */
    @Test
    public void testAddToCart_Product_int() throws WebshopException {

        System.out.println( "addToCart_product_int" );

        ProductContainer inventory = session.getInventory();

        // Regular use case
        inventory.merge( book, 5 );
        session.addToCart( book, 5 );

        int expectedInInventory = 0;
        int expectedInCart = 5;

        assertEquals( "No (zero) interesting book in inventory",
                expectedInInventory, session.getInventory().count(
                        book ) );
        assertEquals( "Five interesting books in cart", expectedInCart,
                session.getCart().count( book ) );

        // Out-of-stock use case
        try {
            session.addToCart( book );
            fail( "WebshopException with NegativeQuantityException expected" );
        } catch ( WebshopException wse ) {
            assertTrue( "NegativeQuantityException expected",
                    wse.getCause() instanceof NegativeQuantityException );
        }

        // No such product use case
        try {
            session.addToCart( kingsman );
            fail( "WebshopException with NoSuchProductException expected" );
        } catch ( WebshopException wse ) {
            assertTrue( "NoSuchProductException expected",
                    wse.getCause() instanceof NoSuchProductException );
        }

    }

    /**
     * Test of removeFromCart method, of class WebshopFacade.
     *
     * @throws WebshopException
     */
    @Test
    public void testRemoveFromCart_Product_int() throws WebshopException {

        System.out.println( "removeFromCart_product_int" );

        ProductContainer inventory = session.getInventory();

        // Regular use case
        inventory.merge( book, 5 );
        session.addToCart( book, 5 );
        session.removeFromCart( book, 2 );

        int expectedInInventory = 2;
        int expectedInCart = 3;

        assertEquals( "Two interesting books in inventory", expectedInInventory,
                session.getInventory().count( book ) );
        assertEquals( "Three interesting books in cart", expectedInCart,
                session.getCart().count( book ) );

    }

    /**
     * Test of removeFromCart method, of class WebshopFacade.
     *
     * @throws WebshopException
     */
    @Test
    public void testRemoveFromCart_Product() throws WebshopException {

        System.out.println( "removeFromCart_product" );

        ProductContainer inventory = session.getInventory();

        // Regular use case
        inventory.merge( book, 5 );
        session.addToCart( book, 5 );
        session.removeFromCart( book );

        int expectedInInventory = 5;

        assertEquals( "Five interesting books in inventory", expectedInInventory,
                session.getInventory().count( book ) );

        try {
            session.getCart().count( book );
            fail( "Interesting book should not be in cart at all" );
        } catch ( NoSuchProductException nspe ) {

        }

    }

    /**
     * Test of getInventory method, of class WebshopFacade. Test non null and
     * result if of type IMInventory.
     */
    @Test
    public void testGetInventory() {
        System.out.println( "getInventory" );
        ProductContainer result = session.getInventory();
        assertNotNull( result );
        assertTrue( "inventory must be of proper type",
                result instanceof webshop.persistence.inmemory.IMInventory );
    }

    /**
     * Test of getInvoice method, of class WebshopFacade. Test non null and of
     * proper type (Invoice).
     */
    @Test
    public void testGetInvoice() {
        System.out.println( "getInvoice" );
        Invoice result = session.getInvoice();
        assertNotNull( result );
        assertTrue( "not and invoice", result instanceof Invoice );
    }

    /**
     * Test of getCart method, of class WebshopFacade. Test non null and of
     * proper type (IMCart).
     */
    @Test
    public void testGetCart() {
        System.out.println( "getCart" );
        ProductContainer result = session.getCart();
        assertNotNull( result );
        assertTrue( "not a cart", result instanceof IMCart );
    }

    /**
     * Test of activateDiscountCode method, of class WebshopFacade. Test if
     * default price strategy is the default indeed and of the proper type
     * (class). Test if correct bonus string activates the correct
     * implementation, also here check for proper type (class). For the test, no
     * computational check needs to be done.
     */
    @Test
    public void testActivateDiscountCode() {

        System.out.println( "activateDiscountCode" );
        String discountCode = "";
        session.setDiscountCode( discountCode );
        session.activateDiscountCode();
        PriceReductionCalculator calc = session.getInvoice().getCalculator();
        assertNotNull( calc );
        assertTrue( "Default expected",
                calc instanceof Invoice.DefaultReduction );

        discountCode = WebshopFacade.BONUS;
        session.setDiscountCode( discountCode );
        session.activateDiscountCode();
        calc = session.getInvoice().getCalculator();
        assertNotNull( calc );
        assertTrue( "Bonus expected", calc instanceof BonusDiscount );
    }

    /**
     * Test of submitOrder method, of class WebshopFacade. Test the
     * postcondition, cart empty (product count 0), inventory same as before
     * submit order.
     *
     */
    @Test
    public void testSubmitOrder() {
        int inventoryItemCount = session.getInventory().itemCount();
        System.out.println( "submitOrder" );
        session.submitOrder();
        ProductContainer cart = session.getCart();
        assertEquals( 0, cart.itemCount() );
        assertEquals( inventoryItemCount, session.getInventory().itemCount() );
    }

    /**
     * Test of getInvoiceData method, of class WebshopFacade. Simply test for
     * non empty string. Test with adding one product to make lines nonempty.
     *
     * Test is mainly to server coverage.
     *
     * @throws webshop.business.WebshopException
     */
    @Test
    public void testGetInvoiceData() throws WebshopException {
        System.out.println( "getInvoiceData" );
        String result = session.getInvoiceData();
        assertFalse( "invoice is empty string", result.isEmpty() );

        session.getInventory().merge( book, 2 );
        session.addToCart( book, 1 );
        System.out.println( "getInvoiceData" );
        result = session.getInvoiceData();
        assertFalse( "invoice is empty string", result.isEmpty() );
        System.out.println( "result = \n" + result );
    }

    /**
     * Test abandon cart. Put something in the inventory. Then transfer it to
     * cart, then abandon cart and see if original qty is back in inventory.
     *
     * @throws webshop.business.WebshopException
     */
    @Test
    public void testAbandonCart() throws WebshopException {

        int initialQty = 10;
        session.getInventory().merge( book, initialQty );
        session.addToCart( book, 5 );
        session.abandonCart();
        assertEquals( "checks and balances", initialQty,
                session.getInventory().count( book ) );

    }

}
