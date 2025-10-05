package info.jab.pi;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Pi Calculator")
class PiCalculatorTest {

    // Test Constants
    private static final double EXPECTED_PI = Math.PI;
    private static final double STANDARD_PRECISION_DELTA = 0.0001;
    private static final String EXPECTED_PI_HIGH_PRECISION = "3.14159265358979323846264338327950288419716939937510";
    private static final int HIGH_PRECISION_DIGITS = 50;
    private static final int COMPARISON_DIGITS = 48; // Allow for rounding in last digits
    private static final long PERFORMANCE_THRESHOLD_MS = 10000L;

    // Test Data Providers
    static Stream<Arguments> piCalculatorImplementations() {
        return Stream.of(
            Arguments.of("Machin-like Formula", new MachinLikeFormulaPiCalculator()),
            Arguments.of("Chudnovsky Algorithm (BBP)", new ChudnovskyPiCalculator())
        );
    }

    static Stream<Arguments> highPrecisionPiCalculatorImplementations() {
        return Stream.of(
            Arguments.of("Machin-like Formula", new MachinLikeFormulaPiCalculator()),
            Arguments.of("Chudnovsky Algorithm (BBP)", new ChudnovskyPiCalculator())
        );
    }

    @ParameterizedTest(name = "Should calculate Pi with standard precision using {0}")
    @MethodSource("piCalculatorImplementations")
    @DisplayName("Calculate Pi with standard double precision")
    void shouldCalculatePiWithStandardPrecision(String algorithmName, PiCalculator calculator) {
        // Given
        // A Pi calculator implementation

        // When
        double actualPi = calculator.calculatePi();

        // Then
        assertThat(actualPi)
            .as("Pi calculation using %s should be accurate within standard delta", algorithmName)
            .isCloseTo(EXPECTED_PI, offset(STANDARD_PRECISION_DELTA));
    }

    @ParameterizedTest(name = "Should calculate Pi with high precision using {0}")
    @MethodSource("highPrecisionPiCalculatorImplementations") 
    @DisplayName("Calculate Pi with high precision using BigDecimal")
    void shouldCalculatePiWithHighPrecision(String algorithmName, PiCalculator calculator) {
        // Given
        assumeThatCalculatorSupportsHighPrecision(calculator);
        HighPrecisionPiCalculator highPrecisionCalculator = (HighPrecisionPiCalculator) calculator;
        String expectedPrefix = EXPECTED_PI_HIGH_PRECISION.substring(0, COMPARISON_DIGITS);

        // When
        String actualPiHighPrecision = highPrecisionCalculator.calculatePiHighPrecision(HIGH_PRECISION_DIGITS);

        // Then
        String actualPrefix = extractPrefixForComparison(actualPiHighPrecision, COMPARISON_DIGITS);
        
        assertThat(actualPrefix)
            .as("High precision Pi calculation using %s should match expected precision (first %d digits)", 
                algorithmName, COMPARISON_DIGITS)
            .isEqualTo(expectedPrefix);
            
        assertThat(actualPiHighPrecision)
            .as("High precision result should have at least %d digits", HIGH_PRECISION_DIGITS)
            .hasSizeGreaterThanOrEqualTo(HIGH_PRECISION_DIGITS + 2); // Including "3." prefix
    }

    @ParameterizedTest(name = "Should calculate Pi efficiently using {0}")
    @MethodSource("piCalculatorImplementations")
    @DisplayName("Calculate Pi within performance threshold")
    void shouldCalculatePiWithinPerformanceThreshold(String algorithmName, PiCalculator calculator) {
        // Given
        // A Pi calculator implementation

        // When
        long startTime = System.nanoTime();
        double actualPi = calculator.calculatePi();
        long endTime = System.nanoTime();

        // Then
        long durationMs = (endTime - startTime) / 1_000_000;
        
        assertThat(durationMs)
            .as("Pi calculation using %s should complete within reasonable time", algorithmName)
            .isLessThan(PERFORMANCE_THRESHOLD_MS);
            
        assertThat(actualPi)
            .as("Pi calculation result should remain accurate during performance measurement")
            .isCloseTo(EXPECTED_PI, offset(STANDARD_PRECISION_DELTA));
    }

    @Test
    @DisplayName("Verify test data providers return expected implementations")
    void shouldProvideExpectedImplementations() {
        // Given
        // Test data providers for Pi calculator implementations

        // When
        long standardImplementationsCount = piCalculatorImplementations().count();
        long highPrecisionImplementationsCount = highPrecisionPiCalculatorImplementations().count();

        // Then
        assertThat(standardImplementationsCount)
            .as("Should provide both Pi calculator implementations")
            .isEqualTo(2);
            
        assertThat(highPrecisionImplementationsCount)
            .as("Should provide both high precision Pi calculator implementations")
            .isEqualTo(2);
    }

    // Helper Methods
    private void assumeThatCalculatorSupportsHighPrecision(PiCalculator calculator) {
        assertThat(calculator)
            .as("Calculator should implement HighPrecisionPiCalculator interface")
            .isInstanceOf(HighPrecisionPiCalculator.class);
    }

    private String extractPrefixForComparison(String piValue, int digits) {
        return piValue.length() >= digits ? piValue.substring(0, digits) : piValue;
    }
}