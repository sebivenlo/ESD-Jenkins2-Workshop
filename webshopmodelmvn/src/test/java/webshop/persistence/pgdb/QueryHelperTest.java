package webshop.persistence.pgdb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import webshop.persistence.PersistenceException;

/**
 * This test class creates and drops a test table.
 *
 * @author hom
 */
public class QueryHelperTest {

    static DataSource datasource;

    /**
     * Test able definition.
     */
    static final String tableDef
            = "create table if not exists yuk (\n"
            + "  i int, "
            + "  b boolean, "
            + "  s varchar(30), "
            + "  d date"
            + ")";
    static final String dropQuery = "drop table if exists yuk ";
    static final String truncateQuery = "truncate yuk";
    static final String insertQuery
            = "insert into yuk (i,b,s,d) values(?,?,?,?)";

    static final String seqDef = "create sequence  yuk_sequence ";
    static final String dropSeq = "drop sequence if exists yuk_sequence";

    public QueryHelperTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        datasource = QueryHelper.createDataSource();
        creatTestTables();
    }

    @After
    public void tearDown() {
        try ( QueryHelper qh = new QueryHelper() ) {
            qh.doDDL( truncateQuery );
        } catch ( SQLException ex ) {
            Logger.getLogger( QueryHelperTest.class.getName() ).
                    log( Level.SEVERE, null, ex );
        }
    }

    @AfterClass
    public static void tearDownClass() {
        dropTestTable();
    }

    /**
     * Test of getConnection method, of class QueryHelper.
     */
    @Test
    public void testGetConnection() {
        System.out.println( "getConnection" );
        try ( QueryHelper helper = new QueryHelper();
                Connection connection = helper.createConnection(); ) {
            assertTrue( connection instanceof Connection );
        } catch ( Exception ex ) {
            System.out.println( "ex = " + ex );
        }
    }

    /**
     * Test of doDelete method, of class QueryHelper.
     */
    @Test
    public void testDoDelete() throws SQLException {
        System.out.println( "doDelete" );
        try ( QueryHelper helper = new QueryHelper() ) {
            // the next method call effectively starts a transaction for
            // this helper.
            helper.createConnection();

            String query = "delete from yuk";
            helper.doDelete( query );
        }
        try ( QueryHelper helper = new QueryHelper();
                ResultSet rs = helper.doSelect( "select * from yuk" ) ) {
            // expect empty set.
            assertFalse( rs.next() );
        }
    }

    /**
     * Test of doSelect method, of class QueryHelper.
     * <br/>
     * Hint: The test implementor is hinted to use the the "try with resources"
     * WITHOUT a catch construct as for instance in the test above. This is the
     * way the QueryHelper is designed and how it helps to reduce try-catch
     * boiler plate code in it's client classes.
     *
     * @throws java.sql.SQLException
     */
    @Test
    public void testDoSelect() throws SQLException {
        System.out.println( "doSelect" );
        fillTestTable();
    }

    /**
     * Test of doInsert method, of class QueryHelper.
     *
     * @throws java.sql.SQLException
     */
    //@Ignore
    @Test
    public void testDoInsert() throws SQLException {

        System.out.println( "doInsert" );
        int rowsAffected = fillTestTable();
        try ( QueryHelper helper = new QueryHelper(); ) {
            assertEquals( "expected 1 row ", 1, rowsAffected );
            ResultSetWrapper rs2 = helper.doSelectWrapped(
                    "select * from yuk where i=?", 1 );
            assertTrue( "has next?", rs2.next() );
            assertEquals( "hi", "Hello", rs2.getString( "s" ) );
        }

    }

    /**
     * Test of doUpdate method, of class QueryHelper.
     */
    @Test
    public void testDoUpdate() {
        System.out.println( "doUpdate" );
        int rowsAffected = fillTestTable();
        try ( QueryHelper helper = new QueryHelper(); ) {
            assertEquals( "expected 1 row ", 1, rowsAffected );
            String query = "update yuk set s=s||' '||? where i=?";
            int result = helper.doUpdate( query, "world", 1 );
            //assertEquals( 1, result );
            ResultSetWrapper rs = helper.doSelectWrapped(
                    "select * from yuk where i=?", 1 );
            assertTrue( "has next?", rs.next() );
            assertEquals( "hi", "Hello world", rs.getString( "s" ) );
        }
    }

    /**
     * Create some test data
     *
     * @return the number of inserted rows.
     */
    int fillTestTable() {

        int rowsAffected;
        try ( QueryHelper qh = new QueryHelper() ) {
            qh.doUpdate( truncateQuery );
            rowsAffected = qh.doInsert( insertQuery, 1, true, "Hello",
                    new java.sql.Date( System.currentTimeMillis() ) );
        }
        return rowsAffected;
    }

    /**
     * Test of wrapException method, of class QueryHelper.
     */
    @Test
    @SuppressWarnings( "ThrowableResultIgnored" )
    public void testWrapException() {
        System.out.println( "wrapException" );
        String msg = "msg";
        String wrappedMessage = "boe";
        Throwable t = new RuntimeException( wrappedMessage );
        try ( QueryHelper helper = new QueryHelper(); ) {
            PersistenceException result = helper.wrapException( msg, t );
            assertTrue( "type check", result instanceof PersistenceException );
            assertEquals( "properly wrapped", t, result.getCause() );
            assertSame( "own msg ", msg, result.getMessage() );
            assertSame( "wrapped msg", wrappedMessage, result.getCause().
                    getMessage() );
        }
    }

    /**
     * Test of doDDL method, of class QueryHelper. This method simply invokes
     * the helper methods dropTestTable and createTestTabel in this test class.
     */
    @Test
    public void testDoDDL() {
        System.out.println( "doCreateTable is implicit in setup" );
        QueryHelperTest.creatTestTables();
        QueryHelperTest.dropTestTable();
        QueryHelperTest.creatTestTables();
    }

    /**
     * Drop the test table.
     */
    private static void dropTestTable() {
        // cleanup
        try ( QueryHelper helper = new QueryHelper(); ) {
            helper.doSelect( dropQuery );
            helper.doSelect( dropSeq );
        } catch ( Exception ex ) {
            System.out.println( "ex = " + ex );
        }
    }

    /**
     * Create the test table in the database.
     */
    private static void creatTestTables() {
        try ( QueryHelper helper = new QueryHelper(); ) {
            helper.doUpdate( tableDef );
            helper.doUpdate( dropSeq );
        } catch ( Exception ex ) {
            System.out.println( "ex = " + ex );
        }
    }

    /**
     * Test of createDataSource method, of class QueryHelper.
     */
    @Test
    public void testCreateDataSource() {
        System.out.println( "createDataSource" );
        DataSource result = QueryHelper.createDataSource();
        assertTrue( result instanceof DataSource );
    }

    @Test
    public void getNextValue() {
        try {
            new QueryHelper().doDDL( seqDef );
        } catch ( SQLException ex ) {
            Logger.getLogger( QueryHelperTest.class.getName() ).
                    log( Level.SEVERE, null, ex );
        }
        System.out.println( "test next value" );
        try ( QueryHelper helper = new QueryHelper(); ) {
            long v1 = helper.getNextValue( "yuk_sequence" );
            long v2 = helper.getNextValue( "yuk_sequence" );
            assertNotSame( "sequence values should be unique", v1, v2 );
        } catch ( Exception ex ) {
            fail( "kapot" );
            System.out.println( "ex = " + ex );
            Logger.getLogger( QueryHelperTest.class.getName() ).
                    log( Level.SEVERE, null, ex );
        } finally {

            try ( QueryHelper helper = new QueryHelper(); ) {
                try {
                    helper.doDDL( dropSeq );
                } catch ( SQLException ex ) {
                    Logger.getLogger( QueryHelperTest.class.getName() ).
                            log( Level.SEVERE, null, ex );
                }
            }
        }

    }
}
