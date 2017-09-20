/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pub;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class which takes a simulation of a pub.
 *
 * @author Pia Erbrath
 */
public class PubSimulator {

    private static List<Guest> guests;

    /**
     * Randomizer to create random int, double and boolean.
     */
    private Random randomizer = new Random();

    /**
     * Method which starts a simulation.
     *
     * @param args the command line arguments
     */
    public static void main( String[] args ) {
        new PubSimulator().run();
    }

    enum PubActivity {

        WELCOME, ORDER, DRINK, CHECK_STOCK, FILL_STOCK, GOODBYE
    }

    public void run() {
        //needed parameter
        guests = new ArrayList<Guest>();

        Pub pub = new Pub( 100 );

        int amountGuest = randomizer.nextInt( 100 );
        //create guests
        for ( int i = 0; i < amountGuest; i++ ) {
            // simulates between non drinkers 
            // and real boozers
            double drinkCapacity = 4* randomizer.nextDouble()+0.1;
            int age = randomizer.nextInt( 120 ) + 1;
            boolean isBob = randomizer.nextBoolean();
            Guest g = new Guest( age, drinkCapacity, isBob );
            guests.add( g );
            pub.welcomeGuest( g );
        }

        //do stuff
        for ( int i = 0; i < 2000; i++ ) {
            System.out.println( "amount of guest: " + pub.getNumberOfGuests() );
            PubActivity activity = PubActivity.values()[ randomizer.nextInt( PubActivity.values().length ) ];

            switch ( activity ) {
                case ORDER:
                    randomOrderingBeer( guests, pub );
                    break;
                case FILL_STOCK:
                    randomFillStock( pub );
                    break;
                case WELCOME:
                    randomWelcome( guests, pub );
                    break;
                case GOODBYE:
                    randomGoodBye( guests, pub );
                    break;
                case CHECK_STOCK:
                    System.out.println( "Current stock is: "
                            + pub.getCurrentStockVolume() );
                    break;
                case DRINK:
                default:

                    pub.orderBeer( new Guest( randomizer.nextInt( 50 ) + 1,
                            randomizer.nextDouble() + randomizer.nextInt( 3 ),
                            false ), DrinkVolume.SMALL );
                    System.out.println( "unknown guest tries to order a beer" );
            }
        }
    }

    /**
     * Orders for a random guest a random beer.
     *
     * @param guests List with guests of the pub.
     * @param pub where beer is ordered.
     */
    void randomOrderingBeer( List<Guest> guests, Pub pub ) {
        int guest = randomizer.nextInt( guests.size() );
        Guest g = guests.get( guest );
        DrinkVolume drink = DrinkVolume.values()[ randomizer.nextInt( DrinkVolume.values().length ) ];

        System.out.println( "ordering " + g.getAge() + " " + drink );
        pub.orderBeer( g, drink );
    }

    /**
     * Add a new guest to the pub.
     *
     * @param guests list with all guests if the pub.
     * @param pub where a guest should be added.
     */
    void randomWelcome( List<Guest> guests, Pub pub ) {
        int age = randomizer.nextInt( 90 );
        double stomach = randomizer.nextDouble() + randomizer.nextInt( 4 );
        boolean bo = randomizer.nextBoolean();
        Guest newGuest = new Guest( age, stomach, bo );
        guests.add( newGuest );
        pub.welcomeGuest( newGuest );
        System.out.println( "add new guest " + newGuest );
    }

    /**
     * Remove a random guest from the pub.
     *
     * @param guests list with all guests of the pub.
     * @param pub where the guest should be removed.
     */
    void randomGoodBye( List<Guest> guests, Pub pub ) {
        int guest = randomizer.nextInt( guests.size() );
        Guest g = guests.get( guest );
        pub.goodByeGuest( g );
        guests.remove( g );
        System.out.println( "removed guest " + guest );
    }

    /**
     * Fill the stock of a pub with a random volume.
     *
     * @param pub where the stock should be filled.
     */
    void randomFillStock( Pub pub ) {
        double add = randomizer.nextDouble() + randomizer.nextInt( 100 );
        System.out.println( "fill stock with " + add + " liters" );
        pub.fillStock( add );
    }

}
