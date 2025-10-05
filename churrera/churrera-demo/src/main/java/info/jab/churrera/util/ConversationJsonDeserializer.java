package info.jab.churrera.util;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class for deserializing JSON strings from conversation content into the specified type.
 * This class extracts JSON content from &lt;result&gt; tags and deserializes it.
 */
public final class ConversationJsonDeserializer {

    /**
     * Regular expression pattern to match content within &lt;result&gt; tags.
     * Uses DOTALL flag to match across multiple lines and captures the content.
     */
    private static final Pattern RESULT_PATTERN = Pattern.compile(
        "<result>(.*?)</result>",
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    /**
     * Jackson ObjectMapper for JSON deserialization.
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private ConversationJsonDeserializer() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Extracts JSON content from &lt;result&gt; tags and deserializes it into the specified type.
     *
     * @param conversationContent the conversation content containing &lt;result&gt; tags with JSON
     * @param targetType the target type class
     * @param <T> the type parameter
     * @return Optional containing the deserialized object, or empty if not found or deserialization fails
     * @throws IllegalArgumentException if conversationContent is null or targetType is null
     */
    public static <T> Optional<T> deserialize(String conversationContent, Class<T> targetType) {
        if (conversationContent == null) {
            throw new IllegalArgumentException("Conversation content cannot be null");
        }
        if (targetType == null) {
            throw new IllegalArgumentException("Target type cannot be null");
        }

        Optional<String> jsonContent = extractJsonFromResult(conversationContent);
        if (!jsonContent.isPresent()) {
            return Optional.empty();
        }

        return deserializeJson(jsonContent.get(), targetType);
    }

    /**
     * Extracts JSON content from &lt;result&gt; tags and deserializes it into a list of the specified type.
     *
     * @param conversationContent the conversation content containing &lt;result&gt; tags with JSON
     * @param targetType the target type class for list elements
     * @param <T> the type parameter
     * @return Optional containing the deserialized list, or empty if not found or deserialization fails
     * @throws IllegalArgumentException if conversationContent is null or targetType is null
     */
    public static <T> Optional<java.util.List<T>> deserializeList(String conversationContent, Class<T> targetType) {
        if (conversationContent == null) {
            throw new IllegalArgumentException("Conversation content cannot be null");
        }
        if (targetType == null) {
            throw new IllegalArgumentException("Target type cannot be null");
        }

        Optional<String> jsonContent = extractJsonFromResult(conversationContent);
        if (!jsonContent.isPresent()) {
            return Optional.empty();
        }

        return deserializeJsonList(jsonContent.get(), targetType);
    }

    /**
     * Extracts JSON content from the last &lt;result&gt; tag found in the conversation content.
     * This method finds the last occurrence to avoid matching instruction text.
     *
     * @param conversationContent the conversation content containing &lt;result&gt; tags
     * @return Optional containing the extracted JSON content, or empty if not found
     */
    private static Optional<String> extractJsonFromResult(String conversationContent) {
        Matcher matcher = RESULT_PATTERN.matcher(conversationContent);
        String lastMatch = null;

        // Find all matches and keep the last one
        while (matcher.find()) {
            lastMatch = matcher.group(1).trim();
        }

        if (lastMatch != null && !lastMatch.isEmpty()) {
            return Optional.of(lastMatch);
        }

        return Optional.empty();
    }

    /**
     * Deserializes a JSON string into the specified type.
     *
     * @param jsonContent the JSON content to deserialize
     * @param targetType the target type class
     * @param <T> the type parameter
     * @return Optional containing the deserialized object, or empty if deserialization fails
     */
    private static <T> Optional<T> deserializeJson(String jsonContent, Class<T> targetType) {
        try {
            T result = OBJECT_MAPPER.readValue(jsonContent, targetType);
            return Optional.of(result);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Deserializes a JSON string into a list of the specified type.
     *
     * @param jsonContent the JSON content to deserialize
     * @param targetType the target type class for list elements
     * @param <T> the type parameter
     * @return Optional containing the deserialized list, or empty if deserialization fails
     */
    private static <T> Optional<java.util.List<T>> deserializeJsonList(String jsonContent, Class<T> targetType) {
        try {
            TypeReference<java.util.List<java.util.Map<String, Object>>> typeRef =
                new TypeReference<java.util.List<java.util.Map<String, Object>>>() {};
            java.util.List<java.util.Map<String, Object>> rawList = OBJECT_MAPPER.readValue(jsonContent, typeRef);

            java.util.List<T> result = new java.util.ArrayList<>();
            for (java.util.Map<String, Object> item : rawList) {
                T convertedItem = OBJECT_MAPPER.convertValue(item, targetType);
                result.add(convertedItem);
            }
            return Optional.of(result);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
