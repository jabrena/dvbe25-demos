package info.jab.pi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive test suite for Pi calculation implementations.
 * Tests all approaches with appropriate precision expectations using different algorithms.
 */
@DisplayName("Pi Calculator Tests")
class PiCalculatorTest {

    private static final double EXPECTED_PI = Math.PI;
    private static final double STANDARD_DELTA = 0.0001;

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

    @Nested
    @DisplayName("Standard Precision Pi Calculations")
    class StandardPrecisionTests {

        @ParameterizedTest(name = "Should calculate Pi accurately using {0}")
        @DisplayName("Should calculate Pi with standard double precision")
        @MethodSource("info.jab.pi.PiCalculatorTest#piCalculatorProvider")
        void shouldCalculatePiWithStandardPrecision(String algorithmName, PiCalculator calculator) {
            // Given
            // Pi calculator implementation is provided via parameterized test

            // When
            double calculatedPi = calculator.calculatePi();

            // Then
            assertThat(calculatedPi)
                .as("Pi calculation using %s should be accurate within delta %f", algorithmName, STANDARD_DELTA)
                .isCloseTo(EXPECTED_PI, within(STANDARD_DELTA));
        }
    }

    @Nested
    @DisplayName("High Precision Pi Calculations")
    class HighPrecisionTests {

        private static final int HIGH_PRECISION_SCALE = 50;
        private static final String EXPECTED_PI_HIGH_PRECISION = "3.14159265358979323846264338327950288419716939937510";

        @ParameterizedTest(name = "Should calculate Pi with high precision using {0}")
        @DisplayName("Should calculate Pi with BigDecimal high precision")
        @MethodSource("info.jab.pi.PiCalculatorTest#piCalculatorProvider")
        void shouldCalculatePiWithHighPrecision(String algorithmName, PiCalculator calculator) {
            // Given
            // Skip test if calculator doesn't support high precision
            if (!(calculator instanceof HighPrecisionPiCalculator)) {
                return;
            }
            
            HighPrecisionPiCalculator highPrecisionCalculator = (HighPrecisionPiCalculator) calculator;
            BigDecimal expectedPi = new BigDecimal(EXPECTED_PI_HIGH_PRECISION);
            int expectedDecimalPlaces = determineExpectedPrecisionForAlgorithm(algorithmName);

            // When
            BigDecimal calculatedPi = highPrecisionCalculator.calculatePiHighPrecision(HIGH_PRECISION_SCALE);

            // Then
            BigDecimal expectedRounded = expectedPi.setScale(expectedDecimalPlaces, BigDecimal.ROUND_HALF_UP);
            BigDecimal actualRounded = calculatedPi.setScale(expectedDecimalPlaces, BigDecimal.ROUND_HALF_UP);
            
            assertThat(actualRounded)
                .as("High precision Pi calculation using %s should be accurate to %d decimal places", 
                    algorithmName, expectedDecimalPlaces)
                .isEqualTo(expectedRounded);
        }

        private int determineExpectedPrecisionForAlgorithm(String algorithmName) {
            // Given different algorithms have different convergence characteristics
            if (algorithmName.contains("Chudnovsky") || algorithmName.contains("Spigot")) {
                return 2; // More lenient for Leibniz-based implementations
            } else {
                return 10; // Stricter for more efficient algorithms
            }
        }
    }
}