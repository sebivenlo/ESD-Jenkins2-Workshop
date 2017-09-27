/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pub;

import PubExceptions.StomachException;

/**
 *This class represents the stomach of a guest.
 * @author pia
 */
public class Stomach {
    /**
     * The maximal volume the stomach could have.
     */
    private final double maxVolume;
    /**
     * The current volume the stomach have.
     */
    private double currentVolume;

    /**
     * Creates a new object of Stomach.
     * The currentVolume will be initialized with 0.
     * @param maxVolume the stomach could have in double. 
     */
    public Stomach(double maxVolume) {
        this.maxVolume = maxVolume;
        currentVolume = 0;
    }

    /**
     * Returns the current volume of the stomach.
     * @return a double
     */
    public double getVolume() {
        return currentVolume; 
    }

    /**
     * Returns the maximum volume the stomach could have.
     * @return a double
     */
    public double getMaxVolume() {
        return maxVolume;
    }

    /**
     * This method adds a drink to the stomach volume.
     * @param drink the drink volume which should be added.
     * @throws StomachException when the stomach is too full.
     */
    void add(DrinkVolume drink) throws StomachException{
        if(currentVolume + drink.getSize() >= maxVolume) throw new StomachException();
        currentVolume += drink.getSize();
    }
    
}
