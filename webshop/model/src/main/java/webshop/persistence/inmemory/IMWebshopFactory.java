package webshop.persistence.inmemory;

import webshop.business.ProductContainer;
import webshop.persistence.mappers.AbstractWebshopFactory;
import webshop.persistence.mappers.InvoiceMapper;
import webshop.persistence.mappers.ProductMapper;

/**
 * Concrete in memory factory. This implementation uses the fact that an enum
 * ensures that there are only as many instance as decalred values in the enum.
 * In this case one, making this class into and <i>eager singleton</i>.
 *
 * @author hom
 * @author hvd
 */
public enum IMWebshopFactory implements AbstractWebshopFactory {

    IMFACTORY;
    private final ProductMapper productMapper;
    private final ProductContainer inventory;

    private IMWebshopFactory() {
        productMapper = IMProductMapper.INSTANCE;
        inventory = new IMInventory( productMapper );
    }

    /**
     * Create a mapper for the in memory world.
     *
     * @return the container.
     */
    @Override
    public ProductContainer createCart() {
        return new IMCart();
    }

    @Override
    public ProductContainer getInventory() {
        return inventory;
    }

    @Override
    public ProductMapper getProductMapper() {
        return productMapper;
    }

    /**
     * This mapper forgets.
     *
     * @return the fake mapper.
     */
    @Override
    public InvoiceMapper getInvoiceMapper() {
        return IMInvoiceMapper.INSTANCE;
    }

}
