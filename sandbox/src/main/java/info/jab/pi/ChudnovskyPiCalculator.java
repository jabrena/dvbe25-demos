package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.stream.IntStream;

/**
* Implementation of Pi calculation using the Chudnovsky algorithm.
* Uses the formula: 1/π = 12 * Σ((-1)^k * (6k)! * (13591409 + 545140134*k)) / ((3k)! * (k!)^3 * 640320^(3k + 3/2))
*/
public class ChudnovskyPiCalculator implements HighPrecisionPiCalculator {

    private static final BigDecimal TWELVE = new BigDecimal("12");
    private static final BigDecimal BASE_640320 = new BigDecimal("640320");
    private static final BigDecimal LINEAR_COEFFICIENT_1 = new BigDecimal("13591409");
    private static final BigDecimal LINEAR_COEFFICIENT_2 = new BigDecimal("545140134");

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        MathContext mc = new MathContext(precision + 20, RoundingMode.HALF_UP);
        int terms = Math.max(20, precision / 10);
        
        BigDecimal sum = IntStream.range(0, terms)
            .mapToObj(k -> calculateChudnovskyTerm(k, mc))
            .reduce(BigDecimal.ZERO, (acc, term) -> {
                BigDecimal sign = new BigDecimal(acc.equals(BigDecimal.ZERO) ? 1 : -1);
                return acc.add(term.multiply(sign, mc), mc);
            });
        
        return BigDecimal.ONE.divide(sum.multiply(TWELVE, mc), mc)
            .setScale(precision, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate a single term of the Chudnovsky series
     */
    private BigDecimal calculateChudnovskyTerm(int k, MathContext mc) {
        BigDecimal factorialRatio = calculateFactorialRatio(k, mc);
        BigDecimal linearTerm = calculateLinearTerm(k, mc);
        BigDecimal powerTerm = calculatePowerTerm(k, mc);
        
        return factorialRatio.multiply(linearTerm, mc).divide(powerTerm, mc);
    }
    
    private BigDecimal calculateFactorialRatio(int k, MathContext mc) {
        return factorial(6 * k, mc)
            .divide(factorial(3 * k, mc).multiply(factorial(k, mc).pow(3, mc), mc), mc);
    }
    
    private BigDecimal calculateLinearTerm(int k, MathContext mc) {
        return LINEAR_COEFFICIENT_1.add(LINEAR_COEFFICIENT_2.multiply(new BigDecimal(k), mc), mc);
    }
    
    private BigDecimal calculatePowerTerm(int k, MathContext mc) {
        BigDecimal power3k = BASE_640320.pow(3 * k, mc);
        BigDecimal power3_2 = BASE_640320.pow(3, mc).sqrt(mc);
        return power3k.multiply(power3_2, mc);
    }
    
    /**
     * Calculate factorial of n using functional approach
     */
    private BigDecimal factorial(int n, MathContext mc) {
        if (n <= 1) {
            return BigDecimal.ONE;
        }
        
        return IntStream.rangeClosed(2, n)
            .mapToObj(i -> new BigDecimal(i))
            .reduce(BigDecimal.ONE, (acc, val) -> acc.multiply(val, mc));
    }
}