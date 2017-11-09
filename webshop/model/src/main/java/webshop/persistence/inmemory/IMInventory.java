package webshop.persistence.inmemory;

import java.io.PrintStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import webshop.business.NegativeQuantityException;
import webshop.business.Product;
import webshop.business.ProductQuantity;
import webshop.persistence.mappers.ProductMapper;

/**
 * Empty implementation of IMProductContainer. To make purpose of
 * IMProductContainer object clear, inventory in this case.
 *
 * @author hom
 */
public class IMInventory extends IMProductContainer {
    public static final String DEFAULT_INVENTORY_FILE= "resources/inventory.csv";
    public IMInventory( ProductMapper productMapper ) {
        List<String> allLines = webshop.utils.Utils.readFile(DEFAULT_INVENTORY_FILE);
        int lineNr = 1;
        for ( String line : allLines ) {
            if ( !line.trim().isEmpty() ) {
                System.out.println( "inventory line = " + line );
                String[] attributes = line.trim().split( "\\s*;\\s*" );
                try {
                    long id = Long.parseLong( attributes[0].trim() );
                    int quantity = Integer.parseInt( attributes[1].trim() );
                    Product p = productMapper.getProductById( id );
                    ProductQuantity pq = new ProductQuantity( p, quantity, 0l );
                    merge( pq );
                } catch ( NumberFormatException | NegativeQuantityException rte ) {
                    Logger.getLogger( IMProductMapper.class.getName() ).log(
                            Level.WARNING,
                            "something wrong with line " + lineNr + " \"" + line
                            + "\"", rte );

                }
                lineNr++;
            }
        }
    }

    public void printInventory(PrintStream out) {
        this.getContents().forEach(pq-> out.println(pq));
    }
}
