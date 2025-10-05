package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Pi calculation using the Chudnovsky algorithm.
 * This is one of the fastest known algorithms for calculating Pi.
 * 
 * Formula: 1/π = 12 * Σ(k=0 to ∞) [(-1)^k * (6k)! * (13591409 + 545140134*k)] / [(3k)! * k!^3 * 640320^(3k + 3/2)]
 */
public class ChudnovskyPiCalculator implements HighPrecisionPiCalculator {

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        // Set precision higher than required to avoid rounding errors during calculation
        int workingPrecision = precision + 20;
        MathContext mc = new MathContext(workingPrecision, RoundingMode.HALF_UP);
        
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal a = BigDecimal.ONE;  // (6k)! / ((3k)! * k!^3)
        BigDecimal b = new BigDecimal("13591409");  // 13591409 + 545140134*k
        BigDecimal c = BigDecimal.ONE;  // 640320^(3k)
        BigDecimal constant545140134 = new BigDecimal("545140134");
        
        BigDecimal base640320 = new BigDecimal("640320");
        BigDecimal base640320Cubed = base640320.pow(3, mc);
        
        for (int k = 0; k < 100; k++) {  // 100 iterations should be enough for high precision
            BigDecimal term = a.multiply(b, mc).divide(c, mc);
            if (k % 2 == 1) {
                term = term.negate();
            }
            sum = sum.add(term, mc);
            
            // Calculate next iteration values
            if (k < 99) {  // Don't calculate for the last iteration
                // Update a: multiply by (6k+1)(6k+2)(6k+3)(6k+4)(6k+5)(6k+6) / ((3k+1)(3k+2)(3k+3) * (k+1)^3)
                BigDecimal numerator = BigDecimal.ONE;
                for (int i = 1; i <= 6; i++) {
                    numerator = numerator.multiply(new BigDecimal(6 * k + i), mc);
                }
                
                BigDecimal denominator = BigDecimal.ONE;
                for (int i = 1; i <= 3; i++) {
                    denominator = denominator.multiply(new BigDecimal(3 * k + i), mc);
                }
                BigDecimal kPlusOne = new BigDecimal(k + 1);
                denominator = denominator.multiply(kPlusOne.pow(3, mc), mc);
                
                a = a.multiply(numerator, mc).divide(denominator, mc);
                
                // Update b: add 545140134
                b = b.add(constant545140134, mc);
                
                // Update c: multiply by 640320^3
                c = c.multiply(base640320Cubed, mc);
            }
            
            // Check for convergence
            if (k > 5 && term.abs().compareTo(new BigDecimal("1E-" + (workingPrecision + 5))) < 0) {
                break;
            }
        }
        
        // Calculate 1/π = 12 * sum / sqrt(640320^3)
        BigDecimal twelve = new BigDecimal("12");
        BigDecimal sqrt640320Cubed = sqrt(base640320Cubed, mc);
        
        BigDecimal oneOverPi = twelve.multiply(sum, mc).divide(sqrt640320Cubed, mc);
        
        // Calculate π = 1 / (1/π)
        BigDecimal pi = BigDecimal.ONE.divide(oneOverPi, mc);
        
        // Round to the required precision
        return pi.setScale(precision, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate square root using Newton's method for BigDecimal
     */
    private BigDecimal sqrt(BigDecimal n, MathContext mc) {
        if (n.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal x = n;
        BigDecimal last;
        
        do {
            last = x;
            x = x.add(n.divide(x, mc), mc).divide(new BigDecimal("2"), mc);
        } while (x.subtract(last, mc).abs().compareTo(new BigDecimal("1E-" + (mc.getPrecision() + 5))) > 0);
        
        return x;
    }
}