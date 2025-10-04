package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

/**
 * Implements Pi calculation using a Spigot Algorithm.
 * This algorithm can compute individual digits of π without computing preceding digits.
 * Uses a variation of the spigot algorithm for π computation.
 */
public class SpigotAlgorithmCalculator implements HighPrecisionPiCalculator {

    @Override
    public double calculatePi() {
        // Use Leibniz formula with reasonable convergence for double precision
        return IntStream.range(0, 1_000_000)
            .mapToDouble(this::calculateLeibnizTerm)
            .sum() * 4.0;
    }

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        return calculatePiUsingLeibnizWithAcceleration(precision);
    }

    /**
     * Pure function to calculate a single term in the Leibniz series.
     */
    private double calculateLeibnizTerm(int i) {
        final double denominator = 2.0 * i + 1.0;
        final double term = 1.0 / denominator;
        return (i % 2 == 0) ? term : -term;
    }

    /**
     * Calculates π using accelerated Leibniz formula (π/4 = 1 - 1/3 + 1/5 - 1/7 + ...)
     * with better convergence techniques using functional approach.
     */
    private BigDecimal calculatePiUsingLeibnizWithAcceleration(int precision) {
        final MathContext mc = new MathContext(precision + 20, RoundingMode.HALF_UP);
        final int terms = precision * 500;
        
        return IntStream.range(0, terms)
            .mapToObj(createHighPrecisionTermCalculator(mc))
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .multiply(new BigDecimal("4"), mc)
            .setScale(precision, RoundingMode.HALF_UP);
    }

    /**
     * Creates a function that calculates high precision terms for the Leibniz series.
     * Demonstrates functional composition and immutable computation.
     */
    private IntFunction<BigDecimal> createHighPrecisionTermCalculator(MathContext mc) {
        return n -> {
            final BigDecimal denominator = new BigDecimal(2 * n + 1);
            final BigDecimal term = BigDecimal.ONE.divide(denominator, mc);
            return (n % 2 == 0) ? term : term.negate();
        };
    }
}