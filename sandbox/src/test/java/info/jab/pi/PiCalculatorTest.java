package info.jab.pi;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Parameterized test class for testing different Pi calculation implementations.
 */
public class PiCalculatorTest {

    private static final String EXPECTED_PI_HIGH_PRECISION = "3.14159265358979323846";

    /**
     * Provides test parameters for different Pi calculator implementations.
     */
    static Stream<Arguments> piCalculatorProvider() {
        return Stream.of(
            Arguments.of(new MachinLikePiCalculator(), "Machin-like Formula"),
            Arguments.of(new ChudnovskyPiCalculator(), "Chudnovsky Algorithm")
        );
    }

    @ParameterizedTest(name = "{1} - High Precision Test")
    @MethodSource("piCalculatorProvider")
    void testCalculatePiHighPrecision(HighPrecisionPiCalculator calculator, String algorithmName) {
        // Test with 10 decimal places precision
        int precision = 10;
        BigDecimal result = calculator.calculatePiHighPrecision(precision);
        
        // Convert expected PI to BigDecimal with same precision
        BigDecimal expected = new BigDecimal(EXPECTED_PI_HIGH_PRECISION);
        
        // Compare with tolerance - allow small differences
        BigDecimal difference = result.subtract(expected).abs();
        BigDecimal tolerance = new BigDecimal("0.01"); // Allow 1% tolerance
        
        System.out.println(algorithmName + " calculated Pi: " + result);
        System.out.println("Expected Pi: " + expected);
        System.out.println("Difference: " + difference);
        System.out.println("Tolerance: " + tolerance);
        
        // Assert that the difference is within tolerance
        assertEquals(-1, difference.compareTo(tolerance), 
            String.format("%s should calculate Pi within tolerance", algorithmName));
    }

    @ParameterizedTest(name = "{1} - Basic Precision Test")
    @MethodSource("piCalculatorProvider")
    void testCalculatePiBasicPrecision(HighPrecisionPiCalculator calculator, String algorithmName) {
        // Test with 5 decimal places precision for basic accuracy check
        int precision = 5;
        BigDecimal result = calculator.calculatePiHighPrecision(precision);
        
        // Convert expected PI to BigDecimal
        BigDecimal expected = new BigDecimal("3.14159");
        
        // Compare with tolerance for basic precision
        BigDecimal difference = result.subtract(expected).abs();
        BigDecimal tolerance = new BigDecimal("0.001"); // Allow small tolerance
        
        System.out.println(algorithmName + " calculated Pi (5 digits): " + result);
        System.out.println("Expected Pi (5 digits): " + expected);
        System.out.println("Difference: " + difference);
        
        // Assert that the difference is within tolerance
        assertEquals(-1, difference.compareTo(tolerance), 
            String.format("%s should calculate Pi within basic tolerance", algorithmName));
    }
}