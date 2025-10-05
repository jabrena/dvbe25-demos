package info.jab.pi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

/**
 * Comprehensive test suite for high-precision Pi calculation algorithms.
 * Tests verify accuracy and precision of different mathematical approaches.
 */
@DisplayName("High Precision Pi Calculator Test Suite")
class HighPrecisionPiCalculatorTest {

    private static final String EXPECTED_PI_HIGH_PRECISION = "3.14159265358979323846";
    private static final BigDecimal TOLERANCE_20_DECIMAL_PLACES = new BigDecimal("0.00000000000000000001");

    /**
     * Provides different Pi calculator implementations for parameterized tests.
     * Includes both Machin-like Formula and Chudnovsky Algorithm implementations.
     */
    static Stream<HighPrecisionPiCalculator> piCalculatorProvider() {
        return Stream.of(
            new MachinFormulaPiCalculator(),
            new ChudnovskyAlgorithmPiCalculator()
        );
    }

    @ParameterizedTest
    @MethodSource("piCalculatorProvider")
    @DisplayName("Should calculate Pi with ultra-high precision (20 decimal places)")
    void should_calculatePiWithUltraHighPrecision_when_requestingTwentyDecimalPlaces(HighPrecisionPiCalculator calculator) {
        // Given
        int requestedPrecision = 20;
        BigDecimal expectedPi = new BigDecimal(EXPECTED_PI_HIGH_PRECISION);

        // When
        BigDecimal actualResult = calculator.calculatePiHighPrecision(requestedPrecision);

        // Then
        BigDecimal difference = actualResult.subtract(expectedPi).abs();
        assertThat(difference)
            .describedAs("Pi calculation should be accurate to 20 decimal places")
            .isLessThanOrEqualTo(TOLERANCE_20_DECIMAL_PLACES);
        
        assertThat(actualResult)
            .describedAs("Calculated Pi value should be close to mathematical constant")
            .usingComparator(BigDecimal::compareTo)
            .isEqualByComparingTo(expectedPi);
    }

    @ParameterizedTest
    @MethodSource("piCalculatorProvider")
    @DisplayName("Should calculate Pi accurately with medium precision (10 decimal places)")
    void should_calculatePiWithMediumPrecision_when_requestingTenDecimalPlaces(HighPrecisionPiCalculator calculator) {
        // Given
        int requestedPrecision = 10;
        BigDecimal expectedPi = new BigDecimal("3.1415926536");

        // When
        BigDecimal actualResult = calculator.calculatePiHighPrecision(requestedPrecision);
        BigDecimal roundedResult = actualResult.setScale(requestedPrecision, RoundingMode.HALF_UP);

        // Then
        assertThat(roundedResult)
            .describedAs("Pi calculation with 10 decimal places should match expected value")
            .isEqualTo(expectedPi);
        
        assertThat(roundedResult.scale())
            .describedAs("Result should have exactly 10 decimal places")
            .isEqualTo(requestedPrecision);
    }

    @ParameterizedTest
    @MethodSource("piCalculatorProvider")
    @DisplayName("Should calculate Pi accurately with basic precision (5 decimal places)")
    void should_calculatePiWithBasicPrecision_when_requestingFiveDecimalPlaces(HighPrecisionPiCalculator calculator) {
        // Given
        int requestedPrecision = 5;
        BigDecimal expectedPi = new BigDecimal("3.14159");

        // When
        BigDecimal actualResult = calculator.calculatePiHighPrecision(requestedPrecision);
        BigDecimal roundedResult = actualResult.setScale(requestedPrecision, RoundingMode.HALF_UP);

        // Then
        assertThat(roundedResult)
            .describedAs("Pi calculation with 5 decimal places should match expected value")
            .isEqualTo(expectedPi);
        
        assertThat(roundedResult.scale())
            .describedAs("Result should have exactly 5 decimal places")
            .isEqualTo(requestedPrecision);
        
        assertThat(roundedResult)
            .describedAs("Basic precision Pi should start with 3.14159")
            .asString()
            .startsWith("3.14159");
    }
}