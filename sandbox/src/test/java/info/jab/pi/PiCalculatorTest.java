package info.jab.pi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Parameterized test class for testing different Pi calculation implementations.
 * Tests verify high precision Pi calculations using multiple algorithms.
 */
@DisplayName("Pi Calculator Tests")
class PiCalculatorTest {

    private static final String EXPECTED_PI_HIGH_PRECISION = "3.14159265358979323846";

    /**
     * Provides different Pi calculator implementations for parameterized tests.
     *
     * @return Stream of HighPrecisionPiCalculator implementations
     */
    static Stream<HighPrecisionPiCalculator> piCalculatorProvider() {
        return Stream.of(
                new MachinLikePiCalculator(),
                new ChudnovskyPiCalculator()
        );
    }

    @ParameterizedTest
    @MethodSource("piCalculatorProvider")
    @DisplayName("Should calculate Pi with high precision matching expected value")
    void should_calculatePiWithHighPrecision_when_using20DecimalPlaces(HighPrecisionPiCalculator calculator) {
        // Given
        int precision = 20;
        BigDecimal expectedPi = new BigDecimal(EXPECTED_PI_HIGH_PRECISION)
                .setScale(precision, RoundingMode.HALF_UP);

        // When
        BigDecimal calculatedPi = calculator.calculatePiHighPrecision(precision);

        // Then
        assertThat(calculatedPi)
                .as("Pi calculation for algorithm %s should match expected value with %d decimal places", 
                    calculator.getClass().getSimpleName(), precision)
                .isEqualTo(expectedPi);
        assertThat(calculatedPi.scale())
                .as("Pi calculation should have the correct scale")
                .isEqualTo(precision);
    }
}