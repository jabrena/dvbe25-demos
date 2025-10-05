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
        assertNotNull(modelProperty.get().getValue());
        assertEquals(String.class, modelProperty.get().getType());

        // Test getting a numeric property
        Optional<PropertyResolver.PropertyValue> delayProperty =
            PropertyResolver.getProperty("application.properties", "delay");

        assertTrue(delayProperty.isPresent());
        assertNotNull(delayProperty.get().getValue());
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
        assertNotNull(modelAsString.get());
        assertTrue(modelAsString.get() instanceof String);

        // Test getting as Integer
        Optional<Integer> delayAsInteger =
            PropertyResolver.getPropertyAs("application.properties", "delay", Integer.class);
        assertTrue(delayAsInteger.isPresent());
        assertNotNull(delayAsInteger.get());
        assertTrue(delayAsInteger.get() instanceof Integer);

        // Test getting as Long
        Optional<Long> delayAsLong =
            PropertyResolver.getPropertyAs("application.properties", "delay", Long.class);
        assertTrue(delayAsLong.isPresent());
        assertNotNull(delayAsLong.get());
        assertTrue(delayAsLong.get() instanceof Long);

        // Test getting non-existent property
        Optional<String> nonExistentAsString =
            PropertyResolver.getPropertyAs("application.properties", "nonExistent", String.class);
        assertFalse(nonExistentAsString.isPresent());
    }

    @Test
    public void testLoadProperties() throws IOException {
        var properties = PropertyResolver.loadProperties("application.properties");

        assertNotNull(properties);
        assertNotNull(properties.getProperty("model"));
        assertNotNull(properties.getProperty("repository"));
        assertNotNull(properties.getProperty("delay"));

        // Test that delay can be parsed as integer
        assertDoesNotThrow(() -> Integer.parseInt(properties.getProperty("delay")));
    }

    @Test
    public void testPropertyValueToString() {
        Optional<PropertyResolver.PropertyValue> property =
            PropertyResolver.getProperty("application.properties", "model");

        assertTrue(property.isPresent());
        String toString = property.get().toString();
        assertNotNull(toString);
        assertTrue(toString.contains("String"));
        assertFalse(toString.isEmpty());
    }

    @Test
    public void testNonExistentResource() {
        Optional<PropertyResolver.PropertyValue> result =
            PropertyResolver.getProperty("non-existent.properties", "key");
        assertFalse(result.isPresent());
    }

    @Test
    public void testTypeDetection() {
        // Test type detection logic directly
        Optional<PropertyResolver.PropertyValue> modelProperty =
            PropertyResolver.getProperty("application.properties", "model");
        assertTrue(modelProperty.isPresent());
        assertEquals(String.class, modelProperty.get().getType());

        Optional<PropertyResolver.PropertyValue> delayProperty =
            PropertyResolver.getProperty("application.properties", "delay");
        assertTrue(delayProperty.isPresent());
        assertEquals(Integer.class, delayProperty.get().getType());
    }
}
