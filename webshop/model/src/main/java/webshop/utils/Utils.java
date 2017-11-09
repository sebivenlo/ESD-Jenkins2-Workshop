package webshop.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class. Not to be instantiated.
 *
 * @author hom
 */
public class Utils {

    private Utils() {
    }

    /**
     * Read a resource (text file) from the class path using the class loader as
     * a list of lines.
     *
     * @param productsFileName
     * @return the list of lines
     * @throws NumberFormatException
     */
    public static List<String> readFile( String productsFileName ) {
        List<String> allLines = new ArrayList<>();

        ClassLoader cl = Utils.class.getClassLoader();
        try ( InputStreamReader isr = new InputStreamReader(
                cl.getResourceAsStream( productsFileName ) );
                BufferedReader reader = new BufferedReader( isr ); ) {
            String l;
            while ( null != ( l = reader.readLine() ) ) {
                allLines.add( l );
            }

        } catch ( IOException ex ) {
            Logger.getLogger( Utils.class.getName() ).log(
                    Level.SEVERE,
                    "could not load file " + productsFileName
                    + ", leaves container empty.", ex );
        }
        return allLines;
    }
}
