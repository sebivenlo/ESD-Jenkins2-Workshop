/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pub;

import PubExceptions.DrunkenException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Pia Erbrath
 */
public class PubTest {

    private Pub pub;

    @Before
    public void setUp() {
        pub = new Pub(30.0);
    }

    @Test
    public void get_current_stock_volume() {
        assertEquals("size should be 30,0", 30.0, 
                pub.getCurrentStockVolume(), 0.001);
    }

    @Test
    public void fill_stock_without_exception() {
        Guest g = new Guest(47, 30, false);
        pub.welcomeGuest(g);
        for (int i = 0; i < 10; i++) {
            pub.orderBeer(g, DrinkVolume.SMALL);
        }
        pub.fillStock(1.0);
        assertEquals("size should be 29,0", 29.0, 
                pub.getCurrentStockVolume(), 0.001);
    }

    @Test
    public void fill_stock_with_exception() {
        Guest g = new Guest(47, 30, false);
        pub.welcomeGuest(g);
        for (int i = 0; i < 10; i++) {
            pub.orderBeer(g, DrinkVolume.PINT);
        }
        System.out.println("###########");
        pub.fillStock(30.0);
        System.out.println("###########");
        assertEquals(24.3, pub.getCurrentStockVolume(), 0.001);
    }

    @Test
    public void add_guest() {
        assertEquals("there should be no guest", 0, pub.getNumberOfGuests());
        pub.welcomeGuest(new Guest(56, 2.0, false));
        assertEquals("there should be one guest", 1, pub.getNumberOfGuests());
    }

    @Test
    public void sayGoodBey_to_guest() {
        Guest g = new Guest(56, 2.0, false);
        pub.welcomeGuest(g);
        assertEquals("there should be one guest", 1, pub.getNumberOfGuests());
        pub.goodByeGuest(g);
        assertEquals("there should be no guest", 0, pub.getNumberOfGuests());
    }

    @Test
    public void test_getNumberOfGuests() {
        assertEquals("there should be no guest", 0, pub.getNumberOfGuests());
    }

    @Test
    public void test_orderBeer_without_being_in() {
        Guest g = new Guest(56, 2, false);
        pub.orderBeer(g, DrinkVolume.PINT);
        assertEquals("guest is not in the pub!", 0, 
                g.getStomach().getVolume(), 0.001);
    }

    @Test
    public void test_orderBeer_without_Exceptions() {
        Guest g = new Guest(56, 2, false);
        pub.welcomeGuest(g);
        pub.orderBeer(g, DrinkVolume.PINT);
        assertEquals("volume should be increase by volume of pint",
                DrinkVolume.PINT.getSize(), g.getStomach().getVolume(), 0.001);
    }

    @Test
    public void test_orderBeer_with_TooYoungEcxeption() {
        Guest g = new Guest(2, 2, false);
        pub.welcomeGuest(g);
        pub.orderBeer(g, DrinkVolume.PINT);
        assertEquals("guest should not get any beer -> too young", 
                0, g.getStomach().getVolume(), 0.001);
    }

    @Test
    public void test_orderBeer_with_BobException() {
        Guest g = new Guest(47, 2, true);
        pub.welcomeGuest(g);
        pub.orderBeer(g, DrinkVolume.PINT);
        assertEquals("guest should not get any beer -> bob", 0, 
                g.getStomach().getVolume(), 0.001);
    }

    @Test
    public void test_orderBeer_with_DrunkenException() {
        Guest g = new Guest(47, 0.1, false);
        pub.welcomeGuest(g);
        try {
            pub.orderBeer(g, DrinkVolume.PINT);
            fail("here should be an exception thrown");
        } catch (DrunkenException d) {
            System.out.println("all ok");
        } catch (Exception e) {
            fail("another exception");
        }

    }

    @Test
    public void test_orderBeer_with_EmptyStockException() {
        Pub pub1 = new Pub(0.1);
        Guest g = new Guest(47, 0.1, false);
        pub1.welcomeGuest(g);
        pub1.orderBeer(g, DrinkVolume.PINT);
        assertEquals("guest should not get any beer -> empty stock", 0, 
                g.getStomach().getVolume(), 0.001);
    }

}
