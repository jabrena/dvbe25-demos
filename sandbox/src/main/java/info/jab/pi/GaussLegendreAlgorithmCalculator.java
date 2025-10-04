package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * Implements Pi calculation using the Gauss-Legendre Algorithm.
 * This is an iterative algorithm that converges quadratically to π.
 * Each iteration approximately doubles the number of correct digits.
 */
public class GaussLegendreAlgorithmCalculator implements HighPrecisionPiCalculator {

    @Override
    public double calculatePi() {
        final GaussLegendreState initialState = new GaussLegendreState(
            1.0, 
            1.0 / Math.sqrt(2.0), 
            0.25, 
            1.0
        );
        
        final GaussLegendreState finalState = Stream.iterate(initialState, this::nextGaussLegendreIteration)
            .skip(1) // Skip initial state
            .limit(10)
            .reduce((first, second) -> second) // Get the last element
            .orElse(initialState);
            
        return calculatePiFromState(finalState);
    }

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        final MathContext mc = new MathContext(precision + 10, RoundingMode.HALF_UP);
        final int iterations = (int) Math.ceil(Math.log(precision) / Math.log(2)) + 5;
        
        final HighPrecisionGaussLegendreState initialState = new HighPrecisionGaussLegendreState(
            BigDecimal.ONE,
            BigDecimal.ONE.divide(sqrt(new BigDecimal("2"), mc), mc),
            new BigDecimal("0.25"),
            BigDecimal.ONE
        );
        
        final HighPrecisionGaussLegendreState finalState = Stream.iterate(
            initialState, 
            state -> nextHighPrecisionGaussLegendreIteration(state, mc)
        )
        .skip(1) // Skip initial state
        .limit(iterations)
        .reduce((first, second) -> second) // Get the last element
        .orElse(initialState);
        
        return calculatePiFromHighPrecisionState(finalState, mc)
               .setScale(precision, RoundingMode.HALF_UP);
    }

    /**
     * Pure function to perform one Gauss-Legendre iteration.
     */
    private GaussLegendreState nextGaussLegendreIteration(GaussLegendreState state) {
        final double aNext = (state.a + state.b) / 2.0;
        final double bNext = Math.sqrt(state.a * state.b);
        final double tNext = state.t - state.p * Math.pow(state.a - aNext, 2);
        final double pNext = 2.0 * state.p;
        
        return new GaussLegendreState(aNext, bNext, tNext, pNext);
    }

    /**
     * Pure function to perform high precision Gauss-Legendre iteration.
     */
    private HighPrecisionGaussLegendreState nextHighPrecisionGaussLegendreIteration(
            HighPrecisionGaussLegendreState state, MathContext mc) {
        final BigDecimal aNext = state.a.add(state.b, mc).divide(new BigDecimal("2"), mc);
        final BigDecimal bNext = sqrt(state.a.multiply(state.b, mc), mc);
        final BigDecimal diff = state.a.subtract(aNext, mc);
        final BigDecimal tNext = state.t.subtract(state.p.multiply(diff.multiply(diff, mc), mc), mc);
        final BigDecimal pNext = state.p.multiply(new BigDecimal("2"), mc);
        
        return new HighPrecisionGaussLegendreState(aNext, bNext, tNext, pNext);
    }

    /**
     * Pure function to calculate Pi from final Gauss-Legendre state.
     */
    private double calculatePiFromState(GaussLegendreState state) {
        return Math.pow(state.a + state.b, 2) / (4.0 * state.t);
    }

    /**
     * Pure function to calculate high precision Pi from final state.
     */
    private BigDecimal calculatePiFromHighPrecisionState(HighPrecisionGaussLegendreState state, MathContext mc) {
        final BigDecimal sum = state.a.add(state.b, mc);
        return sum.multiply(sum, mc).divide(new BigDecimal("4").multiply(state.t, mc), mc);
    }

    /**
     * Functional square root calculation using Newton's method.
     */
    private BigDecimal sqrt(BigDecimal value, MathContext mc) {
        final BigDecimal epsilon = BigDecimal.ONE.divide(BigDecimal.TEN.pow(mc.getPrecision() - 5), mc);
        final UnaryOperator<BigDecimal> newtonIteration = x -> 
            x.add(value.divide(x, mc), mc).divide(new BigDecimal("2"), mc);
        
        return Stream.iterate(value, newtonIteration)
            .limit(mc.getPrecision()) // Practical limit
            .reduce((prev, curr) -> {
                if (curr.subtract(prev, mc).abs().compareTo(epsilon) < 0) {
                    return curr; // Convergence achieved
                }
                return curr;
            })
            .orElse(value);
    }

    /**
     * Immutable data structure for Gauss-Legendre algorithm state.
     */
    private static record GaussLegendreState(double a, double b, double t, double p) {}

    /**
     * Immutable data structure for high precision Gauss-Legendre algorithm state.
     */
    private static record HighPrecisionGaussLegendreState(BigDecimal a, BigDecimal b, BigDecimal t, BigDecimal p) {}
}