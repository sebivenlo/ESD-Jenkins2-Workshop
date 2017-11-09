package webshop.business;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple product entity pojo. The product class specifies details.
 *
 * @author hom
 */
public class Product implements Serializable {

    private final long id;
    /**
     * Describes the product.
     */
    private String description = null;
    /**
     * The VAT for this Product.
     */
    private VATLevel vatLevel;
    /**
     * Price excluding VAT of the product in Euro cents.
     */
    private long priceExclVAT;

    /**
     * Type of the product.
     */
    private ProductType productType;

    /**
     * Required ctor.
     */
    public Product( long id ) {
        this.id = id;
    }

    /**
     * Create ProductQuantity with id.
     *
     * @param id of the product
     * @param description of the product
     * @param priceExclVAT for this product
     * @param vl vat level
     * @param type of the product
     */
    public Product( long id, String description, long priceExclVAT,
            VATLevel vl,
            ProductType type ) {
        this.id = id;
        this.description = description;
        this.priceExclVAT = priceExclVAT;
        this.vatLevel = vl;
        this.productType = type;
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        // make effectively final.
        if ( this.description == null ) {
            this.description = description;
        } else {
            throw new RuntimeException( "Leave me be" );
        }
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

    public void setVatLevel( VATLevel vatLevel ) {
        this.vatLevel = vatLevel;
    }

    public void setPriceExclVAT( long priceExclVAT ) {
        this.priceExclVAT = priceExclVAT;
    }

    public void setProductType( ProductType productType ) {
        this.productType = productType;
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
        if ( this == obj ) {
            // same is always equal
            return true;
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
}
