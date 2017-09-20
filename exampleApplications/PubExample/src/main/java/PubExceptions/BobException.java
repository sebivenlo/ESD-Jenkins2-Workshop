/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PubExceptions;


/**
 * This exception should be thrown when a guest is a bob and want to drink beer.
 * @author Pia Erbrath
 */
public class BobException extends Exception {

    public BobException() {
        super("You are the BOB!");
    }
    
}
