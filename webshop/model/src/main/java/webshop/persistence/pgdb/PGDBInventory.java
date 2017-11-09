package webshop.persistence.pgdb;

/**
 * Uses the inventory table. This class is retrieved via the PGDBWebshopFactory,
 * which takes responsibility of making only one instance.
 *
 * @author hom
 */
class PGDBInventory extends PGDBProductContainer {

    private static final long serialVersionUID = 1L;

    @Override
    public String getTableName() {
        return "inventory";
    }

    public PGDBInventory() {
    }

}
