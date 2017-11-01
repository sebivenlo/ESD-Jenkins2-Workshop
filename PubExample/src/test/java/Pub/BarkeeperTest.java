/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pub;

import PubExceptions.BobException;
import PubExceptions.EmptyStockException;
import PubExceptions.TooYoungException;
import java.util.EmptyStackException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Pia Erbrath
 */
public class BarkeeperTest {

    private Barkeeper barkeeper;
    
    @Before
    public void setUp() {
        barkeeper = new Barkeeper();
    }
    
    @Test
    public void test_tapBeer_ok() {

        try {
            assertSame("Should get a beer with pint volume", DrinkVolume.PINT,
                    barkeeper.tapBeer(new Stock(20), new Guest(56, 2, false),
                            DrinkVolume.PINT).getVolume());

        } catch (TooYoungException | BobException | EmptyStockException ex) {
            fail("here should be no exception");
        }

    }
    
    @Test
    public void test_tapBeer_BobException() {

        try {
            assertSame("Should get a beer with pint volume", DrinkVolume.PINT,
                    barkeeper.tapBeer(new Stock(20), new Guest(56, 2, true),
                            DrinkVolume.PINT).getVolume());
            fail("here should be bob exception");
        } catch (BobException be) {
            System.out.println("all ok");
        } catch (TooYoungException | EmptyStockException ex) {
            fail("here should be another exception");
        }
    }
    
    @Test
    public void test_tapBeer_TooYoungException() {

        try {
            assertSame("Should get a beer with pint volume", DrinkVolume.PINT,
                    barkeeper.tapBeer(new Stock(20), new Guest(2, 2, false),
                            DrinkVolume.PINT).getVolume());
            fail("here should be a tooYoungException");
        } catch(TooYoungException te) {
            System.out.println("all ok");
        } catch (BobException | EmptyStockException ex) {
            fail("here should be another exception");
        }
    }
    
    @Test
    public void test_tapBeer_EmptyStockException() {

        try {
            assertSame("Should get a beer with pint volume", DrinkVolume.PINT,
                    barkeeper.tapBeer(new Stock(0.1), new Guest(56, 2, false), 
                            DrinkVolume.PINT).getVolume());
            fail("here should be an emptyStockException");
        } catch(EmptyStockException te) {
            System.out.println("all ok");
        } catch (TooYoungException | BobException ex) {
            fail("here should be another exception");
        }
    }

}
