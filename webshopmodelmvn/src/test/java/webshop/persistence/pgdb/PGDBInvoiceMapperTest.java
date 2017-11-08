/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webshop.persistence.pgdb;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import webshop.entities.Invoice;
import webshop.entities.Product;
import webshop.business.ProductContainer;
import webshop.entities.InvoiceMapper;
import static webshop.persistence.pgdb.DBProductContainerTest.pmapper;
import static webshop.persistence.pgdb.PGDBTestUtil.createTestTables;

/**
 *
 * @author hom
 */
public class PGDBInvoiceMapperTest {

    InvoiceMapper mapper;
    ProductContainer cart1, cart2;
    static List<Product> products;

    @BeforeClass
    public static void setupClass() throws SQLException, IOException {
        createTestTables( "dbscripts/test_container.sql" );
        createTestTables( "dbscripts/test_invoice.sql" );
        products = new PGDBProductMapper().getAll();
    }

    @Before
    public void setUp() {
        mapper = new PGDBInvoiceMapper() {

            @Override
            String getInvoiceLineTableName() {
                return "test_invoice_line";
            }

            @Override
            String getInvoiceTableName() {
                return "test_invoice";
            }

            @Override
            String getInvoiceIdSequenceName() {
                return "test_invoice_invoice_id_seq";
            }

        };
        cart1 = new MockDBContainer( "test_cart", 1 );
        cart2 = new MockDBContainer( "test_cart", 2 );
        int pCount = products.size();
        for ( int i = 0; i < products.size(); i++ ) {
            cart1.merge( products.get( 0 ), i + 1 );
            cart2.merge( products.get( 0 ), pCount - i + 1 );
        }
    }

    /**
     * Test of save and load methods, of class PGDBInvoiceMapper.
     */
    @Test
    public void testSaveAndLoad() {
        System.out.println( "testSaveAndLoad" );
        Invoice inv1 = new Invoice( cart1 );
        Invoice inv2 = new Invoice( cart2 );
        System.out.println( "inv1 = " + inv1 );
        System.out.println( "inv2 = " + inv2 );
        long id1 = mapper.save( inv1 );
        long id2 = mapper.save( inv2 );
        Invoice linv1 = mapper.load( id1 );
        Invoice linv2 = mapper.load( id1 );
        System.out.println( "linv1 = " + linv1 );
        System.out.println( "linv2 = " + linv2 );
        assertEquals( inv1, linv1 );
        assertEquals( inv2, linv2 );
    }

    /**
     * Test of getInvoiceIDs method, of class PGDBInvoiceMapper.
     */
    @Test
    public void testGetInvoiceIDs() {
    }

    /**
     * Test of load method, of class PGDBInvoiceMapper.
     */
    @Test
    public void testLoad() {
    }

    /**
     * Test of getInvoiceTableName method, of class PGDBInvoiceMapper.
     */
    @Test
    public void testGetInvoiceTableName() {
    }

    /**
     * Test of getInvoiceLineTableName method, of class PGDBInvoiceMapper.
     */
    @Test
    public void testGetInvoiceLineTableName() {
    }

    /**
     * Test of get method, of class PGDBInvoiceMapper.
     */
    @Test
    public void testGet() {
    }

}
