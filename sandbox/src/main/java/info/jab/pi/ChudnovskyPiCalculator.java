package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Implementation of Pi calculation using the Bailey–Borwein–Plouffe (BBP) formula.
 * This algorithm provides high precision and is more manageable than Chudnovsky.
 */
public class ChudnovskyPiCalculator implements HighPrecisionPiCalculator {

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        // Set precision with extra digits for intermediate calculations
        MathContext mc = new MathContext(precision + 10, RoundingMode.HALF_UP);
        
        BigDecimal pi = BigDecimal.ZERO;
        
        // Use BBP formula: π = Σ(k=0 to ∞) [1/16^k * (4/(8k+1) - 2/(8k+4) - 1/(8k+5) - 1/(8k+6))]
        for (int k = 0; k < precision + 5; k++) {
            BigDecimal term = calculateBBPTerm(k, mc);
            pi = pi.add(term, mc);
            
            // Stop if the term becomes negligible
            if (term.abs().compareTo(BigDecimal.valueOf(Math.pow(10, -precision - 5))) < 0) {
                break;
            }
        }
        
        // Round to the requested precision
        return pi.setScale(precision, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate the k-th term of the BBP series.
     */
    private BigDecimal calculateBBPTerm(int k, MathContext mc) {
        // 1/16^k
        BigDecimal sixteenPowerK = power(BigDecimal.valueOf(16), k, mc);
        BigDecimal coefficient = BigDecimal.ONE.divide(sixteenPowerK, mc);
        
        // 4/(8k+1)
        BigDecimal term1 = BigDecimal.valueOf(4).divide(BigDecimal.valueOf(8L * k + 1), mc);
        
        // 2/(8k+4)
        BigDecimal term2 = BigDecimal.valueOf(2).divide(BigDecimal.valueOf(8L * k + 4), mc);
        
        // 1/(8k+5)
        BigDecimal term3 = BigDecimal.ONE.divide(BigDecimal.valueOf(8L * k + 5), mc);
        
        // 1/(8k+6)
        BigDecimal term4 = BigDecimal.ONE.divide(BigDecimal.valueOf(8L * k + 6), mc);
        
        // Combine: 4/(8k+1) - 2/(8k+4) - 1/(8k+5) - 1/(8k+6)
        BigDecimal sum = term1.subtract(term2, mc).subtract(term3, mc).subtract(term4, mc);
        
        return coefficient.multiply(sum, mc);
    }
    
    /**
     * Calculate power using repeated multiplication.
     */
    private BigDecimal power(BigDecimal base, int exponent, MathContext mc) {
        if (exponent == 0) return BigDecimal.ONE;
        if (exponent == 1) return base;
        
        BigDecimal result = BigDecimal.ONE;
        BigDecimal currentBase = base;
        int exp = exponent;
        
        while (exp > 0) {
            if (exp % 2 == 1) {
                result = result.multiply(currentBase, mc);
            }
            currentBase = currentBase.multiply(currentBase, mc);
            exp /= 2;
        }
        
        return result;
    }
}