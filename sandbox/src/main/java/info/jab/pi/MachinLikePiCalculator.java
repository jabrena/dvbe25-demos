package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Pi calculation using Machin's formula: π = 16 * arctan(1/5) - 4 * arctan(1/239)
 * This is a well-known formula discovered by John Machin in 1706.
 */
public class MachinLikePiCalculator implements HighPrecisionPiCalculator {

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        // Set precision with some extra digits for intermediate calculations
        int workingPrecision = precision + 20;
        MathContext mc = new MathContext(workingPrecision, RoundingMode.HALF_UP);
        
        // Machin's formula: π = 16 * arctan(1/5) - 4 * arctan(1/239)
        BigDecimal sixteen = new BigDecimal(16);
        BigDecimal four = new BigDecimal(4);
        BigDecimal five = new BigDecimal(5);
        BigDecimal twoThreeNine = new BigDecimal(239);
        
        BigDecimal arctan1_5 = arctan(BigDecimal.ONE.divide(five, mc), mc);
        BigDecimal arctan1_239 = arctan(BigDecimal.ONE.divide(twoThreeNine, mc), mc);
        
        BigDecimal pi = sixteen.multiply(arctan1_5, mc).subtract(four.multiply(arctan1_239, mc), mc);
        
        // Round to requested precision
        return pi.setScale(precision, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate arctan(x) using Taylor series: arctan(x) = x - x^3/3 + x^5/5 - x^7/7 + ...
     */
    private BigDecimal arctan(BigDecimal x, MathContext mc) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal xSquared = x.multiply(x, mc);
        BigDecimal term = x;
        int n = 1;
        int sign = 1;
        
        // Continue until the term becomes negligibly small
        while (term.abs().compareTo(BigDecimal.ONE.divide(BigDecimal.TEN.pow(mc.getPrecision() - 5), mc)) > 0) {
            BigDecimal addTerm = term.divide(new BigDecimal(n), mc).multiply(new BigDecimal(sign), mc);
            result = result.add(addTerm, mc);
            
            term = term.multiply(xSquared, mc);
            n += 2;
            sign *= -1; // Alternate signs
        }
        
        return result;
    }
}