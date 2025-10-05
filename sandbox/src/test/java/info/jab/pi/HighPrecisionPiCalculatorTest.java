package info.jab.pi;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

/**
 * Parameterized test class for testing different Pi calculation algorithms.
 */
public class HighPrecisionPiCalculatorTest {

    private static final String EXPECTED_PI_HIGH_PRECISION = "3.14159265358979323846";

    /**
     * Provides different Pi calculator implementations for parameterized tests.
     */
    static Stream<HighPrecisionPiCalculator> piCalculatorProvider() {
        return Stream.of(
            new MachinFormulaPiCalculator(),
            new ChudnovskyAlgorithmPiCalculator()
        );
    }

    @ParameterizedTest
    @MethodSource("piCalculatorProvider")
    void testHighPrecisionPiCalculation(HighPrecisionPiCalculator calculator) {
        // Test with 20 decimal places precision
        BigDecimal result = calculator.calculatePiHighPrecision(20);
        
        // Convert expected pi to BigDecimal for comparison
        BigDecimal expectedPi = new BigDecimal(EXPECTED_PI_HIGH_PRECISION);
        
        // Compare with tolerance - check if the result matches expected precision
        BigDecimal difference = result.subtract(expectedPi).abs();
        BigDecimal tolerance = new BigDecimal("0.00000000000000000001"); // 20 decimal places tolerance
        
        assertTrue(difference.compareTo(tolerance) <= 0, 
            String.format("Pi calculation failed. Expected: %s, Got: %s, Difference: %s", 
                expectedPi, result, difference));
    }

    @ParameterizedTest
    @MethodSource("piCalculatorProvider")
    void testPiCalculationWith10Digits(HighPrecisionPiCalculator calculator) {
        BigDecimal result = calculator.calculatePiHighPrecision(10);
        BigDecimal expectedPi = new BigDecimal("3.1415926536");
        
        // Round both to 10 decimal places for comparison
        result = result.setScale(10, RoundingMode.HALF_UP);
        
        assertEquals(expectedPi, result, 
            String.format("Pi calculation with 10 digits failed. Expected: %s, Got: %s", 
                expectedPi, result));
    }

    @ParameterizedTest
    @MethodSource("piCalculatorProvider")
    void testPiCalculationWith5Digits(HighPrecisionPiCalculator calculator) {
        BigDecimal result = calculator.calculatePiHighPrecision(5);
        BigDecimal expectedPi = new BigDecimal("3.14159");
        
        // Round both to 5 decimal places for comparison
        result = result.setScale(5, RoundingMode.HALF_UP);
        
        assertEquals(expectedPi, result, 
            String.format("Pi calculation with 5 digits failed. Expected: %s, Got: %s", 
                expectedPi, result));
    }
}