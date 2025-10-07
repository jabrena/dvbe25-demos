package info.jab.pi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
* Parameterized test class for Pi calculation algorithms.
*/
@DisplayName("Pi Calculator Tests")
class PiCalculatorTest {

    private static final String EXPECTED_PI_HIGH_PRECISION = "3.14159265358979323846";
    private static final int PRECISION = 20;

    @ParameterizedTest
    @MethodSource("piCalculatorProvider")
    @DisplayName("Should calculate Pi with high precision using different algorithms")
    void shouldCalculatePiWithHighPrecision(HighPrecisionPiCalculator calculator) {
        // Given
        BigDecimal expectedPi = new BigDecimal(EXPECTED_PI_HIGH_PRECISION)
                .setScale(PRECISION, RoundingMode.HALF_UP);

        // When
        BigDecimal calculatedPi = calculator.calculatePiHighPrecision(PRECISION);

        // Then
        assertThat(calculatedPi)
                .as("Pi calculation should match expected value for algorithm: %s", 
                    calculator.getClass().getSimpleName())
                .isEqualTo(expectedPi);
    }

    static Stream<Arguments> piCalculatorProvider() {
        return Stream.of(
                Arguments.of(new MachinLikePiCalculator()),
                Arguments.of(new ChudnovskyPiCalculator())
        );
    }
}