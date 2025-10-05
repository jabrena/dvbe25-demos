package info.jab.pi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

/**
 * Parameterized test class for testing high precision Pi calculation implementations.
 */
@DisplayName("High Precision Pi Calculator Tests")
public class HighPrecisionPiCalculatorTest {

    private static final String EXPECTED_PI_HIGH_PRECISION = "3.14159265358979323846";

    /**
     * Provides Pi calculator implementations for parameterized tests.
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
    @DisplayName("Should calculate Pi with high precision")
    void testCalculatePiHighPrecision(HighPrecisionPiCalculator calculator) {
        // Test with 20 decimal places precision
        int precision = 20;

        BigDecimal calculatedPi = calculator.calculatePiHighPrecision(precision);

        // Convert expected value to BigDecimal with same scale for comparison
        BigDecimal expectedPi = new BigDecimal(EXPECTED_PI_HIGH_PRECISION)
                .setScale(precision, RoundingMode.HALF_UP);

        assertEquals(expectedPi, calculatedPi,
                String.format("Pi calculation should match expected value for algorithm: %s",
                        calculator.getClass().getSimpleName()));
    }
}