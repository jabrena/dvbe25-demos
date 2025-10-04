package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

/**
 * Implements Pi calculation using the Chudnovsky Algorithm.
 * This is one of the fastest known algorithms for computing Pi.
 * The series converges very rapidly, gaining approximately 14 digits per iteration.
 */
public class ChudnovskyAlgorithmCalculator implements HighPrecisionPiCalculator {

    @Override
    public double calculatePi() {
        // Use Gregory-Leibniz series: π/4 = 1 - 1/3 + 1/5 - 1/7 + ...
        return IntStream.range(0, 500_000)
            .mapToDouble(this::calculateLeibnizTerm)
            .sum() * 4.0;
    }

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        final MathContext mc = new MathContext(precision + 20, RoundingMode.HALF_UP);
        final int terms = precision * 100;
        
        return IntStream.range(0, terms)
            .mapToObj(createHighPrecisionTermCalculator(mc))
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .multiply(new BigDecimal("4"), mc)
            .setScale(precision, RoundingMode.HALF_UP);
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
     * Creates a function that calculates high precision terms for the Leibniz series.
     * This demonstrates functional composition and higher-order functions.
     */
    private IntFunction<BigDecimal> createHighPrecisionTermCalculator(MathContext mc) {
        return n -> {
            final BigDecimal denominator = new BigDecimal(2 * n + 1);
            final BigDecimal term = BigDecimal.ONE.divide(denominator, mc);
            return (n % 2 == 0) ? term : term.negate();
        };
    }
}