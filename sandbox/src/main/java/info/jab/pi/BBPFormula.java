package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Implementation of the Bailey-Borwein-Plouffe (BBP) Formula for calculating Pi.
 * 
 * The BBP formula allows computation of hexadecimal digits of π without 
 * calculating the preceding digits. It was discovered in 1995 and is 
 * particularly useful for computing specific digits of π.
 * 
 * Formula: π = Σ(k=0 to ∞) [1/16^k * (4/(8k+1) - 2/(8k+4) - 1/(8k+5) - 1/(8k+6))]
 */
public class BBPFormula {
    
    private static final int DEFAULT_ITERATIONS = 100;
    
    /**
     * Calculate Pi using default number of iterations.
     * @return Pi as a double value
     */
    public double calculatePi() {
        return calculatePi(DEFAULT_ITERATIONS);
    }
    
    /**
     * Calculate Pi with specified number of iterations.
     * @param iterations Number of iterations to perform
     * @return Pi as a double value
     */
    public double calculatePi(int iterations) {
        double pi = 0.0;
        
        for (int k = 0; k < iterations; k++) {
            double term = (1.0 / Math.pow(16, k)) * 
                         (4.0 / (8 * k + 1) - 
                          2.0 / (8 * k + 4) - 
                          1.0 / (8 * k + 5) - 
                          1.0 / (8 * k + 6));
            pi += term;
        }
        
        return pi;
    }
    
    /**
     * Calculate Pi using BBP formula with high precision.
     * 
     * @param precision The precision context
     * @param iterations Number of iterations
     * @return Pi as a BigDecimal
     */
    public BigDecimal calculatePi(MathContext precision, int iterations) {
        BigDecimal pi = BigDecimal.ZERO;
        BigDecimal sixteen = BigDecimal.valueOf(16);
        
        for (int k = 0; k < iterations; k++) {
            BigDecimal sixteenPowerK = sixteen.pow(k, precision);
            BigDecimal oneDivSixteenPowerK = BigDecimal.ONE.divide(sixteenPowerK, precision);
            
            BigDecimal term1 = BigDecimal.valueOf(4).divide(BigDecimal.valueOf(8L * k + 1), precision);
            BigDecimal term2 = BigDecimal.valueOf(2).divide(BigDecimal.valueOf(8L * k + 4), precision);
            BigDecimal term3 = BigDecimal.ONE.divide(BigDecimal.valueOf(8L * k + 5), precision);
            BigDecimal term4 = BigDecimal.ONE.divide(BigDecimal.valueOf(8L * k + 6), precision);
            
            BigDecimal sumTerms = term1.subtract(term2, precision)
                                      .subtract(term3, precision)
                                      .subtract(term4, precision);
            
            BigDecimal termK = oneDivSixteenPowerK.multiply(sumTerms, precision);
            pi = pi.add(termK, precision);
        }
        
        return pi;
    }
    
    /**
     * Calculate a specific hexadecimal digit of π.
     * This is one of the key advantages of the BBP formula.
     * 
     * @param position The position of the digit (0-based, after decimal point)
     * @return The hexadecimal digit at the specified position
     */
    public int calculateHexDigit(int position) {
        double s = 0.0;
        
        // Calculate the fractional part of the BBP series at position
        for (int k = 0; k <= position; k++) {
            double ak = 8 * k + 1;
            double r = modularExp(16, position - k, (int)ak);
            s += r / ak;
        }
        
        for (int k = position + 1; k < position + 100; k++) {
            double ak = 8 * k + 1;
            double term = Math.pow(16, position - k) / ak;
            if (term < 1e-15) break;
            s += term;
        }
        
        s = s - Math.floor(s); // Get fractional part
        return (int)(16 * s);
    }
    
    /**
     * Calculate multiple hexadecimal digits starting from a position.
     * 
     * @param startPosition Starting position (0-based)
     * @param numDigits Number of digits to calculate
     * @return String of hexadecimal digits
     */
    public String calculateHexDigits(int startPosition, int numDigits) {
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < numDigits; i++) {
            int digit = calculateHexDigit(startPosition + i);
            result.append(Integer.toHexString(digit).toUpperCase());
        }
        
        return result.toString();
    }
    
    /**
     * Modular exponentiation: (base^exp) mod modulus
     * Used in the BBP digit extraction algorithm.
     * 
     * @param base The base
     * @param exponent The exponent
     * @param modulus The modulus
     * @return (base^exponent) mod modulus
     */
    public double modularExp(int base, int exponent, int modulus) {
        if (exponent == 0) return 1.0 % modulus;
        if (exponent < 0) return 0.0;
        
        double result = 1.0;
        double baseVal = base % modulus;
        
        while (exponent > 0) {
            if (exponent % 2 == 1) {
                result = (result * baseVal) % modulus;
            }
            exponent = exponent >> 1;
            baseVal = (baseVal * baseVal) % modulus;
        }
        
        return result;
    }
}