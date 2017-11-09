package webshop.persistence.inmemory;

import java.io.IOException;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.*;
import webshop.business.Product;
import webshop.business.TestProducts;
import webshop.business.VATLevel;

/**
 * The test are just simple "are you there" and not extensive. The tests depend
 * on the file "src/resources/products.csv, which is read from the class path
 * and should appear in the jar. Not covering the IOException when the
 * products.csv file is not found is okay. Life is hard enough as it is.
 *
 * @author hom
 */
public class IMProductMapperTest {

    IMProductMapper mapper = IMProductMapper.INSTANCE;

    @Before
    public void setUp() {
    }

    /**
     * Test of getAll method, of class IMProductMapper.
     *
     * @throws java.io.IOException ..
     */
    @Test
    public void testGetAll() throws IOException {
        System.out.println( "getAll" );

        List<Product> result = mapper.getAll();
        assertEquals( TestProducts.productCount, result.size() );
    }

    /**
     * Test of getProductByDescriptionPriceVat method, of class IMProductMapper.
     *
     * @throws java.io.IOException ..
     */
    @Test
    public void testGetProductByDescriptionPriceVat() throws IOException {
        System.out.println( "getProductByDescriptionPriceVat" );
        String description = "Battleship";
        long price = 2000L;
        VATLevel vat = VATLevel.HIGH;
        Product expResult = mapper.getProductById( 3l );
        Product result
                = mapper.getProductByDescriptionPriceVat( description, price,
                        vat );
        assertEquals( expResult, result );

        long wrongPrice = 2100l;
        VATLevel wrongVat = VATLevel.LOW;
        // should return nothing a.k.a. null.
        assertNull( mapper.getProductByDescriptionPriceVat( description,
                wrongPrice,
                vat ) );
        assertNull( mapper.getProductByDescriptionPriceVat( description, price,
                wrongVat ) );

    }

    /**
     * Test correct return value with id not in container.
     */
    @Test
    public void testGetProductById_notexisting() {
        assertNull( mapper.getProductById( 0l ) );
    }

}
