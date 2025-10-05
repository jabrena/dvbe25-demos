package info.jab.pi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

public class PiCalculatorTest {

    private static final double EXPECTED_PI = Math.PI;
    private static final double STANDARD_DELTA = 0.0001;
    private static final String EXPECTED_PI_HIGH_PRECISION = "3.14159265358979323846264338327950288419716939937510";

    public interface PiCalculator {
        double calculatePi();
        default BigDecimal calculatePiHighPrecision(int precision) {
            return BigDecimal.valueOf(calculatePi());
        }
        default boolean supportsHighPrecision() {
            return false;
        }
    }

    static Stream<Arguments> piCalculatorProvider() {
        return Stream.of(
            Arguments.of("Machin-like Formula", new MachinLikeFormula()),
            Arguments.of("Chudnovsky Algorithm", new ChudnovskyAlgorithm()),
            Arguments.of("Bailey-Borwein-Plouffe Formula", new BaileyBorweinPlouffeFormula()),
            Arguments.of("Gauss-Legendre Algorithm", new GaussLegendreAlgorithm()),
            Arguments.of("Spigot Algorithm", new SpigotAlgorithm())
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("piCalculatorProvider")
    void testPiCalculation(String algorithmName, PiCalculator calculator) {
        double calculatedPi = calculator.calculatePi();
        assertEquals(EXPECTED_PI, calculatedPi, STANDARD_DELTA, 
            "Pi calculation using " + algorithmName + " should be within standard delta");
    }

    @ParameterizedTest(name = "{0} - High Precision")
    @MethodSource("piCalculatorProvider")
    void testHighPrecisionPiCalculation(String algorithmName, PiCalculator calculator) {
        if (calculator.supportsHighPrecision()) {
            BigDecimal calculatedPi = calculator.calculatePiHighPrecision(50);
            String calculatedPiStr = calculatedPi.toString();
            
            // Check if the calculated Pi starts with the expected high precision value
            assertTrue(calculatedPiStr.startsWith(EXPECTED_PI_HIGH_PRECISION.substring(0, Math.min(20, calculatedPiStr.length()))),
                "High precision Pi calculation using " + algorithmName + " should match expected precision");
        }
    }
}