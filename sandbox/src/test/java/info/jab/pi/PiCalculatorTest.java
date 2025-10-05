package info.jab.pi;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pi Calculator Tests")
class PiCalculatorTest {

    private static final double EXPECTED_PI = Math.PI;
    private static final double STANDARD_DELTA = 0.0001;
    private static final String EXPECTED_PI_HIGH_PRECISION = "3.14159265358979323846264338327950288419716939937510";

    static Stream<Arguments> piCalculatorProvider() {
        return Stream.of(
            Arguments.of("MachinLikeFormula", new MachinLikeFormulaPiCalculator()),
            Arguments.of("ChudnovskyAlgorithm", new ChudnovskyPiCalculator())
        );
    }

    @ParameterizedTest(name = "{0} - Standard precision test")
    @MethodSource("piCalculatorProvider")
    @DisplayName("Test Pi calculation with standard precision")
    void testPiCalculationStandardPrecision(String algorithmName, PiCalculator calculator) {
        double result = calculator.calculatePi();
        assertEquals(EXPECTED_PI, result, STANDARD_DELTA, 
            "Pi calculation using " + algorithmName + " should be accurate within standard delta");
    }

    @ParameterizedTest(name = "{0} - High precision test")
    @MethodSource("piCalculatorProvider")
    @DisplayName("Test Pi calculation with high precision")
    void testPiCalculationHighPrecision(String algorithmName, PiCalculator calculator) {
        if (calculator instanceof HighPrecisionPiCalculator) {
            HighPrecisionPiCalculator highPrecisionCalculator = (HighPrecisionPiCalculator) calculator;
            String result = highPrecisionCalculator.calculatePiHighPrecision(50);
            
            // Compare first 48 digits to allow for rounding differences in the last digits
            String expectedPrefix = EXPECTED_PI_HIGH_PRECISION.substring(0, 48);
            String actualPrefix = result.length() >= 48 ? result.substring(0, 48) : result;
            
            assertTrue(actualPrefix.equals(expectedPrefix), 
                "High precision Pi calculation using " + algorithmName + 
                " should match expected precision (first 48 digits). Expected: " + expectedPrefix + 
                ", Actual: " + actualPrefix);
        } else {
            // For calculators that don't support high precision, we skip this test
            assertTrue(true, "Calculator " + algorithmName + " doesn't support high precision");
        }
    }

    @ParameterizedTest(name = "{0} - Performance test")
    @MethodSource("piCalculatorProvider")
    @DisplayName("Test Pi calculation performance")
    void testPiCalculationPerformance(String algorithmName, PiCalculator calculator) {
        long startTime = System.nanoTime();
        double result = calculator.calculatePi();
        long endTime = System.nanoTime();
        
        long duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds
        
        assertTrue(duration < 10000, // Should complete within 10 seconds
            "Pi calculation using " + algorithmName + " should complete within reasonable time");
        assertEquals(EXPECTED_PI, result, STANDARD_DELTA,
            "Pi calculation result should be accurate even when measuring performance");
    }
}