package webshop.entities;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import org.mockito.runners.MockitoJUnitRunner;
import webshop.business.ProductType;
import webshop.business.VATLevel;
import static webshop.entities.TestProducts.beer;
import static webshop.entities.TestProducts.crisps;

/**
 *
 * @author hom
 */
public class ProductMapperTest {

    List<Product> products;
    ProductMapper mapper;

    @Before
    public void setUp() {
        products = new ArrayList<>();
        products.add( beer );
        products.add( crisps );
        mapper = mock( ProductMapper.class );
        Mockito.when( mapper.getAll() ).thenReturn( products );
        Mockito.when( mapper.nextProductId() ).thenReturn( 23L );
    }

    /**
     * Test of getAll method, of class ProductMapper.
     */
    @Test
    public void testGetAll() {
        System.out.println( "getAll" );

        List<Product> expResult = products;
        List<Product> result = mapper.getAll();
        assertEquals( expResult, result );
    }

    /**
     * Test of getProductByDescriptionPriceVat method, of class ProductMapper.
     */
    @Test
    public void testGetProductByDescriptionPriceVat() {
        System.out.println( "getProductByDescriptionPriceVat" );
        Product product1 = products.get( 0 );
        String description1 = product1.getDescription();
        long price = product1.getConsumerPrice();
        VATLevel vat = product1.getVatLevel();
        Product result
                = mapper.getProductByDescriptionPriceVat( description1, price,
                        vat );
        assertEquals( product1, result );
        // TODO review the generated test code and remove the default call to fail.
        fail( "The test case is a prototype." );
    }

    /**
     * Test of getProductById method, of class ProductMapper.
     */
    @Test
    public void testGetProductById() {
        System.out.println( "getProductById" );
        long id = 23L;

        Product expResult = null;
        Product result = mapper.getProductById( id );
        assertEquals( expResult, result );
        // TODO review the generated test code and remove the default call to fail.
        fail( "The test case is a prototype." );
    }

    /**
     * Test of getProductByDescription method, of class ProductMapper.
     */
    @Test
    public void testGetProductByDescription() {
        System.out.println( "getProductByDescription" );
        String description = beer.getDescription();
        List<Product> expResult = new ArrayList<>();
        expResult.add(beer);
        List<Product> result = mapper.getProductByDescription( description );
        assertEquals( expResult, result );
        // TODO review the generated test code and remove the default call to fail.
        fail( "The test case is a prototype." );
    }

    /**
     * Test of createProductWith method, of class ProductMapper.
     */
    @Test
    public void testCreateProductWith() {
        System.out.println( "createProductWith" );
        
        String description = "French Fries";
        long priceExclVAT = 0L;
        VATLevel vl = VATLevel.HIGH;
        ProductType type = null;
        int min_age = 0;
        Product expResult = null;
        Product result
                = mapper.createProductWith( description, priceExclVAT, vl,
                        type, min_age );
        assertEquals( expResult, result );
        // TODO review the generated test code and remove the default call to fail.
        fail( "The test case is a prototype." );
    }

    /**
     * Test of nextProductId method, of class ProductMapper.
     */
    @Test
    public void testNextProductId() {
        System.out.println( "nextProductId" );
        long expResult = 23L;
        long result = mapper.nextProductId();
        assertEquals( expResult, result );
    }

    /**
     * Test of setProductId method, of class ProductMapper.
     */
    @Test
    public void testSetProductId() {
        System.out.println( "setProductId" );
        Product prod = products.get( 0 );
        long id = 42L;
        Product result = mapper.setProductId( prod, id );
        assertEquals( 42L, result.getId() );
    }
    long id = 0;
}
