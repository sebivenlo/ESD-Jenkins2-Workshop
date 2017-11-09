package webshop.controller;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;

import javax.faces.application.FacesMessage;
import javax.enterprise.context.SessionScoped;

import javax.faces.context.FacesContext;
import javax.inject.Named;
import webshop.business.Product;
import webshop.business.ProductContainer;
import webshop.business.ProductQuantity;
import webshop.persistence.mappers.AbstractWebshopFactory;
import static webshop.persistence.mappers.WebshopFactoryConfigurator.*;
import webshop.business.WebshopException;
import webshop.business.WebshopFacade;
import webshop.persistence.mappers.WebshopFactoryConfigurator;
import webshop.persistence.pgdb.QueryHelper;

/**
 * Implements the "inventory and cart" facelet and this managed bean conforming
 * the given example.
 *
 * <p>
 * Within the managed bean, a WebShopSession object is the facade to all logic
 * in the backend.</p>
 *
 * Within the facelet, the inventory and the shopping cart are shown.
 *
 * <br>
 * Within the inventory, an item can be selected (with button) to be added to
 * the cart. The initial quantity is always 1. When a product is already in the
 * cart, then the button is replaced by the text 'already in cart'.
 *
 * <br>Within the cart, the quantity of a given product can be decreased and
 * increased, always one-at-a-time.<br>
 *
 * For each product in the cart, the remove button can be used to drop the full
 * quantity of this product from the cart.<br>
 *
 * The backend of the system will provide a discount when the discount code
 * 'BONUS' is entered. With the 'Activate' button, the bonus can be
 * activated.<br>
 *
 * The 'submit order' button submits an order and creates a new WebShopSession
 * object.<br>
 *
 * When exceptions occur, an error message on the cart facelet is shown.<br>
 *
 * @author hvd
 */
@SessionScoped
@Named("wsc")
public class WebshopController implements Serializable {

    private static final String PERSISTENCE_CONFIG;// = MEMORY_CONFIG;
    //private static final String PERSISTENCE_CONFIG = DB_CONFIG;

    static {
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(QueryHelper.DBPROPFILE)) {
            props.load(in);
        } catch (Exception ex) {
            System.err.println("Cannot read properties file "
                    + QueryHelper.DBPROPFILE);
        }
        String cfg = props.getProperty("PERSISTENCE_CONFIG", "IM");
        PERSISTENCE_CONFIG = cfg;
    }
    private transient AbstractWebshopFactory factory = CONFIGURATOR.getFactory(
            PERSISTENCE_CONFIG);

    private final WebshopFacade facade;

    public WebshopController() throws WebshopException {
        //this.factory = CONFIGURATOBONUSR.getFactory( DB_CONFIG );
        facade = new WebshopFacade(factory);
    }

    public String addToCart(Product product, int quantity) {

        try {
            facade.addToCart(product, quantity);
        } catch (WebshopException ex) {
            displayExceptionMessage(ex);
        }
        return null;
    }

    /**
     * Remove product from cart.
     *
     * @param product to remove
     * @param quantity to remove
     * @return null (always).
     */
    public String removeFromCart(Product product, int quantity) {

        try {
            facade.removeFromCart(product, quantity);
        } catch (WebshopException ex) {
            displayExceptionMessage(ex);
        }
        return null;
    }

    /**
     * Remove all of product from the cart.
     *
     * @param product
     * @return null
     */
    public String removeAllFromCart(Product product) {
        try {
            facade.removeFromCart(product);
        } catch (WebshopException ex) {
            displayExceptionMessage(ex);
        }
        return null;
    }

    /**
     * Abandon the cart and return content to inventory. Have a look at the
     * facade api.
     *
     * @return null
     */
    public String abandonCart() {
        facade.abandonCart();
        return null;
    }

    /**
     * Add a message, derived form the exception, to the FacesContext.
     *
     * @param ex exception to process
     */
    private void displayExceptionMessage(Exception ex) {
        FacesMessage message = new FacesMessage(ex.getMessage() + "\n"
                + ex.getCause().getMessage());
        FacesContext.getCurrentInstance().addMessage(null, message);
        FacesContext.getCurrentInstance().renderResponse();
    }

    /**
     * Process the cart being submitted. Have a look at the facade api.
     *
     * @return null
     */
    public String submitOrder() {

        facade.submitOrder();
        return null;
    }

    /**
     * Activate the discount code. Have a look at the facade api.
     *
     * @return null
     */
    public String activateDiscountCode() {
        facade.activateDiscountCode();
        return null;
    }

    /**
     * Get the list of product quantities in the inventory.
     *
     * @return the list of product quantities
     */
    public List<ProductQuantity> getInventory() {
        return facade.getInventory().getContents();
    }

    /**
     * Get the list of product quantities in the cart.
     *
     * @return the list of product quantities
     */
    public List<ProductQuantity> getCartContents() {
        return facade.getCart().getContents();
    }

    /**
     * Test if there is anything in the cart.
     *
     * @return true is any item is in the cart.
     */
    public boolean isCartEmpty() {
        return facade.getCart().itemCount() == 0;
    }

    /**
     * See if a specific product is in the cart.
     *
     * @param product
     * @return true if so.
     */
    public boolean isInCart(Product product) {
        return facade.getCart().contains(product);
    }

    /**
     * Get the discount code value.
     *
     * @return the discount code
     */
    public String getDiscountCode() {
        return facade.getDiscountCode();
    }

    /**
     * Set the discount code value.
     *
     * @param discountCode to set
     */
    public void setDiscountCode(String discountCode) {
        facade.setDiscountCode(discountCode);
    }

    /**
     * Get the session id otherwise known as the cart id.
     *
     * @return the id
     */
    public int getSessionId() {
        return facade.getSessionID();
    }

    /**
     * Get the invoice data. This is supposed to be a pre-formatted string that
     * is put inside a html pre block.
     *
     * @return
     */
    public String getInvoiceData() {
        return facade.getInvoiceData();
    }

    /**
     * Is the product on stock?
     *
     * @param p product
     * @return true if it is
     */
    public boolean isAvailable(Product p) {
        ProductContainer pc = facade.getInventory();
        return pc.contains(p) && pc.count(p) > 0;
    }

    /**
     * Not implemented.
     *
     * @return the number of sessions/cart/ known in the shop.
     */
    public int getActiveCarts() {
        return facade.getActiveCarts();
    }

    /**
     * Get the persistence config string.
     *
     * @return the current config, so that the developer can see if the in
     * memory implementation or the postgres implementation is active.
     */
    public String getPersistenceConfig() {
        return PERSISTENCE_CONFIG;
    }

    public LocalDate getCustomerBirthDay() {
        return facade.getCustomerBirthDay();
    }

    public void setCustomerBirthDay(LocalDate bd) {
        facade.setCustomerBirthDay(bd);
    }

    private static final long serialVersionUID = 1L;

    public boolean isAgeCheckeRequired() {
        return facade.isAgeCheckRequired();
    }

    public boolean isAgeOk() {
        return facade.isAgeOk();
    }

    public String checkAge() {
        facade.isAgeOk();
        return null;
    }
}
