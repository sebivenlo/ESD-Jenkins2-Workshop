package webshop.persistence.mappers;

import java.util.List;
import webshop.business.Invoice;

/**
 *
 * @author hom
 */
public interface InvoiceMapper {

    /**
     * Persist the given invoice.
     *
     * @param invoice to persist
     * @return the id as determined by the persistence layer.
     */
    long save( Invoice invoice );

    /**
     * Load the invoice identified by id.
     *
     * @param invoiceId the identifier
     * @return thee invoice or null if it does not exist.
     */
    Invoice load( long invoiceId );

    /**
     * Get the list of known ids.
     *
     * @return the list
     */
    List<Long> getInvoiceIDs();

    /**
     * Get an invoice by id. If the invoice object does not yet exist, try to
     * load it from the persistence storage.
     *
     * @param id1
     * @return the invoice
     */
    Invoice get( long id1 );

    /**
     * Get the next number for the invoice.
     *
     * @return the number.
     */
    long nextInvoiceId();
}
