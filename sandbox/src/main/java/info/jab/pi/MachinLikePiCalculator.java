package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Pi calculation using Machin-like formula: π/4 = 4*arctan(1/5) - arctan(1/239)
 * This is John Machin's formula discovered in 1706.
 */
public class MachinLikePiCalculator implements HighPrecisionPiCalculator {

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        // Set precision higher for intermediate calculations to ensure accuracy
        int workingPrecision = precision + 20;
        MathContext mc = new MathContext(workingPrecision, RoundingMode.HALF_UP);
        
        // Machin's formula: π/4 = 4*arctan(1/5) - arctan(1/239)
        BigDecimal one = BigDecimal.ONE;
        BigDecimal five = new BigDecimal("5");
        BigDecimal twoThreeNine = new BigDecimal("239");
        
        BigDecimal arctan1_5 = arctan(one.divide(five, mc), mc); // arctan(1/5)
        BigDecimal arctan1_239 = arctan(one.divide(twoThreeNine, mc), mc); // arctan(1/239)
        
        BigDecimal piOver4 = BigDecimal.valueOf(4).multiply(arctan1_5, mc).subtract(arctan1_239, mc);
        BigDecimal pi = piOver4.multiply(BigDecimal.valueOf(4), mc);
        
        // Round to the requested precision
        return pi.setScale(precision, RoundingMode.HALF_UP);
    }

    /**
     * Calculates arctan(x) using Taylor series: arctan(x) = x - x³/3 + x⁵/5 - x⁷/7 + ...
     */
    private BigDecimal arctan(BigDecimal x, MathContext mc) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal term = x;
        BigDecimal xSquared = x.multiply(x, mc);
        BigDecimal threshold = BigDecimal.ONE.divide(BigDecimal.TEN.pow(mc.getPrecision()), mc);
        
        for (int n = 1; n < 10000 && term.abs().compareTo(threshold) > 0; n += 2) {
            if ((n - 1) / 2 % 2 == 0) {
                result = result.add(term.divide(BigDecimal.valueOf(n), mc), mc);
            } else {
                result = result.subtract(term.divide(BigDecimal.valueOf(n), mc), mc);
            }
            
            term = term.multiply(xSquared, mc);
        }
        
        return result;
    }
}