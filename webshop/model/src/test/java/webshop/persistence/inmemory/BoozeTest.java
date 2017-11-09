package webshop.persistence.inmemory;

import java.util.function.Predicate;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import webshop.business.Booze;
import webshop.business.Invoice;
import webshop.business.Product;
import webshop.business.ProductContainer;
import webshop.business.ProductQuantity;
import webshop.business.RequiresAgeCheck;

/**
 * Test to work with a lambda expression. This is an integration test. The setup
 * method implicitly test if the ProductMapper correctly classifies the
 * products.
 *
 * @author hom
 */
public class BoozeTest {

    IMProductMapper mapper = IMProductMapper.INSTANCE;
    ProductContainer inventory = new IMInventory( mapper );
    ProductContainer cart;
    Product booze;
    Product crisps;
    Product kingsman;

    @Before
    public void setUp() {
        cart = new IMCart();
        booze = mapper.getProductByDescription( "Warsteiner" ).get( 0 );
        crisps = mapper.getProductByDescription( "Lay's Crisps" ).get( 0 );
        kingsman = mapper.getProductByDescription(
                "Kingsman: The secret Service" ).get( 0 );
    }

    public BoozeTest() {
        mapper.printProducts( System.out );
        ( ( IMInventory ) inventory ).printInventory( System.out );
    }

    //<editor-fold defaultstate="expanded" desc="T01_A1; __STUDENT_ID__ ;WEIGHT 1">
    /**
     * Test that Warsteiner is classified as booze and does require and age
     * check. Assert that the object is of type Product, Booze and
     * RequiresAgeCheck. Also assert that crisps are a normal product. See setup
     * method.
     */
    @Test
    public void testWarsteinerClassifiesAsBooze() {
        //Start Solution::replacewith:://TODO T01_A1 Type of Warsteiner test, asserts
        System.out.println( "booze = " + booze );
        assertTrue( booze instanceof Product );
        assertTrue( booze instanceof RequiresAgeCheck );
        assertTrue( booze instanceof Booze );
        assertTrue( crisps instanceof Product );
        assertFalse( crisps instanceof RequiresAgeCheck );
        assertFalse( crisps instanceof Booze );
        //End Solution::replacewith::fail("test not implemented");
    }
    //</editor-fold>

    //<editor-fold defaultstate="expanded" desc="T01_A2; __STUDENT_ID__ ;WEIGHT 1">
    /**
     * Test if the cart contains booze. Test if cart requires age check. Test if
     * cart without Booze (by putting all e.g. beer back to inventory) does not an
     * require age check. After the asserts, put all products back into the
     * inventory.
     */
    @Test
    public void boozeInCartTest() {
        System.out.println( "booze in cart test" );
        inventory.transferTo( cart, booze, 6 ); // at least a six pack...
        // 1. Create Predicate<ProductQuantity> to do the checking.
        // 2. assert that cart contains contains a products that are
        //    classiffied as product requiring an Age check.
        // Approach stream the car's contents an assert there is some match
        // Use of lambda gives extra points.
        //Start Solution::replacewith:://TODO T01_A2 Create predicate to check if cart correctly checks for Booze
        Predicate<ProductQuantity> check
                = pq -> RequiresAgeCheck.class.isInstance( pq.getProduct() );
        assertTrue( "Prosit", cart.getContents().stream().anyMatch( check ) );
        //End Solution::replacewith::fail("test not implemented");
        // caught, restore to inventory
        cart.transferAllTo( inventory, booze );
        assertTrue( cart.contains( booze ) ); // ghost still there
        cart.purge(); // remove all traces.
        assertFalse( cart.contains( booze ) );
    }
    //</editor-fold>

    /**
     * Test that the invoice properly flags if the cart contains. Always
     * transfer between inventory and cart. Put crisps in, check it does not
     * not, add beer, assert it does. Take note: The invoice should trigger on
     * booze, even if the quantity is zero, so you should purge the cart and
     * assert again.
     */
    @Test
    public void testInvoiceDetectsBooze() {
        System.out.println( "test Invoive detects booze" );
        inventory.transferTo( cart, kingsman, 1 );
        inventory.transferTo( cart, crisps, 8 );
        Invoice invoice = new Invoice().setCart( cart );
        assertFalse( "no booze here", invoice.isAgeCheckRequired() );
        inventory.transferTo( cart, booze, 1 );
        assertTrue( "sneaked by", invoice.isAgeCheckRequired() );
        cart.transferAllTo( inventory, booze );
        assertTrue( "gone but smell still here", invoice.isAgeCheckRequired() );
        cart.purge();
        assertFalse( "no booze here, really", invoice.isAgeCheckRequired() );
    }
}
