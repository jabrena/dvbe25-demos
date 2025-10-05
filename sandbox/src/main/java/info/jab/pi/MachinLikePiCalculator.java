package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Pi calculator using Machin-like formula: π/4 = 4*arctan(1/5) - arctan(1/239)
 * Implemented using functional programming principles with pure functions and immutable operations.
 */
public final class MachinLikePiCalculator implements HighPrecisionPiCalculator {

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        final MathContext mc = new MathContext(precision + 10, RoundingMode.HALF_UP);
        
        // Pure functional approach to Machin's formula: π/4 = 4*arctan(1/5) - arctan(1/239)
        final Function<BigDecimal, BigDecimal> arctanCalculator = x -> calculateArctan(x, mc);
        
        final BigDecimal arctan1_5 = arctanCalculator.apply(new BigDecimal("0.2"));
        final BigDecimal arctan1_239 = arctanCalculator.apply(BigDecimal.ONE.divide(new BigDecimal("239"), mc));
        
        final BigDecimal piOver4 = arctan1_5.multiply(new BigDecimal("4"), mc)
                .subtract(arctan1_239, mc);
        
        return piOver4.multiply(new BigDecimal("4"), mc)
                .setScale(precision, RoundingMode.HALF_UP);
    }
    
    /**
     * Pure function to calculate arctan using Taylor series expansion with functional composition
     */
    private BigDecimal calculateArctan(final BigDecimal x, final MathContext mc) {
        final BigDecimal xSquared = x.multiply(x, mc);
        final BigDecimal convergenceThreshold = new BigDecimal("1E-" + (mc.getPrecision() + 5));
        
        return calculateArctanTerms(x, xSquared, BigDecimal.ZERO, x, 0, mc, convergenceThreshold);
    }
    
    /**
     * Tail-recursive helper function for arctan calculation using trampoline pattern
     */
    private BigDecimal calculateArctanTerms(final BigDecimal x, final BigDecimal xSquared, 
                                           final BigDecimal accumulator, final BigDecimal currentPower, 
                                           final int n, final MathContext mc, 
                                           final BigDecimal convergenceThreshold) {
        if (n >= 1000) {
            return accumulator;
        }
        
        final int denominator = 2 * n + 1;
        final BigDecimal term = currentPower.divide(new BigDecimal(denominator), mc);
        
        // Check for convergence
        if (term.abs().compareTo(convergenceThreshold) < 0 && n > 0) {
            return accumulator;
        }
        
        final BigDecimal newAccumulator = (n % 2 == 0) 
            ? accumulator.add(term, mc) 
            : accumulator.subtract(term, mc);
        
        final BigDecimal nextPower = currentPower.multiply(xSquared, mc);
        
        return calculateArctanTerms(x, xSquared, newAccumulator, nextPower, n + 1, mc, convergenceThreshold);
    }
}