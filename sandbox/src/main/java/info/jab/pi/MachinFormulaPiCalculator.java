package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Pi calculation using Machin's formula: π/4 = 4*arctan(1/5) - arctan(1/239)
 * This gives us: π = 4 * (4*arctan(1/5) - arctan(1/239))
 */
public class MachinFormulaPiCalculator implements HighPrecisionPiCalculator {

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        // Set precision with extra digits for intermediate calculations
        MathContext mc = new MathContext(precision + 10, RoundingMode.HALF_UP);
        
        // Calculate arctan(1/5) and arctan(1/239) using Taylor series
        BigDecimal arctan1_5 = arctan(new BigDecimal("0.2"), mc);
        BigDecimal arctan1_239 = arctan(new BigDecimal(1).divide(new BigDecimal(239), mc), mc);
        
        // Apply Machin's formula: π = 4 * (4*arctan(1/5) - arctan(1/239))
        BigDecimal pi = BigDecimal.valueOf(4)
            .multiply(
                BigDecimal.valueOf(4).multiply(arctan1_5, mc)
                .subtract(arctan1_239, mc), mc);
        
        // Round to requested precision
        return pi.setScale(precision, RoundingMode.HALF_UP);
    }

    /**
     * Calculate arctan(x) using Taylor series expansion:
     * arctan(x) = x - x³/3 + x⁵/5 - x⁷/7 + ...
     */
    private BigDecimal arctan(BigDecimal x, MathContext mc) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal xSquared = x.multiply(x, mc);
        BigDecimal power = x; // current power of x
        
        int n = 1;
        int sign = 1;
        
        while (power.abs().compareTo(new BigDecimal("1E-" + (mc.getPrecision() - 2))) > 0) {
            BigDecimal term = power.divide(new BigDecimal(n), mc);
            
            if (sign > 0) {
                result = result.add(term, mc);
            } else {
                result = result.subtract(term, mc);
            }
            
            power = power.multiply(xSquared, mc);
            n += 2;
            sign *= -1;
            
            // Prevent infinite loop
            if (n > 10000) break;
        }
        
        return result;
    }
}