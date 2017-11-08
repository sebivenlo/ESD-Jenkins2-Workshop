package webshop.persistence.pgdb;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import webshop.entities.Invoice;
import webshop.entities.InvoiceLine;
import webshop.entities.Product;
import webshop.business.ProductQuantity;

import webshop.persistence.PersistenceException;

/**
 * Persists all of invoice to the database. This mapper only provides create and
 * select database operations. Delete and update are not supported in this api.
 * Upon create (store or persist) the invoice and its lines are save in one
 * transaction. This class uses the QueryHelper to do the transaction
 * management.
 *
 * @author hom
 */
public class PGDBInvoiceMapper implements
        webshop.entities.InvoiceMapper {

    /**
     * Save the invoice and its lines.
     *
     * @param invoice the invoice
     * @return the database invoice id.
     */
    @Override
    public long save( Invoice invoice ) {
        int invoiceId = 0;
        String sql1
                = "insert into " + getInvoiceTableName()
                + " (invoice_id,invoice_date) values(?,?)";
        String sql2
                = "insert into " + getInvoiceLineTableName()
                + " (invoice_id,quantity,product_id,sales_price,vat_amount)\n"
                + "values(?,?,?,?,?)";
        try ( QueryHelper qh = new QueryHelper() ) {
            int id = (int) qh.getNextValue( getInvoiceIdSequenceName() );
            qh.doInsert( sql1, id ,invoice.getInvoiceDate());
            invoice.getLines().stream().forEach( il -> {
                qh.doInsert( sql2, id, il.getQuantity(),
                        il.getProduct().getId(), il.getConsumerPrice(), il
                        .getVat() );
            } );
            invoiceId = id;
        }
        return invoiceId;
    }

    @Override
    public List<Long> getInvoiceIDs() {
        String sql = "select invoice_id from " + getInvoiceTableName();
        List<Long> result = new ArrayList<>();
        try ( QueryHelper qh = new QueryHelper() ) {
            ResultSetWrapper rs = qh.doSelectWrapped( sql );
            while ( rs.next() ) {
                result.add( rs.getLong( "invoice_id" ) );
            }
            return result;
        }
    }

    @Override
    public Invoice load( long invoiceId ) {
        String query1
                = "select invoice_id, invoice_date, invoice_line_id, "
                + "quantity, product_id, sales_price, vat_amount"
                + " from " + getInvoiceTableName()
                + " natural join "
                + getInvoiceLineTableName()
                + " where invoice_id=?";
        Invoice result = null;
        List<InvoiceLine> ilist = new ArrayList<>();
        try ( QueryHelper qh = new QueryHelper();
                ResultSetWrapper rs = qh.doSelectWrapped( query1, invoiceId ); ) {
            if ( rs.next() ) {
                // pick up invoice part from first record.
                java.sql.Date invoiceDate = rs.getDate( "invoice_date" );
                do {
                    long productId = rs.getLong( "product_id" );
                    int qty = rs.getInt( "quantity" );
                    Product product = PGDBWebshopFactory.INSTANCE
                            .getProductMapper().getProductById( productId );
                    InvoiceLine il = new InvoiceLine( invoiceId,
                            new ProductQuantity( product, qty ) );
                    ilist.add( il );
                } while ( rs.next() );
                result = new Invoice( invoiceId, invoiceDate, ilist );
            }
        } catch ( PersistenceException pex ) {
            Logger.getLogger( PGDBInvoiceMapper.class.getName()).log( 
                            Level.SEVERE, pex.getCause().getMessage(), pex);
        }
        return result;
    }

    String getInvoiceTableName() {
        return "invoice";
    }

    String getInvoiceLineTableName() {
        return "invoice_line";
    }

    @Override
    public Invoice get( long id1 ) {
        return load( id1 );
    }

    String getInvoiceIdSequenceName() {
        return "test_invoice_invoice_id_seq";
    }
}
