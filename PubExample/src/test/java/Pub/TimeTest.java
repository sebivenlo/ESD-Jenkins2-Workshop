/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pub;

import PubExceptions.InvalidTimeException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Pia Erbrath
 */
public class TimeTest {
  
    @Test
    public void check_invalidTimeException_min_over_59() {
        try{
            Time time = new Time(12, 90);
            fail("min is invalid");
        } catch (InvalidTimeException ex) {
            System.out.println("all ok");
        } catch (Exception e) {
            fail("here should be another exception");
        }
    }
    @Test
    public void check_invalidTimeException_min_under_0() {
        try{
            Time time = new Time(12, -60);
            fail("min is invalid");
        } catch (InvalidTimeException ex) {
            System.out.println("all ok");
        } catch (Exception e) {
            fail("here should be another exception");
        }
    }

    @Test
    public void check_invalidTimeException_hour_over_23() {
        try{
            Time time = new Time(122, 9);
            fail("min is invalid");
        } catch (InvalidTimeException ex) {
            System.out.println("all ok");
        } catch (Exception e) {
            fail("here should be another exception");
        }
    }
    @Test
    public void check_invalidTimeException_hour_under_0() {
        try{
            Time time = new Time(-6, 9);
            fail("min is invalid");
        } catch (InvalidTimeException ex) {
            System.out.println("all ok");
        } catch (Exception e) {
            fail("here should be another exception");
        }
    }
    
    @Test
    public  void correct_minute() {
        try {
            Time time = new Time(12,50);
            assertEquals("min should be 50", 50, time.getMinutes());
        } catch (InvalidTimeException ex) {
            fail("here should be no exception");
        }
    }
    
    @Test 
    public void correct_hour() {
        try {
            Time time = new Time(12,50);
            assertEquals("hour should be 12",12, time.getHours());
        } catch (InvalidTimeException ex) {
            fail("here should be no exception");
        }
    }
    
    @Test 
    public void correct_time() {
        try {
            Time time = new Time(10, 10);
            assertEquals("Time should be the same", time, time.getTime());
        } catch (InvalidTimeException ex) {
            fail("here should be no exception");
        }
    }
    
    @Test
    public void test_toString() {
        try {
            String result = "Time 12:50";
            Time time = new Time(12, 50);
            assertEquals("string should be same", result, time.toString());
        } catch (InvalidTimeException ex) {
           fail("here should be no exception");
        }
    }
    
    @Test
    public void test_toString_decimal() {
        try {
            String restlt = "Time 01:02";
            Time time = new Time(1, 2);
            assertEquals("string should be the same", restlt, time.toString());
        } catch (InvalidTimeException ex) {
            fail("here should be no exception");
        }
    }
    
    @Test
    public void test_equals_with_correct_time_objects() {
        try {
            Time time1 = new Time(12, 50);
            Time time2 = new Time(12, 50);
            Time time3 = new Time(03, 12);
            Time time4 = new Time(03,6);
            
            
            assertEquals("time 1 should be equals with time1",time1, time1);
            assertEquals("time 1 should be equals with time2",time1, time2);
            assertFalse("time1 -> time3 unequals", time1.equals(time3));
            assertFalse("timt3 -> time4 unequals", time3.equals(time4));
        } catch (InvalidTimeException ex) {
            fail("here should be no exception");
        }
    }
    
    @Test
    @SuppressWarnings( "IncompatibleEquals" )
    public void test_equals_with_int() {
        Time time = new Time();
        int i = 0;
        assertFalse("time and int are unequal", time.equals(i));
    }
}
