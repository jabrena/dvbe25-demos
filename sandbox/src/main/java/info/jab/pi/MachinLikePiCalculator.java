package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Pi calculator using Machin-like formula: π/4 = 4*arctan(1/5) - arctan(1/239)
 */
public class MachinLikePiCalculator implements HighPrecisionPiCalculator {

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        MathContext mc = new MathContext(precision + 10, RoundingMode.HALF_UP);
        
        // Machin's formula: π/4 = 4*arctan(1/5) - arctan(1/239)
        BigDecimal arctan1_5 = arctan(new BigDecimal("0.2"), mc);
        BigDecimal arctan1_239 = arctan(BigDecimal.ONE.divide(new BigDecimal("239"), mc), mc);
        
        BigDecimal piOver4 = arctan1_5.multiply(new BigDecimal("4"), mc)
                .subtract(arctan1_239, mc);
        
        BigDecimal pi = piOver4.multiply(new BigDecimal("4"), mc);
        
        return pi.setScale(precision, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate arctan using Taylor series expansion
     */
    private BigDecimal arctan(BigDecimal x, MathContext mc) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal xPower = x;
        BigDecimal xSquared = x.multiply(x, mc);
        
        for (int n = 0; n < 1000; n++) {
            int denominator = 2 * n + 1;
            BigDecimal term = xPower.divide(new BigDecimal(denominator), mc);
            
            if (n % 2 == 0) {
                result = result.add(term, mc);
            } else {
                result = result.subtract(term, mc);
            }
            
            xPower = xPower.multiply(xSquared, mc);
            
            // Check for convergence
            if (term.abs().compareTo(new BigDecimal("1E-" + (mc.getPrecision() + 5))) < 0) {
                break;
            }
        }
        
        return result;
    }
}