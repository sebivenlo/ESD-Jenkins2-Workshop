/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pub;

import PubExceptions.InvalidTimeException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;

/**
 * The Time class represent the time.
 * I created my own one to can use only needed methods.
 * @author Pia Erbrath
 */
public class Time {

    /**
     * the current hour
     */
    private final int hours;
    /**
     * the current minute
     */
    private final int minutes;

    /**
     * This default constructor reads the current time from the system.
     * For this I used the class LocalDateTime with the static method now().
     */
    public Time() {
        LocalDateTime l = LocalDateTime.now();
        hours = l.getHour();
        minutes = l.getMinute();
    }
    
    /**
     * This constructor was created for testing purpose and create new Time objects
     * from an existing one.
     * @param hour the current hour
     * @param min the current minute
     * @throws InvalidTimeException when following conditions are not matched: (0 < hour < 24) and (0 < min < 60).
     */
    Time(int hour, int min) throws InvalidTimeException{
        if(hour < 0 || hour > 23 || min < 0 || min > 59) {
            throw new InvalidTimeException();
        }

        this.minutes = min;
        this.hours = hour;
    }

    /**
     * Returns the minutes.
     * @return minute in int.
     */
    public int getMinutes() {
        return minutes;
    }

    /**
     * Returns the hour.
     * @return hour in int.
     */
    public int getHours() {
        return hours;
    }

    /**
     * This method returns the whole Time object.
     * @return a new Time object.
     * @throws InvalidTimeException when the time object has invalid hour or minutes variables. (see constructor)
     */
    public Time getTime() throws InvalidTimeException {
        return new Time( hours, minutes );
    }

    /**
     * Returns the Time object in an spacial format: ##:##. 
     * @return a String representation.
     */
    @Override
    public String toString() {
        DecimalFormat dec = new DecimalFormat("#00.###");
        return "Time " + dec.format(hours) + ":" + dec.format(minutes);
    }

    /**
     * Looks if this Time object is equals to another object.
     * Control if it is from the same class type and than if the time is the same.
     * @param obj which should be compare.
     * @return true when objects have the same time, else false.
     */
    @Override
    public boolean equals( Object obj ) {

        if ( obj instanceof Time ) {
            Time t = ( Time ) obj;
            if ( t.getHours() == this.getHours() && t.getMinutes() == this.getMinutes() ) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + this.hours;
        hash = 47 * hash + this.minutes;
        return hash;
    }

}
