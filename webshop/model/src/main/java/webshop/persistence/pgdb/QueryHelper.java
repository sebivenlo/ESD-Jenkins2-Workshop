package webshop.persistence.pgdb;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.postgresql.ds.PGSimpleDataSource;
import webshop.persistence.mappers.PersistenceException;

/**
 * This QueryHelper is an attempt to limit the amount of boiler plate code to
 * catch exceptions and not knowing what to do with them.
 *
 * In most cases, it is Okay to let the exceptions bubble up and deal with them
 * where it makes sense, which in most cases is just below or in the business
 * layer.<br/>
 * The solution is to wrapped the checked SQLException into a
 * PersistenceException, which is a RuntimeException.<br/>
 *
 * There are 4 essential database operations, Insert (Create), Update, Select
 * and Delete. With the exception of Select, all these essential methods return
 * an integer or boolean as result.
 * <br/>
 *
 * The Select operation returns a result set which is a closable resource.
 * Closing that in the doSelect(...) method would make that method useless, as
 * the result set cannot be used after the close operation. This makes closing
 * the result set a responsibility of the caller of doSelect. Best way to deal
 * with that issue is using an try-with-resources construct in the caller.
 * <br/>
 *
 * @author hom
 */
public final class QueryHelper implements AutoCloseable {
    
    private Object[] lastArgs = null;

    /**
     * Helper needs a connection to work with. It starts out with creating a
     * data source.
     */
    public QueryHelper() {
        createDataSource();
    }

    /**
     * The connection is used and saved to notify that a transaction is taking
     * place. When it is null, no transaction is active in this helper.
     */
    private volatile Connection connection = null;

    /**
     * Create a connection with auto commit off. Sql exception is wrapped in
     * PersistenceException.
     *
     * @return the connection.
     */
    public synchronized Connection createConnection() {
        if (null == connection) {
            try {
                connection = dataSource.getConnection();
                connection.setAutoCommit(false);
            } catch (SQLException ex) {
                throw wrapException("create connection failed", ex);
            }
        }
        return connection;
    }

    /**
     * Test if connection exists, and if not, create one. The use of this method
     * is to create a connection when none is available or otherwise use the
     * existing one.
     *
     *
     * @return true if there already was a connection and false when there was
     * none but is now.
     */
    public synchronized boolean isTransaction() {
        if (null != connection) {
            return true;
        } else {
            createConnection();
            return false;
        }
    }

    /**
     * Execute a prepared statement for delete, using new connection.
     *
     * @param query string with parameter positions
     * @param args the parameters
     * @throws PersistenceException exception with wrapped cause.
     */
    public void doDelete(String query, Object... args) {
        boolean transactional = isTransaction();
        
        doDelete(createConnection(), query, args);
        if (!transactional) {
            commit();
        }
    }

