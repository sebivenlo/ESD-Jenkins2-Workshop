/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pub;

/**
 * This Enum represent the volume of the ordered drink.
 *
 * @author Pia Erbrath
 */
public enum DrinkVolume {

    PINT( 0.57 ), SMALL( 0.2 ), LARGE( 1.0 );

    /**
     * The size of the volume type.
     */
    private final double size;

    /**
     * Constructor of this class. It is private so that only inside of this
     * class an object of type Volume can be created.
     *
     * @param size of the drink.
     */
    private DrinkVolume( double size ) {
        this.size = size;
    }

    /**
     * Returns the size of the DrinkVolume type.
     *
     * @return the size of the volume as double.
     */
    public double getSize() {
        return size;
    }
}
