package webshop.business;

import java.time.Period;
import static webshop.business.ProductType.BEVERAGE;

/**
 * A product that requires an age check before sale. This implementation
 * requires a minimum age of 18 years of the buyer. Exam: Use of polymorphism.
 * Use {@link java.time.Period} to express age. {@link RequiresAgeCheck}
 *
 * @author hom
 */
//<editor-fold defaultstate="expanded" desc="T01_B1; __STUDENT_ID__ ;WEIGHT 1">
// TODO T01_B1 implement Booze class.
//Start Solution::replacewith::public class Booze {
public class Booze extends Product implements RequiresAgeCheck {

    private static final long serialVersionUID = 1L;

    public Booze( long id ) {
        super( id );
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
    public Booze( long id, String description, long priceExclVAT, VATLevel vl ) {
        super( id, description, priceExclVAT, vl, BEVERAGE );
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
//End Solution::replacewith::

}
//</editor-fold>
