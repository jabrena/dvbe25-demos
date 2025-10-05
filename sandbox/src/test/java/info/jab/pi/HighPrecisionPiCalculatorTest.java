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
    private static final int TEST_PRECISION = 20;

    /**
     * Provides instances of different Pi calculation implementations for parameterized testing.
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
    @DisplayName("Should calculate Pi with 20 decimal places precision matching expected mathematical value")
    void should_calculatePiWithHighPrecision_when_requestedPrecisionIs20DecimalPlaces(HighPrecisionPiCalculator calculator) {
        // Given
        int requestedPrecision = TEST_PRECISION;
        BigDecimal expectedPi = new BigDecimal(EXPECTED_PI_HIGH_PRECISION)
                .setScale(requestedPrecision, RoundingMode.HALF_UP);

        // When
        BigDecimal actualCalculatedPi = calculator.calculatePiHighPrecision(requestedPrecision);

        // Then
        assertThat(actualCalculatedPi)
                .as("Pi calculation for %s should match expected mathematical value with %d decimal places", 
                    calculator.getClass().getSimpleName(), requestedPrecision)
                .isEqualByComparingTo(expectedPi)
                .satisfies(result -> {
                    assertThat(result.scale())
                            .as("Result should have exactly %d decimal places", requestedPrecision)
                            .isEqualTo(requestedPrecision);
                });
    }
}