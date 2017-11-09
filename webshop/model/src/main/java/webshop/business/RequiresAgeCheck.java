package webshop.business;

import java.time.LocalDate;
import java.time.Period;

/**
 * Age checker. For products that required age check, such as liquor or beer and
 * wine. Use the {@link java.time} API to do an age check, based on
 * {@link java.time.LocalDate} for Date and {@link java.time.Period} for
 * time/date span.
 *
 * @author hom
 */
public interface RequiresAgeCheck {

    //<editor-fold defaultstate="expanded" desc="T01_B2; __STUDENT_ID__ ;WEIGHT 1">
    /**
     * Check age. If birthday is null return false. If getRequiredAge returns
     * null return true, otherwise verify that the distance between now and
     * birthday is at least the
     * <i>requiredAge</i>.
     *
     * @param birthday to check
     * @return false if birthDay == null or birthday &gt; now()-requiredAge
     */
    default boolean ageOk( LocalDate birthday ) {
        //Start Solution::replacewith:://TODO T01_B2 implement ageOk
        if ( null == birthday ) {
            return false;
        }
        Period age = getRequiredAge();
        if ( age == null ) {
            return true;
        }
        return birthday
                .plus( age )
                .isBefore( LocalDate.now() );
        //End Solution::replacewith::return false; // will never sell
    }
    //</editor-fold>

    /**
     * Produce a positive period matching the age to be checked.
     *
     * @return the period.
     */
    Period getRequiredAge();
}
