package webshop.business.exceptions;

import webshop.business.WebshopException;
import org.junit.Test;

/**
 * Purpose of this class is to get coverage in all business packages to 100%.
 * Just thrown some variants of the exceptions to invoke the 3 constructors,
 * although throwing is not really needed to invoke the constructors.
 *
 * @author hom
 */
public class WebShopExceptionTest {

    /**
     * Call all constructors and throw the result of the last.
     *
     * @throws webshop.business.WebshopException just for the kicks.
     */
    @Test( expected = WebshopException.class )
    public void testThrowSome() throws WebshopException {
        WebshopException ex = new WebshopException( "Hello World" );
        ex = new WebshopException( new RuntimeException(
                "Something stupid happened" ) );
        ex = new WebshopException( "Hi tester, catch this",
                new RuntimeException( "Something stupid happened" ) );
        throw ex;
    }

}
