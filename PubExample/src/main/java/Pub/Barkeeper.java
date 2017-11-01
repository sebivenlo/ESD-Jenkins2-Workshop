/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pub;

import PubExceptions.BobException;
import PubExceptions.EmptyStockException;
import PubExceptions.TooYoungException;

/**
 * This class represent the barkeeper of the pub.
 * @author Pia Erbrath
 */
public class Barkeeper {

    /**
     * The default constructor. 
     */
    Barkeeper() {
    }

    /**
     * This method taps a beer from the stock.
     * @param stock from where the beer should be taps.
     * @param orderer the guest which is ordered.
     * @param volume the size of the beer which is ordered.
     * @return a new Beer object.
     * @throws TooYoungException when the orderer is not eighteen.
     * @throws BobException when the orderer is the bob.
     * @throws EmptyStockException when the stock does not have enough liter for
     *  this beer
     */
    public Beer tapBeer(Stock stock, Guest orderer, DrinkVolume volume) throws 
            TooYoungException, BobException, EmptyStockException {
        if (orderer.getAge() < 18) {
            throw new TooYoungException();
        }
        if (orderer.isBob()) {
            throw new BobException();
        }
        return stock.getBeer(volume, new Time());

    }

}
