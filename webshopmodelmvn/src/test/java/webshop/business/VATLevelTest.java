/*
 *  Copyright Pieter van den Hombergh 2010/.
 *  Fontys Hogeschool voor Techniek en logistiek Venlo Netherlands.
 *  Software Engineering. Website: http://www.fontysvenlo.org
 *  This file may be used distributed under GPL License V2.
 */
package webshop.business;

import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
import static webshop.business.VATLevel.*;

/**
 *
 * @author hom
 */
public class VATLevelTest {

    /**
     * Test of getPercentage method, of class VATLevel.
     */
    @Test
    public void testGetPercentage() {
        assertEquals( "NONE percentage", 0, NONE.getPercentage(),
                0.00001 );
        assertEquals( "LOW percentage", 6.0, LOW.getPercentage(),
                0.00001 );
        assertEquals( "HIGH percentage", 19.0, HIGH.getPercentage(),
                0.00001 );
    }

    /**
     * Test of computeVAT method, of class VATLevel. Ensure to test NONE level
     * too, for coverage.
     */
    @Test
    public void testComputeVAT() {
        long expected = 190;
        long price = 1000;
        assertEquals( "High VAT", expected, VATLevel.HIGH.computeVAT( price ) );
        expected = 0;
        assertEquals( "No VAT", expected, VATLevel.NONE.computeVAT( price ) );
    }

    /**
     * Test of computePriceWithoutVAT method, of class VATLevel.
     */
    @Test
    public void testComputePriceWithoutVat() {
        long expected = 1000;
        long price = 1190;
        assertEquals( "High VATLess Price", expected,
                VATLevel.HIGH.computePriceWithoutVAT( price ) );
        System.out.println( "computePriceWithoutVAT" );

        price = 1060L;
        VATLevel instance = VATLevel.LOW;
        long expResult = 1000L;
        long result = instance.computePriceWithoutVAT( price );
        assertEquals( expResult, result );

    }

    /**
     * Test of computeVATPartInPrice method, of class VATLevel. Also compute for
     * level NONE, because of coverage because this has an overwritten
     * implementation.
     */
    @Test
    public void testComputeVATPartInPrice() {

        System.out.println( "computeVATPartInPrice" );
        long price = 1190L;
        VATLevel instance = HIGH;
        long expResult = 190L;
        long result = instance.computeVATPartInPrice( price );
        assertEquals( expResult, result );

        // for level NONE
        instance = NONE;
        expResult = 0;
        result = instance.computeVATPartInPrice( price );
        assertEquals( expResult, result );
    }

    /**
     * Test of values method, of class VATLevel.
     */
    @Test
    public void testValues() {
        System.out.println( "values" );
        VATLevel[] expResult = new VATLevel[]{ NONE, LOW, HIGH };
        VATLevel[] result = VATLevel.values();
        assertArrayEquals( expResult, result );
    }

    /**
     * Test of valueOf method, of class VATLevel.
     */
    @Test
    public void testValueOf() {
        System.out.println( "valueOf" );
        String name = "NONE";
        VATLevel expResult = NONE;
        VATLevel result = VATLevel.valueOf( name );
        assertEquals( expResult, result );
    }

    /**
     * Test of shortStringToVatLevel method, of class VATLevel.
     */
    @Test
    public void testShortStringToVatLevel() {
        System.out.println( "shortStringToVatLevel" );
        String v = "";
        VATLevel result = VATLevel.shortStringToVatLevel( v );
        assertEquals( VATLevel.NONE, result );

        result = VATLevel.shortStringToVatLevel( "H" );
        assertEquals( VATLevel.HIGH, result );
        result = VATLevel.shortStringToVatLevel( "L" );
        assertEquals( VATLevel.LOW, result );

        result = VATLevel.shortStringToVatLevel( "N" );
        assertEquals( VATLevel.NONE, result );

        result = VATLevel.shortStringToVatLevel( "X" );
        assertEquals( VATLevel.NONE, result );
    }

    /**
     * Test of dbName method, of class VATLevel.
     */
    @Test
    public void testDbName() {
        System.out.println( "dbName" );
        VATLevel instance = VATLevel.HIGH;
        String expResult = "H";
        String result = instance.dbName();
        assertEquals( expResult, result );

        instance = VATLevel.LOW;
        expResult = "L";
        result = instance.dbName();
        assertEquals( expResult, result );

        instance = VATLevel.NONE;
        expResult = "N";
        result = instance.dbName();
        assertEquals( expResult, result );
    }

    /**
     * Test of consumerPrice method, of class VATLevel.
     */
    @Test
    public void testConsumerPrice() {
        System.out.println( "consumerPrice" );
        long price = 1000L;
        VATLevel instance = VATLevel.HIGH;
        long expResult = 1190L;
        long result = instance.consumerPrice( price );
        assertEquals( "VAT level calc wrong", expResult, result );
    }
}
