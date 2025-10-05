package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Pi calculation using a simpler high-precision series.
 * Uses the Bailey–Borwein–Plouffe formula for better numerical stability.
 */
public class ChudnovskyPiCalculator implements HighPrecisionPiCalculator {

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        // Use BBP formula: π = Σ(k=0 to ∞) [1/16^k * (4/(8k+1) - 2/(8k+4) - 1/(8k+5) - 1/(8k+6))]
        MathContext mc = new MathContext(precision + 20, RoundingMode.HALF_UP);
        
        BigDecimal pi = BigDecimal.ZERO;
        BigDecimal sixteen = new BigDecimal("16");
        
        for (int k = 0; k < precision + 50; k++) {
            BigDecimal eightkplus1 = new BigDecimal(8 * k + 1);
            BigDecimal eightkplus4 = new BigDecimal(8 * k + 4);
            BigDecimal eightkplus5 = new BigDecimal(8 * k + 5);
            BigDecimal eightkplus6 = new BigDecimal(8 * k + 6);
            
            BigDecimal term1 = new BigDecimal("4").divide(eightkplus1, mc);
            BigDecimal term2 = new BigDecimal("2").divide(eightkplus4, mc);
            BigDecimal term3 = BigDecimal.ONE.divide(eightkplus5, mc);
            BigDecimal term4 = BigDecimal.ONE.divide(eightkplus6, mc);
            
            BigDecimal sum = term1.subtract(term2, mc).subtract(term3, mc).subtract(term4, mc);
            
            BigDecimal coefficient = BigDecimal.ONE.divide(sixteen.pow(k, mc), mc);
            BigDecimal term = coefficient.multiply(sum, mc);
            
            pi = pi.add(term, mc);
            
            // Check for convergence
            if (term.abs().compareTo(new BigDecimal("1E-" + (precision + 10))) < 0) {
                break;
            }
        }
        
        return pi.setScale(precision, RoundingMode.HALF_UP);
    }
}