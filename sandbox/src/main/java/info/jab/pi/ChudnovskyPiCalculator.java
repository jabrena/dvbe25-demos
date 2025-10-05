package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Implements Pi calculation using the Chudnovsky algorithm.
 * This is one of the fastest known algorithms for calculating Pi.
 * 
 * The formula is:
 * 1/π = 12 * Σ(k=0 to ∞) [(-1)^k * (6k)! * (545140134k + 13591409)] / [(3k)! * (k!)^3 * 640320^(3k+3/2)]
 */
public class ChudnovskyPiCalculator implements HighPrecisionPiCalculator {

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        // Add extra precision for intermediate calculations
        int workingPrecision = precision + 20;
        MathContext mc = new MathContext(workingPrecision, RoundingMode.HALF_UP);
        
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal c = new BigDecimal("426880").multiply(sqrt(new BigDecimal("10005"), mc), mc);
        
        // Calculate series terms until convergence
        for (int k = 0; k < 100; k++) {
            BigDecimal numerator = factorial(6 * k)
                    .multiply(new BigDecimal("545140134").multiply(BigDecimal.valueOf(k), mc)
                    .add(new BigDecimal("13591409"), mc), mc);
            
            BigDecimal denominator = factorial(3 * k)
                    .multiply(factorial(k).pow(3), mc)
                    .multiply(new BigDecimal("640320").pow(3 * k), mc);
            
            BigDecimal term = numerator.divide(denominator, mc);
            
            if (k % 2 == 1) {
                term = term.negate();
            }
            
            sum = sum.add(term, mc);
            
            // Check for convergence
            if (term.abs().compareTo(BigDecimal.ONE.divide(BigDecimal.TEN.pow(workingPrecision), mc)) < 0) {
                break;
            }
        }
        
        BigDecimal pi = c.divide(sum, mc);
        
        // Round to the requested precision
        return pi.setScale(precision, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate factorial using BigDecimal
     */
    private BigDecimal factorial(int n) {
        if (n <= 1) return BigDecimal.ONE;
        BigDecimal result = BigDecimal.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigDecimal.valueOf(i));
        }
        return result;
    }
    
    /**
     * Calculate square root using Newton's method
     */
    private BigDecimal sqrt(BigDecimal value, MathContext mc) {
        if (value.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal x = value;
        BigDecimal lastX;
        
        do {
            lastX = x;
            x = x.add(value.divide(x, mc), mc).divide(BigDecimal.valueOf(2), mc);
        } while (x.subtract(lastX, mc).abs().compareTo(BigDecimal.ONE.divide(BigDecimal.TEN.pow(mc.getPrecision()), mc)) > 0);
        
        return x;
    }
}