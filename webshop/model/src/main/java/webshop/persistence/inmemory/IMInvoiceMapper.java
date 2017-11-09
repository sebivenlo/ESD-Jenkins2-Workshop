/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webshop.persistence.inmemory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import webshop.business.Invoice;
import webshop.business.InvoiceLine;
import static webshop.persistence.inmemory.FSPersistenceNames.getDirFor;
import static webshop.persistence.inmemory.FSPersistenceNames.getFileFor;
import webshop.persistence.mappers.InvoiceMapper;

/**
 * Hybrid mapper that works mainly in memory but also persists to the file
 * system of the underlying operating system. This is a singleton by being an
 * enum type.
 *
 * @author hom
 */
enum IMInvoiceMapper implements InvoiceMapper {

    INSTANCE;
    Map<Long, Invoice> invoiceMap = new ConcurrentHashMap<>();
    Map<Long, List<InvoiceLine>> invoiceLineMap = new ConcurrentHashMap<>();

    private long lastInvoiceNumber = 0;
    private long lastInvoiceLineNumber = 0;

    //<editor-fold defaultstate="expanded" desc="T03_B; __STUDENT_ID__ ;WEIGHT 1">
    /**
     * Writes the invoice object to the appropriate file.
     *
     * You can use the static method
     * {@link webshop.persistence.inmemory.FSPersistenceNames#getFileFor} (see
     * imports) to aquire a reference to such file. <br/>
     * Use {@link java.io.ObjectOutputStream} to write the invoice to the path.
     * For the exam it is required that you use try-with-resources and the
     * appropriately "wrapped" streams. Note that streams in this context are
     * the streams as defined in {@link java.io} and {@link java.io}, NOT the
     * java.util.stream package.<br/>
     *
     * Make sure you log the checked exceptions that might occur. NetBeans IDE
     * can be helpful in this case. You may NOT alter the signature or throws
     * clause of this method.
     *
     * After the invoice object has been written successful to file, it should
     * be detached from the cart (use detachedFromCart() method)
     *
     * @param invoice the object to save
     * @return the invoice id of the saved invoice
     */
    @Override
    public long save( Invoice invoice ) {
        long invoiceId = nextInvoiceId();
        invoice.setNumber( invoiceId );
        invoiceMap.put( invoiceId, invoice );
        List<InvoiceLine> lines = invoice.getLines();

        for ( InvoiceLine il : lines ) {
            il.setInvoiceNumber( invoiceId );
        }
        invoiceLineMap.put( invoiceId, lines );
        //TODO T03_B implement IMInvoiceMapper.save(Invoice). read javadoc first
        //Start Solution::replacewith:: try {
        try (
                FileOutputStream fos
                = new FileOutputStream( getFileFor( Invoice.class, invoiceId ) );
                ObjectOutputStream oos = new ObjectOutputStream( fos ); ) {

            oos.writeObject( invoice );
        
        } catch ( FileNotFoundException ex ) {
            Logger.getLogger( IMInvoiceMapper.class.getName() ).log(
                    Level.SEVERE, null, ex );
        } catch ( IOException ex ) {
            Logger.getLogger( IMInvoiceMapper.class.getName() ).log(
                    Level.SEVERE, null, ex );
        }
        //End Solution::replacewith::// end of task.
        invoice.detachFromCart();
        return invoiceId;
    }
    //</editor-fold>

    @Override
    public List<Long> getInvoiceIDs() {
        List<Long> result = new ArrayList<>();
        result.addAll( invoiceMap.keySet() );
        result.sort( ( a, b ) -> Long.compare( a, b ) );
        return result;
    }

    /**
     * Loads the invoice from the file system and saves it into the local map.
     *
     * @param invoiceId for the invoice
     * @return the requested invoice
     */
    @Override
    public Invoice load( long invoiceId ) {
        Invoice result = null;
        try ( FileInputStream fis
                = new FileInputStream( getFileFor( Invoice.class, invoiceId ) );
                ObjectInputStream ois = new ObjectInputStream( fis ); ) {
            result = (Invoice) ois.readObject();
            invoiceMap.put( invoiceId, result );
            invoiceLineMap.put( invoiceId, result.getLines() );
        } catch ( FileNotFoundException ex ) {
            Logger.getLogger( IMInvoiceMapper.class.getName() )
                    .log( Level.SEVERE, null, ex );
        } catch ( IOException | ClassNotFoundException ex ) {
            Logger.getLogger( IMInvoiceMapper.class.getName() )
                    .log( Level.SEVERE, null, ex );
        }
        return result;
    }

    private static final Pattern isSerFile = Pattern.compile( "^\\d+.ser$" );
    private static final BiPredicate<Path, BasicFileAttributes> matcher
            = ( Path p, BasicFileAttributes fa ) -> isSerFile.matcher( p
                    .getFileName().toString() ).matches();

    /**
     * Loads all from invoices under the designated directory. The Map are
     * loaded with the files found under the directory determined by
     * {@link webshop.persistence.inmemory.FSPersistenceNames#getDirFor(java.lang.Class)}.
     */
    public void loadAll() throws IOException {
        Path dir = getDirFor( Invoice.class );
        Files.find( dir, 1, matcher ).forEach( f -> {
            Long id = Long.parseLong(
                    f.getFileName().toString().split( "\\." )[0] );
            load( id );
        } );
    }

    /**
     * For testing, clean up file system.
     *
     */
    void deleteAllFiles() {
        try {
            Path dir = getDirFor( Invoice.class );
            Files.find( dir, 1, matcher ).forEach( f -> {
                try {
                    Files.deleteIfExists( f );
                    System.out.println( "dropped " + f );
                } catch ( IOException ex ) {
                    Logger.getLogger( IMInvoiceMapper.class.getName() ).log(
                            Level.SEVERE, null, ex );
                }
            } );
        } catch ( IOException ex ) {
            Logger.getLogger( IMInvoiceMapper.class.getName() ).log(
                    Level.SEVERE, null, ex );
        }
    }

    /**
     * Delete indicated invoice. Remove from maps. If the file does not exist,
     * no exception is thrown.
     *
     * @param invoiceId to be deleted.
     */
    void delete( long invoiceId ) {
        try {
            File f = getFileFor( Invoice.class, invoiceId );
            f.delete();
            invoiceLineMap.remove( invoiceId );
            invoiceMap.remove( invoiceId );
        } catch ( IOException ex ) {
            Logger.getLogger( IMInvoiceMapper.class.getName() ).log(
                    Level.SEVERE, null, ex );
        }
    }

    /**
     * Clear the maps for this mapper.
     */
    void clearMaps() {
        this.invoiceLineMap.clear();
        this.invoiceMap.clear();
    }

    /**
     * Get and invoice by id.
     *
     * @param id1 of the invoice
     * @return the invoice or null if not available.
     */
    @Override
    public Invoice get( long id1 ) {
        if ( !invoiceMap.containsKey( id1 ) ) {
            load( id1 );
        }
        return invoiceMap.get( id1 );
    }

    private long nextInvoiceId = 0;

    /**
     * The in memory sequence.
     *
     * @return next number.
     */
    @Override
    public long nextInvoiceId() {
        return ++nextInvoiceId;
    }
}
