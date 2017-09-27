/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pub;

/**
 * THis class represent a beer.
 * A beer will have an id, a size and a ordered time.
 * @author Pia Erbrath
 */
public class Beer {

    /**
     * Time the beer is created.
     */
    private final Time time;
    /**
     * The size of the beer. 
     */
    private final DrinkVolume volume;
    /**
     * Number which indicate how many beers was ordered.
     */
    private static int AMOUNT = 0;
    /**
     * Id of the beer, depended on the amount.
     */
    private final int id;
    
    /**
     * Creates a new Beer object. 
     * @param volume the beer should have.
     * @param time the beer is created.
     */
    public Beer(DrinkVolume volume, Time time) {
        this.time = time;
        this.volume = volume;
        id = ++AMOUNT;
    }

    /**
     * Returns the size of the beer.
     * @return a DrinkVolume object.
     */
    public DrinkVolume getVolume() {
        return volume;
    }

    /**
     * Returns the time the beer was created.
     * @return a Time object.
     */
    public Time getTime() {
        return time;
    }

    /**
     * Returns the ID of the beer.
     * @return an int.
     */
    public int getID() {
        return id;
    }
    
}
