package info.jab.churrera.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Utility class for storing string content to files.
 * Provides methods to save content to files in various locations.
 */
public final class FileStorage {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private FileStorage() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Stores string content to a file at the specified path.
     * Creates directories if they don't exist and overwrites existing files.
     *
     * @param fileContent the content to store in the file
     * @param fileName the name of the file
     * @param path the directory path where the file should be stored
     * @throws IllegalArgumentException if any parameter is null or empty
     * @throws RuntimeException if the file cannot be created or written
     */
    public static void storeToFile(String fileContent, String fileName, String path) {
        if (fileContent == null) {
            throw new IllegalArgumentException("File content cannot be null");
        }
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }

        try {
            // Create the directory path if it doesn't exist
            Path directoryPath = Paths.get(path);
            Files.createDirectories(directoryPath);

            // Create the full file path
            Path filePath = directoryPath.resolve(fileName);

            // Write content to file, overwriting if it exists
            Files.write(filePath, fileContent.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            System.out.println("Content stored to file " + filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store content to file " + fileName + " at path " + path + ": " + e.getMessage(), e);
        }
    }

    /**
     * Stores string content to a file in the resources directory.
     * This is a convenience method for storing test data or sample files.
     *
     * @param fileContent the content to store in the file
     * @param fileName the name of the file
     * @throws IllegalArgumentException if any parameter is null or empty
     * @throws RuntimeException if the file cannot be created or written
     */
    public static void storeToResources(String fileContent, String fileName) {
        storeToFile(fileContent, fileName, "src/main/resources");
    }

    /**
     * Stores string content to a file in the test resources directory.
     * This is a convenience method for storing test data files.
     *
     * @param fileContent the content to store in the file
     * @param fileName the name of the file
     * @throws IllegalArgumentException if any parameter is null or empty
     * @throws RuntimeException if the file cannot be created or written
     */
    public static void storeToTestResources(String fileContent, String fileName) {
        storeToFile(fileContent, fileName, "src/test/resources");
    }
}
