package info.jab.pi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("High Precision Pi Calculator Tests")
class HighPrecisionPiCalculatorTest {

    private static final String EXPECTED_PI_HIGH_PRECISION = "3.14159265358979323846";

    static Stream<HighPrecisionPiCalculator> piCalculatorProvider() {
        return Stream.of(
                new MachinLikePiCalculator(),
                new ChudnovskyPiCalculator()
        );
    }

    @ParameterizedTest
    @MethodSource("piCalculatorProvider")
    @DisplayName("Should calculate Pi with high precision using different algorithms")
    void shouldCalculatePiWithHighPrecision_whenUsingDifferentAlgorithms(HighPrecisionPiCalculator calculator) {
        // Given
        int precision = 20;
        BigDecimal expectedPi = new BigDecimal(EXPECTED_PI_HIGH_PRECISION)
                .setScale(precision, RoundingMode.HALF_UP);

        // When
        BigDecimal calculatedPi = calculator.calculatePiHighPrecision(precision);

        // Then
        assertThat(calculatedPi)
                .as("Pi calculation should match expected value for algorithm: %s", 
                    calculator.getClass().getSimpleName())
                .isEqualTo(expectedPi);
    }
}