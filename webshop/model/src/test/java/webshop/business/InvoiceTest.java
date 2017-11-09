package webshop.business;

import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import webshop.persistence.inmemory.IMCart;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static webshop.business.TestProducts.*;

/**
 * Test class of the Invoice. The setup initialises a cart with one DVD and 2
 * drinks. Hint: use the supplied setup.
 *
 * @author hom
 */
public class InvoiceTest {

    IMCart cart;
    IMCart emptyCart = new IMCart();

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        cart = new IMCart();
        cart.merge( diamonds, 1 );
        cart.merge( beverage, 2 );
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test of getNumber method, of class Invoice.
     */
    @Test
    public void testSetGetNumber() {
        System.out.println( "getNumber" );
        Invoice instance = new Invoice().setCart( new IMCart() );
        long expResult = 3;
        instance.setNumber( 3 );
        long result = instance.getNumber();
        assertEquals( expResult, result );
    }

    /**
     * Test of getFinalPrice method, of class Invoice. Test both default and
     * 50% off. Check for coverage too.
     */
    @Test
    public void testSetGetSpecialPrice() {
        System.out.println( "getSpecialPrice" );
        Invoice invoice = new Invoice().setCart( cart );
        // try a trick (for coverage)
        invoice.setPriceReductionCalculator( null );
        long expResult = invoice.getTotalPriceIncludingVAT();
        System.out.println( "total price = " + expResult );
        long result = invoice.getFinalPrice();

        assertEquals( expResult, result );
        PriceReductionCalculator spc = new PriceReductionCalculator() {

            @Override
            public long getReduction( ProductContainer pc ) {
                return pc.getValueIncludingVat() / 2; // real rip off
            }
        };

        invoice.setPriceReductionCalculator( spc );
        expResult /= 2;
        result = invoice.getFinalPrice();
        assertEquals( expResult, result );
    }

    /**
     * Test Invoice.getVatValue(VATLevel). Implementation should return correct
     * sum of vat for each level. If there are no products with a certain
     * VATLevel in the cart, 0 is expected as result.
     */
    @Test
    public void testGetVATValue() {
        int qtyHigh = 3;
        int qtyLow = 4;
        int qtyNone = 5;
        long expectedHigh
                = diamonds.getVatLevel().computeVAT( qtyHigh
                        * diamonds.getPriceExclVAT() );
        long expectedLow = beverage.getVatLevel().computeVAT( qtyLow
                * beverage.getPriceExclVAT() );
        long expectedNone = 0;
        cart = new IMCart();
        cart.merge( beverage, qtyLow );
        cart.merge( diamonds, qtyHigh );
        cart.merge( noVATProduct, qtyNone );
        Invoice i = new Invoice().setCart( cart );
        System.out.println( "cart = " + cart );
        assertEquals( "Tax office1 ", expectedHigh,
                i.getVATValue( VATLevel.HIGH ) );
        assertEquals( "Tax office2 ", expectedLow, i.getVATValue( VATLevel.LOW ) );
        assertEquals( "Tax office3 ", expectedNone,
                i.getVATValue( VATLevel.NONE ) );

    }

    /**
     * Test of setNumber method, of class Invoice.
     */
    @Test
    public void testSetNumber() {
        System.out.println( "setNumber" );
        int number = 2;
        Invoice invoice = new Invoice().setCart( cart );
        invoice.setNumber( number );
        assertEquals( "setter getter test", number, invoice.getNumber() );

    }

    //<editor-fold defaultstate="expanded" desc="T06_A; __STUDENT_ID__ ;WEIGHT 1">
    /**
     * Test of mapCartToLines method, of class Invoice.
     * <ol>
     * <li>Check if the correct number of lines is returned.</li>
     * <li>Test if the proper products and</li>
     * <li>quantities are specified.</li>
     * </ol>
     * Note that the order of the lines is determined by the insertion order of
     * the product in the cart. You can rely on this fact.<br/>
     *
     * The setup method of the class adds 1 high vat product and 2 low vat
     * products.
     */
    @Test
    public void testMapCartToLines() {

        System.out.println( "getLines" );
        //Start Solution::replacewith:://TODO T06_A test mapCartToLines
        Invoice invoice = new Invoice().setCart( cart );
        List<InvoiceLine> lines = invoice.getLines();
        assertEquals( "default is 1 line", 2, lines.size() );

        assertEquals( diamonds, lines.get( 0 ).getProduct() );
        assertEquals( "high vat qty", 1, lines.get( 0 ).getQuantity() );

        assertEquals( beverage, lines.get( 1 ).getProduct() );
        assertEquals( "low vat qty", 2, lines.get( 1 ).getQuantity() );
        //End Solution::replacewith::fail("test not implemented");

    }
    //</editor-fold>

