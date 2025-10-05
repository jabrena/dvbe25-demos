package info.jab.churrera.util;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for deserializing XML results wrapped in &lt;result&gt; tags into specific data types.
 *
 * This class provides methods to extract and convert content from XML result tags
 * into various Java data types such as String, Integer, Double, Boolean, etc.
 */
public final class XmlResultDeserializer {

    /**
     * Regular expression pattern to match content within &lt;result&gt; tags.
     * Uses DOTALL flag to match across multiple lines and captures the content.
     */
    private static final Pattern RESULT_PATTERN = Pattern.compile(
        "<result>(.*?)</result>",
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private XmlResultDeserializer() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Extracts the first result content from XML and returns it as a clean String.
     * Returns only the value content without any wrapper formatting.
     *
     * @param xmlContent the XML content containing &lt;result&gt; tags
     * @return Optional containing the extracted result as String, or empty if not found
     * @throws IllegalArgumentException if xmlContent is null
     */
    public static Optional<String> extractResult(String xmlContent) {
        if (xmlContent == null) {
            throw new IllegalArgumentException("XML content cannot be null");
        }

        Matcher matcher = RESULT_PATTERN.matcher(xmlContent);
        if (matcher.find()) {
            String result = matcher.group(1).trim();
            // Return only the clean value, removing any potential wrapper text
            return Optional.of(cleanResultValue(result));
        }

        return Optional.empty();
    }

    /**
     * Extracts the first result content from XML and converts it to the specified type.
     *
     * @param xmlContent the XML content containing &lt;result&gt; tags
     * @param targetType the target type class
     * @param <T> the type parameter
     * @return Optional containing the converted result, or empty if not found or conversion fails
     * @throws IllegalArgumentException if xmlContent is null or targetType is null
     */
    public static <T> Optional<T> extractResultAs(String xmlContent, Class<T> targetType) {
        if (xmlContent == null) {
            throw new IllegalArgumentException("XML content cannot be null");
        }
        if (targetType == null) {
            throw new IllegalArgumentException("Target type cannot be null");
        }

        Optional<String> resultOpt = extractResult(xmlContent);
        if (!resultOpt.isPresent()) {
            return Optional.empty();
        }

        String result = resultOpt.get();
        return convertToType(result, targetType);
    }

    /**
     * Extracts all result contents from XML and returns them as a list of clean Strings.
     * Returns only the value content without any wrapper formatting.
     *
     * @param xmlContent the XML content containing multiple &lt;result&gt; tags
     * @return List of extracted results as Strings, empty list if none found
     * @throws IllegalArgumentException if xmlContent is null
     */
    public static java.util.List<String> extractAllResults(String xmlContent) {
        if (xmlContent == null) {
            throw new IllegalArgumentException("XML content cannot be null");
        }

        java.util.List<String> results = new java.util.ArrayList<>();
        Matcher matcher = RESULT_PATTERN.matcher(xmlContent);

        while (matcher.find()) {
            String result = matcher.group(1).trim();
            results.add(cleanResultValue(result));
        }

        return results;
    }

    /**
     * Extracts all result contents from XML and converts them to the specified type.
     *
     * @param xmlContent the XML content containing multiple &lt;result&gt; tags
     * @param targetType the target type class
     * @param <T> the type parameter
     * @return List of converted results, empty list if none found or all conversions fail
     * @throws IllegalArgumentException if xmlContent is null or targetType is null
     */
    public static <T> java.util.List<T> extractAllResultsAs(String xmlContent, Class<T> targetType) {
        if (xmlContent == null) {
            throw new IllegalArgumentException("XML content cannot be null");
        }
        if (targetType == null) {
            throw new IllegalArgumentException("Target type cannot be null");
        }

        java.util.List<String> stringResults = extractAllResults(xmlContent);
        java.util.List<T> convertedResults = new java.util.ArrayList<>();

        for (String result : stringResults) {
            convertToType(result, targetType).ifPresent(convertedResults::add);
        }

        return convertedResults;
    }

    /**
     * Cleans the result value by removing any wrapper formatting or labels.
     * This ensures only the actual value content is returned.
     *
     * @param value the raw result value
     * @return the cleaned value
     */
    private static String cleanResultValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return value;
        }

