/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pub;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Pia Erbrath
 */
public class BeerTest {
    
    private Time time;
    
    @Before
    public void setUp() {
        time = new Time();
    }
    
    
    @Test
    public void test_getVolume() {
        Beer beer = new Beer(DrinkVolume.SMALL, time);
        assertSame("small should be small", DrinkVolume.SMALL, beer.getVolume());
    }
    
    @Test
    public void test_getTime() {
        Beer beer = new Beer(DrinkVolume.PINT, time);
        assertSame("the time shpuld be 12:30", time, beer.getTime());
    }
    
    @Test
    public void test_getOrderID() {
        Beer beer = new Beer(DrinkVolume.PINT, time);
        assertTrue("id needs to be higher than 0", 0 < beer.getID());
    }
    
}
