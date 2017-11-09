package webshop.persistence.mappers;

import webshop.business.ProductContainer;

/**
 *
 * @author hom
 */
public interface AbstractWebshopFactory {

    /**
     * Create a cart for the business side.
     *
     * @return a cart.
     */
    ProductContainer createCart();

    /**
     * Get or create the Inventory for the business Side. The method creates the
     * inventory on first call and returns the same object on the next calls.
     *
     * @return the inventory.
     */
    ProductContainer getInventory();

    /**
     * Get or create the PGDBProductMapper for the business Side. The method creates
     * the mapper on first call and returns the same object on the next calls.
     *
     * @return the mapper.
     *
     */
    ProductMapper getProductMapper();
    
    /**
     * Get or create a invoice mapper.
     * @return the mapper
     */
    InvoiceMapper getInvoiceMapper();
}
