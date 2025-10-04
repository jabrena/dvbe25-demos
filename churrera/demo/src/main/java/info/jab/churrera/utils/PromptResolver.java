package info.jab.churrera.utils;

import java.nio.charset.StandardCharsets;

/**
 * Utility class for loading prompt files from classpath resources.
 *
 * This class provides methods to load prompt content from text files
 * located in the classpath resources directory.
 */
public final class PromptResolver {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private PromptResolver() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Gets a prompt by loading from classpath resource with the specified filename.
     * This method should be called at runtime, not during class initialization.
     *
     * @param fileName the name of the prompt file (e.g., "prompt1.txt", "prompt2.txt")
     * @return the prompt content from the specified file
     * @throws IllegalArgumentException if fileName is null or empty
     * @throws RuntimeException if the file cannot be found or read
     */
    public static String retrieve(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }

        try (var inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new RuntimeException("Could not find " + fileName + " in classpath");
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load prompt from " + fileName + ": " + e.getMessage(), e);
        }
    }
}