    /**
     * Test of getTotalPriceIncludingVAT method, of class Invoice.
     */
    @Test
    public void testGetTotalPriceIncludingVAT() {

        System.out.println( "getTotalPriceIncludingVAT" );
        Invoice invoice = new Invoice().setCart( cart );
        long lowVATUnit = beverage.getVatLevel().consumerPrice( beverage
                .getPriceExclVAT() );
        long highVATUnit = diamonds.getVatLevel().consumerPrice( diamonds
                .getPriceExclVAT() );

        long expResult = 1 * highVATUnit + 2 * lowVATUnit;
        long result = invoice.getTotalPriceIncludingVAT();
        assertEquals( expResult, result );

    }

    //<editor-fold defaultstate="expanded" desc="T04_A; __STUDENT_ID__ ;WEIGHT 1">
    /**
     * Test of getTotalPriceExcludingVAT method, of class Invoice. Test with
     * empty cart (see setup) . Then test with filled cart. Compute the prices
     * from the product and do not use numeric (self computed) constants here.
     */
    @Test
    public void testGetTotalPriceExcludingVAT() {
        System.out.println( "getTotalPriceExcludingVAT" );
        //Start Solution::replacewith:://TODO T04_A testGetTotalPriceExcludingVAT
        Invoice invoice = new Invoice().setCart( cart );
        long expResult = 1 * diamonds.getPriceExclVAT()
                + 2 * beverage.getPriceExclVAT();
        long result = invoice.getTotalPriceExcludingVAT();
        assertEquals( expResult, result );
        //End Solution::replacewith::fail("test getTotalPriceExcludingVAT not implemented");
    }
    //</editor-fold>

    /**
     * Test of getFinalPrice method, of class Invoice. Check for the case of
     * no special price, null calculator and with a special price, 10% off.
     */
    @Test
    public void testGetSpecialPrice() {
        System.out.println( "getSpecialPrice" );
        Invoice invoice = new Invoice().setCart( cart );
        final long expResult = invoice.getTotalPriceIncludingVAT();
        long result = invoice.getFinalPrice();
        assertEquals( expResult, result );

        invoice.setPriceReductionCalculator( null );
        result = invoice.getFinalPrice();
        assertEquals( expResult, result );

        invoice.setPriceReductionCalculator( new BonusDiscount() );
        long expResult2 = expResult - ( expResult / 10 );
        result = invoice.getFinalPrice();
        assertEquals( "client will complain", expResult2, result );
    }

    /**
     * Test of setPriceReductionCalculator method, of class Invoice.
     */
    @Test
    public void testSetGetPriceReductionCalculator() {
        System.out.println( "setPriceReductionCalculator" );
        PriceReductionCalculator cp = new BonusDiscount();
        Invoice instance = new Invoice().setCart( cart );
        instance.setPriceReductionCalculator( cp );
        assertSame( cp, instance.getCalculator() );
    }

    /**
     * Test if the invoice does a proper age check.
     */
    @Test
    public void testAgeCheck() {
        System.out.println( "testAgeCheck = " );
        Invoice invoice = new Invoice().setCart( cart );
        // default cart has only simplem beverage
        assertFalse( "beverage check ", invoice.isAgeCheckRequired() );
        cart.merge( beer, 6 );
        assertTrue( "beer check ", invoice.isAgeCheckRequired() );
        assertFalse( "null check", invoice.isAgeOk( invoice
                .getCustomerBirthDay() ) );
        LocalDate justTooYoung = LocalDate.now().minus( Period.ofYears( 17 )
                .minusDays( 1 ) );
        invoice.setCustomerBirthDay( justTooYoung );
        assertFalse( "too young", invoice
                .isAgeOk( invoice.getCustomerBirthDay() ) );
        LocalDate authorsDay = LocalDate.of( 1955, Month.MARCH, 18 );
        System.out.println( "authorsDay = " + authorsDay );
        assertTrue( "a beer a day keeps the doctor away", invoice.isAgeOk(
                authorsDay ) );
    }

    @Test
    public void testEquals() {
        Invoice inv1 = new Invoice().setCart( cart );
        System.out.println( "cart = " + cart );
        ProductContainer cart2 = new IMCart();
        cart2.merge( diamonds, 1 );
        cart2.merge( beverage, 2 );
        Invoice inv2 = new Invoice().setCart( cart2 );
        assertEquals( "same cart contents", inv2, inv1 );

    }
}
