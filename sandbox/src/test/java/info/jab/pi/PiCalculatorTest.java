package info.jab.pi;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PiCalculatorTest {

    private static final double EXPECTED_PI = Math.PI;
    private static final double STANDARD_DELTA = 0.0001;
    private static final String EXPECTED_PI_HIGH_PRECISION = "3.14159265358979323846264338327950288419716939937510";

    @ParameterizedTest
    @MethodSource("providePiCalculators")
    void testPiCalculation(String algorithmName, PiCalculator calculator) {
        double calculatedPi = calculator.calculatePi();
        assertEquals(EXPECTED_PI, calculatedPi, STANDARD_DELTA, 
            "Pi calculation using " + algorithmName + " should be accurate within standard delta");
    }

    @ParameterizedTest
    @MethodSource("provideHighPrecisionPiCalculators")
    void testHighPrecisionPiCalculation(String algorithmName, HighPrecisionPiCalculator calculator) {
        String calculatedPi = calculator.calculatePiHighPrecision(50);
        
        // For high precision, we compare the first 48 digits (allowing some tolerance for rounding)
        String expectedPrefix = EXPECTED_PI_HIGH_PRECISION.substring(0, 48);
        String actualPrefix = calculatedPi.length() >= 48 ? calculatedPi.substring(0, 48) : calculatedPi;
        
        assertEquals(expectedPrefix, actualPrefix, 
            "High precision Pi calculation using " + algorithmName + " should match expected precision to 48 digits");
    }

    static Stream<Arguments> providePiCalculators() {
        return Stream.of(
            Arguments.of("Machin-like Formula", new MachinPiCalculator()),
            Arguments.of("Bailey-Borwein-Plouffe Formula", new BBPPiCalculator()),
            Arguments.of("Gauss-Legendre Algorithm", new GaussLegendrePiCalculator()),
            Arguments.of("Spigot Algorithm", new SpigotPiCalculator())
        );
    }

    static Stream<Arguments> provideHighPrecisionPiCalculators() {
        return Stream.of(
            Arguments.of("Chudnovsky Algorithm", new ChudnovskyPiCalculator()),
            Arguments.of("Machin-like Formula", new MachinPiCalculator()),
            Arguments.of("Bailey-Borwein-Plouffe Formula", new BBPPiCalculator())
        );
    }
}