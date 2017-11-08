package webshop.entities;

import webshop.entities.InvoiceLine;
import webshop.entities.Product;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import webshop.business.ProductQuantity;
import static webshop.entities.TestProducts.*;
/**
 * Test of getters and setters and toString() for coverage. Note the test data,
 * Toys for boys.
 *
 * @author hom
 */
public class InvoiceLineTest {

    InvoiceLine invoiceLine;
    ProductQuantity porsches = new ProductQuantity(TestProducts.porsche, 2 );

    public InvoiceLineTest() {
    }

    @Before
    public void setUp() {
        invoiceLine = new InvoiceLine( 42, porsches );
    }

    /**
     * Test of toString method, of class InvoiceLine.
     */
    @Test
    public void testToString() {
        System.out.println( "toString" );
        String result = invoiceLine.toString();
        assertNotNull( result );
    }

    /**
     * Test of getVat method, of class InvoiceLine.
     */
    @Test
    public void testGetVat() {
        System.out.println( "getVat" );
        long expResult = porsches.getQuantity()*porsches.getProduct().getVat();
        long result = invoiceLine.getVat();
        assertEquals( expResult, result );
    }

    /**
     * Test of getInvoiceNumber method, of class InvoiceLine. Default is null, which
     * is okay test-wise. This test serves coverage.
     */
    @Test
    public void testGetInvoice() {
        System.out.println( "getInvoiceNumber" );
        long expResult = 42;
        long result = invoiceLine.getInvoiceNumber();
        assertEquals( expResult, result );
    }

    /**
     * Test of getProduct method, of class InvoiceLine.
     */
    @Test
    public void testGetProduct() {
        System.out.println( "getProduct" );
        Product result = invoiceLine.getProduct();
        assertSame( porsches.getProduct(), result );
    }

    /**
     * Test of getQuantity method, of class InvoiceLine.
     */
    @Test
    public void testGetQuantity() {
        System.out.println( "getQuantity" );
        int expResult = 2;
        int result = invoiceLine.getQuantity();
        assertEquals( expResult, result );
    }

    /**
     * Test of getConsumerPrice method, of class InvoiceLine.
     */
    @Test
    public void testGetConsumerPrice() {
        System.out.println( "getConsumerPrice" );
        long expResult = 2 * porsche.getConsumerPrice();
        long result = invoiceLine.getConsumerPrice();
        assertEquals( expResult, result );
    }

    /**
     * Test of getConsumerUnitPrice method, of class InvoiceLine.
     */
    @Test
    public void testGetConsumerUnitPrice() {
        System.out.println( "getConsumerUnitPrice" );
        long expResult = porsche.getConsumerPrice();
        long result = invoiceLine.getConsumerUnitPrice();
        assertEquals( expResult, result );
    }
}
