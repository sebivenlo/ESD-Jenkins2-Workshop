package webshop.business;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import webshop.persistence.inmemory.IMCart;
import static webshop.entities.TestProducts.*;

/**
 * Test bonus calculator. Setup provides a cart with two products with price
 * excluding VAT of 800 and 3 products of HIGH Vat and 2 products of lowVat.
 *
 * @author hom
 */
public class NoVatOnFilmsTest {

    IMCart cart;

    @Before
    public void setUp() {
        cart = new IMCart();
        cart.merge( diamonds, 3 );
        cart.merge( beverage, 2 );
    }

    //<editor-fold defaultstate="expanded" desc="T04_A; __STUDENT_ID__ ;WEIGHT 1">
    /**
     * Test of getReduction method, of class NoVatOnFilms.
     */
    @Test
    public void testGetReduction() {
        System.out.println( "getSalesPrice no vat on films" );
        //Start Solution::replacewith:://TODO T04_A reduction on Film
        PriceReductionCalculator instance = new NoVatOnFilms();
        long expResult = 3 * diamonds.getVat();
        long result = instance.getReduction( cart );
        assertEquals( expResult, result );
        //End Solution::replacewith::
    }
    //</editor-fold>
}
