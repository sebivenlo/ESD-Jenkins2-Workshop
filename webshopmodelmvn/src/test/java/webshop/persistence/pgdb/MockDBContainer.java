package webshop.persistence.pgdb;

import javax.sql.DataSource;

/**
 * Simple mock container with table name with owner.
 *
 * @author hom
 */
public class MockDBContainer extends PGDBProductContainer {

    private final String tablename;
    private long owner = 0;

    public MockDBContainer(  String tablename, int owner ) {
        this.tablename = tablename;
        this.owner = owner;
    }

    @Override
    public long getOwner() {
        return owner;
    }

    @Override
    public void setOwner( long owner ) {
        this.owner = owner;
    }

    @Override
    public String getTableName() {
        return tablename;
    }


}
