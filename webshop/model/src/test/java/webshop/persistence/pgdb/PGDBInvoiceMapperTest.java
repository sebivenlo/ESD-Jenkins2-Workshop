/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webshop.persistence.pgdb;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import webshop.business.Invoice;
import webshop.business.Product;
import webshop.business.ProductContainer;
import webshop.persistence.mappers.InvoiceMapper;
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
                return "test_invoice_lines";
            }

            @Override
            String getInvoiceTableName() {
                return "test_invoices";
            }

            @Override
            String getInvoiceIdSequenceName() {
                return "test_invoices_invoice_id_seq";
            }

        };
        cart1 = new MockDBContainer( "test_carts", 1 );
        cart2 = new MockDBContainer( "test_carts", 2 );
        int pCount = products.size();
        for ( int i = 0; i < products.size(); i++ ) {
            cart1.merge( products.get( 0 ), i + 1 );
            cart2.merge( products.get( 0 ), pCount - i + 1 );
        }
    }

    //<editor-fold defaultstate="expanded" desc="T07_A; __STUDENT_ID__ ;WEIGHT 1">
    /**
     * Test of save and load methods, of class PGDBInvoiceMapper. Use two
     * invoices. Save them first than load back and verify. You may rely on a
     * fitting implementation of Invoice.equals().
     */
    @Test
    public void testSaveAndLoad() {
        System.out.println( "testSaveAndLoad" );
        //Start Solution::replacewith:://TODO T07_A test save and load
        Invoice inv1 = new Invoice().setCart( cart1 );
        inv1.setCustomerBirthDay( LocalDate.of( 1955, Month.MARCH, 18 ) );
        inv1.setNumber( mapper.nextInvoiceId() );
        Invoice inv2 = new Invoice().setCart( cart2 );
        inv2.setNumber( mapper.nextInvoiceId() );
        long id1 = mapper.save( inv1 );
        long id2 = mapper.save( inv2 );
        Invoice linv1 = mapper.load( id1 );
        Invoice linv2 = mapper.load( id2 );
        assertEquals( inv1, linv1 );
        assertEquals( inv2, linv2 );
        //End Solution::replacewith::fail("test not implemented");
    }
    //</editor-fold>
}
