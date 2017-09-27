/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PubExceptions;


/**
 * This exception should be thrown when a guest get the StomachException.
 * @author Pia Erbrath
 */
public class DrunkenException extends RuntimeException {

    public DrunkenException() {
        super("You are drunk!");
    }
    
    
    
}
