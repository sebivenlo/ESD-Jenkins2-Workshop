/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pub;

import PubExceptions.EmptyStockException;
import PubExceptions.OverflowedStockException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Pia Erbrath
 */
public class StockTest {

    private Time time;
    private Stock stock;

    @Before
    public void setUp() {
        time = new Time();
        stock = new Stock(30.57);
    }

    @Test
    public void test_getSize() {
        assertEquals("stock volume should be 30,57", 30.57,
                stock.getMaximumSize(), 0.001);
    }

    @Test
    public void test_getCurrentSize() {
        assertEquals("current stock volume should be 30,57", 30.57, 
                stock.getMaximumSize(), 0.001);
    }

    @Test
    public void test_getBeer() {

        try {
            Beer b = stock.getBeer(DrinkVolume.PINT, time);
            assertSame("here should be the right beer", DrinkVolume.PINT, 
                    b.getVolume());
        } catch (EmptyStockException ex) {
            fail("here should be no exception");
        }

    }

    @Test
    public void test_EmptyStockException() {
        try {
            Stock stock1 = new Stock(0.1);
            stock1.getBeer(DrinkVolume.PINT, time);
            fail("here should be an exception");
        } catch (EmptyStockException es) {
            System.out.println("all ok");
        } catch (Exception e) {
            fail("here sshould be another exception");
        }
    }

    @Test
    public void fill_stock() {
        try {
            assertEquals("stock volume should be normal", 30.57,
                stock.getCurrentSize(), 0.001);
            stock.getBeer(DrinkVolume.SMALL, time);
            assertEquals("stock volume should be lower", 30.37,
                stock.getCurrentSize(), 0.001);
            stock.fill(0.2);;
            assertEquals("stock should now have a volume from 30", 30.57,
                stock.getCurrentSize(), 0.001);
        } catch (Exception ex) {
            fail("here should be no exception");
        }
    }

    @Test
    public void test_OverflowedStockException_fill() {
        try {
            stock.fill(30.0);
            fail("here shouuld be an exception");
        } catch (OverflowedStockException ex) {
            System.out.println("all ok");
        } catch (Exception e) {
            fail("here should be another exception");
        }
    }
}
