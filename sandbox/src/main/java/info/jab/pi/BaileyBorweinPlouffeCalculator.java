package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

/**
 * Implements Pi calculation using the Bailey-Borwein-Plouffe (BBP) Formula.
 * The BBP formula allows computation of hexadecimal digits of π without 
 * requiring the computation of preceding digits.
 * Formula: π = Σ(k=0 to ∞) [1/16^k * (4/(8k+1) - 2/(8k+4) - 1/(8k+5) - 1/(8k+6))]
 */
public class BaileyBorweinPlouffeCalculator implements HighPrecisionPiCalculator {

    @Override
    public double calculatePi() {
        return IntStream.range(0, 100)
            .mapToDouble(this::calculateBBPTerm)
            .sum();
    }

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        final MathContext mc = new MathContext(precision + 10, RoundingMode.HALF_UP);
        final BigDecimal epsilon = BigDecimal.ONE.divide(BigDecimal.TEN.pow(precision + 5), mc);
        final int maxIterations = precision * 2;
        
        return IntStream.range(0, maxIterations)
            .mapToObj(createHighPrecisionBBPTermCalculator(mc))
            .takeWhile(term -> term.abs().compareTo(epsilon) >= 0)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(precision, RoundingMode.HALF_UP);
    }

    /**
     * Pure function to calculate a single BBP formula term.
     */
    private double calculateBBPTerm(int k) {
        final double sixteenPowerK = Math.pow(16.0, -k);
        final double eightK = 8.0 * k;
        
        return sixteenPowerK * (
            4.0 / (eightK + 1) - 
            2.0 / (eightK + 4) - 
            1.0 / (eightK + 5) - 
            1.0 / (eightK + 6)
        );
    }

    /**
     * Creates a function that calculates high precision BBP terms.
     * Demonstrates functional composition and immutable computation.
     */
    private IntFunction<BigDecimal> createHighPrecisionBBPTermCalculator(MathContext mc) {
        return k -> {
            final BigDecimal sixteen = new BigDecimal("16");
            final BigDecimal sixteenPowerK = BigDecimal.ONE.divide(sixteen.pow(k, mc), mc);
            final BigDecimal eightK = new BigDecimal(8L * k);
            
            final BigDecimal term1 = new BigDecimal("4").divide(eightK.add(BigDecimal.ONE), mc);
            final BigDecimal term2 = new BigDecimal("2").divide(eightK.add(new BigDecimal("4")), mc);
            final BigDecimal term3 = BigDecimal.ONE.divide(eightK.add(new BigDecimal("5")), mc);
            final BigDecimal term4 = BigDecimal.ONE.divide(eightK.add(new BigDecimal("6")), mc);
            
            final BigDecimal sumTerms = term1.subtract(term2, mc).subtract(term3, mc).subtract(term4, mc);
            return sumTerms.multiply(sixteenPowerK, mc);
        };
    }
}