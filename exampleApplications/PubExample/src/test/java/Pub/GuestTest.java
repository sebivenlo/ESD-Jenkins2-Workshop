/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pub;

import PubExceptions.DrunkenException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Pia Erbrath
 */
public class GuestTest {

    private Time time;
    Guest g;

    @Before
    public void setUp() {
        time = new Time();
        g = new Guest(18,0.8, false);
    }

    @Test
    public void test_getAge() {
        assertEquals("should be 18", 18, g.getAge());
    }

    @Test
    public void test_getStomach() {
        assertEquals("stomach volume should be 0.8", 0.8, g.getStomach()
                .getMaxVolume(), 0.001);
    }


    @Test
    public void drink_ok() {
        try {
            g.drink(new Beer(DrinkVolume.PINT, time));
            assertEquals("stomach volume should be 0.57", 0.57, g.getStomach()
                .getVolume(), 0.001);
        } catch (Exception e) {
            fail("here should no exception thrown");
        }
    }

    @Test
    public void drink_exception_drunken() {
        Guest g1 = new Guest(56, 0.2, false);
        try {
            g1.drink(new Beer(DrinkVolume.PINT, time));
            fail("here should be an exception");
        } catch (DrunkenException d) {
            System.out.println("all ok");
        } catch (Exception e) {
            fail("here should be another exception");
        }
    }
    
    @Test
    public void test_getBob() {
        Guest b = new Guest(56, 2, true);
        assertTrue("should be bob", b.isBob());
        assertFalse("is no bob", g.isBob());
    }
}
