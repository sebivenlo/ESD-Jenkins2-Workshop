package webshop.persistence.pgdb;

/**
 * Provides a name for a table for the purpose of persistence.
 * @author hom
 */
public interface NamedTable {
    /**
     * Name your backing table.
     * @return the table name.
     */
    String getTableName();
}
