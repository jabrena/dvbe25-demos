package info.jab.churrera.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

/**
 * Test class for XmlResultDeserializer utility.
 */
@DisplayName("XmlResultDeserializer Tests")
class XmlResultDeserializerTest {

    @Test
    @DisplayName("Should extract single result as String")
    void testExtractSingleResultAsString() {
        String xmlContent = "Some text before <result>Hello World</result> some text after";

        Optional<String> result = XmlResultDeserializer.extractResult(xmlContent);

        assertTrue(result.isPresent());
        assertEquals("Hello World", result.get());
    }

    @Test
    @DisplayName("Should extract single result as Integer")
    void testExtractSingleResultAsInteger() {
        String xmlContent = "Some text before <result>42</result> some text after";

        Optional<Integer> result = XmlResultDeserializer.extractResultAs(xmlContent, Integer.class);

        assertTrue(result.isPresent());
        assertEquals(Integer.valueOf(42), result.get());
    }

    @Test
    @DisplayName("Should extract single result as Double")
    void testExtractSingleResultAsDouble() {
        String xmlContent = "Some text before <result>3.14159</result> some text after";

        Optional<Double> result = XmlResultDeserializer.extractResultAs(xmlContent, Double.class);

        assertTrue(result.isPresent());
        assertEquals(Double.valueOf(3.14159), result.get());
    }

    @Test
    @DisplayName("Should extract single result as Boolean")
    void testExtractSingleResultAsBoolean() {
        String xmlContent = "Some text before <result>true</result> some text after";

        Optional<Boolean> result = XmlResultDeserializer.extractResultAs(xmlContent, Boolean.class);

        assertTrue(result.isPresent());
        assertEquals(Boolean.TRUE, result.get());
    }

    @Test
    @DisplayName("Should extract multiple results as Strings")
    void testExtractMultipleResultsAsStrings() {
        String xmlContent = "First <result>Result 1</result> Second <result>Result 2</result> Third <result>Result 3</result>";

        List<String> results = XmlResultDeserializer.extractAllResults(xmlContent);

        assertEquals(3, results.size());
        assertEquals("Result 1", results.get(0));
        assertEquals("Result 2", results.get(1));
        assertEquals("Result 3", results.get(2));
    }

    @Test
    @DisplayName("Should extract multiple results as Integers")
    void testExtractMultipleResultsAsIntegers() {
        String xmlContent = "First <result>10</result> Second <result>20</result> Third <result>30</result>";

        List<Integer> results = XmlResultDeserializer.extractAllResultsAs(xmlContent, Integer.class);

        assertEquals(3, results.size());
        assertEquals(Integer.valueOf(10), results.get(0));
        assertEquals(Integer.valueOf(20), results.get(1));
        assertEquals(Integer.valueOf(30), results.get(2));
    }

    @Test
    @DisplayName("Should handle multiline results")
    void testExtractMultilineResult() {
        String xmlContent = "Some text before <result>Line 1\nLine 2\nLine 3</result> some text after";

        Optional<String> result = XmlResultDeserializer.extractResult(xmlContent);

        assertTrue(result.isPresent());
        assertEquals("Line 1\nLine 2\nLine 3", result.get());
    }

    @Test
    @DisplayName("Should handle case insensitive result tags")
    void testExtractCaseInsensitiveResult() {
        String xmlContent = "Some text before <RESULT>Case Insensitive</RESULT> some text after";

        Optional<String> result = XmlResultDeserializer.extractResult(xmlContent);

        assertTrue(result.isPresent());
        assertEquals("Case Insensitive", result.get());
    }

    @Test
    @DisplayName("Should return empty when no result tags found")
    void testExtractNoResults() {
        String xmlContent = "Some text without result tags";

        Optional<String> result = XmlResultDeserializer.extractResult(xmlContent);
        List<String> allResults = XmlResultDeserializer.extractAllResults(xmlContent);

        assertFalse(result.isPresent());
        assertTrue(allResults.isEmpty());
    }

