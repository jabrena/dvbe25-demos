package info.jab.pi;

/**
 * Interface for Pi calculation algorithms that support high precision results.
 */
public interface HighPrecisionPiCalculator {
    /**
     * Calculate the value of Pi with high precision.
     * 
     * @param precision the number of decimal places to calculate
     * @return the calculated value of Pi as a string with the specified precision
     */
    String calculatePiHighPrecision(int precision);
}