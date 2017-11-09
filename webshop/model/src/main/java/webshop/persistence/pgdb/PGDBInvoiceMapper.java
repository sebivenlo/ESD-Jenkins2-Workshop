package webshop.persistence.pgdb;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import webshop.business.Invoice;
import webshop.business.InvoiceLine;
import webshop.business.Product;
import webshop.business.ProductQuantity;
import static java.sql.Date.valueOf;
import java.util.Optional;
import webshop.business.VATLevel;

import webshop.persistence.mappers.PersistenceException;

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
        webshop.persistence.mappers.InvoiceMapper {

    /**
     * Save the invoice and its lines.
     *
     * @param invoice the invoice
     * @return the database invoice id.
     */
    @Override
    public long save( Invoice invoice ) {
        long invoiceId = invoice.getNumber();
        String sql1
                = "insert into " + getInvoiceTableName()
                + " (invoice_id,invoice_date,final_sales_price,"
                + "low_vat_value,high_vat_value,price_reduction,customer_birthday)"
                + " values(?,?,?,?,?,?,?)";
        String sql2
                = "insert into " + getInvoiceLineTableName()
                + " (invoice_id,quantity,product_id,sales_price,vat_amount)\n"
                + "values(?,?,?,?,?)";
        try ( QueryHelper qh = new QueryHelper() ) {
            if ( invoiceId == 0 ) {
                invoiceId = nextInvoiceId( qh );
            } else {
                invoiceId = invoice.getNumber();
            }
            final long dbId = invoiceId;
            java.sql.Date cbd = null;
            if ( invoice.getCustomerBirthDay() != null ) {
                cbd = valueOf( invoice.getCustomerBirthDay() );
            }
            qh.doInsert( sql1, dbId, valueOf( invoice.getInvoiceDate() ),
                    invoice.getTotalPriceIncludingVAT(),
                    invoice.getVATValue( VATLevel.LOW ),
                    invoice.getVATValue( VATLevel.HIGH ),
                    invoice.getPriceReduction(),
                    cbd );
            List<InvoiceLine> lines = invoice.getLines();
            for ( InvoiceLine il : lines ) {
                il.setInvoiceNumber( dbId );
                qh.doInsert( sql2, dbId, il.getQuantity(),
                        il.getProduct().getId(), il.getConsumerPrice(), il
                        .getVat() );

            }
        }
        return invoiceId;
    }

    public long nextInvoiceId( final QueryHelper qh ) {
        return qh.getNextValue( getInvoiceIdSequenceName() );
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

    //<editor-fold defaultstate="expanded" desc="T07_B implement load PRO2_2; __STUDENT_ID__ ;WEIGHT 2">
    /**
     * Load the invoice from the database. Note that we use a join in the sql
     * query, which pulls out invoice and invoice line data at once in one
     * result set. Rationale: less round trip to the database and therefor a lot
     * faster. To deal with this problem you need to use a do {...} while(...)
     * construction, nested inside if ()- test to verify that the result set has
     * any data (HINT: call rs.next()), because you can pick up the invoice data
     * from the first record. <br/>
     * See the invoice table definition (on your exam documentation or in the
     * sql table defintion) on what fields need to be restored into the invoice
     * object. The same applies to the invoice line.
     *
     * @param invoiceId
     * @return the loaded invoice
     */
    @Override
    public Invoice load( long invoiceId ) {
        String query1
                = "select * "
                + " from " + getInvoiceTableName()
                + " natural join "
                + getInvoiceLineTableName()
                + " where invoice_id=? order by invoice_line_id";
        Invoice invoice = null;
        LocalDate invoiceDate;
        long final_sales_price;
        long low_vat_value;
        long high_vat_value;
        long price_reduction;
        LocalDate customer_birthday;

        List<InvoiceLine> ilist = new ArrayList<>();
        try ( QueryHelper qh = new QueryHelper();
                ResultSetWrapper rs = qh.doSelectWrapped( query1, invoiceId ); ) {
            // Use a if and then a do {..  } while() loop
            //Start Solution::replacewith:://TODO T07_B implement part of PGDBInvoiceMapper.load()
            if ( rs.next() ) {
                // pick up invoice part from first record.
                invoiceDate = Optional.ofNullable(
                        rs.getDate( "invoice_date" ) )
                        .map( sql -> sql.toLocalDate() )
                        .orElse( null );
                final_sales_price = rs.getLong( "final_sales_price" );
                low_vat_value = rs.getLong( "low_vat_value" );
                high_vat_value = rs.getLong( "high_vat_value" );
                price_reduction = rs.getLong( "price_reduction" );
                customer_birthday = Optional.ofNullable(
                        rs.getDate( "customer_birthday" ) )
                        .map( sql -> sql.toLocalDate() )
                        .orElse( null );
                do {
                    long productId = rs.getLong( "product_id" );
                    int qty = rs.getInt( "quantity" );
                    Product product = PGDBWebshopFactory.INSTANCE
                            .getProductMapper().getProductById( productId );
                    InvoiceLine il = new InvoiceLine( invoiceId,
                            new ProductQuantity( product, qty ) );
                    ilist.add( il );
                } while ( rs.next() );
                invoice = new Invoice( invoiceId, invoiceDate, ilist );
                invoice.setCustomerBirthDay( customer_birthday );
                invoice.setPriceReduction( price_reduction );
                invoice.setLowVatValue( low_vat_value );
                invoice.setHighVatValue( high_vat_value );
                invoice.setFinalSalesPrice( final_sales_price );

            }
            //End Solution::replacewith::
        } catch ( PersistenceException pex ) {
            Logger.getLogger( PGDBInvoiceMapper.class.getName() ).log(
                    Level.SEVERE, pex.getCause().getMessage(), pex );
        }
        return invoice;
    }
    //</editor-fold>

    String getInvoiceTableName() {
        return "invoices";
    }

    String getInvoiceLineTableName() {
        return "invoice_lines";
    }

    @Override
    public Invoice get( long id1 ) {
        return load( id1 );
    }

    String getInvoiceIdSequenceName() {
        return "invoices_invoice_id_seq";
    }

    @Override
    public long nextInvoiceId() {
        return new QueryHelper().getNextValue( getInvoiceIdSequenceName() );
    }

}
