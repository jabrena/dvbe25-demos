package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Implements Pi calculation using Machin-like formula.
 * Uses the formula: π/4 = 4*arctan(1/5) - arctan(1/239)
 * This is one of the most famous Machin-like formulas for calculating Pi.
 */
public class MachinLikePiCalculator implements HighPrecisionPiCalculator {

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        // Add extra precision for intermediate calculations
        int workingPrecision = precision + 10;
        MathContext mc = new MathContext(workingPrecision, RoundingMode.HALF_UP);
        
        // Machin's formula: π/4 = 4*arctan(1/5) - arctan(1/239)
        BigDecimal arctan1_5 = arctan(BigDecimal.ONE.divide(BigDecimal.valueOf(5), mc), mc);
        BigDecimal arctan1_239 = arctan(BigDecimal.ONE.divide(BigDecimal.valueOf(239), mc), mc);
        
        BigDecimal piDiv4 = BigDecimal.valueOf(4).multiply(arctan1_5, mc)
                          .subtract(arctan1_239, mc);
        
        BigDecimal pi = piDiv4.multiply(BigDecimal.valueOf(4), mc);
        
        // Round to the requested precision
        return pi.setScale(precision, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculates arctan using Taylor series expansion.
     * arctan(x) = x - x³/3 + x⁵/5 - x⁷/7 + ...
     */
    private BigDecimal arctan(BigDecimal x, MathContext mc) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal xSquared = x.multiply(x, mc);
        BigDecimal xPower = x;
        BigDecimal term;
        int n = 1;
        boolean add = true;
        
        do {
            term = xPower.divide(BigDecimal.valueOf(n), mc);
            if (add) {
                result = result.add(term, mc);
            } else {
                result = result.subtract(term, mc);
            }
            
            xPower = xPower.multiply(xSquared, mc);
            n += 2;
            add = !add;
            
        } while (term.abs().compareTo(BigDecimal.ONE.divide(BigDecimal.TEN.pow(mc.getPrecision()), mc)) > 0 && n < 1000);
        
        return result;
    }
}