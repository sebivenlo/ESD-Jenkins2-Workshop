package webshop.persistence.pgdb;

import org.junit.Test;
import static org.junit.Assert.*;
import webshop.persistence.mappers.ProductContainerMapper;
import webshop.persistence.mappers.ProductMapper;

/**
 * Just to satisfy coverage.
 *
 * @author hom
 */
public class PGDBWebshopFactoryTest {

    /**
     * Test of createCart method, of class PGDBWebshopFactory.
     */
    @Test
    public void testCreateCart() {
        System.out.println( "createCart" );
        PGDBWebshopFactory instance = PGDBWebshopFactory.INSTANCE;
        ProductContainerMapper result = instance.createCart();
        assertTrue( result instanceof PGDBCart );
    }

    /**
     * Test of getInventory method, of class PGDBWebshopFactory.
     */
    @Test
    public void testGetInventory() {
        System.out.println( "getInventory" );
        PGDBWebshopFactory instance = PGDBWebshopFactory.INSTANCE;
        ProductContainerMapper result = instance.getInventory();
        assertTrue( result instanceof PGDBInventory );
    }

    /**
     * Test of getProductMapper method, of class PGDBWebshopFactory.
     */
    @Test
    public void testGetProductMapper() {
        System.out.println( "getProductMapper" );
        PGDBWebshopFactory instance = PGDBWebshopFactory.INSTANCE;
        ProductMapper result = instance.getProductMapper();
        assertTrue( result instanceof PGDBProductMapper );
    }

}
