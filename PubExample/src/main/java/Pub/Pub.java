/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pub;

import PubExceptions.BobException;
import PubExceptions.EmptyStockException;
import PubExceptions.OverflowedStockException;
import PubExceptions.TooYoungException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class represent a typical pub with guests and beer.
 *
 * @author Pia Erbrath
 */
public class Pub {

    /**
     * The beer stock of this pub.
     */
    private final Stock stock;
    /**
     * The barkeeper of the pub which taps the beer.
     */
    private final Barkeeper barkeeper;
    /**
     * The storage of every guest which is in the pub.
     */
    private final List<Guest> guests;

    /**
     * Creates a new pub with a barkeeper, a stock and no guests.
     *
     * @param stockVolume which the pub should have.
     */
    public Pub(double stockVolume) {
        stock = new Stock(stockVolume);
        guests = new ArrayList<>();
        barkeeper = new Barkeeper();
    }

    /**
     * Returns the number of guests which are current in the pub.
     *
     * @return the amount in int.
     */
    public int getNumberOfGuests() {
        return guests.size();
    }

    /**
     * Adds a guests to the pub.
     *
     * @param guest which comes in.
     */
    public void welcomeGuest(Guest guest) {
        guests.add(guest);
    }

    /**
     * Removes a guest from the pub.
     *
     * @param guest which needs to go / goes.
     */
    public void goodByeGuest(Guest guest) {
        guests.remove(guest);
    }

    /**
     * Orders a beer for a certain guest.
     *
     * @param orderer guest which order the beer.
     * @param type size of the beer.
     */
    public void orderBeer(Guest orderer, DrinkVolume type) {
        if (guests.contains(orderer)) {
            try {

                Beer beer = barkeeper.tapBeer(stock, orderer, type);
                orderer.drink(beer);
            } catch (TooYoungException | BobException | EmptyStockException e) {
                System.out.println(e);
            }
        }
    }

    /**
     * Fills the stock.
     *
     * @param delivery liters(double) which should be added.
     */
    public void fillStock(double delivery) {
        try {
            stock.fill(delivery);
        } catch (OverflowedStockException ex) {
            System.out.println(ex);
        }
    }

    /**
     * Returns the current volume of the stock.
     *
     * @return a double with the current stock size.
     */
    public double getCurrentStockVolume() {
        return stock.getCurrentSize();
    }

}
