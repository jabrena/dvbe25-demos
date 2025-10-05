package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Pi calculation using Machin-like formula: π/4 = 4*arctan(1/5) - arctan(1/239)
 * This is based on John Machin's formula from 1706.
 */
public class MachinLikePiCalculator implements HighPrecisionPiCalculator {

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        // Set precision higher than required to avoid rounding errors during calculation
        int workingPrecision = precision + 10;
        MathContext mc = new MathContext(workingPrecision, RoundingMode.HALF_UP);
        
        // Machin's formula: π/4 = 4*arctan(1/5) - arctan(1/239)
        BigDecimal arctan1_5 = arctan(new BigDecimal("0.2"), mc);  // arctan(1/5)
        BigDecimal arctan1_239 = arctan(new BigDecimal("1").divide(new BigDecimal("239"), mc), mc);  // arctan(1/239)
        
        BigDecimal piOver4 = new BigDecimal("4").multiply(arctan1_5, mc).subtract(arctan1_239, mc);
        BigDecimal pi = piOver4.multiply(new BigDecimal("4"), mc);
        
        // Round to the required precision
        return pi.setScale(precision, RoundingMode.HALF_UP);
    }

    /**
     * Calculate arctan using Taylor series expansion:
     * arctan(x) = x - x³/3 + x⁵/5 - x⁷/7 + ...
     */
    private BigDecimal arctan(BigDecimal x, MathContext mc) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal xSquared = x.multiply(x, mc);
        BigDecimal xPower = x;
        BigDecimal term;
        int n = 1;
        boolean subtract = false;
        
        do {
            term = xPower.divide(new BigDecimal(n), mc);
            if (subtract) {
                result = result.subtract(term, mc);
            } else {
                result = result.add(term, mc);
            }
            
            xPower = xPower.multiply(xSquared, mc);
            n += 2;
            subtract = !subtract;
            
            // Continue until the term becomes negligible
        } while (term.abs().compareTo(new BigDecimal("1E-" + (mc.getPrecision() + 5))) > 0 && n < 10000);
        
        return result;
    }
}