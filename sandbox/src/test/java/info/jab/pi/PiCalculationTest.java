package info.jab.pi;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PiCalculationTest {

    private static final double EXPECTED_PI = Math.PI;
    private static final double STANDARD_DELTA = 0.0001;
    private static final String EXPECTED_PI_HIGH_PRECISION = "3.14159265358979323846264338327950288419716939937510";

    @ParameterizedTest(name = "{0} - Standard precision test")
    @MethodSource("piCalculatorProviders")
    void testPiCalculationStandardPrecision(String algorithmName, PiCalculator calculator) {
        double calculatedPi = calculator.calculatePi();
        assertEquals(EXPECTED_PI, calculatedPi, STANDARD_DELTA, 
                "Algorithm " + algorithmName + " failed standard precision test");
    }

    @ParameterizedTest(name = "{0} - High precision test")
    @MethodSource("highPrecisionPiCalculatorProviders")
    void testPiCalculationHighPrecision(String algorithmName, HighPrecisionPiCalculator calculator) {
        String calculatedPi = calculator.calculatePiHighPrecision(50);
        assertTrue(calculatedPi.startsWith(EXPECTED_PI_HIGH_PRECISION.substring(0, 30)), 
                "Algorithm " + algorithmName + " failed high precision test. Expected to start with: " + 
                EXPECTED_PI_HIGH_PRECISION.substring(0, 30) + " but got: " + calculatedPi.substring(0, Math.min(30, calculatedPi.length())));
    }

    static Stream<Arguments> piCalculatorProviders() {
        return Stream.of(
                Arguments.of("Machin-like Formula", new MachinLikeFormula()),
                Arguments.of("Chudnovsky Algorithm", new ChudnovskyAlgorithm()),
                Arguments.of("Bailey-Borwein-Plouffe Formula", new BaileyBorweinPlouffeFormula()),
                Arguments.of("Gauss-Legendre Algorithm", new GaussLegendreAlgorithm()),
                Arguments.of("Spigot Algorithm", new SpigotAlgorithm())
        );
    }

    static Stream<Arguments> highPrecisionPiCalculatorProviders() {
        return Stream.of(
                Arguments.of("Chudnovsky Algorithm", new ChudnovskyAlgorithm()),
                Arguments.of("Bailey-Borwein-Plouffe Formula", new BaileyBorweinPlouffeFormula()),
                Arguments.of("Gauss-Legendre Algorithm", new GaussLegendreAlgorithm()),
                Arguments.of("Spigot Algorithm", new SpigotAlgorithm())
        );
    }
}