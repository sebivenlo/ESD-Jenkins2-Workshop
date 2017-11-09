package webshop.persistence.inmemory;

import static org.junit.Assert.*;
import org.junit.Test;
import webshop.business.ProductContainer;
import webshop.persistence.mappers.ProductMapper;

/**
 *
 * @author hom
 */
public class IMWebshopFactoryTest {

    /**
     * Test of createCart method, of class IMWebshopFactory.
     */
    @Test
    public void testCreateCart() {
        System.out.println( "createCart" );
        IMWebshopFactory instance = IMWebshopFactory.IMFACTORY;
        Class<?> expResult = IMCart.class;
        ProductContainer result = instance.createCart();
        assertEquals( expResult, result.getClass() );
    }

    /**
     * Test of getInventory method, of class IMWebshopFactory.
     */
    @Test
    public void testGetInventory() {
        System.out.println( "getInventory" );
        IMWebshopFactory instance = IMWebshopFactory.IMFACTORY;
        Class<?> expResult = IMInventory.class;
        ProductContainer result = instance.getInventory();
        assertEquals( expResult, result.getClass() );
    }

    /**
     * Test of getProductMapper method, of class IMWebshopFactory.
     */
    @Test
    public void testGetProductMapper() {
        System.out.println( "getProductMapper" );
        IMWebshopFactory instance = IMWebshopFactory.IMFACTORY;
        Class<?> expResult = IMProductMapper.class;
        ProductMapper result = instance.getProductMapper();
        assertEquals( expResult, result.getClass() );
    }
}
