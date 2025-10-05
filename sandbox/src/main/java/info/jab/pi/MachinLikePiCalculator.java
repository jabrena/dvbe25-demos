package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Immutable functional Pi calculator using Machin-like formula: π/4 = 4*arctan(1/5) - arctan(1/239)
 * This implementation is based on John Machin's formula from 1706.
 * 
 * All methods are pure functions with no side effects.
 */
public final class MachinLikePiCalculator implements HighPrecisionPiCalculator {

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal ONE = BigDecimal.ONE;
    private static final BigDecimal TWO = new BigDecimal("2");
    private static final BigDecimal FOUR = new BigDecimal("4");
    private static final BigDecimal ONE_FIFTH = new BigDecimal("0.2");
    private static final BigDecimal TWO_HUNDRED_THIRTY_NINE = new BigDecimal("239");
    private static final int MAX_ITERATIONS = 10000;

    @Override
    public BigDecimal calculatePiHighPrecision(final int precision) {
        final PrecisionContext context = PrecisionContext.withDefaultBuffer(precision);
        
        final BigDecimal piResult = calculateMachinFormula(context)
                .apply(context.mathContext());
        return piResult.setScale(precision, context.mathContext().getRoundingMode());
    }

    /**
     * Pure function that creates a computation for Machin's formula.
     * Returns a function that can be applied to a MathContext.
     */
    private Function<MathContext, BigDecimal> calculateMachinFormula(final PrecisionContext context) {
        return mc -> {
            final BigDecimal arctan1_5 = calculateArctan(ONE_FIFTH, mc);
            final BigDecimal arctan1_239 = calculateArctan(ONE.divide(TWO_HUNDRED_THIRTY_NINE, mc), mc);
            
            final BigDecimal piOver4 = FOUR.multiply(arctan1_5, mc).subtract(arctan1_239, mc);
            return piOver4.multiply(FOUR, mc);
        };
    }

    /**
     * Pure function to calculate arctan using Taylor series expansion:
     * arctan(x) = x - x³/3 + x⁵/5 - x⁷/7 + ...
     */
    private BigDecimal calculateArctan(final BigDecimal x, final MathContext mc) {
        return arctanSeriesAccumulator(x, mc, new ArctanState(ZERO, x, x.multiply(x, mc), 1, false));
    }

    /**
     * Immutable state for arctan calculation series.
     */
    private record ArctanState(
            BigDecimal result,
            BigDecimal xPower,
            BigDecimal xSquared,
            int n,
            boolean subtract
    ) {
        
        ArctanState nextIteration(final MathContext mc) {
            final BigDecimal term = xPower.divide(new BigDecimal(n), mc);
            final BigDecimal newResult = subtract 
                    ? result.subtract(term, mc) 
                    : result.add(term, mc);
            
            return new ArctanState(
                    newResult,
                    xPower.multiply(xSquared, mc),
                    xSquared,
                    n + 2,
                    !subtract
            );
        }
        
        BigDecimal getCurrentTerm(final MathContext mc) {
            return xPower.divide(new BigDecimal(n), mc);
        }
        
        boolean hasConverged(final MathContext mc) {
            final BigDecimal threshold = new BigDecimal("1E-" + (mc.getPrecision() + 5));
            return getCurrentTerm(mc).abs().compareTo(threshold) <= 0 || n >= MAX_ITERATIONS;
        }
    }

    /**
     * Tail-recursive function for arctan series calculation using trampoline pattern.
     */
    private BigDecimal arctanSeriesAccumulator(final BigDecimal x, final MathContext mc, final ArctanState state) {
        if (state.hasConverged(mc)) {
            return state.result();
        }
        return arctanSeriesAccumulator(x, mc, state.nextIteration(mc));
    }
}