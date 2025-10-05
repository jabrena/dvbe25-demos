package info.jab.pi;

/**
 * Marker interface for Pi calculators that support high precision calculation
 */
public interface HighPrecisionPiCalculator extends PiCalculator {
    
    /**
     * Calculate Pi with high precision using BigDecimal
     * @param precision number of decimal places
     * @return Pi value as string with high precision
     */
    String calculatePiHighPrecision(int precision);
}