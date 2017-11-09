package webshop.persistence.inmemory;

import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import webshop.business.Invoice;
import webshop.business.ProductContainer;
import static webshop.business.TestProducts.*;
import webshop.persistence.mappers.InvoiceMapper;

/**
 * Test file system mapper. Test serialisation and de-serialisation.
 *
 * @author hom
 */
public class IMInvoiceMapperTest {

    InvoiceMapper mapper = IMInvoiceMapper.INSTANCE;
    IMInvoiceMapper imim = ( IMInvoiceMapper ) mapper;
    ProductContainer cart1;
    ProductContainer cart2;
    Invoice inv1;
    Invoice inv2;

    /**
     * Sets up two carts and invoices.
     */
    @Before
    public void setUp() {
        cart1 = new IMCart();
        cart2 = new IMCart();
        cart1.merge( porsche, 1 );
        cart1.merge( book, 1 );
        cart2.merge( beer, 6 );
        cart2.merge( crisps, 2 );
        inv1 = new Invoice().setCart( cart1 );
        inv2 = new Invoice().setCart( cart1 );
    }

    //<editor-fold defaultstate="expanded" desc="T03_A; __STUDENT_ID__ ;WEIGHT 1">
    /**
     * Test of save method, of class IMInvoiceMapper. Create an invoice from a
     * cart, persist it, check if the appropriate file exists in the file system
     * and read the invoice back into a second instance. The file and directory
     * is created by
     * {@link webshop.persistence.inmemory.FSPersistenceNames#getFileFor(java.lang.Class, long)}.
     * You may want to study that class too. Assert that save and restored
     * object are the equal by the
     * {@link webshop.business.Invoice#equals(Object )}.
     */
    @Test
    public void testSaveAndLoad() {
        System.out.println( "toBeSaved = " + inv1 );
        //Start Solution::replacewith:://TODO T03_A testSaveAndLoad of im persistence
        System.out.println( "==================================" );
        long id = mapper.save( inv1 );

        Invoice restored = mapper.load( id );
        System.out.println( "restored = " + restored );
        System.out.println( "==================================" );
        assertEquals( "not same invoice", inv1, restored );
        imim.deleteAllFiles();
        imim.clearMaps();
        //End Solution::replacewith::fail("test not implemented");
    }
    //</editor-fold>

    /**
     * Test of getInvoiceIDs method, of class IMInvoiceMapper. Create two carts
     * and from those two invoices and persist them. Verify the number of
     * invoices before and after persistence. Verify that numbers of the created
     * invoices are in the returned list.
     */
    @Test
    public void testGetInvoiceIDs() {

        int mapperCount = imim.invoiceMap.size();
        long id1 = mapper.save( inv1 );
        long id2 = mapper.save( inv2 );
        assertEquals( "missing some", mapperCount + 2, imim.invoiceMap.size() );
        assertTrue( mapper.getInvoiceIDs().contains( id1 ) );
        assertTrue( mapper.getInvoiceIDs().contains( id2 ) );
        imim.deleteAllFiles();
        imim.clearMaps();

    }

    @Test
    public void testLoadAll() throws IOException {
        imim.deleteAllFiles();
        long id1 = mapper.save( inv1 );
        long id2 = mapper.save( inv2 );
        imim.clearMaps();// remove local mapping
        imim.loadAll();
        assertEquals( 2, imim.invoiceLineMap.size() );
        assertEquals( 2, imim.invoiceMap.size() );
        assertTrue( imim.invoiceLineMap.containsKey( id1 ) );
        assertTrue( imim.invoiceLineMap.containsKey( id2 ) );

        Invoice il1 = mapper.get( id1 );
        Invoice il2 = mapper.get( id2 );
        assertEquals( "invoices 1 are not same", inv1, il1 );
        assertEquals( "invoices 2 are not same", inv2, il2 );

    }

}