        String trimmed = value.trim();

        // Remove common wrapper patterns like "[RESULT, value]" or "RESULT: value"
        if (trimmed.startsWith("[RESULT,") && trimmed.endsWith("]")) {
            // Extract content between [RESULT, and ]
            int startIndex = 8; // Length of "[RESULT,"
            int endIndex = trimmed.length() - 1;
            if (startIndex < endIndex) {
                String content = trimmed.substring(startIndex, endIndex).trim();
                // Remove trailing comma if present
                if (content.endsWith(",")) {
                    content = content.substring(0, content.length() - 1).trim();
                }
                return content;
            }
        }

        // Remove "RESULT:" prefix if present
        if (trimmed.startsWith("RESULT:")) {
            return trimmed.substring(7).trim();
        }

        // Remove "RESULT " prefix if present
        if (trimmed.startsWith("RESULT ")) {
            return trimmed.substring(7).trim();
        }

        return trimmed;
    }

    /**
     * Converts a string value to the specified target type.
     *
     * @param value the string value to convert
     * @param targetType the target type class
     * @param <T> the type parameter
     * @return Optional containing the converted value, or empty if conversion fails
     */
    @SuppressWarnings("unchecked")
    private static <T> Optional<T> convertToType(String value, Class<T> targetType) {
        if (value == null || value.trim().isEmpty()) {
            return Optional.empty();
        }

        String cleanedValue = cleanResultValue(value);
        if (cleanedValue == null || cleanedValue.trim().isEmpty()) {
            return Optional.empty();
        }

        String trimmedValue = cleanedValue.trim();

        try {
            if (targetType == String.class) {
                return Optional.of((T) trimmedValue);
            } else if (targetType == Integer.class) {
                return Optional.of((T) Integer.valueOf(trimmedValue));
            } else if (targetType == Long.class) {
                return Optional.of((T) Long.valueOf(trimmedValue));
            } else if (targetType == Double.class) {
                return Optional.of((T) Double.valueOf(trimmedValue));
            } else if (targetType == Float.class) {
                return Optional.of((T) Float.valueOf(trimmedValue));
            } else if (targetType == Boolean.class) {
                return Optional.of((T) Boolean.valueOf(trimmedValue));
            } else if (targetType == Character.class) {
                if (trimmedValue.length() == 1) {
                    return Optional.of((T) Character.valueOf(trimmedValue.charAt(0)));
                }
                return Optional.empty();
            } else {
                // For other types, try to find a constructor that takes a String
                try {
                    return Optional.of((T) targetType.getConstructor(String.class).newInstance(trimmedValue));
                } catch (Exception e) {
                    return Optional.empty();
                }
            }
        } catch (NumberFormatException e) {
            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Checks if the XML content contains any result tags.
     *
     * @param xmlContent the XML content to check
     * @return true if result tags are found, false otherwise
     * @throws IllegalArgumentException if xmlContent is null
     */
    public static boolean hasResults(String xmlContent) {
        if (xmlContent == null) {
            throw new IllegalArgumentException("XML content cannot be null");
        }

        return RESULT_PATTERN.matcher(xmlContent).find();
    }

    /**
     * Counts the number of result tags in the XML content.
     *
     * @param xmlContent the XML content to analyze
     * @return the number of result tags found
     * @throws IllegalArgumentException if xmlContent is null
     */
    public static int countResults(String xmlContent) {
        if (xmlContent == null) {
            throw new IllegalArgumentException("XML content cannot be null");
        }

        Matcher matcher = RESULT_PATTERN.matcher(xmlContent);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }
}
