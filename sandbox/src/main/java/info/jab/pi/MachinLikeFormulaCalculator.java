package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Implements Pi calculation using Machin-like Formula.
 * Uses the formula: π/4 = 4 * arctan(1/5) - arctan(1/239)
 * This is John Machin's formula from 1706.
 */
public class MachinLikeFormulaCalculator implements HighPrecisionPiCalculator {

    @Override
    public double calculatePi() {
        // Using Machin's formula: π/4 = 4 * arctan(1/5) - arctan(1/239)
        double arctan1_5 = arctan(1.0 / 5.0);
        double arctan1_239 = arctan(1.0 / 239.0);
        return 4.0 * (4.0 * arctan1_5 - arctan1_239);
    }

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        MathContext mc = new MathContext(precision + 10, RoundingMode.HALF_UP);
        
        BigDecimal five = new BigDecimal("5", mc);
        BigDecimal twoThirtyNine = new BigDecimal("239", mc);
        BigDecimal four = new BigDecimal("4", mc);
        
        BigDecimal arctan1_5 = arctanHighPrecision(BigDecimal.ONE.divide(five, mc), mc);
        BigDecimal arctan1_239 = arctanHighPrecision(BigDecimal.ONE.divide(twoThirtyNine, mc), mc);
        
        BigDecimal result = four.multiply(four.multiply(arctan1_5, mc).subtract(arctan1_239, mc), mc);
        return result.setScale(precision, RoundingMode.HALF_UP);
    }

    /**
     * Calculates arctan using Taylor series expansion.
     * arctan(x) = x - x³/3 + x⁵/5 - x⁷/7 + ...
     */
    private double arctan(double x) {
        double result = 0.0;
        double term = x;
        double xSquared = x * x;
        int n = 1;
        
        while (Math.abs(term) > 1e-15) {
            result += term / n;
            term *= -xSquared;
            n += 2;
        }
        
        return result;
    }

    /**
     * High precision arctan calculation using Taylor series.
     */
    private BigDecimal arctanHighPrecision(BigDecimal x, MathContext mc) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal term = x;
        BigDecimal xSquared = x.multiply(x, mc);
        BigDecimal minusXSquared = xSquared.negate();
        int n = 1;
        
        BigDecimal epsilon = BigDecimal.ONE.divide(BigDecimal.TEN.pow(mc.getPrecision() - 5), mc);
        
        while (term.abs().compareTo(epsilon) > 0 && n < 1000) {
            result = result.add(term.divide(new BigDecimal(n), mc), mc);
            term = term.multiply(minusXSquared, mc);
            n += 2;
        }
        
        return result;
    }
}