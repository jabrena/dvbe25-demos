package info.jab.pi;

import java.math.BigDecimal;

/**
 * Interface for Pi calculation algorithms that support high precision calculations.
 */
public interface HighPrecisionPiCalculator {

    /**
     * Calculates Pi with high precision using the implemented algorithm.
     *
     * @param precision Number of decimal places to calculate
     * @return Pi value as a BigDecimal with specified precision
     */
    BigDecimal calculatePiHighPrecision(int precision);
}