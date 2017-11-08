package webshop.business;

import webshop.entities.TestProducts;
import webshop.entities.Product;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import webshop.persistence.inmemory.IMCart;

/**
 * Test bonus calculator. Setup provides a cart with two products with total
 * price of 800.
 *
 * @author hom
 */
public class BonusDiscountTest {

    IMCart cart;
    Product diamonds = TestProducts.diamonds;

    Product lowVatProduct = TestProducts.beer;

    @Before
    public void setUp() {
        cart = new IMCart();
        cart.merge(diamonds, 1 );
        cart.merge( lowVatProduct, 2 );
    }

    /**
     * Test of getReduction method, of class BonusDiscount. (10% off)
     */
    @Test
    public void testGetReduction() {
        System.out.println( "getSalesPrice" );
        BonusDiscount instance = new BonusDiscount();

        long expResult = ( 1 * diamonds.getConsumerPrice() + 2
                * lowVatProduct.getConsumerPrice() ) / 10;
        long result = instance.getReduction( cart );
        assertEquals( expResult, result );
    }
}
