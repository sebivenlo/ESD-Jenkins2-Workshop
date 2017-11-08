package webshop.entities;

import webshop.business.ProductType;
import webshop.business.VATLevel;
import webshop.entities.Product;
import webshop.entities.Booze;
import static webshop.business.ProductType.BD;
import static webshop.business.ProductType.BEVERAGE;
import static webshop.business.ProductType.FOOD;

/**
 * Set of test products to keep test code dry and all
 *
 * @author hom
 */
public class TestProducts {
    
    /**
     * DVD, HIGH, 600.
     */
    public static final Product diamonds = new Product( 1,"Diamonds are Forever",
            600,
            VATLevel.HIGH, ProductType.DVD );
    /**
     * Beverage, LOW, 100.
     */
    public static final Product beverage = new Product( 2, "Bevererage", 100,
            VATLevel.LOW,
            ProductType.BEVERAGE );
    /**
     * Expensive car, HIGH, 1230095, LUXURY .
     */
    public static final Product porsche = new Product( 3, "Targa 911", 1230095,
            VATLevel.HIGH,
            ProductType.LUXURY );
    /**
     * Popular film , HIGH, 2655 BD.
     */
    public static final Product kingsman
            = new Product( 4, "Kingsman: The Secret Service", 2655,
                    VATLevel.HIGH, BD );
    /**
     * Crunchy salty stuff, HIGH, 200 FOOD.
     */
    public static final Product crisps = new Product( 5,"Smiths", 200,
            VATLevel.HIGH, FOOD );
    /**
     * Necessity for real men, 100 LOW.
     */
    public static final Product beer
            = new Booze( "Warsteiner", 100, VATLevel.LOW );
    static{
        beer.setId( 6L);
    }
    /**
     * Book we all know, LOW, 4800.
     */
    public static final Product book = new Product(7, "Objects First", 4800,
            VATLevel.LOW,
            ProductType.BOOK );
    /**
     * No vat product.
     */
    public static final Product noVATProduct = new Product(
            8,"some example product", 200,
            VATLevel.NONE, ProductType.LUXURY );
    /** Thing you always get for free. */
    public static final Product pricelessProduct
            = new Product(9, "Unclear", 0,
                    VATLevel.NONE, FOOD );

}
