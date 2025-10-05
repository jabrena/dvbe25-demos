package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

/**
 * Immutable functional Pi calculator using the Chudnovsky algorithm.
 * This is one of the fastest known algorithms for calculating Pi.
 * 
 * Formula: 1/π = 12 * Σ(k=0 to ∞) [(-1)^k * (6k)! * (13591409 + 545140134*k)] / [(3k)! * k!^3 * 640320^(3k + 3/2)]
 * 
 * All methods are pure functions with no side effects.
 */
public final class ChudnovskyPiCalculator implements HighPrecisionPiCalculator {

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal ONE = BigDecimal.ONE;
    private static final BigDecimal TWO = new BigDecimal("2");
    private static final BigDecimal TWELVE = new BigDecimal("12");
    private static final BigDecimal CHUDNOVSKY_CONSTANT = new BigDecimal("13591409");
    private static final BigDecimal CHUDNOVSKY_INCREMENT = new BigDecimal("545140134");
    private static final BigDecimal BASE_640320 = new BigDecimal("640320");
    private static final int MAX_ITERATIONS = 100;

    @Override
    public BigDecimal calculatePiHighPrecision(final int precision) {
        final PrecisionContext context = PrecisionContext.withExtendedBuffer(precision);
        
        final BigDecimal piResult = calculateChudnovskyFormula(context)
                .apply(context.mathContext());
        return piResult.setScale(precision, context.mathContext().getRoundingMode());
    }

    /**
     * Pure function that creates a computation for Chudnovsky's formula.
     * Returns a function that can be applied to a MathContext.
     */
    private Function<MathContext, BigDecimal> calculateChudnovskyFormula(final PrecisionContext context) {
        return mc -> {
            final BigDecimal base640320Cubed = BASE_640320.pow(3, mc);
            final ChudnovskySeriesResult seriesResult = calculateChudnovskySeries(mc, base640320Cubed);
            
            final BigDecimal sqrt640320Cubed = calculateSquareRoot(base640320Cubed, mc);
            final BigDecimal oneOverPi = TWELVE.multiply(seriesResult.sum(), mc).divide(sqrt640320Cubed, mc);
            
            return ONE.divide(oneOverPi, mc);
        };
    }

    /**
     * Immutable result of Chudnovsky series calculation.
     */
    private record ChudnovskySeriesResult(BigDecimal sum, int iterations) {}

    /**
     * Immutable state for Chudnovsky series calculation.
     */
    private record ChudnovskyState(
            BigDecimal sum,
            BigDecimal a,  // (6k)! / ((3k)! * k!^3)
            BigDecimal b,  // 13591409 + 545140134*k
            BigDecimal c,  // 640320^(3k)
            int k
    ) {
        
        static ChudnovskyState initial() {
            return new ChudnovskyState(ZERO, ONE, CHUDNOVSKY_CONSTANT, ONE, 0);
        }
        
        ChudnovskyState nextIteration(final MathContext mc, final BigDecimal base640320Cubed) {
            final BigDecimal term = a.multiply(b, mc).divide(c, mc);
            final BigDecimal signedTerm = (k % 2 == 1) ? term.negate() : term;
            final BigDecimal newSum = sum.add(signedTerm, mc);
            
            if (k >= MAX_ITERATIONS - 1) {
                return new ChudnovskyState(newSum, a, b, c, k + 1);
            }
            
            // Calculate next iteration coefficients
            final BigDecimal newA = calculateNextA(mc);
            final BigDecimal newB = b.add(CHUDNOVSKY_INCREMENT, mc);
            final BigDecimal newC = c.multiply(base640320Cubed, mc);
            
            return new ChudnovskyState(newSum, newA, newB, newC, k + 1);
        }
        
        private BigDecimal calculateNextA(final MathContext mc) {
            final BigDecimal numerator = IntStream.rangeClosed(1, 6)
                    .mapToObj(i -> new BigDecimal(6 * k + i))
                    .reduce(ONE, (acc, val) -> acc.multiply(val, mc));
            
            final BigDecimal denominator = IntStream.rangeClosed(1, 3)
                    .mapToObj(i -> new BigDecimal(3 * k + i))
                    .reduce(ONE, (acc, val) -> acc.multiply(val, mc))
                    .multiply(new BigDecimal(k + 1).pow(3, mc), mc);
            
            return a.multiply(numerator, mc).divide(denominator, mc);
        }
        
        BigDecimal getCurrentTerm(final MathContext mc) {
            return a.multiply(b, mc).divide(c, mc);
        }
        
        boolean hasConverged(final MathContext mc) {
            if (k < 5) return false;
            final BigDecimal threshold = new BigDecimal("1E-" + (mc.getPrecision() + 5));
            return getCurrentTerm(mc).abs().compareTo(threshold) < 0;
        }
    }

    /**
     * Pure function to calculate Chudnovsky series using functional approach.
     */
    private ChudnovskySeriesResult calculateChudnovskySeries(final MathContext mc, final BigDecimal base640320Cubed) {
        ChudnovskyState state = ChudnovskyState.initial();
        
        while (state.k < MAX_ITERATIONS && !state.hasConverged(mc)) {
            state = state.nextIteration(mc, base640320Cubed);
        }
        
        return new ChudnovskySeriesResult(state.sum, state.k);
    }

    /**
     * Pure function to calculate square root using Newton's method with trampoline pattern.
     */
    private BigDecimal calculateSquareRoot(final BigDecimal n, final MathContext mc) {
        if (n.equals(ZERO)) {
            return ZERO;
        }
        
        return newtonMethodTrampoline(n, mc, n);
    }

    /**
     * Tail-recursive Newton's method using trampoline pattern for stack safety.
     */
    private BigDecimal newtonMethodTrampoline(final BigDecimal n, final MathContext mc, final BigDecimal current) {
        final BigDecimal next = current.add(n.divide(current, mc), mc).divide(TWO, mc);
        final BigDecimal threshold = new BigDecimal("1E-" + (mc.getPrecision() + 5));
        
        if (next.subtract(current, mc).abs().compareTo(threshold) <= 0) {
            return next;
        }
        
        return newtonMethodTrampoline(n, mc, next);
    }
}