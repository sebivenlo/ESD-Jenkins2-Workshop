package webshop.persistence.pgdb;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Iterator;

/**
 * Wraps result set and massages SQLExceptions into PersistenceExceptions. To
 * make the use of result set a bit less boilerplate, use this class to wrap any
 * thrown exceptions into a PersistenceException.
 * <br/>
 * To the tester: Do not try to test this beast by hand, because that can prove
 * to be quite a lot of work. Use a mocking framework like mockito to do that.
 *
 * @author hom
 */
public class ResultSetWrapper implements AutoCloseable {

    private final ResultSet resultSet;

    private final QueryHelper helper;

    public ResultSetWrapper( QueryHelper helper, ResultSet resultSet ) {
        this.resultSet = resultSet;
        this.helper = helper;
    }

    /**
     * Sometimes you need the real, untamed beast.
     *
     * @return the result set of this wrapper.
     */
    public ResultSet unwrap() {
        return resultSet;
    }

    public boolean next() {
        try {
            return resultSet.next();
        } catch ( SQLException ex ) {
            throw helper.wrapException( "next", ex );
        }
    }

    public boolean isBeforeFirst() {
        try {
            return resultSet.isBeforeFirst();
        } catch ( SQLException ex ) {
            throw helper.wrapException( "isBeforeFirst", ex );
        }
    }

    public boolean isAfterLast() {
        try {
            return resultSet.isAfterLast();
        } catch ( SQLException ex ) {
            throw helper.wrapException( "isAfterLast", ex );
        }
    }

    public boolean isFirst() {
        try {
            return resultSet.isFirst();
        } catch ( SQLException ex ) {
            throw helper.wrapException( "isFirst", ex );
        }
    }

    public boolean isLast() {
        try {
            return resultSet.isLast();
        } catch ( SQLException ex ) {
            throw helper.wrapException( "isLast", ex );
        }
    }

    public boolean first() {
        try {
            return resultSet.first();
        } catch ( SQLException ex ) {
            throw helper.wrapException( "first", ex );
        }
    }

    public boolean last() {
        try {
            return resultSet.last();
        } catch ( SQLException ex ) {
            throw helper.wrapException( "last", ex );
        }
    }

    public boolean isClosed() {
        try {
            return resultSet.isClosed();
        } catch ( SQLException ex ) {
            throw helper.wrapException( "isClosed", ex );
        }
    }

    public void close() {
        try {
            resultSet.close();
        } catch ( SQLException ex ) {
            throw helper.wrapException( "close", ex );
        }
    }

    public short getShort( int columnIndex ) {
        try {
            return resultSet.getShort( columnIndex );
        } catch ( SQLException ex ) {
            throw helper.wrapException( "getShort", ex );
        }
    }

    public short getShort( String columnName ) {
        try {
            return resultSet.getShort( columnName );
        } catch ( SQLException ex ) {
            throw helper.wrapException( "getShort", ex );
        }
    }

    public boolean getBoolean( String columnLabel ) {
        try {
            return resultSet.getBoolean( columnLabel );
        } catch ( SQLException ex ) {
            throw helper.wrapException( "getBoolean", ex );
        }
    }

    public byte getByte( String columnLabel ) {
        try {
            return resultSet.getByte( columnLabel );
        } catch ( SQLException ex ) {
            throw helper.wrapException( "getByte", ex );
        }
    }

    public int getInt( String columnLabel ) {
        try {
            return resultSet.getInt( columnLabel );
        } catch ( SQLException ex ) {
            throw helper.wrapException( "getInt", ex );
        }
    }

    public long getLong( String columnLabel ) {
        try {
            return resultSet.getLong( columnLabel );
        } catch ( SQLException ex ) {
            throw helper.wrapException( "getLong ", ex );
        }
    }

    public float getFloat( String columnLabel ) {
        try {
            return resultSet.getFloat( columnLabel );
        } catch ( SQLException ex ) {
            throw helper.wrapException( "getFloat", ex );
        }
    }

    public double getDouble( String columnLabel ) {
        try {
            return resultSet.getDouble( columnLabel );
        } catch ( SQLException ex ) {
            throw helper.wrapException( "getDouble", ex );
        }
    }

    public byte[] getBytes( String columnLabel ) {
        try {
            return resultSet.getBytes( columnLabel );
        } catch ( SQLException ex ) {
            throw helper.wrapException( "getBytes", ex );
        }
    }

    public java.sql.Date getDate( String columnLabel ) {
        try {
            return resultSet.getDate( columnLabel );
        } catch ( SQLException ex ) {
            throw helper.wrapException( "getDate "+ex, ex );
        }
    }

    public java.sql.Timestamp getTimeStamp( String columnLabel ) {
        try {
            return resultSet.getTimestamp(columnLabel );
        } catch ( SQLException ex ) {
            throw helper.wrapException( "getDate", ex );
        }
    }

    public Time getTime( String columnLabel ) {
        try {
            return resultSet.getTime( columnLabel );
        } catch ( SQLException ex ) {
            throw helper.wrapException( "getTime", ex );
        }
    }

    public Timestamp getTimestamp( String columnLabel ) {
        try {
            return resultSet.getTimestamp( columnLabel );
        } catch ( SQLException ex ) {
            throw helper.wrapException( "getTime", ex );
        }
    }

    public InputStream getBinaryStream( String columnLabel ) {
        try {
            return resultSet.getBinaryStream( columnLabel );
        } catch ( SQLException ex ) {
            throw helper.wrapException( "getBinaryStream", ex );
        }
    }

    public int getInt( int columnIndex ) {
        try {
            return resultSet.getInt( columnIndex );
        } catch ( SQLException ex ) {
            throw helper.wrapException( "getInt", ex );
        }
    }

    public long getLong( int columnIndex ) {
        try {
            return resultSet.getLong( columnIndex );
        } catch ( SQLException ex ) {
            throw helper.wrapException( "getInt", ex );
        }
    }

    public Object getObject( String columnLabel ) {
        try {
            return resultSet.getObject( columnLabel );
        } catch ( SQLException ex ) {
            throw helper.wrapException( "getObject", ex );
        }
    }

    public Reader getCharacterStream( String columnLabel ) {
        try {
            return resultSet.getCharacterStream( columnLabel );
        } catch ( SQLException ex ) {
            throw helper.wrapException( "getCharacterStream", ex );
        }
    }

    public BigDecimal getBigDecimal( String columnLabel ) {
        try {
            return resultSet.getBigDecimal( columnLabel );
        } catch ( SQLException ex ) {
            throw helper.wrapException( "getBigDecimal", ex );
        }
    }

    public Blob getBlob( String columnLabel ) {
        try {
            return resultSet.getBlob( columnLabel );
        } catch ( SQLException ex ) {
            throw helper.wrapException( "getBlob", ex );
        }
    }

    public Clob getClob( String columnLabel ) {
        try {
            return resultSet.getClob( columnLabel );
        } catch ( SQLException ex ) {
            throw helper.wrapException( "getClob", ex );
        }
    }

    public String getString( String columnLabel ) {
        try {
            return resultSet.getString( columnLabel );
        } catch ( SQLException ex ) {
            throw helper.wrapException( "getString", ex );
        }
    }

    Object[] getQueryArgs() {
        return helper.getLastArgs();
    }
}
