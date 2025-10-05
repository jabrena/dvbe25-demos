package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Implements Pi calculation using Machin-like formula with functional programming principles.
 * Uses the formula: π/4 = 4*arctan(1/5) - arctan(1/239)
 * This is one of the most famous Machin-like formulas for calculating Pi.
 * 
 * This implementation follows functional programming principles:
 * - Pure functions with no side effects
 * - Immutable state transformations
 * - Functional composition of mathematical operations
 */
public final class MachinLikePiCalculator implements HighPrecisionPiCalculator {

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        return calculateMachinFormula(precision);
    }
    
    /**
     * Pure function that calculates Pi using Machin's formula.
     * This function has no side effects and always produces the same output for the same input.
     */
    private static BigDecimal calculateMachinFormula(int precision) {
        final var workingPrecision = precision + 10;
        final var mc = new MathContext(workingPrecision, RoundingMode.HALF_UP);
        
        // Functional composition of Machin's formula: π/4 = 4*arctan(1/5) - arctan(1/239)
        final var piQuarter = computePiQuarter(mc);
        final var pi = piQuarter.multiply(BigDecimal.valueOf(4), mc);
        
        return pi.setScale(precision, RoundingMode.HALF_UP);
    }
    
    /**
     * Pure function that computes π/4 using Machin's formula components.
     */
    private static BigDecimal computePiQuarter(MathContext mc) {
        final var arctan1_5 = computeArctan(createFraction(1, 5, mc), mc);
        final var arctan1_239 = computeArctan(createFraction(1, 239, mc), mc);
        
        return BigDecimal.valueOf(4)
            .multiply(arctan1_5, mc)
            .subtract(arctan1_239, mc);
    }
    
    /**
     * Pure function factory for creating fractions.
     */
    private static BigDecimal createFraction(int numerator, int denominator, MathContext mc) {
        return BigDecimal.valueOf(numerator).divide(BigDecimal.valueOf(denominator), mc);
    }
    
    /**
     * Pure function that calculates arctan using Taylor series expansion.
     * arctan(x) = x - x³/3 + x⁵/5 - x⁷/7 + ...
     * 
     * Uses functional approach with immutable state transformations.
     */
    private static BigDecimal computeArctan(BigDecimal x, MathContext mc) {
        final var seriesState = new ArctanSeriesState(
            BigDecimal.ZERO,    // result
            x,                  // xPower
            x.multiply(x, mc),  // xSquared
            1,                  // n
            true,               // add
            convergenceThreshold(mc)
        );
        
        return computeArctanSeries(seriesState, mc);
    }
    
    /**
     * Immutable state holder for arctan series computation.
     */
    private record ArctanSeriesState(
        BigDecimal result,
        BigDecimal xPower,
        BigDecimal xSquared,
        int n,
        boolean add,
        BigDecimal threshold
    ) {}
    
    /**
     * Tail-recursive computation of arctan series using functional approach.
     */
    private static BigDecimal computeArctanSeries(ArctanSeriesState state, MathContext mc) {
        final var term = state.xPower().divide(BigDecimal.valueOf(state.n()), mc);
        
        // Base case: convergence reached or max iterations
        if (term.abs().compareTo(state.threshold()) <= 0 || state.n() >= 1000) {
            return state.result();
        }
        
        // Functional state transformation
        final var newResult = state.add() 
            ? state.result().add(term, mc)
            : state.result().subtract(term, mc);
            
        final var newState = new ArctanSeriesState(
            newResult,
            state.xPower().multiply(state.xSquared(), mc),
            state.xSquared(),
            state.n() + 2,
            !state.add(),
            state.threshold()
        );
        
        return computeArctanSeries(newState, mc);
    }
    
    /**
     * Pure function that creates convergence threshold.
     */
    private static BigDecimal convergenceThreshold(MathContext mc) {
        return BigDecimal.ONE.divide(BigDecimal.TEN.pow(mc.getPrecision()), mc);
    }
}