package info.jab.pi;

/**
 * Interface for Pi calculation algorithms that support high precision calculations.
 * Extends the base PiCalculator with high-precision capabilities using pure functional methods.
 */
public interface HighPrecisionPiCalculator extends PiCalculator {
    
    /**
     * Calculates Pi with high precision using the implemented algorithm.
     * This should be a pure function with no side effects.
     * 
     * @param precision Number of decimal places to calculate (must be positive)
     * @return Pi value as a String with specified precision
     * @throws IllegalArgumentException if precision is not positive
     */
    String calculatePiHighPrecision(int precision);
    
    /**
     * Default validation for precision parameter.
     * Pure function that validates input constraints.
     * 
     * @param precision the precision to validate
     * @throws IllegalArgumentException if precision is not positive
     */
    default void validatePrecision(final int precision) {
        if (precision <= 0) {
            throw new IllegalArgumentException("Precision must be positive, got: " + precision);
        }
    }
    
    /**
     * Calculate Pi with validated precision parameter.
     * Combines validation with calculation in a functional pipeline.
     * 
     * @param precision Number of decimal places to calculate
     * @return Pi value as a String with specified precision
     * @throws IllegalArgumentException if precision is not positive
     */
    default String calculatePiHighPrecisionSafe(final int precision) {
        validatePrecision(precision);
        return calculatePiHighPrecision(precision);
    }
}