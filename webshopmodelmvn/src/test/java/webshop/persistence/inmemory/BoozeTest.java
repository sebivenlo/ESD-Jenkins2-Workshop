package webshop.persistence.inmemory;

import java.util.function.Predicate;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import webshop.entities.Booze;
import webshop.entities.Product;
import webshop.business.ProductContainer;
import webshop.business.ProductQuantity;
import webshop.business.RequiresAgeCheck;

/**
 * Test to work with a lambda expression. This is a PRO2 task.
 *
 * @author hom
 */
public class BoozeTest {

    IMProductMapper mapper = new IMProductMapper(IMProductMapper.DEFAULT_PRODUCT_FILE);
    ProductContainer inventory = new IMInventory(new IMProductMapper(IMProductMapper.DEFAULT_PRODUCT_FILE));
    ProductContainer cart;
    
    @Before 
    public void setUp(){
        cart = new IMCart();
    
    }
    public BoozeTest() {
        mapper.printProducts(System.out);
        ((IMInventory) inventory).printInventory(System.out);
    }

    /**
     * Test if the cart contains booze. Approach : stream the cart and see if it
     * contains a product that requires an age check.
     *
     */
    @Test
    public void boozeInCartTest() {
        Product b = mapper.getProductByDescription("Warsteiner").get(0);
        System.out.println("b = " + b);
        assertTrue(b instanceof RequiresAgeCheck);
        assertTrue(b instanceof Booze);
        inventory.transferTo(cart, b, 6); // at least a six pack...
        Predicate<ProductQuantity> check = pq -> RequiresAgeCheck.class.isInstance(pq.getProduct());
        assertTrue("Prosit", inventory.getContents().stream().anyMatch(check));
        // caught, restore to inventory
        cart.transferAllTo(inventory, b);
        assertTrue(cart.contains(b)); // ghost still there
        cart.purge(); // remove all traces.
        assertFalse(cart.contains(b));
    }
}
