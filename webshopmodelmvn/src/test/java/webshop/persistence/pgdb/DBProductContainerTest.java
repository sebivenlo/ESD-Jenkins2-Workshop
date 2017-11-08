package webshop.persistence.pgdb;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import webshop.entities.Product;
import webshop.business.ProductContainer;
import webshop.business.ProductQuantity;
import webshop.business.WebshopException;
import static webshop.persistence.pgdb.PGDBTestUtil.createTestTables;

/**
 * Does some Non Unit like database operations to Get things tested. This class
 * sets up a default container and retrieves a few products from the product
 * table to use in the tests.
 *
 * @author hom
 */
public class DBProductContainerTest {

    PGDBProductContainer inventory;
    PGDBProductContainer cart;
    PGDBProductContainer cart2;
    static PGDBProductMapper pmapper;
    // To use in all tests.
    Product p1, p2, p3, p4;

    @BeforeClass
    public static void setupClass() throws SQLException, IOException {
        // Greetings from Uncle Bob
        pmapper = new PGDBProductMapper();
        createTestTables("dbscripts/test_container.sql");
    }

    @Before
    public void setUp() throws IOException {
        inventory = new MockDBContainer( "test_inventory", 0 );
        cart = new MockDBContainer( "test_cart", 1 );
        cart2 = new MockDBContainer( "test_cart", 2 );
        assertEquals( "test_inventory", inventory.getTableName() );
        assertEquals( "test_cart", cart.getTableName() );
        List<Product> pList = pmapper.getAll();
        p1 = pList.get( 0 );
        p2 = pList.get( 1 );
        p3 = pList.get( 2 );
        p4 = pList.get( 3 );
    }

    @AfterClass
    public static void teardownClass() {
        new QueryHelper().doUpdate( "drop table if exists test_inventory;\n"
                + "drop table if exists test_cart;\n" );
    }

    /**
     * Test of getTableName method, of class PGDBProductContainer.
     */
    @Test
    public void testGetTableName() {
        assertEquals( "test_inventory", inventory.getTableName() );
        assertEquals( "test_cart", cart.getTableName() );
    }

    /**
     * Test of contains method, of class PGDBProductContainer. This test
     * initially depends on a filled database.
     *
     * @throws SQLException ...
     */
    @Test
    public void testContains() {
        System.out.println( "p from mapper = " + p1 );
        assertTrue( "expect it to be contained", inventory.contains( p1 ) );
        //assertEquals( "count 1 expected ", 1,  );
    }

    /**
     * Test of count(Product) method, of class PGDBProductContainer.
     */
    @Test
    public void testCount() {
        int count = inventory.count( p1 );
        ProductQuantity pq = inventory.merge( p1, 2 );
        assertNotNull( "should have pq", pq );
        assertEquals( count + 2, pq.getQuantity() );
    }

    /**
     * Test of drainTo method, of class PGDBProductContainer.
     */
    @Test
    public void testDrainTo() {
    }

    /**
     * Test of find method, of class PGDBProductContainer.
     */
    @Test
    public void testFind() {
        ProductQuantity pq = inventory.find( p1 );
        assertNull( inventory.find( null ) );
        assertNotNull( "Not found", pq );
        assertEquals( p1, pq.getProduct() );
    }

    /**
     * Test of getContents method, of class PGDBProductContainer. The order in
     * which the product quantities are stored is not predictable, so before
     * comparing results, make sure to sort the expected and result lists. For
     * that you can use the given comparator.
     */
    @Test
    public void testGetContents() {

        Comparator<ProductQuantity> comp = Comparator.comparing( pq -> pq
                .getProduct().getId() );

        // start with empty cart
        cart.empty();

        ProductQuantity pq1 = new ProductQuantity( p1, 4 );
        ProductQuantity pq2 = new ProductQuantity( p2, 8 );
        cart.merge( pq1 );
        cart.merge( pq2 );

        List<ProductQuantity> expected = new ArrayList<>();
        expected.add( pq1 );
        expected.add( pq2 );
        expected.sort( comp );
        System.out.println( "order list expected = " + expected );

        List<ProductQuantity> result = cart.getContents();
        System.out.println( "I got result        = " + result );
        assertEquals( "put two in ", 2, result.size() );
        result.sort( comp );
        assertTrue( expected.equals( result ) );
    }

    /**
     * Test of itemCount method, of class PGDBProductContainer.
     */
    @Test
    public void testItemCount() {
        int expected = 0;
        List<Product> allProducts = pmapper.getAll();

        for ( Product p : allProducts ) {
            expected += inventory.count( p );
        }
        assertEquals( "counts are the same", expected, inventory.itemCount() );

    }

