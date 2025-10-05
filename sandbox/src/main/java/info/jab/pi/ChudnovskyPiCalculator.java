package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * Pi calculator using the Bailey–Borwein–Plouffe formula.
 * Formula: π = Σ(k=0 to ∞) [1/16^k * (4/(8k+1) - 2/(8k+4) - 1/(8k+5) - 1/(8k+6))]
 * Refactored to use functional programming principles with immutable computations.
 */
public class ChudnovskyPiCalculator implements HighPrecisionPiCalculator {
    
    // BBP Formula Constants
    private static final int BASE = 16;
    private static final int MULTIPLIER_8 = 8;
    private static final double CONVERGENCE_THRESHOLD = 1e-15;
    private static final int DEFAULT_MAX_ITERATIONS = 100;
    
    // BBP Formula Coefficients
    private static final double COEFF_1 = 4.0;
    private static final double COEFF_2 = 2.0;
    private static final double COEFF_3 = 1.0;
    private static final double COEFF_4 = 1.0;
    
    // BBP Formula Denominators
    private static final int DENOM_OFFSET_1 = 1;
    private static final int DENOM_OFFSET_2 = 4;
    private static final int DENOM_OFFSET_3 = 5;
    private static final int DENOM_OFFSET_4 = 6;
    
    @Override
    public double calculatePi() {
        return IntStream.range(0, DEFAULT_MAX_ITERATIONS)
            .mapToDouble(this::calculateBbpTerm)
            .takeWhile(term -> Math.abs(term) >= CONVERGENCE_THRESHOLD)
            .sum();
    }
    
    @Override
    public String calculatePiHighPrecision(final int precision) {
        validatePrecision(precision);
        
        final MathContext mc = new MathContext(precision + 10, RoundingMode.HALF_UP);
        final int maxIterations = precision * 2;
        
        final BigDecimal convergenceThreshold = createHighPrecisionThreshold(precision, mc);
        final IntFunction<BigDecimal> termCalculator = k -> calculateBbpTermHighPrecision(k, mc);
        final Predicate<BigDecimal> convergenceTest = 
            term -> term.abs().compareTo(convergenceThreshold) >= 0;
        
        final BigDecimal pi = IntStream.range(0, maxIterations)
            .mapToObj(termCalculator)
            .takeWhile(convergenceTest)
            .reduce(BigDecimal.ZERO, (sum, term) -> sum.add(term, mc));
        
        return pi.setScale(precision, RoundingMode.HALF_UP).toPlainString();
    }
    
    /**
     * Calculate single BBP formula term for standard precision.
     * Pure function with no side effects.
     */
    private double calculateBbpTerm(final int k) {
        final double power16Inverse = Math.pow(BASE, -k);
        final double k8 = MULTIPLIER_8 * k;
        
        final double term1 = COEFF_1 / (k8 + DENOM_OFFSET_1);
        final double term2 = COEFF_2 / (k8 + DENOM_OFFSET_2);
        final double term3 = COEFF_3 / (k8 + DENOM_OFFSET_3);
        final double term4 = COEFF_4 / (k8 + DENOM_OFFSET_4);
        
        return power16Inverse * (term1 - term2 - term3 - term4);
    }
    
    /**
     * Calculate single BBP formula term for high precision.
     * Pure function utilizing immutable BigDecimal operations.
     */
    private BigDecimal calculateBbpTermHighPrecision(final int k, final MathContext mc) {
        final BigDecimal power16Inverse = calculatePowerInverse(BASE, k, mc);
        final BigDecimal k8 = new BigDecimal(MULTIPLIER_8 * k, mc);
        
        final Function<Integer, BigDecimal> denominatorCalculator = 
            offset -> k8.add(new BigDecimal(offset, mc), mc);
        
        final BigDecimal term1 = new BigDecimal(COEFF_1, mc)
            .divide(denominatorCalculator.apply(DENOM_OFFSET_1), mc);
        final BigDecimal term2 = new BigDecimal(COEFF_2, mc)
            .divide(denominatorCalculator.apply(DENOM_OFFSET_2), mc);
        final BigDecimal term3 = new BigDecimal(COEFF_3, mc)
            .divide(denominatorCalculator.apply(DENOM_OFFSET_3), mc);
        final BigDecimal term4 = new BigDecimal(COEFF_4, mc)
            .divide(denominatorCalculator.apply(DENOM_OFFSET_4), mc);
        
        final BigDecimal termSum = term1.subtract(term2, mc)
            .subtract(term3, mc)
            .subtract(term4, mc);
        
        return power16Inverse.multiply(termSum, mc);
    }
    
    /**
     * Calculate 1/base^exponent efficiently for high precision.
     * Pure function with memoization potential.
     */
    private BigDecimal calculatePowerInverse(final int base, final int exponent, final MathContext mc) {
        if (exponent == 0) {
            return BigDecimal.ONE;
        }
        
        final BigDecimal baseDecimal = new BigDecimal(base, mc);
        final BigDecimal power = IntStream.range(0, exponent)
            .boxed()
            .reduce(BigDecimal.ONE, 
                (acc, i) -> acc.multiply(baseDecimal, mc), 
                (p1, p2) -> p1.multiply(p2, mc));
        
        return BigDecimal.ONE.divide(power, mc);
    }
    
    /**
     * Create convergence threshold for high precision calculations.
     * Pure function returning immutable threshold.
     */
    private BigDecimal createHighPrecisionThreshold(final int precision, final MathContext mc) {
        return BigDecimal.ONE.divide(
            BigDecimal.TEN.pow(precision + 5, mc), mc);
    }
}