package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Pi calculator using the Chudnovsky algorithm.
 * This is one of the fastest known algorithms for computing Pi.
 * 
 * The formula is:
 * 1/π = 12 * Σ(k=0 to ∞) [(-1)^k * (6k)! * (545140134k + 13591409)] / [(3k)! * k!^3 * 426880^(2k+1/2)]
 */
public class ChudnovskyPiCalculator implements HighPrecisionPiCalculator {

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        // Set working precision higher than target to avoid rounding errors
        int workingPrecision = precision + 50;
        MathContext mc = new MathContext(workingPrecision, RoundingMode.HALF_UP);
        
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal tolerance = BigDecimal.ONE.movePointLeft(workingPrecision - 10);
        
        // Constants for Chudnovsky algorithm
        BigDecimal a = new BigDecimal("13591409");
        BigDecimal b = new BigDecimal("545140134");
        BigDecimal c = new BigDecimal("640320");
        
        // Calculate terms iteratively
        for (int k = 0; k < 50; k++) {
            // Calculate (6k)!
            BigDecimal factorial6k = factorial(6 * k, mc);
            
            // Calculate (3k)!
            BigDecimal factorial3k = factorial(3 * k, mc);
            
            // Calculate k!
            BigDecimal factorialK = factorial(k, mc);
            
            // Calculate 545140134*k + 13591409
            BigDecimal linearTerm = b.multiply(BigDecimal.valueOf(k), mc).add(a, mc);
            
            // Calculate numerator: (-1)^k * (6k)! * (545140134*k + 13591409)
            BigDecimal numerator = factorial6k.multiply(linearTerm, mc);
            if (k % 2 == 1) {
                numerator = numerator.negate();
            }
            
            // Calculate denominator: (3k)! * (k!)³ * 640320^(3k)
            BigDecimal denominator = factorial3k
                .multiply(factorialK.pow(3, mc), mc)
                .multiply(c.pow(3 * k, mc), mc);
            
            // Calculate the term
            BigDecimal term = numerator.divide(denominator, mc);
            sum = sum.add(term, mc);
            
            // Check convergence
            if (term.abs().compareTo(tolerance) < 0) {
                break;
            }
        }
        
        // Calculate π using the Chudnovsky formula
        // π = 426880 * sqrt(10005) / sum
        BigDecimal sqrt10005 = sqrt(new BigDecimal("10005"), mc);
        BigDecimal coefficient = new BigDecimal("426880").multiply(sqrt10005, mc);
        BigDecimal pi = coefficient.divide(sum, mc);
        
        // Round to target precision
        return pi.setScale(precision, RoundingMode.HALF_UP);
    }

    /**
     * Calculate factorial of n using BigDecimal
     */
    private BigDecimal factorial(int n, MathContext mc) {
        if (n <= 1) return BigDecimal.ONE;
        
        BigDecimal result = BigDecimal.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigDecimal.valueOf(i), mc);
        }
        return result;
    }

    /**
     * Calculate square root using Newton-Raphson method
     */
    private BigDecimal sqrt(BigDecimal value, MathContext mc) {
        BigDecimal x = value;
        BigDecimal prev;
        BigDecimal two = new BigDecimal("2");
        
        do {
            prev = x;
            x = x.add(value.divide(x, mc), mc).divide(two, mc);
        } while (x.subtract(prev).abs().compareTo(BigDecimal.ONE.movePointLeft(mc.getPrecision() - 5)) > 0);
        
        return x;
    }
}