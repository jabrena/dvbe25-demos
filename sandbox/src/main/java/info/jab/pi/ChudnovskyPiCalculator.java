package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
* Implementation of Pi calculation using the Chudnovsky algorithm.
* Uses the formula: 1/π = 12 * Σ((-1)^k * (6k)! * (13591409 + 545140134*k)) / ((3k)! * (k!)^3 * 640320^(3k + 3/2))
*/
public class ChudnovskyPiCalculator implements HighPrecisionPiCalculator {

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        MathContext mc = new MathContext(precision + 20, RoundingMode.HALF_UP);
        
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal sign = BigDecimal.ONE;
        
        // Calculate the number of terms needed for the desired precision
        int terms = Math.max(20, precision / 10); // More terms for better precision
        
        for (int k = 0; k < terms; k++) {
            BigDecimal term = calculateTerm(k, mc);
            sum = sum.add(term.multiply(sign, mc), mc);
            sign = sign.negate();
        }
        
        // Calculate 1/π
        BigDecimal oneOverPi = sum.multiply(new BigDecimal("12"), mc);
        
        // Calculate π = 1 / (1/π)
        BigDecimal pi = BigDecimal.ONE.divide(oneOverPi, mc);
        
        return pi.setScale(precision, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate a single term of the Chudnovsky series
     */
    private BigDecimal calculateTerm(int k, MathContext mc) {
        // (6k)! / ((3k)! * (k!)^3)
        BigDecimal numerator = factorial(6 * k, mc);
        BigDecimal denominator = factorial(3 * k, mc).multiply(
            factorial(k, mc).pow(3, mc), mc);
        
        BigDecimal fraction = numerator.divide(denominator, mc);
        
        // (13591409 + 545140134*k)
        BigDecimal linearTerm = new BigDecimal("13591409")
            .add(new BigDecimal("545140134").multiply(new BigDecimal(k), mc), mc);
        
        // 640320^(3k + 3/2) = 640320^(3k) * 640320^(3/2)
        BigDecimal base640320 = new BigDecimal("640320");
        BigDecimal power3k = base640320.pow(3 * k, mc);
        BigDecimal power3_2 = base640320.pow(3, mc).sqrt(mc); // 640320^(3/2)
        BigDecimal powerTerm = power3k.multiply(power3_2, mc);
        
        return fraction.multiply(linearTerm, mc).divide(powerTerm, mc);
    }
    
    /**
     * Calculate factorial of n
     */
    private BigDecimal factorial(int n, MathContext mc) {
        if (n <= 1) {
            return BigDecimal.ONE;
        }
        
        BigDecimal result = BigDecimal.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(new BigDecimal(i), mc);
        }
        return result;
    }
}