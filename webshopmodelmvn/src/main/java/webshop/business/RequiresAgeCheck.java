package webshop.business;

import java.time.LocalDate;
import java.time.Period;

/**
 * Age checker. For products that required age check, such as liquor or beer and
 * wine. Use the {@link java.time} API to do an check.
 *
 * @author hom
 */
public interface RequiresAgeCheck {

    default boolean ageOk(LocalDate birthDay) {
        return birthDay
                .plus(getRequiredAge())
                .isBefore(LocalDate.now());
    }

    Period getRequiredAge();
}
