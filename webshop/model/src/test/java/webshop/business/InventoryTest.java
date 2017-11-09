package webshop.business;

import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
import webshop.persistence.inmemory.IMInventory;
import webshop.persistence.mappers.ProductMapper;
import static webshop.persistence.mappers.WebshopFactoryConfigurator.CONFIGURATOR;

/**
 *
 * @author hom
 */
public class InventoryTest {

    public InventoryTest() {
    }

    /**
     * Test of toString, of class IMInventory. The toString is not business
     * relevant, so invoking it suffices to satisfy our coverage requirement.
     * The only thing we test is that the result is non-null and of length &gt;
     * 0.
     */
    @Test
    public void testToString() {
        ProductMapper mapper
                = CONFIGURATOR.
                getFactory().getProductMapper();
        String result = new IMInventory( mapper ).toString();
        assertNotNull( result );
        assertFalse( result.isEmpty() );
    }

}
