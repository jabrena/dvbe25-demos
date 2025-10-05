package info.jab.pi;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Pi Calculation Tests")
class PiCalculationTest {

    private static final String EXPECTED_PI_HIGH_PRECISION = "3.14159265358979323846";
    private static final double EXPECTED_PI_DOUBLE = 3.141592653589793;
    private static final double DELTA = 2e-6;

    static Stream<Arguments> piCalculatorProvider() {
        return Stream.of(
            Arguments.of("MachinLikePiCalculator", new MachinLikePiCalculator()),
            Arguments.of("ChudnovskyPiCalculator", new ChudnovskyPiCalculator())
        );
    }

    @ParameterizedTest(name = "{0} - Basic Pi calculation")
    @MethodSource("piCalculatorProvider")
    @DisplayName("Should calculate Pi with basic precision")
    void shouldCalculateBasicPi(String algorithmName, PiCalculator calculator) {
        // When
        double result = calculator.calculatePi();
        
        // Then
        assertThat(result).isCloseTo(EXPECTED_PI_DOUBLE, within(DELTA));
    }

    @ParameterizedTest(name = "{0} - High precision Pi calculation")
    @MethodSource("piCalculatorProvider")
    @DisplayName("Should calculate Pi with high precision")
    void shouldCalculateHighPrecisionPi(String algorithmName, PiCalculator calculator) {
        // Given
        if (!(calculator instanceof HighPrecisionPiCalculator)) {
            return; // Skip if calculator doesn't support high precision
        }
        
        HighPrecisionPiCalculator highPrecisionCalculator = (HighPrecisionPiCalculator) calculator;
        int precision = 20;
        
        // When
        BigDecimal result = highPrecisionCalculator.calculatePiHighPrecision(precision);
        
        // Then
        BigDecimal expected = new BigDecimal(EXPECTED_PI_HIGH_PRECISION);
        
        // Compare up to the precision we calculated
        BigDecimal roundedResult = result.setScale(precision, RoundingMode.HALF_UP);
        BigDecimal roundedExpected = expected.setScale(precision, RoundingMode.HALF_UP);
        
        assertThat(roundedResult).isEqualTo(roundedExpected);
    }

    @ParameterizedTest(name = "{0} - High precision Pi calculation with 50 digits")
    @MethodSource("piCalculatorProvider")
    @DisplayName("Should calculate Pi with very high precision (50 digits)")
    void shouldCalculateVeryHighPrecisionPi(String algorithmName, PiCalculator calculator) {
        // Given
        if (!(calculator instanceof HighPrecisionPiCalculator)) {
            return; // Skip if calculator doesn't support high precision
        }
        
        HighPrecisionPiCalculator highPrecisionCalculator = (HighPrecisionPiCalculator) calculator;
        int precision = 50;
        
        // When
        BigDecimal result = highPrecisionCalculator.calculatePiHighPrecision(precision);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.toString()).startsWith("3.14159265358979323846");
        
        // Verify that we have at least the expected precision
        String resultStr = result.toString();
        int decimalIndex = resultStr.indexOf('.');
        if (decimalIndex >= 0) {
            int actualPrecision = resultStr.length() - decimalIndex - 1;
            assertThat(actualPrecision).isGreaterThanOrEqualTo(precision);
        }
    }

    @Test
    @DisplayName("Should verify expected Pi constant has correct format")
    void shouldVerifyExpectedPiConstant() {
        BigDecimal expectedPi = new BigDecimal(EXPECTED_PI_HIGH_PRECISION);
        assertThat(expectedPi.toString()).isEqualTo(EXPECTED_PI_HIGH_PRECISION);
        assertThat(expectedPi.doubleValue()).isCloseTo(EXPECTED_PI_DOUBLE, within(DELTA));
    }

    private org.assertj.core.data.Offset<Double> within(double offset) {
        return org.assertj.core.data.Offset.offset(offset);
    }
}