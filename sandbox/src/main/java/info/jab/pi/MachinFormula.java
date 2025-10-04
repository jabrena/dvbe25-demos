package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Implementation of Machin-like Formula for calculating Pi.
 * Uses the formula: π/4 = 4*arctan(1/5) - arctan(1/239)
 * This is one of the classical formulas for computing π with high precision.
 */
public class MachinFormula {
    
    private static final int DEFAULT_PRECISION = 50;
    
    /**
     * Calculate Pi using default precision.
     * @return Pi as a double value
     */
    public double calculatePi() {
        // Use Java's built-in Math functions for double precision
        double arctan1_5 = Math.atan(1.0 / 5.0);
        double arctan1_239 = Math.atan(1.0 / 239.0);
        
        // π/4 = 4*arctan(1/5) - arctan(1/239)
        double piOver4 = 4.0 * arctan1_5 - arctan1_239;
        
        // π = 4 * (π/4)
        return 4.0 * piOver4;
    }
    
    /**
     * Calculate Pi with specified precision using Machin's formula.
     * π/4 = 4*arctan(1/5) - arctan(1/239)
     * 
     * @param precision The precision context to use
     * @return Pi as a BigDecimal
     */
    public BigDecimal calculatePi(MathContext precision) {
        BigDecimal five = BigDecimal.valueOf(5);
        BigDecimal twoThreeNine = BigDecimal.valueOf(239);
        BigDecimal four = BigDecimal.valueOf(4);
        
        // Calculate arctan(1/5)
        BigDecimal arctan1_5 = arctan(BigDecimal.ONE.divide(five, precision), precision);
        
        // Calculate arctan(1/239)
        BigDecimal arctan1_239 = arctan(BigDecimal.ONE.divide(twoThreeNine, precision), precision);
        
        // π/4 = 4*arctan(1/5) - arctan(1/239)
        BigDecimal piOver4 = four.multiply(arctan1_5, precision).subtract(arctan1_239, precision);
        
        // π = 4 * (π/4)
        return four.multiply(piOver4, precision);
    }
    
    /**
     * Calculate arctangent using Taylor series expansion.
     * arctan(x) = x - x³/3 + x⁵/5 - x⁷/7 + ...
     * 
     * @param x The value to calculate arctan of
     * @param precision The precision context
     * @return arctan(x)
     */
    public BigDecimal arctan(BigDecimal x, MathContext precision) {
        BigDecimal result = x;
        BigDecimal xSquared = x.multiply(x, precision);
        BigDecimal xPower = x.multiply(xSquared, precision); // x^3
        
        for (int n = 3; n <= precision.getPrecision() * 2; n += 2) {
            BigDecimal term = xPower.divide(BigDecimal.valueOf(n), precision);
            
            if ((n - 1) / 2 % 2 == 1) {
                result = result.subtract(term, precision);
            } else {
                result = result.add(term, precision);
            }
            
            xPower = xPower.multiply(xSquared, precision);
            
            // Check convergence
            if (term.abs().compareTo(BigDecimal.valueOf(Math.pow(10, -precision.getPrecision()))) < 0) {
                break;
            }
        }
        
        return result;
    }
}