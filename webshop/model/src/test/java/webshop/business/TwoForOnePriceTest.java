package webshop.business;

import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import static webshop.business.ProductType.BD;
import static webshop.business.ProductType.BEVERAGE;
import static webshop.business.ProductType.FOOD;
import static webshop.business.TestProducts.*;

/**
 * Exercise in using Mockito to test Invoice.PriceReductionCalculator without
 * using a real ProductContainer. implementation In
 *
 * @author hom
 */
@RunWith( MockitoJUnitRunner.class )
public class TwoForOnePriceTest {

    List<ProductQuantity> productQuantities;
    // will be mocked.
    ProductContainer cart;

    PriceReductionCalculator calculator;

    //<editor-fold defaultstate="expanded" desc="T02_A; __STUDENT_ID__ ;WEIGHT 1">
    /**
     * Set up a mock for the cart and load it with a list of given products and
     * spy on the reduction calculator. Set up the mock to return a list product
     * quantities having 1 kingsman, 6 (even number) beer and 9 (odd number) of
     * crisps. Set up a spy on a TwoForOnPrice("Smiths") price reduction
     * calculator, to be able to verify that the method is called. Note that
     * only the crisps should benefit from this bonus, because the
     * TwoForOnePrice is parameterised with that product description.
     *
     */
    @Before
    public void setUp() {
        //Start Solution::replacewith:://TODO T02_A set up mock cart with return values.
        calculator = Mockito.spy( new TwoForOnePrice( "Lay's Crisps" ) );
        cart = Mockito.mock( ProductContainer.class );
        productQuantities = new ArrayList<>();
        productQuantities.add( new ProductQuantity( kingsman, 1 ) );
        productQuantities.add( new ProductQuantity( beer, 6 ) );
        productQuantities.add( new ProductQuantity( crisps, 9 ) );
        Mockito.when( cart.getContents() ).thenReturn( productQuantities );

        //End Solution::replacewith::
    }
    //</editor-fold>

    @After
    public void tearDown() {
    }

    /**
     * Test of getReduction method effect by creating an invoice with a mock
     * product container (cart), of class TwoForOnePrice.
     *
     * <b>Test plan:</b><br/>
     * <i>setup</i><br/>
     * Compute the consumer price of all goods without price reduction and make
     * the mock return this when called for getValueIncludingVat(). Compute the
     * reduction, which should be half (integer calculation) of the crisps price
     * and remember it for the verify phase.<br/>
     * Then create a SUT (Invoice in this case), give it the spied upon
     * calculator<br/>
     *
     * <i>execution</i><br/>
 invoke the getFinalPrice on the sut.
 <br/>
     * <i>verify phase</i><br/>
     * Ensure that cart.getContents is at least called 2 times. Ensure that
     * calculator.getReduction(cart) is called. Assert that the outcome is the
     * expected outcome.
     */
    @Test
    public void testGetSalesPrice() {
        System.out.println( "Two for one Mock test getSalesPrice" );
        long beerPrice = beer.getConsumerPrice();
        long crispsPrice = crisps.getConsumerPrice();
        long consumerPrice = kingsman.getConsumerPrice() + 6 * beerPrice + 9
                * crispsPrice;

        long reduction = 0 * beerPrice + 4 * crispsPrice;

        // create sut
        Invoice sut = new Invoice().setCart( cart );
        sut.setPriceReductionCalculator( calculator );
        long expResult = consumerPrice - reduction;

        // invoke method
        long actual = sut.getFinalPrice();

        // verify method invocations on mock
        Mockito.verify( cart, times( 2 ) ).getContents();
        Mockito.verify( calculator ).getReduction( cart );

        // verify outcome of computation.
        assertEquals( "reduction calculation went wrong", expResult, actual );
    }
}
