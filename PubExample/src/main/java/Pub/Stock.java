/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pub;

import PubExceptions.EmptyStockException;
import PubExceptions.OverflowedStockException;

/**
 * This class represents a stock. It has a maximal volume and from this volume a
 * beer could be tap or the volume could be filled again.
 * @author Pia Erbrath
 */
public class Stock {

    /**
     * The maximal volume of the stock.
     */
    private final double size;
    /**
     * The current volume of the stock. 
     */
    private double currentSize;

    /**
     * This constructor creates a new stock object with an maximum volume.
     * The currentSize is equals to maximum volume, because the stock will be 
     * created as a full stock. 
     * @param generalSize the maximum volume of the stock in double. 
     */
    public Stock(double generalSize) {
        this.size = generalSize;
        this.currentSize = generalSize;
    }

    /**
     * This method taps a beer from the stock.
     * @param volume the size of the beer.
     * @param time the beer is ordered.
     * @return a Beer object.
     * @throws EmptyStockException when the stock has not enough volume for the 
     *  ordered beer. 
     */
    public Beer getBeer(DrinkVolume volume, Time time) throws 
            EmptyStockException {
        if (currentSize < volume.getSize()) {
            throw new EmptyStockException();
        }
        currentSize -= volume.getSize();
        return new Beer(volume, time);
    }

    /**
     * This method returns the maximum volume the stock could have.
     * @return the size in double.
     */
    public double getMaximumSize() {
        return size;
    }

    /**
     * The method fill the stock again.
     * @param liters which should be filled in.
     * @throws OverflowedStockException when the maximum volume of the stock 
     * will be exceeded.
     */
    public void fill(double liters) throws OverflowedStockException {
        if (currentSize + liters > size) {
            throw new OverflowedStockException();
        }
        currentSize += liters;
    }

    /**
     * Returns the current amount of liter in the stock.
     * @return liters in double.
     */
    public double getCurrentSize() {
        double d = currentSize;
        return d;
    }

}
