package info.jab.pi;

/**
 * Interface for Pi calculation algorithms that support high precision calculations.
 */
public interface HighPrecisionPiCalculator extends PiCalculator {
    
    /**
     * Calculates Pi with high precision using the implemented algorithm.
     * 
     * @param precision Number of decimal places to calculate
     * @return Pi value as a String with specified precision
     */
    String calculatePiHighPrecision(int precision);
}