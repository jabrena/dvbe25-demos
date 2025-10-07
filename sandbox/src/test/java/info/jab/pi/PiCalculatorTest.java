package info.jab.pi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
* Test class for Pi calculation algorithms using TDD approach.
*/
class PiCalculatorTest {

    private static final String EXPECTED_PI_HIGH_PRECISION = "3.14159265358979323846";

    @ParameterizedTest
    @MethodSource("piCalculatorProvider")
    @DisplayName("Given a Pi calculator, when calculating Pi with high precision, then it should return the expected value")
    void givenPiCalculator_whenCalculatingPiWithHighPrecision_thenShouldReturnExpectedValue(HighPrecisionPiCalculator calculator) {
        // Given
        int precision = 20;
        BigDecimal expectedPi = new BigDecimal(EXPECTED_PI_HIGH_PRECISION)
                .setScale(precision, RoundingMode.HALF_UP);

        // When
        BigDecimal calculatedPi = calculator.calculatePiHighPrecision(precision);

        // Then
        assertThat(calculatedPi)
                .as("Pi calculation should match expected value for algorithm: %s", calculator.getClass().getSimpleName())
                .isEqualTo(expectedPi);
    }

    static Stream<HighPrecisionPiCalculator> piCalculatorProvider() {
        return Stream.of(
                new MachinLikePiCalculator(),
                new ChudnovskyPiCalculator()
        );
    }
}