    /**
     * Test of merge method, of class PGDBProductContainer.
     */
    @Test
    public void testMerge_Product_int() throws WebshopException {
        int count = inventory.count( p1 );
        ProductQuantity pq = inventory.merge( p1, 2 );
        assertNotNull( "should have merged into " + inventory.getTableName(), pq );
        System.out.println( " new pq = " + pq );
        assertEquals( count + 2, pq.getQuantity() );
        // clean up
        inventory.take( p1, 2 );
    }

    /**
     * Test of merge method, of class PGDBProductContainer.
     */
    @Test
    public void testMerge_ProductQuantity() {
        int count = cart2.count( p1 );
        ProductQuantity pq = new ProductQuantity( p1, 3 );
        cart2.merge( pq );
        cart2.merge( pq );
        assertEquals( count + 2 * 3, cart2.count( p1 ) );
        // cleanup 
        cart2.take( p1, 6 );
    }

    /**
     * Test of purge method, of class PGDBProductContainer.
     */
    @Test
    public void testPurge() throws WebshopException {
        System.out.println( "test Purge" );

        cart.merge( p3, 2 );
        assertTrue( cart.contains( p3 ) );

        int count = cart.count( p3 );
        System.out.println( "count p = " + count );
        cart.takeAll( p3 );
        assertTrue( cart.contains( p3 ) );
        cart.purge();
        long cartOwner = cart.getOwner();
        System.out.println( "cartOwner = " + cartOwner );
        assertFalse( cart.contains( p3 ) );

    }

    //<editor-fold defaultstate="expanded" desc="T03_A; __STUDENT_ID__ ;WEIGHT 1">
    /**
     * Test of take method, of class PGDBProductContainer.
     */
    @Test
    public void testTake() {
        System.out.println("test PGDBProductContainer.take");
        //Start Solution::replacewith:://TODO T03_A pgdb test take from database
        int qty = 1;
        System.out.println( ">>>p2 = " + p2 );
        inventory.merge( p2, qty );
        int afterMerge = inventory.count( p2 );
        ProductQuantity pqTake = inventory.take( p2, qty );
        int expectedInventory = afterMerge - 1;
        assertEquals( "checks and balances 1", expectedInventory,
                inventory.count( p2 ) );
        Product px =pqTake.getProduct();
        System.out.println( ">>>px = " + px );
        assertSame( "checks and balances 2", p2, px );
        assertEquals( "checks and balances 3", qty, pqTake.getQuantity() );
        //End Solution::replacewith::fail("Test not implemented");
    }
    //</editor-fold>

    /**
     * Test of takeAll method, of class PGDBProductContainer.
     */
    @Test
    public void testTakeAll() {
        System.out.println( "testTakeAll" );
        Product p = p3;
        System.out.println( ">>>p = " + p );
        int preMerge = inventory.count( p );
        inventory.merge( p, 2 );
        int count = inventory.count( p );
        assertEquals( preMerge + 2, count );
        int newOwner = 1;
        // put some in
        ProductQuantity pq = inventory.takeAll( p );
        assertNotNull( "take all has no result", pq );
        assertEquals( count, pq.getQuantity() );
        Product p2 = pq.getProduct();
        System.out.println( ">>>p2 = " + p2 );
        assertSame( p, p2 );
    }

    /**
     * Test positive case, allowable transfer. Use other cart, using same table.
     *
     * @throws Exception should not happen
     */
    @Test
    public void testTransferTo() throws Exception {
        System.out.println( "testTransferTo" );
        int count = cart2.count( p1 );
        int countCart = cart.count( p1 );

        int qty = 2;
        cart2.merge( p1, qty );
        cart2.transferTo( cart, p1, qty );
        assertEquals( "balance cart2", count, cart2.count( p1 ) );
        assertEquals( "check cart", countCart + qty, cart.count( p1 ) );
    }

    /**
     * Try to take more then is available. Test if an exception is thrown.
     */
    @Test
    public void testTransferTo_Greedy() {
        int qty = 2;
        int countCart = cart.count( p3 );
        inventory.merge( p3, qty );
        int count = inventory.count( p3 );
        try {
            inventory.transferTo( cart, p3, count + 2 * qty );
            fail( "should not happen, greedy" );
        } catch ( Exception ex ) {
            Throwable t = ex.getCause();
            System.out.println( "caucht" + t.toString() + " " + t.getMessage() );
        } catch ( Throwable tw ) {
            fail( "wrong exception" );
        } finally {
            assertEquals( "balance inventory", count, inventory.count( p3 ) );
            assertEquals( "check cart", countCart, cart.count( p3 ) );
        }
    }


}
