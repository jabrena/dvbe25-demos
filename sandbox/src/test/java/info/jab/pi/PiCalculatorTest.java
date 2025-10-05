package info.jab.pi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive test suite for Pi calculation algorithms.
 * Tests different implementations using parameterized testing with Given-When-Then structure.
 */
@DisplayName("Pi Calculator Test Suite")
class PiCalculatorTest {

    private static final String EXPECTED_PI_HIGH_PRECISION = "3.14159265358979323846";

    /**
     * Provides test parameters for different Pi calculator implementations.
     */
    static Stream<Arguments> providePiCalculatorImplementations() {
        return Stream.of(
            Arguments.of(new MachinLikePiCalculator(), "Machin-like Formula"),
            Arguments.of(new ChudnovskyPiCalculator(), "Chudnovsky Algorithm")
        );
    }

    @ParameterizedTest(name = "should calculate Pi with high precision using {1}")
    @MethodSource("providePiCalculatorImplementations")
    @DisplayName("High Precision Pi Calculation Test")
    void should_calculatePiWithHighPrecision_when_usingTenDecimalPlaces(
            HighPrecisionPiCalculator calculator, String algorithmName) {
        
        // Given
        int requestedPrecision = 10;
        BigDecimal expectedPi = new BigDecimal(EXPECTED_PI_HIGH_PRECISION);
        BigDecimal toleranceThreshold = new BigDecimal("0.01");

        // When
        BigDecimal calculatedPi = calculator.calculatePiHighPrecision(requestedPrecision);

        // Then
        BigDecimal absoluteDifference = calculatedPi.subtract(expectedPi).abs();
        
        assertThat(calculatedPi)
            .as("Pi calculated by %s should be a valid BigDecimal", algorithmName)
            .isNotNull();
            
        assertThat(absoluteDifference)
            .as("Pi calculated by %s should be within tolerance of expected value", algorithmName)
            .isLessThan(toleranceThreshold);
            
        assertThat(calculatedPi.scale())
            .as("Pi calculated by %s should have the requested precision", algorithmName)
            .isEqualTo(requestedPrecision);
    }

    @ParameterizedTest(name = "should calculate Pi with basic precision using {1}")
    @MethodSource("providePiCalculatorImplementations")
    @DisplayName("Basic Precision Pi Calculation Test")
    void should_calculatePiWithBasicPrecision_when_usingFiveDecimalPlaces(
            HighPrecisionPiCalculator calculator, String algorithmName) {
        
        // Given
        int requestedPrecision = 5;
        BigDecimal expectedPi = new BigDecimal("3.14159");
        BigDecimal toleranceThreshold = new BigDecimal("0.001");

        // When
        BigDecimal calculatedPi = calculator.calculatePiHighPrecision(requestedPrecision);

        // Then
        BigDecimal absoluteDifference = calculatedPi.subtract(expectedPi).abs();
        
        assertThat(calculatedPi)
            .as("Pi calculated by %s should be a valid BigDecimal", algorithmName)
            .isNotNull();
            
        assertThat(absoluteDifference)
            .as("Pi calculated by %s should be within tolerance of expected value", algorithmName)
            .isLessThan(toleranceThreshold);
            
        assertThat(calculatedPi.scale())
            .as("Pi calculated by %s should have the requested precision", algorithmName)
            .isEqualTo(requestedPrecision);
    }

    @ParameterizedTest(name = "should calculate Pi correctly within expected range using {1}")
    @MethodSource("providePiCalculatorImplementations")
    @DisplayName("Pi Value Range Validation Test")
    void should_calculatePiWithinExpectedRange_when_usingAnyPrecision(
            HighPrecisionPiCalculator calculator, String algorithmName) {
        
        // Given
        int precision = 8;
        BigDecimal piLowerBound = new BigDecimal("3.14");
        BigDecimal piUpperBound = new BigDecimal("3.15");

        // When
        BigDecimal calculatedPi = calculator.calculatePiHighPrecision(precision);

        // Then
        assertThat(calculatedPi)
            .as("Pi calculated by %s should be within the expected range", algorithmName)
            .isBetween(piLowerBound, piUpperBound);
            
        assertThat(calculatedPi.toString())
            .as("Pi calculated by %s should start with '3.14'", algorithmName)
            .startsWith("3.14");
    }
}