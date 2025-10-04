package info.jab.churrera.util;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for converting PML (Prompt Markup Language) XML files to Markdown format
 * using XSLT transformations.
 */
public final class PmlConverter {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private PmlConverter() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Converts a PML XML file to Markdown format using the specified XSLT transformation.
     *
     * @param pmlFile the path to the PML XML file in classpath resources
     * @param xsltFile the path to the XSLT file in classpath resources
     * @return the converted Markdown content as a String
     * @throws IllegalArgumentException if either parameter is null or empty
     * @throws RuntimeException if the conversion fails
     */
    public static String toMarkdown(String pmlFile, String xsltFile) {
        if (pmlFile == null || pmlFile.trim().isEmpty()) {
            throw new IllegalArgumentException("PML file path cannot be null or empty");
        }
        if (xsltFile == null || xsltFile.trim().isEmpty()) {
            throw new IllegalArgumentException("XSLT file path cannot be null or empty");
        }

        try {
            // Load PML XML content from classpath
            String pmlContent = ClasspathResolver.retrieve(pmlFile);

            // Load XSLT content from classpath
            String xsltContent = ClasspathResolver.retrieve(xsltFile);

            // Create input streams
            InputStream pmlStream = new ByteArrayInputStream(pmlContent.getBytes(StandardCharsets.UTF_8));
            InputStream xsltStream = new ByteArrayInputStream(xsltContent.getBytes(StandardCharsets.UTF_8));

            // Create transformer
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(xsltStream));

            // Perform transformation
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            transformer.transform(new StreamSource(pmlStream), new StreamResult(outputStream));

            // Return result as String
            return outputStream.toString(StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException("Failed to convert PML to Markdown: " + e.getMessage(), e);
        }
    }

    /**
     * Converts a PML XML file to Markdown format using the default XSLT transformation.
     * Uses the default XSLT file located at "pml/pml-to-md.xsl".
     *
     * @param pmlFile the path to the PML XML file in classpath resources
     * @return the converted Markdown content as a String
     * @throws IllegalArgumentException if pmlFile is null or empty
     * @throws RuntimeException if the conversion fails
     */
    public static String toMarkdown(String pmlFile) {
        return toMarkdown(pmlFile, "pml/pml-to-md.xsl");
    }
}
