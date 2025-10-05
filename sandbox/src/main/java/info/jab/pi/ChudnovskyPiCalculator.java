package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

/**
 * Implements Pi calculation using the Chudnovsky algorithm with functional programming principles.
 * This is one of the fastest known algorithms for calculating Pi.
 * 
 * The formula is:
 * 1/π = 12 * Σ(k=0 to ∞) [(-1)^k * (6k)! * (545140134k + 13591409)] / [(3k)! * (k!)^3 * 640320^(3k+3/2)]
 * 
 * This implementation follows functional programming principles:
 * - Pure functions with no side effects
 * - Immutable state transformations
 * - Functional composition for mathematical operations
 */
public final class ChudnovskyPiCalculator implements HighPrecisionPiCalculator {

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        return calculateChudnovskyFormula(precision);
    }
    
    /**
     * Pure function that calculates Pi using the Chudnovsky algorithm.
     * This function has no side effects and always produces the same output for the same input.
     */
    private static BigDecimal calculateChudnovskyFormula(int precision) {
        final var workingPrecision = precision + 20;
        final var mc = new MathContext(workingPrecision, RoundingMode.HALF_UP);
        
        final var seriesSum = computeChudnovskySeries(mc);
        final var constant = computeChudnovskyConstant(mc);
        final var pi = constant.divide(seriesSum, mc);
        
        return pi.setScale(precision, RoundingMode.HALF_UP);
    }
    
    /**
     * Pure function that computes the Chudnovsky series sum.
     */
    private static BigDecimal computeChudnovskySeries(MathContext mc) {
        return IntStream.range(0, 100)
            .mapToObj(k -> computeSeriesTerm(k, mc))
            .takeWhile(term -> isTermSignificant(term, mc))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Pure function that computes the Chudnovsky constant.
     */
    private static BigDecimal computeChudnovskyConstant(MathContext mc) {
        final var baseConstant = new BigDecimal("426880");
        final var sqrtArgument = new BigDecimal("10005");
        return baseConstant.multiply(computeSqrt(sqrtArgument, mc), mc);
    }
    
    /**
     * Pure function that computes a single term in the Chudnovsky series.
     */
    private static BigDecimal computeSeriesTerm(int k, MathContext mc) {
        final var numerator = computeNumerator(k, mc);
        final var denominator = computeDenominator(k, mc);
        final var term = numerator.divide(denominator, mc);
        
        return (k % 2 == 1) ? term.negate() : term;
    }
    
    /**
     * Pure function that computes the numerator for a series term.
     */
    private static BigDecimal computeNumerator(int k, MathContext mc) {
        final var factorialPart = computeFactorial(6 * k);
        final var linearPart = new BigDecimal("545140134")
            .multiply(BigDecimal.valueOf(k), mc)
            .add(new BigDecimal("13591409"), mc);
        
        return factorialPart.multiply(linearPart, mc);
    }
    
    /**
     * Pure function that computes the denominator for a series term.
     */
    private static BigDecimal computeDenominator(int k, MathContext mc) {
        final var factorial3k = computeFactorial(3 * k);
        final var factorialK = computeFactorial(k);
        final var factorialKCubed = factorialK.pow(3);
        final var powerPart = new BigDecimal("640320").pow(3 * k);
        
        return factorial3k
            .multiply(factorialKCubed, mc)
            .multiply(powerPart, mc);
    }
    
    /**
     * Pure function that checks if a term is significant for convergence.
     */
    private static boolean isTermSignificant(BigDecimal term, MathContext mc) {
        final var threshold = BigDecimal.ONE.divide(
            BigDecimal.TEN.pow(mc.getPrecision()), mc
        );
        return term.abs().compareTo(threshold) >= 0;
    }
    
    /**
     * Pure function that calculates factorial using functional approach.
     * Uses memoization pattern for efficiency.
     */
    private static final ConcurrentHashMap<Integer, BigDecimal> FACTORIAL_CACHE = 
        new ConcurrentHashMap<>();
    
    private static BigDecimal computeFactorial(int n) {
        if (n <= 1) {
            return BigDecimal.ONE;
        }
        
        return FACTORIAL_CACHE.computeIfAbsent(n, key -> 
            IntStream.rangeClosed(2, key)
                .mapToObj(BigDecimal::valueOf)
                .reduce(BigDecimal.ONE, BigDecimal::multiply)
        );
    }
    
    /**
     * Pure function that calculates square root using Newton's method with functional approach.
     */
    private static BigDecimal computeSqrt(BigDecimal value, MathContext mc) {
        if (value.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }
        
        final var sqrtState = new SqrtIterationState(
            value,  // current approximation
            BigDecimal.ZERO,  // previous approximation
            convergenceThreshold(mc)
        );
        
        return computeSqrtIteration(sqrtState, value, mc);
    }
    
    /**
     * Immutable state holder for square root iteration.
     */
    private record SqrtIterationState(
        BigDecimal current,
        BigDecimal previous,
        BigDecimal threshold
    ) {}
    
    /**
     * Tail-recursive square root computation using functional approach.
     */
    private static BigDecimal computeSqrtIteration(SqrtIterationState state, BigDecimal value, MathContext mc) {
        final var next = state.current()
            .add(value.divide(state.current(), mc), mc)
            .divide(BigDecimal.valueOf(2), mc);
            
        final var difference = next.subtract(state.current(), mc).abs();
        
        // Base case: convergence reached
        if (difference.compareTo(state.threshold()) <= 0) {
            return next;
        }
        
        // Functional state transformation
        final var newState = new SqrtIterationState(next, state.current(), state.threshold());
        return computeSqrtIteration(newState, value, mc);
    }
    
    /**
     * Pure function that creates convergence threshold.
     */
    private static BigDecimal convergenceThreshold(MathContext mc) {
        return BigDecimal.ONE.divide(BigDecimal.TEN.pow(mc.getPrecision()), mc);
    }
}