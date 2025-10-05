package info.jab.pi;

/**
 * Interface for Pi calculators that support high precision calculations
 */
public interface HighPrecisionPiCalculator extends PiCalculator {
    /**
     * Calculate Pi with high precision
     * @param precision number of decimal places
     * @return Pi value as string with specified precision
     */
    String calculatePiHighPrecision(int precision);
}