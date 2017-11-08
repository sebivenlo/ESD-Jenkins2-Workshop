package webshop.entities;

import java.time.Period;
import webshop.business.RequiresAgeCheck;
import webshop.business.VATLevel;
import static webshop.business.ProductType.BEVERAGE;

/**
 * A product that requires an age check before sale. This implementation
 * requires a minimum age of 18 years of the buyer. Exam: Use of polymorphism.
 *
 * @author hom
 */
public class Booze extends Product implements RequiresAgeCheck {

    public static Product boozeWith( long id, String description, int price, VATLevel vl ) {
        return new Booze(id,description,price,VATLevel.HIGH);
    }
    /**
     * Required ctor, by {@link Product#Product(long, java.lang.String, long, webshop.business.VATLevel,webshop.business.ProductType)
     * }.
     *
     * @param id
     * @param description
     * @param priceExclVAT
     * @param vl
     */
    Booze(  String description, long priceExclVAT, VATLevel vl ) {
        super( description, priceExclVAT, vl, BEVERAGE );
    }
    Booze( long id, String description, long priceExclVAT, VATLevel vl ) {
        super( description, priceExclVAT, vl, BEVERAGE );
    }

    private static final Period REQUIRED_AGE = Period.of( 18, 0, 0 );

    /**
     * Required age expressed as a period of time, which is the period since the
     * birthday.
     *
     * @return the required age
     */
    @Override
    public Period getRequiredAge() {
        return REQUIRED_AGE;
    }

}
