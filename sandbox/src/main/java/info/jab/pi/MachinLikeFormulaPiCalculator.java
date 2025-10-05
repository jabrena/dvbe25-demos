package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Implementation of Pi calculation using Machin-like Formula.
 * Uses the formula: π/4 = 4*arctan(1/5) - arctan(1/239)
 * This is John Machin's formula from 1706.
 */
public class MachinLikeFormulaPiCalculator implements HighPrecisionPiCalculator {

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        // Set math context with extra precision for intermediate calculations
        int workingPrecision = precision + 10;
        MathContext mc = new MathContext(workingPrecision, RoundingMode.HALF_UP);
        
        // Calculate using Machin's formula: π/4 = 4*arctan(1/5) - arctan(1/239)
        BigDecimal arctan1_5 = arctan(new BigDecimal("0.2"), mc); // arctan(1/5)
        BigDecimal arctan1_239 = arctan(BigDecimal.ONE.divide(new BigDecimal("239"), mc), mc); // arctan(1/239)
        
        // π/4 = 4*arctan(1/5) - arctan(1/239)
        BigDecimal piOver4 = new BigDecimal("4").multiply(arctan1_5, mc).subtract(arctan1_239, mc);
        
        // π = 4 * (π/4)
        BigDecimal pi = new BigDecimal("4").multiply(piOver4, mc);
        
        // Round to requested precision
        return pi.setScale(precision, RoundingMode.HALF_UP);
    }

    /**
     * Calculates arctan using Taylor series expansion.
     * arctan(x) = x - x³/3 + x⁵/5 - x⁷/7 + ...
     */
    private BigDecimal arctan(BigDecimal x, MathContext mc) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal xPower = x; // x^1
        BigDecimal xSquared = x.multiply(x, mc); // x²
        
        boolean positive = true;
        int denominator = 1;
        
        // Continue until convergence (when term becomes negligible)
        for (int i = 0; i < mc.getPrecision() * 2; i++) {
            BigDecimal term = xPower.divide(new BigDecimal(denominator), mc);
            
            if (positive) {
                result = result.add(term, mc);
            } else {
                result = result.subtract(term, mc);
            }
            
            // Check for convergence
            if (term.abs().compareTo(BigDecimal.ONE.divide(BigDecimal.TEN.pow(mc.getPrecision()), mc)) < 0) {
                break;
            }
            
            // Prepare for next iteration
            xPower = xPower.multiply(xSquared, mc); // x^(2n+1)
            denominator += 2; // 1, 3, 5, 7, ...
            positive = !positive;
        }
        
        return result;
    }
}