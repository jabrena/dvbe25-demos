package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Pi calculator using Machin-like formula: π/4 = 4*arctan(1/5) - arctan(1/239)
 * This is John Machin's formula from 1706, one of the most famous arctangent formulas.
 */
public class MachinLikeFormulaPiCalculator implements HighPrecisionPiCalculator {
    
    @Override
    public double calculatePi() {
        // Using Machin's formula: π/4 = 4*arctan(1/5) - arctan(1/239)
        double arctan1_5 = calculateArctan(1.0/5.0, 50);
        double arctan1_239 = calculateArctan(1.0/239.0, 50);
        
        return 4.0 * (4.0 * arctan1_5 - arctan1_239);
    }
    
    @Override
    public String calculatePiHighPrecision(int precision) {
        MathContext mc = new MathContext(precision + 10, RoundingMode.HALF_UP);
        
        BigDecimal five = new BigDecimal("5", mc);
        BigDecimal two39 = new BigDecimal("239", mc);
        BigDecimal four = new BigDecimal("4", mc);
        
        // Calculate arctan(1/5) and arctan(1/239) with high precision
        BigDecimal arctan1_5 = calculateArctanHighPrecision(BigDecimal.ONE.divide(five, mc), mc);
        BigDecimal arctan1_239 = calculateArctanHighPrecision(BigDecimal.ONE.divide(two39, mc), mc);
        
        // π = 4 * (4*arctan(1/5) - arctan(1/239))
        BigDecimal pi = four.multiply(
            four.multiply(arctan1_5, mc).subtract(arctan1_239, mc), mc);
        
        return pi.setScale(precision, RoundingMode.HALF_UP).toPlainString();
    }
    
    /**
     * Calculate arctan(x) using Taylor series for standard precision.
     */
    private double calculateArctan(double x, int terms) {
        double result = 0.0;
        double x_squared = x * x;
        double x_power = x;
        
        for (int i = 0; i < terms; i++) {
            int denominator = 2 * i + 1;
            double term = x_power / denominator;
            
            if (i % 2 == 0) {
                result += term;
            } else {
                result -= term;
            }
            
            x_power *= x_squared;
        }
        
        return result;
    }
    
    /**
     * Calculate arctan(x) using Taylor series for high precision.
     */
    private BigDecimal calculateArctanHighPrecision(BigDecimal x, MathContext mc) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal x_squared = x.multiply(x, mc);
        BigDecimal x_power = x;
        
        // Use enough terms to achieve required precision
        int maxTerms = mc.getPrecision() * 2;
        
        for (int i = 0; i < maxTerms; i++) {
            BigDecimal denominator = new BigDecimal(2 * i + 1, mc);
            BigDecimal term = x_power.divide(denominator, mc);
            
            if (i % 2 == 0) {
                result = result.add(term, mc);
            } else {
                result = result.subtract(term, mc);
            }
            
            x_power = x_power.multiply(x_squared, mc);
            
            // Check for convergence
            if (term.abs().compareTo(BigDecimal.ONE.divide(
                BigDecimal.TEN.pow(mc.getPrecision(), mc), mc)) < 0) {
                break;
            }
        }
        
        return result;
    }
}