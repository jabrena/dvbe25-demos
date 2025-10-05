package info.jab.pi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Pi calculation using the Chudnovsky algorithm.
 * This algorithm converges extremely rapidly and is used by many computer programs
 * to calculate π to billions of digits.
 * 
 * Formula: 1/π = 12 * Σ(k=0 to ∞) [(-1)^k * (6k)! * (545140134*k + 13591409)] / [(3k)! * (k!)^3 * 640320^(3k+3/2)]
 */
public class ChudnovskyPiCalculator implements HighPrecisionPiCalculator {

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        // Set precision with extra digits for intermediate calculations
        int workingPrecision = precision + 50;
        MathContext mc = new MathContext(workingPrecision, RoundingMode.HALF_UP);
        
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal c = new BigDecimal(426880).multiply(sqrt(new BigDecimal(10005), mc), mc);
        
        // Calculate terms until convergence
        for (int k = 0; k < precision / 14 + 5; k++) {
            BigDecimal term = chudnovskyTerm(k, mc);
            sum = sum.add(term, mc);
            
            // Check for convergence - if term is very small relative to sum, stop
            if (k > 0 && term.abs().compareTo(sum.abs().divide(BigDecimal.TEN.pow(workingPrecision - 10), mc)) < 0) {
                break;
            }
        }
        
        BigDecimal pi = c.divide(sum, mc);
        
        // Round to requested precision
        return pi.setScale(precision, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate a single term of the Chudnovsky series
     */
    private BigDecimal chudnovskyTerm(int k, MathContext mc) {
        // Calculate (6k)!
        BigInteger factorial6k = factorial(6 * k);
        
        // Calculate (3k)!
        BigInteger factorial3k = factorial(3 * k);
        
        // Calculate k!
        BigInteger factorialK = factorial(k);
        
        // Calculate 545140134*k + 13591409
        BigInteger numeratorLinear = BigInteger.valueOf(545140134L)
            .multiply(BigInteger.valueOf(k))
            .add(BigInteger.valueOf(13591409L));
        
        // Calculate (-1)^k
        int sign = (k % 2 == 0) ? 1 : -1;
        
        // Calculate 640320^(3k)
        BigInteger base640320 = BigInteger.valueOf(640320L);
        BigInteger power640320_3k = base640320.pow(3 * k);
        
        // Calculate numerator: (-1)^k * (6k)! * (545140134*k + 13591409)
        BigInteger numerator = factorial6k.multiply(numeratorLinear);
        if (sign < 0) {
            numerator = numerator.negate();
        }
        
        // Calculate denominator: (3k)! * (k!)^3 * 640320^(3k)
        BigInteger denominator = factorial3k
            .multiply(factorialK.pow(3))
            .multiply(power640320_3k);
        
        return new BigDecimal(numerator).divide(new BigDecimal(denominator), mc);
    }
    
    /**
     * Calculate factorial using BigInteger
     */
    private BigInteger factorial(int n) {
        if (n <= 1) {
            return BigInteger.ONE;
        }
        
        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }
    
    /**
     * Calculate square root using Newton's method
     */
    private BigDecimal sqrt(BigDecimal n, MathContext mc) {
        if (n.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal x = n;
        BigDecimal lastX;
        BigDecimal two = new BigDecimal(2);
        
        do {
            lastX = x;
            x = x.add(n.divide(x, mc), mc).divide(two, mc);
        } while (x.subtract(lastX).abs().compareTo(BigDecimal.ONE.divide(BigDecimal.TEN.pow(mc.getPrecision() - 5), mc)) > 0);
        
        return x;
    }
}