/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Pia Erbrath
 */
public class DrinkVolumeTest {

    @Test
    public void testValues() {
        DrinkVolume[] l = DrinkVolume.values();
        assertTrue("wrong length, should be 2", 3 == l.length);
        List<DrinkVolume> li = new ArrayList();
        li.addAll(Arrays.asList(l));
        assertTrue("PINT should be available", li.contains(DrinkVolume.PINT));
        assertTrue("SMALL should be available", li.contains(DrinkVolume.SMALL));
        assertTrue("SMALL should be available", li.contains(DrinkVolume.LARGE));
    }

    @Test
    public void testValueOf() {
        DrinkVolume s = DrinkVolume.SMALL;
        DrinkVolume p = DrinkVolume.PINT;

        assertSame("Small should be small", s, DrinkVolume.valueOf("SMALL"));
        assertSame("Pint should be pint", p, DrinkVolume.valueOf("PINT"));
    }

    @Test
    public void size_of_pint() {
        assertEquals("size should be 0,57", 0.57, DrinkVolume.PINT.getSize(), 0.001);
    }

    @Test
    public void size_of_small() {
        assertEquals("size should be 0,2", 0.2, DrinkVolume.SMALL.getSize(), 0.001);
    }

    @Test
    public void size_of_large() {
        assertEquals("size should be 1.0", 1.0, DrinkVolume.LARGE.getSize(), 0.001);
    }

}
