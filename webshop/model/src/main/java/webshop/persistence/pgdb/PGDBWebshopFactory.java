package webshop.persistence.pgdb;

import webshop.persistence.mappers.InvoiceMapper;
import webshop.persistence.mappers.ProductContainerMapper;
import webshop.persistence.mappers.ProductMapper;

/**
 * Concrete factory of db implementations. This class also provides the data
 * source for all db related operations.
 *
 * This implementation uses the Holder + enum idiom to realise thread safe
 * singletons for the three products of this factory.
 *
 * @author hom
 */
public enum PGDBWebshopFactory implements
        webshop.persistence.mappers.AbstractWebshopFactory {

    INSTANCE;

    /**
     * Create a new cart entry in the database. The implementor should make sure
     * that the owner gets a unique ID.
     *
     * @return a fresh cart.
     */
    @Override
    public ProductContainerMapper createCart() {
        return new PGDBCart();
    }

    /**
     * Get the inventory. This implementation uses the database version.
     *
     * @return the inventory mapper.
     */
    @Override
    public ProductContainerMapper getInventory() {
        return InventoryHolder.HOLDER.INVENTORY;
    }

    /**
     * Get the productMapper.
     *
     * @return the product mapper.
     */
    @Override
    public ProductMapper getProductMapper() {
        return MapperHolder.HOLDER.MAPPER;
    }

    /**
     * Get the invoice mapper.
     *
     * @return the instance
     */
    @Override
    public InvoiceMapper getInvoiceMapper() {
        return InvoiceMapperHolder.HOLDER.MAPPER;
    }

    private enum InvoiceMapperHolder {

        HOLDER;
        private final InvoiceMapper MAPPER
                = new webshop.persistence.pgdb.PGDBInvoiceMapper();
    }

    private enum InventoryHolder {

        HOLDER;
        private final ProductContainerMapper INVENTORY = new PGDBInventory();
    }

    private enum MapperHolder {

        HOLDER;
        private final ProductMapper MAPPER = new PGDBProductMapper();
    }

}
