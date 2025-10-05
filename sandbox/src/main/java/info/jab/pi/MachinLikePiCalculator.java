package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Pi calculation using Machin-like formula.
 * Uses Machin's formula: π/4 = 4*arctan(1/5) - arctan(1/239)
 */
public class MachinLikePiCalculator implements HighPrecisionPiCalculator {

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        // Set precision with some extra digits for intermediate calculations
        MathContext mc = new MathContext(precision + 10, RoundingMode.HALF_UP);
        
        // Machin's formula: π/4 = 4*arctan(1/5) - arctan(1/239)
        BigDecimal arctan1_5 = arctan(BigDecimal.ONE.divide(new BigDecimal("5"), mc), mc);
        BigDecimal arctan1_239 = arctan(BigDecimal.ONE.divide(new BigDecimal("239"), mc), mc);
        
        // π/4 = 4*arctan(1/5) - arctan(1/239)
        BigDecimal piOver4 = new BigDecimal("4").multiply(arctan1_5, mc).subtract(arctan1_239, mc);
        
        // π = 4 * (π/4)
        BigDecimal pi = new BigDecimal("4").multiply(piOver4, mc);
        
        // Round to the requested precision
        return pi.setScale(precision, RoundingMode.HALF_UP);
    }

    /**
     * Calculate arctan(x) using Taylor series expansion.
     * arctan(x) = x - x³/3 + x⁵/5 - x⁷/7 + ...
     */
    private BigDecimal arctan(BigDecimal x, MathContext mc) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal xSquared = x.multiply(x, mc);
        BigDecimal term = x;
        int n = 1;
        
        // Continue until the term becomes negligible
        while (term.abs().compareTo(new BigDecimal("1E-" + (mc.getPrecision() - 2))) > 0 && n < 2000) {
            // Add or subtract the term based on the series: x - x³/3 + x⁵/5 - x⁷/7 + ...
            BigDecimal contribution = term.divide(new BigDecimal(n), mc);
            if ((n - 1) / 2 % 2 == 0) {
                result = result.add(contribution, mc);
            } else {
                result = result.subtract(contribution, mc);
            }
            
            term = term.multiply(xSquared, mc);
            n += 2;
        }
        
        return result;
    }
}