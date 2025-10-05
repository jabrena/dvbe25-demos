package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * Pi calculator using Machin-like formula: π/4 = 4*arctan(1/5) - arctan(1/239)
 * This is John Machin's formula from 1706, one of the most famous arctangent formulas.
 * Refactored to use functional programming principles with pure functions and immutable state.
 */
public class MachinLikeFormulaPiCalculator implements HighPrecisionPiCalculator {
    
    // Constants for Machin's formula
    private static final double DENOMINATOR_5 = 5.0;
    private static final double DENOMINATOR_239 = 239.0;
    private static final double COEFFICIENT = 4.0;
    private static final int DEFAULT_TERMS = 50;
    private static final double CONVERGENCE_THRESHOLD = 1e-15;
    
    @Override
    public double calculatePi() {
        // Using Machin's formula: π/4 = 4*arctan(1/5) - arctan(1/239)
        final double arctan1_5 = calculateArctanFunctional(1.0 / DENOMINATOR_5, DEFAULT_TERMS);
        final double arctan1_239 = calculateArctanFunctional(1.0 / DENOMINATOR_239, DEFAULT_TERMS);
        
        return COEFFICIENT * (COEFFICIENT * arctan1_5 - arctan1_239);
    }
    
    @Override
    public String calculatePiHighPrecision(final int precision) {
        validatePrecision(precision);
        
        final MathContext mc = new MathContext(precision + 10, RoundingMode.HALF_UP);
        
        final BigDecimal five = new BigDecimal("5", mc);
        final BigDecimal two39 = new BigDecimal("239", mc);
        final BigDecimal four = new BigDecimal("4", mc);
        
        // Calculate arctan(1/5) and arctan(1/239) with high precision using functional approach
        final BigDecimal arctan1_5 = calculateArctanHighPrecisionFunctional(
            BigDecimal.ONE.divide(five, mc), mc);
        final BigDecimal arctan1_239 = calculateArctanHighPrecisionFunctional(
            BigDecimal.ONE.divide(two39, mc), mc);
        
        // π = 4 * (4*arctan(1/5) - arctan(1/239))
        final BigDecimal pi = four.multiply(
            four.multiply(arctan1_5, mc).subtract(arctan1_239, mc), mc);
        
        return pi.setScale(precision, RoundingMode.HALF_UP).toPlainString();
    }
    
    /**
     * Calculate arctan(x) using Taylor series with functional programming approach.
     * Pure function with no side effects.
     */
    private double calculateArctanFunctional(final double x, final int maxTerms) {
        final double xSquared = x * x;
        
        return IntStream.range(0, maxTerms)
            .mapToDouble(i -> calculateArctanTerm(x, xSquared, i))
            .takeWhile(term -> Math.abs(term) >= CONVERGENCE_THRESHOLD)
            .sum();
    }
    
    /**
     * Calculate individual arctan Taylor series term.
     * Pure function for calculating term at index i.
     */
    private double calculateArctanTerm(final double x, final double xSquared, final int index) {
        final double xPower = Math.pow(x, 2 * index + 1);
        final int denominator = 2 * index + 1;
        final double term = xPower / denominator;
        
        return (index % 2 == 0) ? term : -term;
    }
    
    /**
     * Calculate arctan(x) using Taylor series for high precision with functional approach.
     * Utilizes stream processing for immutable computation.
     */
    private BigDecimal calculateArctanHighPrecisionFunctional(final BigDecimal x, final MathContext mc) {
        final BigDecimal xSquared = x.multiply(x, mc);
        final int maxTerms = mc.getPrecision() * 2;
        final BigDecimal convergenceThreshold = createConvergenceThreshold(mc);
        
        final Function<Integer, BigDecimal> termCalculator = 
            createHighPrecisionTermCalculator(x, xSquared, mc);
        final Predicate<BigDecimal> convergenceTest = 
            term -> term.abs().compareTo(convergenceThreshold) >= 0;
        
        return IntStream.range(0, maxTerms)
            .mapToObj(i -> termCalculator.apply(i))
            .takeWhile(convergenceTest)
            .reduce(BigDecimal.ZERO, (sum, term) -> sum.add(term, mc));
    }
    
    /**
     * Create a function to calculate high-precision arctan terms.
     * Returns a pure function with captured context.
     */
    private Function<Integer, BigDecimal> createHighPrecisionTermCalculator(
            final BigDecimal x, final BigDecimal xSquared, final MathContext mc) {
        
        return index -> {
            final BigDecimal xPower = calculatePower(x, xSquared, index, mc);
            final BigDecimal denominator = new BigDecimal(2 * index + 1, mc);
            final BigDecimal term = xPower.divide(denominator, mc);
            
            return (index % 2 == 0) ? term : term.negate();
        };
    }
    
    /**
     * Calculate x^(2*index + 1) efficiently using memoized powers.
     * Pure function for power calculation.
     */
    private BigDecimal calculatePower(final BigDecimal x, final BigDecimal xSquared, 
                                    final int index, final MathContext mc) {
        return IntStream.range(0, index)
            .boxed()
            .reduce(x, (power, i) -> power.multiply(xSquared, mc), (p1, p2) -> p1.multiply(p2, mc));
    }
    
    /**
     * Create convergence threshold for high precision calculations.
     * Pure function returning immutable threshold.
     */
    private BigDecimal createConvergenceThreshold(final MathContext mc) {
        return BigDecimal.ONE.divide(
            BigDecimal.TEN.pow(mc.getPrecision(), mc), mc);
    }
}