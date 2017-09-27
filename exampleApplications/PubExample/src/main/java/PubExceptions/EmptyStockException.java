/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PubExceptions;


/**
 * This exception should be thrown when the stock is empty and the barkeeper tries to tap beer from it.
 * @author Pia Erbrath
 */
public class EmptyStockException extends  Exception{

    public EmptyStockException() {
        super("Sorry, stock is empty!");
    }
    
}
