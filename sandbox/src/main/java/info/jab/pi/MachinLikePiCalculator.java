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
        MathContext mc = new MathContext(precision + 10, RoundingMode.HALF_UP);
        
        // Calculate arctan(1/5) using Taylor series
        BigDecimal arctan5 = calculateArctan(BigDecimal.ONE.divide(BigDecimal.valueOf(5), mc), precision + 10);
        
        // Calculate arctan(1/239) using Taylor series
        BigDecimal arctan239 = calculateArctan(BigDecimal.ONE.divide(BigDecimal.valueOf(239), mc), precision + 10);
        
        // Apply Machin's formula: π/4 = 4*arctan(1/5) - arctan(1/239)
        BigDecimal piOver4 = arctan5.multiply(BigDecimal.valueOf(4), mc).subtract(arctan239, mc);
        
        // Multiply by 4 to get π
        BigDecimal pi = piOver4.multiply(BigDecimal.valueOf(4), mc);
        
        return pi.setScale(precision, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate arctan(x) using Taylor series: arctan(x) = x - x³/3 + x⁵/5 - x⁷/7 + ...
     */
    private BigDecimal calculateArctan(BigDecimal x, int precision) {
        MathContext mc = new MathContext(precision + 10, RoundingMode.HALF_UP);
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal xSquared = x.multiply(x, mc);
        BigDecimal term = x;
        BigDecimal sign = BigDecimal.ONE;
        
        // Use enough terms to achieve desired precision
        int maxTerms = precision * 2;
        
        for (int i = 1; i <= maxTerms; i += 2) {
            BigDecimal currentTerm = term.multiply(sign, mc).divide(BigDecimal.valueOf(i), mc);
            result = result.add(currentTerm, mc);
            
            // Check if we've converged (term is smaller than our precision)
            if (currentTerm.abs().compareTo(BigDecimal.ONE.scaleByPowerOfTen(-precision)) < 0) {
                break;
            }
            
            term = term.multiply(xSquared, mc);
            sign = sign.negate();
        }
        
        return result;
    }
}