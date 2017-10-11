/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PubExceptions;

/**
 * This exception should be thrown when an object tries to fill the stock but 
 * the maximal volume is reached. 
 * @author Pia Erbrath
 */
public class OverflowedStockException extends Exception{

    public OverflowedStockException() {
        super("Stock is too full");
    }
    
}
