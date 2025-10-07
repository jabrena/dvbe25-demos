package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
* Implementation of Pi calculation using the Chudnovsky algorithm.
* This algorithm is known for its rapid convergence and high precision.
*/
public class ChudnovskyPiCalculator implements HighPrecisionPiCalculator {

    // Constants for Chudnovsky algorithm
    private static final BigDecimal C = new BigDecimal("426880");
    private static final BigDecimal L = new BigDecimal("13591409");
    private static final BigDecimal X = new BigDecimal("1");
    private static final BigDecimal M = new BigDecimal("1");
    private static final BigDecimal K = new BigDecimal("6");
    private static final BigDecimal A = new BigDecimal("13591409");
    private static final BigDecimal B = new BigDecimal("545140134");
    private static final BigDecimal C_CONST = new BigDecimal("640320");

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        MathContext mc = new MathContext(precision + 20, RoundingMode.HALF_UP);
        
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal term = BigDecimal.ONE;
        
        // Calculate number of iterations needed for desired precision
        int maxIterations = Math.max(10, precision / 14); // Chudnovsky converges very quickly
        
        for (int k = 0; k < maxIterations; k++) {
            BigDecimal currentTerm = calculateChudnovskyTerm(k, mc);
            sum = sum.add(currentTerm, mc);
            
            // Check convergence
            if (currentTerm.abs().compareTo(BigDecimal.ONE.scaleByPowerOfTen(-precision - 5)) < 0) {
                break;
            }
        }
        
        // Calculate pi using Chudnovsky formula: π = C * sqrt(10005) / sum
        BigDecimal sqrt10005 = calculateSqrt(new BigDecimal("10005"), precision + 10);
        BigDecimal pi = C.multiply(sqrt10005, mc).divide(sum, mc);
        
        return pi.setScale(precision, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate the k-th term in the Chudnovsky series
     */
    private BigDecimal calculateChudnovskyTerm(int k, MathContext mc) {
        // Calculate numerator: (6k)! * (545140134k + 13591409)
        BigDecimal sixKFactorial = factorial(BigDecimal.valueOf(6 * k), mc);
        BigDecimal numerator = sixKFactorial.multiply(
            B.multiply(BigDecimal.valueOf(k), mc).add(A, mc), mc);
        
        // Calculate denominator: (3k)! * (k!)^3 * (-640320)^(3k)
        BigDecimal threeKFactorial = factorial(BigDecimal.valueOf(3 * k), mc);
        BigDecimal kFactorial = factorial(BigDecimal.valueOf(k), mc);
        BigDecimal kFactorialCubed = kFactorial.pow(3, mc);
        
        BigDecimal cTo3k = C_CONST.pow(3 * k, mc);
        if (k % 2 == 1) {
            cTo3k = cTo3k.negate();
        }
        
        BigDecimal denominator = threeKFactorial.multiply(kFactorialCubed, mc)
            .multiply(cTo3k, mc);
        
        return numerator.divide(denominator, mc);
    }
    
    /**
     * Calculate factorial using iterative approach for better performance
     */
    private BigDecimal factorial(BigDecimal n, MathContext mc) {
        if (n.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ONE;
        }
        
        BigDecimal result = BigDecimal.ONE;
        BigDecimal i = BigDecimal.ONE;
        while (i.compareTo(n) <= 0) {
            result = result.multiply(i, mc);
            i = i.add(BigDecimal.ONE);
        }
        return result;
    }
    
    /**
     * Calculate square root using Newton's method
     */
    private BigDecimal calculateSqrt(BigDecimal value, int precision) {
        MathContext mc = new MathContext(precision + 10, RoundingMode.HALF_UP);
        
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal x = value.divide(BigDecimal.valueOf(2), mc);
        BigDecimal tolerance = BigDecimal.ONE.scaleByPowerOfTen(-precision);
        
        for (int i = 0; i < 50; i++) { // Max 50 iterations
            BigDecimal xNew = x.add(value.divide(x, mc), mc).divide(BigDecimal.valueOf(2), mc);
            
            if (xNew.subtract(x, mc).abs().compareTo(tolerance) < 0) {
                return xNew;
            }
            x = xNew;
        }
        
        return x;
    }
}