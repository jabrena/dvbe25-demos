package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Pi calculation using Bailey–Borwein–Plouffe formula:
 * π = Σ(k=0 to ∞) [1/16^k * (4/(8k+1) - 2/(8k+4) - 1/(8k+5) - 1/(8k+6))]
 * This provides high precision and is computationally stable.
 */
public class ChudnovskyPiCalculator implements HighPrecisionPiCalculator {

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        int workingPrecision = precision + 30;
        MathContext mc = new MathContext(workingPrecision, RoundingMode.HALF_UP);
        
        BigDecimal pi = BigDecimal.ZERO;
        BigDecimal sixteen = BigDecimal.valueOf(16);
        BigDecimal powerOf16 = BigDecimal.ONE;
        
        BigDecimal threshold = BigDecimal.ONE.divide(BigDecimal.TEN.pow(workingPrecision - 2), mc);
        
        for (int k = 0; k < 10000; k++) {
            // Calculate 1/16^k
            BigDecimal coefficient = BigDecimal.ONE.divide(powerOf16, mc);
            
            // Calculate the bracketed expression
            BigDecimal term1 = BigDecimal.valueOf(4).divide(BigDecimal.valueOf(8L * k + 1), mc);
            BigDecimal term2 = BigDecimal.valueOf(2).divide(BigDecimal.valueOf(8L * k + 4), mc);
            BigDecimal term3 = BigDecimal.ONE.divide(BigDecimal.valueOf(8L * k + 5), mc);
            BigDecimal term4 = BigDecimal.ONE.divide(BigDecimal.valueOf(8L * k + 6), mc);
            
            BigDecimal bracketedSum = term1.subtract(term2, mc).subtract(term3, mc).subtract(term4, mc);
            
            BigDecimal termValue = coefficient.multiply(bracketedSum, mc);
            pi = pi.add(termValue, mc);
            
            // Check for convergence
            if (termValue.abs().compareTo(threshold) < 0) {
                break;
            }
            
            powerOf16 = powerOf16.multiply(sixteen, mc);
        }
        
        return pi.setScale(precision, RoundingMode.HALF_UP);
    }
}