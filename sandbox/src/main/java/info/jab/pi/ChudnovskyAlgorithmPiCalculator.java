package info.jab.pi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Pi calculation using the Chudnovsky algorithm.
 * This is one of the fastest known algorithms for computing π.
 * 
 * Formula: 1/π = 12 * Σ(k=0 to ∞) [(-1)^k * (6k)! * (545140134*k + 13591409)] / [(3k)! * (k!)^3 * 640320^(3k+3/2)]
 * 
 * This implementation follows functional programming principles with immutable state
 * and pure functions.
 */
public final class ChudnovskyAlgorithmPiCalculator implements HighPrecisionPiCalculator {

    // Constants for the Chudnovsky algorithm
    private static final long CONSTANT_A = 545140134L;
    private static final long CONSTANT_B = 13591409L;
    private static final long CONSTANT_C = 640320L;
    private static final BigDecimal CONSTANT_426880 = new BigDecimal("426880");
    private static final BigDecimal CONSTANT_10005 = new BigDecimal("10005");

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        MathContext mc = createMathContext(precision);
        
        return computePiUsingChudnovskyFormula(precision, mc);
    }

    /**
     * Pure function to create MathContext with appropriate precision buffer.
     */
    private static MathContext createMathContext(int precision) {
        return new MathContext(precision + 20, RoundingMode.HALF_UP);
    }

    /**
     * Pure function that computes Pi using the Chudnovsky formula.
     */
    private static BigDecimal computePiUsingChudnovskyFormula(int precision, MathContext mc) {
        BigDecimal c = CONSTANT_426880.multiply(sqrt(CONSTANT_10005, mc), mc);
        
        BigDecimal seriesSum = IntStream.range(0, precision / 14 + 5)
            .mapToObj(k -> calculateSeriesTerm(k, mc))
            .takeWhile(term -> isTermSignificant(term, precision))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return c.divide(seriesSum, mc).setScale(precision, RoundingMode.HALF_UP);
    }

    /**
     * Pure function to determine if a series term is still significant for the calculation.
     */
    private static boolean isTermSignificant(BigDecimal term, int precision) {
        BigDecimal threshold = new BigDecimal("1E-" + (precision + 10));
        return term.abs().compareTo(threshold) >= 0;
    }

    /**
     * Pure function to calculate individual term for Chudnovsky series.
     */
    private static BigDecimal calculateSeriesTerm(int k, MathContext mc) {
        ChudnovskyTerm term = ChudnovskyTerm.forIteration(k);
        return term.calculateValue(mc);
    }

    /**
     * Immutable record representing a Chudnovsky series term.
     */
    private record ChudnovskyTerm(
        int iteration,
        BigInteger factorial6k,
        BigInteger factorial3k,
        BigInteger factorialKCubed,
        BigInteger numeratorConstant,
        int sign
    ) {
        
        static ChudnovskyTerm forIteration(int k) {
            return new ChudnovskyTerm(
                k,
                computeFactorial(6 * k),
                computeFactorial(3 * k),
                computeFactorial(k).pow(3),
                computeNumeratorConstant(k),
                (k % 2 == 0) ? 1 : -1
            );
        }
        
        BigDecimal calculateValue(MathContext mc) {
            BigInteger numerator = factorial6k.multiply(numeratorConstant);
            if (sign < 0) {
                numerator = numerator.negate();
            }
            
            BigInteger denominator = factorial3k
                .multiply(factorialKCubed)
                .multiply(BigInteger.valueOf(CONSTANT_C).pow(3 * iteration));
            
            return new BigDecimal(numerator).divide(new BigDecimal(denominator), mc);
        }
        
        private static BigInteger computeNumeratorConstant(int k) {
            return BigInteger.valueOf(CONSTANT_A)
                .multiply(BigInteger.valueOf(k))
                .add(BigInteger.valueOf(CONSTANT_B));
        }
    }

    /**
     * Pure function to calculate factorial using functional approach with memoization opportunity.
     * This implementation uses IntStream for functional style while maintaining efficiency.
     */
    private static BigInteger computeFactorial(int n) {
        return (n <= 1) ? BigInteger.ONE : 
            IntStream.rangeClosed(2, n)
                .mapToObj(BigInteger::valueOf)
                .reduce(BigInteger.ONE, BigInteger::multiply);
    }

    /**
     * Pure function to calculate square root using Newton's method.
     * Uses functional iteration with takeWhile for convergence.
     */
    private static BigDecimal sqrt(BigDecimal value, MathContext mc) {
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        Function<BigDecimal, BigDecimal> newtonStep = x -> 
            x.add(value.divide(x, mc)).divide(BigDecimal.valueOf(2), mc);
        
        BigDecimal threshold = new BigDecimal("1E-" + (mc.getPrecision() - 5));
        
        return newtonIteration(value, newtonStep, threshold, mc);
    }

    /**
     * Pure recursive function for Newton's method iteration using functional approach.
     */
    private static BigDecimal newtonIteration(BigDecimal current, Function<BigDecimal, BigDecimal> step, 
                                            BigDecimal threshold, MathContext mc) {
        BigDecimal next = step.apply(current);
        BigDecimal difference = next.subtract(current).abs();
        
        return (difference.compareTo(threshold) <= 0) ? next :
            newtonIteration(next, step, threshold, mc);
    }
}