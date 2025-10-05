package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Pi calculator using Machin's formula: π/4 = 4*arctan(1/5) - arctan(1/239)
 * This provides high precision calculation using Taylor series expansion of arctan.
 */
public class MachinLikePiCalculator implements HighPrecisionPiCalculator {

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        // Set working precision higher than target to avoid rounding errors
        int workingPrecision = precision + 10;
        MathContext mc = new MathContext(workingPrecision, RoundingMode.HALF_UP);
        
        // Calculate π/4 = 4*arctan(1/5) - arctan(1/239)
        BigDecimal arctan1_5 = arctan(new BigDecimal("0.2"), mc);
        BigDecimal arctan1_239 = arctan(BigDecimal.ONE.divide(new BigDecimal("239"), mc), mc);
        
        BigDecimal piOver4 = new BigDecimal("4").multiply(arctan1_5, mc).subtract(arctan1_239, mc);
        BigDecimal pi = piOver4.multiply(new BigDecimal("4"), mc);
        
        // Round to target precision
        return pi.setScale(precision, RoundingMode.HALF_UP);
    }

    /**
     * Calculate arctan(x) using Taylor series: arctan(x) = x - x³/3 + x⁵/5 - x⁷/7 + ...
     */
    private BigDecimal arctan(BigDecimal x, MathContext mc) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal xSquared = x.multiply(x, mc);
        BigDecimal xPower = x;
        BigDecimal tolerance = BigDecimal.ONE.movePointLeft(mc.getPrecision());
        
        int n = 1;
        while (true) {
            BigDecimal term = xPower.divide(BigDecimal.valueOf(n), mc);
            
            if (n % 4 == 1) {
                result = result.add(term, mc);
            } else {
                result = result.subtract(term, mc);
            }
            
            // Check convergence
            if (term.abs().compareTo(tolerance) < 0) {
                break;
            }
            
            xPower = xPower.multiply(xSquared, mc);
            n += 2;
            
            // Safety check to prevent infinite loops
            if (n > 10000) {
                break;
            }
        }
        
        return result;
    }
}