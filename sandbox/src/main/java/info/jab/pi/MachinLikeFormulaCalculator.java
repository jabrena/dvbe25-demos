package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Implements Pi calculation using Machin-like Formula.
 * Uses the formula: π/4 = 4 * arctan(1/5) - arctan(1/239)
 * This is John Machin's formula from 1706.
 */
public class MachinLikeFormulaCalculator implements HighPrecisionPiCalculator {

    @Override
    public double calculatePi() {
        // Using Machin's formula: π/4 = 4 * arctan(1/5) - arctan(1/239)
        final double arctan1_5 = calculateArctanSeries(1.0 / 5.0);
        final double arctan1_239 = calculateArctanSeries(1.0 / 239.0);
        return 4.0 * (4.0 * arctan1_5 - arctan1_239);
    }

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        final MathContext mc = new MathContext(precision + 10, RoundingMode.HALF_UP);
        
        final BigDecimal five = new BigDecimal("5", mc);
        final BigDecimal twoThirtyNine = new BigDecimal("239", mc);
        final BigDecimal four = new BigDecimal("4", mc);
        
        final Function<BigDecimal, BigDecimal> arctanCalculator = x -> 
            calculateArctanHighPrecisionSeries(x, mc);
        
        final BigDecimal arctan1_5 = arctanCalculator.apply(BigDecimal.ONE.divide(five, mc));
        final BigDecimal arctan1_239 = arctanCalculator.apply(BigDecimal.ONE.divide(twoThirtyNine, mc));
        
        return four.multiply(four.multiply(arctan1_5, mc).subtract(arctan1_239, mc), mc)
                   .setScale(precision, RoundingMode.HALF_UP);
    }

    /**
     * Calculates arctan using Taylor series expansion in functional style.
     * arctan(x) = x - x³/3 + x⁵/5 - x⁷/7 + ...
     */
    private double calculateArctanSeries(double x) {
        return createTaylorSeriesStream(x)
            .limit(1000) // Practical limit to avoid infinite stream
            .takeWhile(term -> Math.abs(term) > 1e-15)
            .mapToDouble(Double::doubleValue)
            .sum();
    }

    /**
     * Creates a stream of Taylor series terms for arctan calculation.
     * Demonstrates functional composition and lazy evaluation.
     */
    private Stream<Double> createTaylorSeriesStream(double x) {
        final double xSquared = x * x;
        
        return Stream.iterate(
            new TaylorTerm(x, 1),
            term -> new TaylorTerm(term.value * (-xSquared), term.denominator + 2)
        ).map(term -> term.value / term.denominator);
    }

    /**
     * High precision arctan calculation using functional Taylor series.
     */
    private BigDecimal calculateArctanHighPrecisionSeries(BigDecimal x, MathContext mc) {
        final BigDecimal epsilon = BigDecimal.ONE.divide(BigDecimal.TEN.pow(mc.getPrecision() - 5), mc);
        final BigDecimal xSquared = x.multiply(x, mc);
        final BigDecimal minusXSquared = xSquared.negate();
        
        return Stream.iterate(
            new HighPrecisionTaylorTerm(x, BigDecimal.ONE),
            term -> new HighPrecisionTaylorTerm(
                term.value.multiply(minusXSquared, mc),
                term.denominator.add(new BigDecimal("2"))
            )
        )
        .limit(1000) // Practical limit
        .takeWhile(term -> term.value.abs().compareTo(epsilon) > 0)
        .map(term -> term.value.divide(term.denominator, mc))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Immutable data structure for Taylor series terms (double precision).
     */
    private static record TaylorTerm(double value, int denominator) {}

    /**
     * Immutable data structure for high precision Taylor series terms.
     */
    private static record HighPrecisionTaylorTerm(BigDecimal value, BigDecimal denominator) {}
}