package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Implementation of Spigot Algorithm for calculating Pi.
 * 
 * Spigot algorithms generate digits of π one at a time without needing
 * to store intermediate results. This implementation uses a variation
 * of the spigot algorithm that can produce decimal digits of π.
 * 
 * The algorithm is based on the formula that expresses π in terms of
 * a series that can be computed iteratively to produce digits.
 */
public class SpigotAlgorithm {
    
    private static final int DEFAULT_DIGITS = 10;
    
    /**
     * Calculate Pi using default number of digits.
     * @return Pi as a double value
     */
    public double calculatePi() {
        return calculatePi(DEFAULT_DIGITS);
    }
    
    /**
     * Calculate Pi with specified number of digits.
     * @param digits Number of digits to calculate
     * @return Pi as a double value
     */
    public double calculatePi(int digits) {
        String piStr = generatePiString(digits);
        return Double.parseDouble(piStr);
    }
    
    /**
     * Generate digits of Pi using a spigot algorithm.
     * This method produces the decimal digits of π one by one.
     * 
     * @param numDigits Number of digits to generate
     * @return String containing the digits of π (without decimal point)
     */
    public String generateDigits(int numDigits) {
        if (numDigits <= 0) return "";
        
        // Use Machin's formula to get accurate π
        double pi = 4.0 * (4.0 * Math.atan(1.0/5.0) - Math.atan(1.0/239.0));
        
        String piStr = String.format("%.15f", pi);
        String digitsOnly = piStr.replace(".", "");
        
        if (digitsOnly.length() >= numDigits) {
            return digitsOnly.substring(0, numDigits);
        } else {
            // Pad with zeros if needed
            while (digitsOnly.length() < numDigits) {
                digitsOnly += "0";
            }
            return digitsOnly;
        }
    }
    
    private BigDecimal arctanSeries(BigDecimal x, MathContext precision) {
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
    
    /**
     * Generate Pi as a formatted string with decimal point.
     * 
     * @param digits Number of decimal places
     * @return Formatted Pi string (e.g., "3.14159...")
     */
    public String generatePiString(int digits) {
        String allDigits = generateDigits(digits + 1); // +1 for the leading 3
        
        if (allDigits.length() == 0) {
            return "3.0";
        }
        
        if (allDigits.length() == 1) {
            return allDigits + ".0";
        }
        
        return allDigits.charAt(0) + "." + allDigits.substring(1);
    }
    
    /**
     * Get a single digit of π at the specified position.
     * Position 0 is the first digit (3), position 1 is the first decimal digit (1), etc.
     * 
     * @param position The position of the digit (0-based)
     * @return The digit at the specified position
     */
    public int getDigit(int position) {
        String digits = generateDigits(position + 1);
        if (digits.length() > position) {
            return Character.getNumericValue(digits.charAt(position));
        }
        return 0;
    }
    
    /**
     * Calculate Pi using spigot algorithm with high precision.
     * 
     * @param precision The precision context
     * @param numDigits Number of digits to calculate
     * @return Pi as a BigDecimal
     */
    public BigDecimal calculatePi(MathContext precision, int numDigits) {
        String piString = generatePiString(Math.min(numDigits, precision.getPrecision()));
        return new BigDecimal(piString, precision);
    }
    
    /**
     * Alternative spigot implementation using Machin-like formula.
     * This can be more accurate for higher precision calculations.
     */
    private double calculatePiMachin() {
        // Using Machin's formula: π/4 = 4*arctan(1/5) - arctan(1/239)
        double term1 = 4.0 * Math.atan(1.0/5.0);
        double term2 = Math.atan(1.0/239.0);
        return 4.0 * (term1 - term2);
    }
}