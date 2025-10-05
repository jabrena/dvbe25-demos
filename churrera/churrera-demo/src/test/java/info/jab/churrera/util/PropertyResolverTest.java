package info.jab.churrera.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Optional;

/**
 * Test class for PropertyResolver utility.
 */
public class PropertyResolverTest {

    @Test
    public void testGetProperty() {
        // Test getting a string property
        Optional<PropertyResolver.PropertyValue> modelProperty =
            PropertyResolver.getProperty("application.properties", "model");

        assertTrue(modelProperty.isPresent());
        assertEquals("claude-4-sonnet", modelProperty.get().getValue());
        assertEquals(String.class, modelProperty.get().getType());

        // Test getting a numeric property
        Optional<PropertyResolver.PropertyValue> delayProperty =
            PropertyResolver.getProperty("application.properties", "delay");

        assertTrue(delayProperty.isPresent());
        assertEquals("30", delayProperty.get().getValue());
        assertEquals(Integer.class, delayProperty.get().getType());

        // Test getting a non-existent property
        Optional<PropertyResolver.PropertyValue> nonExistentProperty =
            PropertyResolver.getProperty("application.properties", "nonExistent");

        assertFalse(nonExistentProperty.isPresent());
    }

    @Test
    public void testGetPropertyAs() {
        // Test getting as String
        Optional<String> modelAsString =
            PropertyResolver.getPropertyAs("application.properties", "model", String.class);
        assertTrue(modelAsString.isPresent());
        assertEquals("claude-4-sonnet", modelAsString.get());

        // Test getting as Integer
        Optional<Integer> delayAsInteger =
            PropertyResolver.getPropertyAs("application.properties", "delay", Integer.class);
        assertTrue(delayAsInteger.isPresent());
        assertEquals(Integer.valueOf(30), delayAsInteger.get());

        // Test getting as Long
        Optional<Long> delayAsLong =
            PropertyResolver.getPropertyAs("application.properties", "delay", Long.class);
        assertTrue(delayAsLong.isPresent());
        assertEquals(Long.valueOf(30L), delayAsLong.get());

        // Test getting non-existent property
        Optional<String> nonExistentAsString =
            PropertyResolver.getPropertyAs("application.properties", "nonExistent", String.class);
        assertFalse(nonExistentAsString.isPresent());
    }

    @Test
    public void testLoadProperties() throws IOException {
        var properties = PropertyResolver.loadProperties("application.properties");

        assertNotNull(properties);
        assertEquals("claude-4-sonnet", properties.getProperty("model"));
        assertEquals("https://github.com/jabrena/dvbe25-demos", properties.getProperty("repository"));
        assertEquals("30", properties.getProperty("delay"));
    }

    @Test
    public void testPropertyValueToString() {
        Optional<PropertyResolver.PropertyValue> property =
            PropertyResolver.getProperty("application.properties", "model");

        assertTrue(property.isPresent());
        String toString = property.get().toString();
        assertTrue(toString.contains("claude-4-sonnet"));
        assertTrue(toString.contains("String"));
    }

    @Test
    public void testNonExistentResource() {
        Optional<PropertyResolver.PropertyValue> result =
            PropertyResolver.getProperty("non-existent.properties", "key");
        assertFalse(result.isPresent());
    }

    @Test
    public void testTypeDetection() {
        // Create a test properties file with different types
        var properties = new java.util.Properties();
        properties.setProperty("stringValue", "hello world");
        properties.setProperty("intValue", "42");
        properties.setProperty("longValue", "123456789012345");
        properties.setProperty("doubleValue", "3.14159");
        properties.setProperty("floatValue", "2.71828");
        properties.setProperty("booleanTrue", "true");
        properties.setProperty("booleanFalse", "false");
        properties.setProperty("emptyValue", "");

        // Test type detection logic directly
        assertEquals(String.class, PropertyResolver.getProperty("application.properties", "model").get().getType());
        assertEquals(Integer.class, PropertyResolver.getProperty("application.properties", "delay").get().getType());
    }
}