    /**
     * Execute a prepared statement for delete using existing connection. This
     * version can participate in a transaction.
     *
     * @param conn connection to use
     * @param query string with parameter positions
     * @param args the parameters
     * @throws PersistenceException exception with wrapped cause.
     */
    public void doDelete(Connection conn, String query, Object... args) {
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            setStatementParameters(pst, args);
            pst.executeUpdate();
        } catch (SQLException ex) {
            throw wrapException("doDelete failed with query \"" + query
                    + "\" with args " + Arrays.deepToString(args),
                    ex);
        }
    }

    /**
     * Set the arguments in the prepares statement. The method substitutes
     * question marks with the arguments from the args list.
     *
     * @param args the arguments to substitute
     * @param pst the statement to complete
     * @return the statement with the questions marks substituted by the
     * parameter from the args list.
     * @throws SQLException when something goes wrong.
     */
    PreparedStatement setStatementParameters(final PreparedStatement pst,
            Object... args)
            throws SQLException {
        int argNo = 1;
        for (Object arg : args) {
            pst.setObject(argNo++, arg);
        }
        return pst;
    }

    /**
     * Do select with new connection. This methods detects if it is part of a
     * transaction. If not, it will close the connection.
     *
     * @param query sql
     * @param args parameters to query
     * @return result set created by query
     */
    public ResultSet doSelect(String query, Object... args) {
        boolean transactional = isTransaction();
        ResultSet rs = doSelect(createConnection(), query, args);
        if (!transactional) {
            commit();
        }
        
        return rs;
    }

    /**
     * Do the work to create a result set from a select query string,
     * substituting the parameters. Use existing (passed) connection. The
     * ResultSet returned from the execution of the query must NOT be
     * closed.<br/>
     * Closing of the ResultSet is up to the receiver of it.
     *
     * @param conn connection to use.
     * @param query to execute. It should probably start with except.
     * @param args the things to place instead of the question marks
     * @return a result set wrapper
     */
    public ResultSet doSelect(Connection conn, String query,
            Object... args) {
        ResultSet resultSet = null;
        
        try {
            PreparedStatement pst = conn.prepareStatement(query);
            setStatementParameters(pst, args);
            System.out.println("pst = " + pst);
            resultSet = pst.executeQuery();
        } catch (SQLException ex) {
            throw wrapException("doSelect failed with query \"" + query
                    + "\" with args " + Arrays.deepToString(args),
                    ex);
        }
        
        return resultSet;
        
    }

    /**
     * Do insert with new connection.
     *
     * @param query to do
     * @param args to the query
     * @return the result of the insert.
     */
    public int doInsert(String query, Object... args) {
        boolean transactional = isTransaction();
        
        Connection con = createConnection();
        int result = doInsert(con, query, args);
        if (!transactional) {
            commit();
        }
        
        return result;
    }

    /**
     * Do insert with existing connection. Note that this version does not
     * commit.
     *
     * @param conn connection to use
     * @param query to do
     * @param args to the query
     * @return the result of the insert.
     */
    public int doInsert(Connection conn, String query, Object... args) {
        int result = 0;
        
        try (PreparedStatement pst = conn.prepareStatement(query);) {
            setStatementParameters(pst, args);
            result = pst.executeUpdate();
        } catch (SQLException ex) {
            String msg = "doInsert failed with query \"" + query
                    + "\" with args " + Arrays.deepToString(args);
            Logger.getLogger(QueryHelper.class.getName())
                    .log(Level.SEVERE, msg, ex);
            throw wrapException(msg, ex);
        }
        return result;
    }

    /**
     * Execute the query string, after filling in the provided args and is
     * NON-transactional.
     *
     * @param query sql to execute
     * @param args parameters to the query
     * @return the result of the update statement
     */
    public int doUpdate(String query, Object... args) {
        boolean transactional = isTransaction();
        
        int result = doUpdate(createConnection(), query, args);
        if (!transactional) {
            commit();
        }
        return result;
    }

    /**
     * Do update with existing connection. Note that this version does not
     * commit.
     *
     * @param conn connection to use
     * @param query the work
     * @param args with these
     * @return the result of the update statement
     */
    public int doUpdate(Connection conn, String query, Object... args) {
        int result = 0;
        try (PreparedStatement pst = connection.prepareStatement(query);) {
            setStatementParameters(pst, args);
            result = pst.executeUpdate();
        } catch (SQLException ex) {
            throw wrapException("doUpdate failed with query \"" + query
                    + "\" with args " + Arrays.deepToString(args),
                    ex);
        }
        
        return result;
    }

    /**
     * Do DDL (Data definition language) operation like create or drop table.
     * Create a table from the query string. This is the only method that does
     * not warp its exception.
     *
     * @param ddl string definition of table, ready for prepared statement
     * without parameters.
     * @throws java.sql.SQLException ...
     */
    public void doDDL(String ddl) throws
            SQLException {
        System.out.println(ddl + ";\n");
        try (Connection con = dataSource.getConnection()) {
            con.prepareStatement(ddl).execute();
        }
    }

    /**
     * Warp sql exception in a Persistence exception.
     *
     * @param msg in the message
     * @param t cause is the wrapped exception
     * @return the wrapper with wrapped exception contained.
     */
    public PersistenceException wrapException(String msg, Throwable t) {
        if (t instanceof SQLException) {
            rollbackAndClose();
        }
        return new PersistenceException(msg, t);
    }
    
    private static volatile DataSource dataSource = null;
    public static final String DBPROPFILE="/opt/payara41/glassfish/config/webshop-db.properties";
    /**
     * Create a data source with fixed credentials. Sufficient for an exam where
     * we do not want to hassle with configuration files.
     *
     * @return the new connection, configured and all
     */
    public static DataSource createDataSource() {
        synchronized (QueryHelper.class) {
            if (null == dataSource) {
                PGSimpleDataSource pds = new PGSimpleDataSource();
                Properties props = new Properties();
                try (InputStream in = new FileInputStream(DBPROPFILE)){
                    props.load(in);
                    props.forEach((a, b) -> {
                        System.out.println(a + "->" + b);
                    });
                } catch (Exception ignored) {
                    System.err.println("cannot load props from file "+DBPROPFILE);
                }
                pds.setServerName(props.getProperty("host", "localhost"));
                pds.setDatabaseName(props.getProperty("database",
                        "webshop"));
                pds.setUser(props.getProperty("user", "exam"));
                pds.setPassword(props.getProperty("password", "exam"));
                dataSource = pds;
            }
        }
        return dataSource;
    }

    /**
     * Get next sequence value from named sequence.
     *
     * @param sequenceName for which to get a value.
     * @return sequence value
     * @throws PersistenceException when something wrong
     */
    public long getNextValue(String sequenceName) {
        String sql = "select nextval(?)";
        ResultSetWrapper r = doSelectWrapped(sql, sequenceName);
        // move to first row.
        r.next();
        long result = r.getLong(1); // first and only column.
        return result;
    }

    /**
     * Warp a result set into a exception wrapper, to tame it a little. Set the
     * ResultSetWrapper for which methods are accessible in the resultset.
     *
     * @param conn connection to use
     * @param query to execute
     * @param args to pass
     * @return wrapped result set
     */
    public ResultSetWrapper doSelectWrapped(Connection conn, String query,
            Object... args) {
        return new ResultSetWrapper(this, doSelect(conn, query, args));
    }

    /**
     * Warp a result set into a exception wrapper, to tame it a little. Set the
     * ResultSetWrapper for which methods are accessible in the result set.
     *
     * @param query to execute
     * @param args to pass
     * @return wrapped result set
     */
    public ResultSetWrapper doSelectWrapped(String query, Object... args) {
        return new ResultSetWrapper(this, doSelect(createConnection(), query,
                args));
    }

    /**
     * The helper is auto closeable. Also closed the connection, if set.
     */
    @Override
    public void close() {
        if (null != connection) {
            
            try {
                if (!connection.getAutoCommit()) {
                    connection.commit();
                }
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException ex) {
                throw new PersistenceException(null, ex);
            }
        }
        connection = null;
        lastArgs = null;
    }

    /**
     * Commits for this helper.//, then nulls the connection.
     */
    public void commit() {
        if (null != connection) {
            try {
                connection.commit();
            } catch (SQLException ex) {
                throw new PersistenceException(null, ex);
            }
        }
        //connection = null;
    }

    /**
     * Rolls back for this helper, then nulls the connection.
     */
    public void rollbackAndClose() {
        if (null != connection) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new PersistenceException("failure on rollback", ex);
            }
        }
        //connection = null;
    }
    
    public Object[] getLastArgs() {
        return lastArgs;
    }
    
}
