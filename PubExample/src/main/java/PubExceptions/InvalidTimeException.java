/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PubExceptions;


/**
 * This exception should be thrown when an time object has an invalid value for 
 * hour or/and minute.
 * @author Pia Erbrath
 */
public class InvalidTimeException extends Exception{

    public InvalidTimeException() {
        super("Time is invalid!");
    }
    
}
