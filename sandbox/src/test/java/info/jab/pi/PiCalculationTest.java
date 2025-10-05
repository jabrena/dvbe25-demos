package info.jab.pi;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class PiCalculationTest {

    private static final double EXPECTED_PI = Math.PI;
    private static final double STANDARD_DELTA = 0.0001;
    private static final String EXPECTED_PI_HIGH_PRECISION = "3.14159265358979323846264338327950288419716939937510";

    static Stream<Arguments> piCalculators() {
        return Stream.of(
            Arguments.of("Machin-like Formula", new MachinLikeFormulaPiCalculator()),
            Arguments.of("Chudnovsky Algorithm", new ChudnovskyPiCalculator()),
            Arguments.of("Bailey-Borwein-Plouffe (BBP) Formula", new BBPPiCalculator()),
            Arguments.of("Gauss-Legendre Algorithm", new GaussLegendrePiCalculator()),
            Arguments.of("Spigot Algorithm", new SpigotPiCalculator())
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("piCalculators")
    void testPiCalculationWithStandardPrecision(String algorithmName, PiCalculator calculator) {
        double result = calculator.calculatePi();
        assertEquals(EXPECTED_PI, result, STANDARD_DELTA, 
            algorithmName + " should calculate Pi with standard precision");
    }

    @ParameterizedTest(name = "{0} (High Precision)")
    @MethodSource("piCalculators")
    void testPiCalculationWithHighPrecision(String algorithmName, PiCalculator calculator) {
        if (calculator instanceof HighPrecisionPiCalculator highPrecisionCalculator) {
            String result = highPrecisionCalculator.calculatePiHighPrecision(50);
            assertTrue(result.startsWith(EXPECTED_PI_HIGH_PRECISION.substring(0, 20)),
                algorithmName + " should calculate Pi with high precision");
        }
    }

    @ParameterizedTest(name = "{0} - Performance Test")
    @MethodSource("piCalculators")
    void testPiCalculationPerformance(String algorithmName, PiCalculator calculator) {
        long startTime = System.nanoTime();
        double result = calculator.calculatePi();
        long endTime = System.nanoTime();
        
        double executionTimeMs = (endTime - startTime) / 1_000_000.0;
        
        // Verify result is correct
        assertEquals(EXPECTED_PI, result, STANDARD_DELTA);
        
        // Log performance (should complete within reasonable time - 10 seconds)
        assertTrue(executionTimeMs < 10000, 
            algorithmName + " should complete within 10 seconds, took " + executionTimeMs + "ms");
        
        System.out.printf("%s execution time: %.2f ms%n", algorithmName, executionTimeMs);
    }
}