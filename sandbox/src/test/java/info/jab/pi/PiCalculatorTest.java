package info.jab.pi;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.*;
import java.util.stream.Stream;
import java.math.BigDecimal;

/**
 * Parameterized test class for all Pi calculation implementations
 */
public class PiCalculatorTest {

    private static final double EXPECTED_PI = Math.PI;
    private static final double STANDARD_DELTA = 0.0001;
    private static final String EXPECTED_PI_HIGH_PRECISION = "3.14159265358979323846264338327950288419716939937510";

    /**
     * Provides all Pi calculator implementations for parameterized tests
     */
    private static Stream<Arguments> piCalculatorProvider() {
        return Stream.of(
            Arguments.of("Machin-like Formula", new MachinFormulaPiCalculator()),
            Arguments.of("Chudnovsky Algorithm", new ChudnovskyPiCalculator()),
            Arguments.of("Bailey-Borwein-Plouffe Formula", new BBPPiCalculator()),
            Arguments.of("Gauss-Legendre Algorithm", new GaussLegendrePiCalculator()),
            Arguments.of("Spigot Algorithm", new SpigotPiCalculator())
        );
    }

    @ParameterizedTest(name = "{0} should calculate Pi with standard precision")
    @MethodSource("piCalculatorProvider")
    void testPiCalculationStandardPrecision(String algorithmName, PiCalculator calculator) {
        double calculatedPi = calculator.calculatePi();
        assertEquals(EXPECTED_PI, calculatedPi, STANDARD_DELTA, 
            algorithmName + " should calculate Pi within standard delta");
    }

    @ParameterizedTest(name = "{0} should calculate Pi with high precision")
    @MethodSource("piCalculatorProvider")
    void testPiCalculationHighPrecision(String algorithmName, PiCalculator calculator) {
        if (calculator instanceof HighPrecisionPiCalculator) {
            HighPrecisionPiCalculator highPrecisionCalculator = (HighPrecisionPiCalculator) calculator;
            String calculatedPi = highPrecisionCalculator.calculatePiHighPrecision(50);
            assertTrue(calculatedPi.startsWith(EXPECTED_PI_HIGH_PRECISION.substring(0, 20)),
                algorithmName + " should calculate Pi with high precision accuracy");
        }
    }

    @ParameterizedTest(name = "{0} should calculate Pi with specified iterations")
    @MethodSource("piCalculatorProvider")
    void testPiCalculationWithIterations(String algorithmName, PiCalculator calculator) {
        double calculatedPi = calculator.calculatePi(1000);
        assertEquals(EXPECTED_PI, calculatedPi, STANDARD_DELTA,
            algorithmName + " should calculate Pi with specified iterations");
    }

    @ParameterizedTest(name = "{0} should not return NaN or infinite values")
    @MethodSource("piCalculatorProvider")
    void testPiCalculationValidValues(String algorithmName, PiCalculator calculator) {
        double calculatedPi = calculator.calculatePi();
        assertFalse(Double.isNaN(calculatedPi), 
            algorithmName + " should not return NaN");
        assertFalse(Double.isInfinite(calculatedPi), 
            algorithmName + " should not return infinite value");
        assertTrue(calculatedPi > 0, 
            algorithmName + " should return positive value");
    }

    @ParameterizedTest(name = "{0} should be consistent across multiple calculations")
    @MethodSource("piCalculatorProvider")
    void testPiCalculationConsistency(String algorithmName, PiCalculator calculator) {
        double firstCalculation = calculator.calculatePi();
        double secondCalculation = calculator.calculatePi();
        assertEquals(firstCalculation, secondCalculation, 1e-10,
            algorithmName + " should return consistent results");
    }
}