    @Test
    @DisplayName("Should return empty when conversion fails")
    void testExtractInvalidConversion() {
        String xmlContent = "Some text before <result>not a number</result> some text after";

        Optional<Integer> result = XmlResultDeserializer.extractResultAs(xmlContent, Integer.class);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should handle empty result content")
    void testExtractEmptyResult() {
        String xmlContent = "Some text before <result></result> some text after";

        Optional<String> result = XmlResultDeserializer.extractResult(xmlContent);

        assertTrue(result.isPresent());
        assertEquals("", result.get());
    }

    @Test
    @DisplayName("Should handle whitespace in result content")
    void testExtractWhitespaceResult() {
        String xmlContent = "Some text before <result>   Hello World   </result> some text after";

        Optional<String> result = XmlResultDeserializer.extractResult(xmlContent);

        assertTrue(result.isPresent());
        assertEquals("Hello World", result.get()); // Should be trimmed
    }

    @Test
    @DisplayName("Should check if results exist")
    void testHasResults() {
        String xmlWithResults = "Some text before <result>Hello</result> some text after";
        String xmlWithoutResults = "Some text without result tags";

        assertTrue(XmlResultDeserializer.hasResults(xmlWithResults));
        assertFalse(XmlResultDeserializer.hasResults(xmlWithoutResults));
    }

    @Test
    @DisplayName("Should count results correctly")
    void testCountResults() {
        String xmlContent = "First <result>Result 1</result> Second <result>Result 2</result> Third <result>Result 3</result>";
        String xmlWithoutResults = "Some text without result tags";

        assertEquals(3, XmlResultDeserializer.countResults(xmlContent));
        assertEquals(0, XmlResultDeserializer.countResults(xmlWithoutResults));
    }

    @Test
    @DisplayName("Should throw exception for null input")
    void testNullInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            XmlResultDeserializer.extractResult(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            XmlResultDeserializer.extractResultAs(null, String.class);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            XmlResultDeserializer.extractResultAs("test", null);
        });
    }

    @Test
    @DisplayName("Should handle Character type conversion")
    void testCharacterConversion() {
        String xmlContent = "Some text before <result>A</result> some text after";

        Optional<Character> result = XmlResultDeserializer.extractResultAs(xmlContent, Character.class);

        assertTrue(result.isPresent());
        assertEquals(Character.valueOf('A'), result.get());
    }

    @Test
    @DisplayName("Should handle invalid Character conversion")
    void testInvalidCharacterConversion() {
        String xmlContent = "Some text before <result>AB</result> some text after";

        Optional<Character> result = XmlResultDeserializer.extractResultAs(xmlContent, Character.class);

        assertFalse(result.isPresent()); // Should fail because "AB" is not a single character
    }

    @Test
    @DisplayName("Should clean wrapper patterns like [RESULT, value]")
    void testCleanWrapperPatterns() {
        String xmlContent = "Some text before <result>[RESULT, Hello World]</result> some text after";

        Optional<String> result = XmlResultDeserializer.extractResult(xmlContent);

        assertTrue(result.isPresent());
        assertEquals("Hello World", result.get());
    }

    @Test
    @DisplayName("Should clean RESULT: prefix")
    void testCleanResultPrefix() {
        String xmlContent = "Some text before <result>RESULT: Hello World</result> some text after";

        Optional<String> result = XmlResultDeserializer.extractResult(xmlContent);

        assertTrue(result.isPresent());
        assertEquals("Hello World", result.get());
    }

    @Test
    @DisplayName("Should clean RESULT prefix")
    void testCleanResultPrefixWithoutColon() {
        String xmlContent = "Some text before <result>RESULT Hello World</result> some text after";

        Optional<String> result = XmlResultDeserializer.extractResult(xmlContent);

        assertTrue(result.isPresent());
        assertEquals("Hello World", result.get());
    }

    @Test
    @DisplayName("Should handle multiple results with wrapper patterns")
    void testMultipleResultsWithWrapperPatterns() {
        String xmlContent = "First <result>[RESULT, Value 1]</result> Second <result>RESULT: Value 2</result> Third <result>RESULT Value 3</result>";

        List<String> results = XmlResultDeserializer.extractAllResults(xmlContent);

        assertEquals(3, results.size());
        assertEquals("Value 1", results.get(0));
        assertEquals("Value 2", results.get(1));
        assertEquals("Value 3", results.get(2));
    }
}
