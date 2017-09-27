/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PubExceptions;


/**
 * This exception should be thrown when the maximal volume of the stomach is 
 * reached.
 * @author Pia Erbrath
 */
public class StomachException extends Exception{
     
    public StomachException() {
        super("stomach is full");
    }
    
}
