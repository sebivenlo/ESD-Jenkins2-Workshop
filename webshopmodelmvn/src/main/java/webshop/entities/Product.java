package webshop.entities;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import webshop.business.ProductType;
import webshop.business.VATLevel;

/**
 * Simple product entity pojo. The product class specifies details.
 *
 * @author hom
 */
public class Product implements Serializable {

    public static Product productWith( long id, String description, int price, VATLevel vl, ProductType t ) {
        return new Product( id, description, price, vl, t );
    }

    private long id;
    /**
     * Describes the product.
     */
    private String description;
    /**
     * The VAT for this Product.
     */
    private VATLevel vatLevel;
    /**
     * Price excluding VAT of the product in Euro cents.
     */
    private long priceExclVAT;

    /**
     *
     */
    private ProductType productType;

    /**
     * Required to do all mapping things.
     */
    public Product() {
    }

    /**
     * Create ProductQuantity with id.
     *
     * @param id           of the product
     * @param description  of the product
     * @param priceExclVAT for this product
     * @param vl           vat level
     * @param type         of the product
     */
    Product( long id, String description, long priceExclVAT, VATLevel vl,
            ProductType type ) {
        this( description, priceExclVAT, vl, type );
        this.id = id;
    }

    /**
     * Create ProductQuantity without id.
     *
     * @param description  of the product
     * @param priceExclVAT for this product
     * @param vl           vat level
     * @param type         of the product
     */
    Product( String description, long priceExclVAT, VATLevel vl,
            ProductType type ) {
        this.description = description;
        this.vatLevel = vl;
        this.priceExclVAT = priceExclVAT;
        this.productType = type;
    }

    public long getId() {
        return id;
    }

    void setId( long id ) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public VATLevel getVatLevel() {
        return vatLevel;
    }

    @Override
    public String toString() {
        return "Product{" + "id=" + id + ", description=" + description
                + ", vatLevel=" + vatLevel + ", price=" + priceExclVAT + '}';
    }

    public long getPriceExclVAT() {
        return priceExclVAT;
    }

    public long getVat() {
        return vatLevel.computeVAT( priceExclVAT );

    }

    public long getConsumerPrice() {
        return getPriceExclVAT() + getVat();
    }

    @Override
    public int hashCode() {
        return Objects.hash( this.description, this.priceExclVAT, this.vatLevel,
                this.productType );
    }

    @Override
    public boolean equals( Object obj ) {
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final Product other = ( Product ) obj;
        if ( this.id != other.id ) {
            return false;
        }
        if ( !Objects.equals( this.description, other.description ) ) {
            return false;
        }
        if ( this.productType != other.productType ) {
            return false;
        }
        if ( this.vatLevel != other.vatLevel ) {
            return false;
        }
        return this.priceExclVAT == other.priceExclVAT;
    }

    public ProductType getProductType() {
        return productType;
    }
    protected static Map<Long, Product> identityMap = new ConcurrentHashMap<>();
}
