package webshop.persistence.pgdb;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Use the cart table. This cart uses next vale of the database sequence defined
 * for the cart.
 *
 * @author hom
 */
class PGDBCart extends PGDBProductContainer {

    private static final long serialVersionUID = 1L;

    public PGDBCart() {
        try (QueryHelper qh = new QueryHelper()){
            owner_id = qh.getNextValue( "session_id" );
            System.out.println( "new cart owner_id = " + owner_id );
        }
    }

    @Override
    public String getTableName() {
        return "carts";
    }
}
