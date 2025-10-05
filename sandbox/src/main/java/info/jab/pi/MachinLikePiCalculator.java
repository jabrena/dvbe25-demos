package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Implementation of Pi calculation using Machin-like formulas.
 * Uses Machin's formula: π/4 = 4*arctan(1/5) - arctan(1/239)
 */
public class MachinLikePiCalculator implements HighPrecisionPiCalculator {

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        // Set precision with extra digits for intermediate calculations
        MathContext mc = new MathContext(precision + 10, RoundingMode.HALF_UP);
        
        // Calculate π/4 = 4*arctan(1/5) - arctan(1/239)
        BigDecimal arctan1_5 = arctan(BigDecimal.ONE.divide(BigDecimal.valueOf(5), mc), mc);
        BigDecimal arctan1_239 = arctan(BigDecimal.ONE.divide(BigDecimal.valueOf(239), mc), mc);
        
        BigDecimal piOver4 = BigDecimal.valueOf(4).multiply(arctan1_5, mc).subtract(arctan1_239, mc);
        BigDecimal pi = piOver4.multiply(BigDecimal.valueOf(4), mc);
        
        // Round to the requested precision
        return pi.setScale(precision, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate arctan(x) using Taylor series expansion:
     * arctan(x) = x - x³/3 + x⁵/5 - x⁷/7 + ...
     */
    private BigDecimal arctan(BigDecimal x, MathContext mc) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal xPower = x;
        BigDecimal xSquared = x.multiply(x, mc);
        
        int n = 1;
        BigDecimal term;
        
        do {
            // Calculate current term: x^n / n
            term = xPower.divide(BigDecimal.valueOf(n), mc);
            
            // Add or subtract based on alternating series
            if ((n - 1) / 2 % 2 == 0) {
                result = result.add(term, mc);
            } else {
                result = result.subtract(term, mc);
            }
            
            // Prepare for next iteration
            xPower = xPower.multiply(xSquared, mc);
            n += 2;
            
        } while (term.abs().compareTo(BigDecimal.valueOf(Math.pow(10, -mc.getPrecision()))) > 0 && n < 10000);
        
        return result;
    }
}