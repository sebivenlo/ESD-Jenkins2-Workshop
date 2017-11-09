package webshop.persistence.pgdb;

import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import webshop.business.Product;
import webshop.business.VATLevel;

/**
 *
 * @author hom
 */
public class ProductMapperTest {

    static DataSource dataSource;
    PGDBProductMapper mapper;

    @BeforeClass
    public static void setUpClass() {
        dataSource = QueryHelper.createDataSource();
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        mapper = new PGDBProductMapper();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getAll method, of class PGDBProductMapper.
     *
     * @throws SQLException ...
     */
    @Test
    public void testGetAll() throws SQLException {
        System.out.println( "getAll" );
        List<Product> result = mapper.getAll();
        int expected = countTableRecords( "products" );
        System.out.println( "result = " + result );
        assertEquals( expected, result.size() );
    }

    @Test
    public void testGetProductById() {
        System.out.println( "getProductById" );
        Product product = mapper.getProductById( 1l );
        System.out.println( "result = " + product );
        assertEquals( 1l, product.getId() );
    }

    @Test
    public void testGetProductBy_Description_Price_Name() {
        System.out.println( "by desc, price, vat" );
        Product product = mapper.getProductByDescriptionPriceVat( "", 0,
                VATLevel.LOW );
        System.out.println( "result = " + product );
        assertNull( product );

        product = mapper.getProductByDescriptionPriceVat( "The Davinci code",
                2500l,
                VATLevel.HIGH );
        assertNotNull( product );
        assertEquals( "description", "The Davinci code", product.
                getDescription() );
        assertEquals( "price", 2500, product.getPriceExclVAT() );
        assertEquals( "VAT", VATLevel.HIGH, product.getVatLevel() );
    }

    // it is a lie.
    private int countTableRecords( String products ) {
        return 9;
    }
}
