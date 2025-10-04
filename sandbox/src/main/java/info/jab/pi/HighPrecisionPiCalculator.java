package info.jab.pi;

import java.math.BigDecimal;

/**
 * Interface for high precision Pi calculation implementations.
 * Extends PiCalculator to provide high precision calculations using BigDecimal.
 */
public interface HighPrecisionPiCalculator extends PiCalculator {
    
    /**
     * Calculates the value of Pi with high precision using BigDecimal.
     * 
     * @param precision The number of decimal places for precision
     * @return The calculated value of Pi as a BigDecimal with specified precision
     */
    BigDecimal calculatePiHighPrecision(int precision);
}