package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Pi calculation using Machin's formula: π/4 = 4*arctan(1/5) - arctan(1/239)
 * This gives us: π = 4 * (4*arctan(1/5) - arctan(1/239))
 * 
 * This implementation follows functional programming principles with immutable state
 * and pure functions.
 */
public final class MachinFormulaPiCalculator implements HighPrecisionPiCalculator {

    // Constants for Machin's formula
    private static final BigDecimal ONE_FIFTH = new BigDecimal("0.2");
    private static final BigDecimal ONE_239TH = new BigDecimal(1).divide(new BigDecimal(239), MathContext.DECIMAL128);
    private static final BigDecimal FOUR = BigDecimal.valueOf(4);

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        MathContext mc = createMathContext(precision);
        
        return computePiUsingMachinFormula(mc, precision);
    }

    /**
     * Pure function to create MathContext with appropriate precision buffer.
     */
    private static MathContext createMathContext(int precision) {
        return new MathContext(precision + 10, RoundingMode.HALF_UP);
    }

    /**
     * Pure function that computes Pi using Machin's formula.
     */
    private static BigDecimal computePiUsingMachinFormula(MathContext mc, int precision) {
        BigDecimal arctan1_5 = computeArctan(ONE_FIFTH, mc);
        BigDecimal arctan1_239 = computeArctan(ONE_239TH, mc);
        
        // Apply Machin's formula: π = 4 * (4*arctan(1/5) - arctan(1/239))
        BigDecimal piValue = FOUR.multiply(
            FOUR.multiply(arctan1_5, mc).subtract(arctan1_239, mc), mc);
        
        return piValue.setScale(precision, RoundingMode.HALF_UP);
    }

    /**
     * Pure function to calculate arctan(x) using Taylor series expansion:
     * arctan(x) = x - x³/3 + x⁵/5 - x⁷/7 + ...
     */
    private static BigDecimal computeArctan(BigDecimal x, MathContext mc) {
        ArctanSeries series = ArctanSeries.initialize(x, mc);
        return series.calculateConvergentSum(mc);
    }

    /**
     * Immutable record representing the state of an arctan Taylor series calculation.
     */
    private record ArctanSeries(
        BigDecimal x,
        BigDecimal xSquared,
        BigDecimal convergenceThreshold
    ) {
        
        static ArctanSeries initialize(BigDecimal x, MathContext mc) {
            BigDecimal threshold = new BigDecimal("1E-" + (mc.getPrecision() - 2));
            return new ArctanSeries(x, x.multiply(x, mc), threshold);
        }
        
        BigDecimal calculateConvergentSum(MathContext mc) {
            return calculateTermsUntilConvergence(
                ArctanTermState.initial(x), 
                mc
            );
        }
        
        private BigDecimal calculateTermsUntilConvergence(ArctanTermState state, MathContext mc) {
            if (!state.isSignificant(convergenceThreshold) || state.exceedsMaxIterations()) {
                return state.result();
            }
            
            ArctanTermState nextState = state.nextTerm(xSquared, mc);
            return calculateTermsUntilConvergence(nextState, mc);
        }
    }

    /**
     * Immutable record representing the state of a single term in the arctan series.
     */
    private record ArctanTermState(
        BigDecimal result,
        BigDecimal currentPower,
        int denominator,
        int sign,
        int iteration
    ) {
        
        private static final int MAX_ITERATIONS = 10000;
        
        static ArctanTermState initial(BigDecimal x) {
            return new ArctanTermState(
                BigDecimal.ZERO,
                x,
                1,
                1,
                0
            );
        }
        
        ArctanTermState nextTerm(BigDecimal xSquared, MathContext mc) {
            BigDecimal term = currentPower.divide(new BigDecimal(denominator), mc);
            BigDecimal newResult = (sign > 0) ? 
                result.add(term, mc) : 
                result.subtract(term, mc);
            
            return new ArctanTermState(
                newResult,
                currentPower.multiply(xSquared, mc),
                denominator + 2,
                -sign,
                iteration + 1
            );
        }
        
        boolean isSignificant(BigDecimal threshold) {
            return currentPower.abs().compareTo(threshold) > 0;
        }
        
        boolean exceedsMaxIterations() {
            return iteration >= MAX_ITERATIONS;
        }
    }
}