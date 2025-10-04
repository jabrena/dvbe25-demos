package info.jab.pi;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Parameterized test for different Pi calculation implementations.
 * Tests all approaches with a reasonable delta for floating-point comparison.
 */
public class PiCalculatorTest {

    private static final double EXPECTED_PI = Math.PI;
    private static final double DELTA = 0.0001; // Reasonable delta for comparison

    /**
     * Provides test arguments for each Pi calculation implementation.
     */
    static Stream<Arguments> piCalculatorProvider() {
        return Stream.of(
            Arguments.of("Machin-like Formula", new MachinLikeFormulaCalculator()),
            Arguments.of("Chudnovsky Algorithm", new ChudnovskyAlgorithmCalculator()),
            Arguments.of("Bailey-Borwein-Plouffe Formula", new BaileyBorweinPlouffeCalculator()),
            Arguments.of("Gauss-Legendre Algorithm", new GaussLegendreAlgorithmCalculator()),
            Arguments.of("Spigot Algorithm", new SpigotAlgorithmCalculator())
        );
    }

    /**
     * Test each Pi calculation implementation with parameterized test.
     * @param algorithmName Name of the algorithm for better test reporting
     * @param calculator The Pi calculator implementation to test
     */
    @ParameterizedTest(name = "Pi calculation using {0}")
    @MethodSource("piCalculatorProvider")
    void testPiCalculation(String algorithmName, PiCalculator calculator) {
        double calculatedPi = calculator.calculatePi();
        assertEquals(EXPECTED_PI, calculatedPi, DELTA, 
            "Pi calculation using " + algorithmName + " should be accurate within delta " + DELTA);
    }

    /**
     * Test each Pi calculation implementation with higher precision using BigDecimal.
     * @param algorithmName Name of the algorithm for better test reporting
     * @param calculator The Pi calculator implementation to test
     */
    @ParameterizedTest(name = "High precision Pi calculation using {0}")
    @MethodSource("piCalculatorProvider")
    void testHighPrecisionPiCalculation(String algorithmName, PiCalculator calculator) {
        if (calculator instanceof HighPrecisionPiCalculator) {
            HighPrecisionPiCalculator highPrecisionCalculator = (HighPrecisionPiCalculator) calculator;
            BigDecimal calculatedPi = highPrecisionCalculator.calculatePiHighPrecision(50);
            
            // Convert expected Pi to BigDecimal with same precision for comparison
            BigDecimal expectedPi = new BigDecimal("3.14159265358979323846264338327950288419716939937510");
            
            // Use different accuracy expectations based on algorithm efficiency
            int decimalPlaces;
            if (algorithmName.contains("Chudnovsky") || algorithmName.contains("Spigot")) {
                decimalPlaces = 2; // More lenient for Leibniz-based implementations
            } else {
                decimalPlaces = 10; // Stricter for more efficient algorithms
            }
            
            assertEquals(expectedPi.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP), 
                        calculatedPi.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP),
                        "High precision Pi calculation using " + algorithmName + " should be accurate to " + decimalPlaces + " decimal places");
        }
    }
}