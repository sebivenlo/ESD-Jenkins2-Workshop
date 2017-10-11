/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pub;

import PubExceptions.StomachException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Pia Erbrath
 */
public class StomachTest {
    
    Stomach sto;
    
    @Before
    public void setUp() {
        sto = new Stomach(0.9);
    }

    @Test
    public void test_getVolume() {
        assertEquals("stomach should be empty", 0.0, sto.getVolume(), 0.001);
    }

    @Test
    public void test_getMaxVolume() {
        assertEquals("volume should be 0.9",0.9, sto.getMaxVolume(), 0.001);
    }

    @Test
    public void test_addBeer_ok() {
        try {
            assertEquals("volume should be 0.0", 0.0, sto.getVolume(), 0.001);
            sto.add(DrinkVolume.PINT);
            assertEquals("volume should be 0.57", 0.57, sto.getVolume(), 0.001);
        } catch (Exception e) {
            fail("here should be no exception");
        }
    }

    @Test
    public void test_addBeer_Exception() {
        Stomach sto = new Stomach(0.1);
        try {
            sto.add(DrinkVolume.PINT);
            fail("here should be an exception");
        } catch (StomachException se) {
            System.out.println("all ok");
        } catch (Exception e) {
            fail("here should be another exception");
        }
    }

    
}
