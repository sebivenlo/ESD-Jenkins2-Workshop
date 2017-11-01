/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pub;

import PubExceptions.DrunkenException;
import PubExceptions.StomachException;

/**
 *  This class represent a typical guest of a pub.
 * He / she want drink beer and can be drunken.
 * @author Pia Erbrath
 */
public class Guest {
    
    /**
     * The stomach of the guest.
     */
    private final Stomach stomach;
    /**
     * The age of the guest.
     */
    private final int age;
    /**
     * Represent the bob position.
     */
    private final boolean bob;

    /**
     * Creates a new guest.
     * @param age of the guest.
     * @param stomachVolume of the guest.
     * @param bob yes?
     */
    public Guest(int age, double stomachVolume, boolean bob) {
        this.age = age;
        this.stomach = new Stomach(stomachVolume);
        this.bob = bob;
    }

    /**
     * Returns the age of the guest.
     * @return the age as int.
     */
    public int getAge() {
        return age;
    }

    /**
     * Returns the stomach of the guest.
     * @return a Stomach object.
     */
    public Stomach getStomach() {
        return stomach;
    }
    
    /**
     * Answers the question is this guest a bob.
     * @return a boolean.
     */
    public boolean isBob() {
        return bob;
    }


    public void drink(Beer beer) throws DrunkenException{
        try {
            stomach.add(beer.getVolume());
        } catch (StomachException ex) {
            throw new DrunkenException();
        }
    }
    
}
