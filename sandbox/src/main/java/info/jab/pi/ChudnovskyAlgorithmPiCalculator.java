package info.jab.pi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Pi calculation using the Chudnovsky algorithm.
 * This is one of the fastest known algorithms for computing π.
 * 
 * Formula: 1/π = 12 * Σ(k=0 to ∞) [(-1)^k * (6k)! * (545140134*k + 13591409)] / [(3k)! * (k!)^3 * 640320^(3k+3/2)]
 */
public class ChudnovskyAlgorithmPiCalculator implements HighPrecisionPiCalculator {

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        // Set precision with extra digits for intermediate calculations
        MathContext mc = new MathContext(precision + 20, RoundingMode.HALF_UP);
        
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal c = new BigDecimal("426880").multiply(sqrt(new BigDecimal("10005"), mc), mc);
        
        // Calculate series terms until convergence
        for (int k = 0; k < precision / 14 + 5; k++) {
            BigDecimal term = calculateTerm(k, mc);
            sum = sum.add(term, mc);
            
            // Check for convergence
            if (term.abs().compareTo(new BigDecimal("1E-" + (precision + 10))) < 0) {
                break;
            }
        }
        
        // Calculate π = c / sum
        BigDecimal pi = c.divide(sum, mc);
        
        // Round to requested precision
        return pi.setScale(precision, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate individual term for Chudnovsky series
     */
    private BigDecimal calculateTerm(int k, MathContext mc) {
        // Calculate (6k)!
        BigInteger factorial6k = factorial(6 * k);
        
        // Calculate (3k)!
        BigInteger factorial3k = factorial(3 * k);
        
        // Calculate (k!)^3
        BigInteger factorialK = factorial(k);
        BigInteger factorialKCubed = factorialK.pow(3);
        
        // Calculate 545140134*k + 13591409
        BigInteger numeratorConstant = BigInteger.valueOf(545140134L)
            .multiply(BigInteger.valueOf(k))
            .add(BigInteger.valueOf(13591409L));
        
        // Calculate (-1)^k
        int sign = (k % 2 == 0) ? 1 : -1;
        
        // Calculate numerator: (-1)^k * (6k)! * (545140134*k + 13591409)
        BigInteger numerator = factorial6k.multiply(numeratorConstant);
        if (sign < 0) {
            numerator = numerator.negate();
        }
        
        // Calculate denominator: (3k)! * (k!)^3 * 640320^(3k)
        BigInteger denominator = factorial3k.multiply(factorialKCubed);
        BigInteger power640320 = BigInteger.valueOf(640320L).pow(3 * k);
        denominator = denominator.multiply(power640320);
        
        // Convert to BigDecimal and divide
        BigDecimal numeratorBD = new BigDecimal(numerator);
        BigDecimal denominatorBD = new BigDecimal(denominator);
        
        return numeratorBD.divide(denominatorBD, mc);
    }
    
    /**
     * Calculate factorial of n
     */
    private BigInteger factorial(int n) {
        if (n <= 1) return BigInteger.ONE;
        
        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }
    
    /**
     * Calculate square root using Newton's method
     */
    private BigDecimal sqrt(BigDecimal value, MathContext mc) {
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal x = value;
        BigDecimal prev;
        
        do {
            prev = x;
            x = x.add(value.divide(x, mc)).divide(BigDecimal.valueOf(2), mc);
        } while (x.subtract(prev).abs().compareTo(new BigDecimal("1E-" + (mc.getPrecision() - 5))) > 0);
        
        return x;
    }
}