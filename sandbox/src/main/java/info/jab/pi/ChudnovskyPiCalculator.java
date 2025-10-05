package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Pi calculator using the Chudnovsky algorithm
 * Implemented using functional programming principles with pure functions and immutable operations.
 */
public final class ChudnovskyPiCalculator implements HighPrecisionPiCalculator {

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        final MathContext mc = new MathContext(precision + 20, RoundingMode.HALF_UP);
        final int iterations = precision / 14 + 10;
        
        final BigDecimal constant = new BigDecimal("426880")
                .multiply(calculateSqrt(new BigDecimal("10005"), mc), mc);
        
        final BigDecimal sum = IntStream.range(0, iterations)
                .mapToObj(k -> calculateChudnovskyTerm(k, mc))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return constant.divide(sum, mc)
                .setScale(precision, RoundingMode.HALF_UP);
    }
    
    /**
     * Pure function to calculate a single term in the Chudnovsky series
     */
    private BigDecimal calculateChudnovskyTerm(final int k, final MathContext mc) {
        final Function<Integer, BigDecimal> factorialCalculator = n -> calculateFactorial(n, mc);
        
        final BigDecimal numerator = factorialCalculator.apply(6 * k)
                .multiply(new BigDecimal("13591409")
                        .add(new BigDecimal("545140134")
                                .multiply(new BigDecimal(k), mc), mc), mc);
        
        final BigDecimal denominator = factorialCalculator.apply(3 * k)
                .multiply(factorialCalculator.apply(k).pow(3), mc)
                .multiply(new BigDecimal("640320").pow(3 * k), mc);
        
        final BigDecimal term = numerator.divide(denominator, mc);
        
        return (k % 2 == 1) ? term.negate() : term;
    }
    
    /**
     * Pure function to calculate factorial using functional approach with streams
     */
    private BigDecimal calculateFactorial(final int n, final MathContext mc) {
        if (n <= 1) {
            return BigDecimal.ONE;
        }
        
        return IntStream.rangeClosed(2, n)
                .mapToObj(BigDecimal::new)
                .reduce(BigDecimal.ONE, (acc, val) -> acc.multiply(val, mc));
    }
    
    /**
     * Pure function to calculate square root using Newton's method with trampoline pattern
     */
    private BigDecimal calculateSqrt(final BigDecimal n, final MathContext mc) {
        if (n.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }
        
        final BigDecimal convergenceThreshold = new BigDecimal("1E-" + (mc.getPrecision() - 5));
        return calculateSqrtIteration(n, n, mc, convergenceThreshold);
    }
    
    /**
     * Tail-recursive helper function for square root calculation
     */
    private BigDecimal calculateSqrtIteration(final BigDecimal n, final BigDecimal x, 
                                            final MathContext mc, final BigDecimal convergenceThreshold) {
        final BigDecimal nextX = x.add(n.divide(x, mc)).divide(new BigDecimal("2"), mc);
        
        if (x.subtract(nextX).abs().compareTo(convergenceThreshold) <= 0) {
            return nextX;
        }
        
        return calculateSqrtIteration(n, nextX, mc, convergenceThreshold);
    }
}