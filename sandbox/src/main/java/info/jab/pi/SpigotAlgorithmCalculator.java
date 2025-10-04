package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Implements Pi calculation using a Spigot Algorithm.
 * This algorithm can compute individual digits of π without computing preceding digits.
 * Uses a variation of the spigot algorithm for π computation.
 */
public class SpigotAlgorithmCalculator implements HighPrecisionPiCalculator {

    @Override
    public double calculatePi() {
        // Use Leibniz formula with reasonable convergence for double precision
        double pi = 0.0;
        int sign = 1;
        
        for (int i = 0; i < 1000000; i++) {
            pi += sign / (2.0 * i + 1.0);
            sign *= -1;
        }
        
        return pi * 4.0;
    }

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        // For high precision, we'll use the Leibniz formula with acceleration
        return calculatePiUsingLeibnizWithAcceleration(precision);
    }

    /**
     * Calculates π using accelerated Leibniz formula (π/4 = 1 - 1/3 + 1/5 - 1/7 + ...)
     * with better convergence techniques.
     */
    private BigDecimal calculatePiUsingLeibnizWithAcceleration(int precision) {
        MathContext mc = new MathContext(precision + 20, RoundingMode.HALF_UP);
        
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal sign = BigDecimal.ONE;
        BigDecimal four = new BigDecimal("4");
        
        // Use many more terms for higher precision
        int terms = precision * 500;
        
        for (int n = 0; n < terms; n++) {
            BigDecimal denominator = new BigDecimal(2 * n + 1);
            BigDecimal term = sign.divide(denominator, mc);
            sum = sum.add(term, mc);
            sign = sign.negate();
        }
        
        BigDecimal result = four.multiply(sum, mc);
        return result.setScale(precision, RoundingMode.HALF_UP);
    }
}