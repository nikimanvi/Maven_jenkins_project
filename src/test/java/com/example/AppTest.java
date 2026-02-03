package com.example;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for App class
 */
public class AppTest {
    
    @Test
    public void testGetMessage() {
        App app = new App();
        String message = app.getMessage();
        assertNotNull("Message should not be null", message);
        assertEquals("Build successful with Jenkins!", message);
    }
    
    @Test
    public void testAdd() {
        App app = new App();
        assertEquals(5, app.add(2, 3));
        assertEquals(0, app.add(-5, 5));
        assertEquals(-10, app.add(-5, -5));
    }
    
    @Test
    public void testMultiply() {
        App app = new App();
        assertEquals(6, app.multiply(2, 3));
        assertEquals(-25, app.multiply(-5, 5));
        assertEquals(25, app.multiply(-5, -5));
    }
}
