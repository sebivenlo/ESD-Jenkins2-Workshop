package webshop.persistence.mappers;

import webshop.persistence.inmemory.IMWebshopFactory;
import webshop.persistence.pgdb.PGDBWebshopFactory;

/**
 * Configure the factory, to allow the client application/project to ask for an
 * implementation of the abstract factory.
 *
 * @author hom
 */
public enum WebshopFactoryConfigurator {

    CONFIGURATOR;

    public static final String DB_CONFIG = "PGDB";
    /**
     * Default.
     */
    public static final String MEMORY_CONFIG = "IM";

    /**
     * Default factory produces in memory implementations.
     *
     * @return the factory
     */
    public AbstractWebshopFactory getFactory() {
        return getFactory( MEMORY_CONFIG );
    }

    /**
     * Get a specific implementation.
     *
     * @param factoryType one of MEMORY_CONFIG or DB_CONFIG.
     * @return the requested factory.
     */
    public AbstractWebshopFactory getFactory( String factoryType ) {

        AbstractWebshopFactory fac;
        System.out.println( "factoryType = " + factoryType );
        switch ( factoryType ) {
            default:
            case MEMORY_CONFIG:
                fac = IMWebshopFactory.IMFACTORY;
                break;
            case DB_CONFIG:
                fac = PGDBWebshopFactory.INSTANCE;
                break;
        }
        return fac;
    }
}
