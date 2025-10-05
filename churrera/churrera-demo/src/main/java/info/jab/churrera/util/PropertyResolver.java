package info.jab.churrera.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Optional;

/**
 * Utility class for resolving properties from classpath resources.
 * Provides methods to read property values and determine their types.
 */
public class PropertyResolver {

    /**
     * Represents a property value with its type information.
     */
    public static class PropertyValue {
        private final String value;
        private final Class<?> type;

        public PropertyValue(String value, Class<?> type) {
            this.value = value;
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public Class<?> getType() {
            return type;
        }

        @Override
        public String toString() {
            return String.format("PropertyValue{value='%s', type=%s}", value, type.getSimpleName());
        }
    }

    /**
     * Loads properties from a classpath resource and returns a PropertyValue for the specified key.
     *
     * @param resourcePath the path to the properties file in classpath (e.g., "application.properties")
     * @param key the property key to retrieve
     * @return Optional containing PropertyValue with value and type, or empty if not found
     * @throws IOException if the resource cannot be loaded
     */
    public static Optional<PropertyValue> getProperty(String resourcePath, String key) {
        try {
            Properties properties = loadProperties(resourcePath);
            String value = properties.getProperty(key);

            if (value == null) {
                return Optional.empty();
            }

            Class<?> type = determineType(value);
            return Optional.of(new PropertyValue(value, type));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    /**
     * Loads properties from a classpath resource.
     *
     * @param resourcePath the path to the properties file in classpath
     * @return Properties object containing all properties from the file
     * @throws IOException if the resource cannot be loaded
     */
    public static Properties loadProperties(String resourcePath) throws IOException {
        Properties properties = new Properties();

        try (InputStream inputStream = PropertyResolver.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            properties.load(inputStream);
        }

        return properties;
    }

    /**
     * Determines the type of a property value based on its string representation.
     *
     * @param value the property value as string
     * @return the determined type class
     */
    private static Class<?> determineType(String value) {
        if (value == null || value.trim().isEmpty()) {
            return String.class;
        }

        String trimmedValue = value.trim();

        // Check for boolean
        if ("true".equalsIgnoreCase(trimmedValue) || "false".equalsIgnoreCase(trimmedValue)) {
            return Boolean.class;
        }

        // Check for integer
        try {
            Integer.parseInt(trimmedValue);
            return Integer.class;
        } catch (NumberFormatException e) {
            // Not an integer, continue checking
        }

        // Check for long
        try {
            Long.parseLong(trimmedValue);
            return Long.class;
        } catch (NumberFormatException e) {
            // Not a long, continue checking
        }

        // Check for double
        try {
            Double.parseDouble(trimmedValue);
            return Double.class;
        } catch (NumberFormatException e) {
            // Not a double, continue checking
        }

        // Check for float
        try {
            Float.parseFloat(trimmedValue);
            return Float.class;
        } catch (NumberFormatException e) {
            // Not a float, continue checking
        }

        // Default to String
        return String.class;
    }

    /**
     * Gets a property value as a specific type.
     *
     * @param resourcePath the path to the properties file in classpath
     * @param key the property key to retrieve
     * @param type the expected type class
     * @param <T> the type parameter
     * @return Optional containing the typed value, or empty if not found or conversion fails
     */
    public static <T> Optional<T> getPropertyAs(String resourcePath, String key, Class<T> type){
        Optional<PropertyValue> propertyValue = getProperty(resourcePath, key);

        if (!propertyValue.isPresent()) {
            return Optional.empty();
        }

        String value = propertyValue.get().getValue();

        try {
            if (type == String.class) {
                return Optional.of(type.cast(value));
            } else if (type == Integer.class) {
                return Optional.of(type.cast(Integer.parseInt(value)));
            } else if (type == Long.class) {
                return Optional.of(type.cast(Long.parseLong(value)));
            } else if (type == Double.class) {
                return Optional.of(type.cast(Double.parseDouble(value)));
            } else if (type == Float.class) {
                return Optional.of(type.cast(Float.parseFloat(value)));
            } else if (type == Boolean.class) {
                return Optional.of(type.cast(Boolean.parseBoolean(value)));
            } else {
                return Optional.empty();
            }
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
