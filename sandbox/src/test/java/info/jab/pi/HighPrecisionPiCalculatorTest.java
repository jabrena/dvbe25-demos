package info.jab.pi;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("High Precision Pi Calculator Tests")
class HighPrecisionPiCalculatorTest {

    private static final String EXPECTED_PI_HIGH_PRECISION = "3.14159265358979323846";

    static Stream<HighPrecisionPiCalculator> piCalculatorProvider() {
        return Stream.of(
            new MachinLikeFormulaPiCalculator(),
            new ChudnovskyAlgorithmPiCalculator()
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

    @ParameterizedTest
    @MethodSource("piCalculatorProvider")
    @DisplayName("Should calculate Pi with different precisions")
    void testCalculatePiWithDifferentPrecisions(HighPrecisionPiCalculator calculator) {
        // Test with different precision levels
        int[] precisions = {5, 10, 15, 20};
        
        for (int precision : precisions) {
            BigDecimal calculatedPi = calculator.calculatePiHighPrecision(precision);
            
            // Convert expected value to BigDecimal with same scale for comparison
            BigDecimal expectedPi = new BigDecimal(EXPECTED_PI_HIGH_PRECISION)
                .setScale(precision, RoundingMode.HALF_UP);
            
            assertEquals(expectedPi, calculatedPi,
                String.format("Pi calculation with precision %d should match expected value for algorithm: %s", 
                    precision, calculator.getClass().getSimpleName()));
        }
    }
}