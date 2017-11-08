/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webshop.business;

import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author hom
 */
public class RequiresAgeCheckTest {

    public RequiresAgeCheckTest() {
    }

    /**
     * Test of ageOk method, of class RequiresAgeCheck.
     */
    @Test
    public void testAgeOk() {
        RequiresAgeCheck ageCheck1 = new RequiresAgeCheckImpl();
        assertTrue("realy old",ageCheck1.ageOk(LocalDate.MIN));
        assertFalse("too young",ageCheck1.ageOk(LocalDate.now().minus(Period.of(17, 0,1))));
        LocalDate justToYoung = LocalDate.MAX.now().minusYears(18).plusDays(1);
        System.out.println("justToYoung = " + justToYoung);
        assertFalse("just too young",ageCheck1.ageOk(justToYoung));
        LocalDate happyNow = justToYoung.minusDays(2);
        System.out.println("happyNow = " + happyNow);
        assertTrue("18, may boose now, are you happy?",ageCheck1.ageOk(happyNow));
        assertTrue("author",ageCheck1.ageOk(LocalDate.of(1955, Month.MARCH, 18)));
    }

    /**
     * Test of getRequiredAge method, of class RequiresAgeCheck.
     */
    @Test
    public void testGetRequiredAge() {
    }

    public class RequiresAgeCheckImpl implements RequiresAgeCheck {

        private final int requiredYears;
        private final Period age;

        public RequiresAgeCheckImpl(int requiredYears) {
            this.requiredYears = requiredYears;
            age = Period.of(this.requiredYears, 0, 0);
        }

        public RequiresAgeCheckImpl() {
            this(18);
        }

        @Override
        public Period getRequiredAge() {
            return age;
        }
    }

}
