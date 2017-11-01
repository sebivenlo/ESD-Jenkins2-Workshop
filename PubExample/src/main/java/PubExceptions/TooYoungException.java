/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PubExceptions;




/**
 * This exception should be thrown when a guest orders a beer but he/she is not 
 * older than eighteen.
 * @author Pia Erbrath
 */
public class TooYoungException extends Exception{

    public TooYoungException() {
        super("You are not 18!");
    }
    
}
