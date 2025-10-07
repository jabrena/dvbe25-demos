package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
* Implementation of Pi calculation using Machin-like formulas.
* Uses the formula: π/4 = 4*arctan(1/5) - arctan(1/239)
*/
public class MachinLikePiCalculator implements HighPrecisionPiCalculator {

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        MathContext mc = new MathContext(precision + 2000, RoundingMode.HALF_UP);
        
        // Calculate arctan(1/5) and arctan(1/239)
        BigDecimal arctan1_5 = arctan(BigDecimal.ONE.divide(new BigDecimal("5"), mc), mc);
        BigDecimal arctan1_239 = arctan(BigDecimal.ONE.divide(new BigDecimal("239"), mc), mc);
        
        // π/4 = 4*arctan(1/5) - arctan(1/239)
        BigDecimal piOver4 = arctan1_5.multiply(new BigDecimal("4"), mc).subtract(arctan1_239, mc);
        
        // π = 4 * (π/4)
        BigDecimal pi = piOver4.multiply(new BigDecimal("4"), mc);
        
        return pi.setScale(precision, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate arctan(x) using Taylor series: arctan(x) = x - x³/3 + x⁵/5 - x⁷/7 + ...
     */
    private BigDecimal arctan(BigDecimal x, MathContext mc) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal xSquared = x.multiply(x, mc);
        BigDecimal term = x;
        BigDecimal sign = BigDecimal.ONE;
        
        // Use enough terms for high precision - need more terms for smaller x values
        int maxTerms = Math.max(2000000, mc.getPrecision() * 2000);
        
        for (int i = 1; i <= maxTerms; i += 2) {
            BigDecimal termWithSign = term.multiply(sign, mc);
            result = result.add(termWithSign, mc);
            term = term.multiply(xSquared, mc);
            sign = sign.negate();
        }
        
        return result;
    }
}