package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Pi calculation using Machin-like formula
 * Uses the formula: π/4 = 4*arctan(1/5) - arctan(1/239)
 */
public class MachinLikeFormulaPiCalculator implements HighPrecisionPiCalculator {
    
    @Override
    public double calculatePi() {
        // Using Machin's formula: π/4 = 4*arctan(1/5) - arctan(1/239)
        double term1 = 4 * arctan(1.0/5.0);
        double term2 = arctan(1.0/239.0);
        return 4 * (term1 - term2);
    }
    
    @Override
    public String calculatePiHighPrecision(int precision) {
        MathContext mc = new MathContext(precision + 10, RoundingMode.HALF_UP);
        
        BigDecimal five = new BigDecimal("5");
        BigDecimal twoThreeNine = new BigDecimal("239");
        BigDecimal four = new BigDecimal("4");
        
        BigDecimal term1 = four.multiply(arctanBigDecimal(BigDecimal.ONE.divide(five, mc), mc), mc);
        BigDecimal term2 = arctanBigDecimal(BigDecimal.ONE.divide(twoThreeNine, mc), mc);
        BigDecimal piQuarter = term1.subtract(term2, mc);
        BigDecimal pi = four.multiply(piQuarter, mc);
        
        return pi.setScale(precision, RoundingMode.HALF_UP).toPlainString();
    }
    
    private double arctan(double x) {
        // Taylor series for arctan(x) = x - x³/3 + x⁵/5 - x⁷/7 + ...
        double result = 0;
        double term = x;
        double xSquared = x * x;
        
        for (int n = 1; n <= 100; n += 2) {
            result += term / n;
            term *= -xSquared;
        }
        return result;
    }
    
    private BigDecimal arctanBigDecimal(BigDecimal x, MathContext mc) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal term = x;
        BigDecimal xSquared = x.multiply(x, mc);
        
        for (int n = 1; n <= 100; n += 2) {
            BigDecimal divisor = new BigDecimal(n);
            result = result.add(term.divide(divisor, mc), mc);
            term = term.multiply(xSquared.negate(), mc);
            
            // Check for convergence
            if (term.abs().compareTo(new BigDecimal("1E-" + (mc.getPrecision() + 5))) < 0) {
                break;
            }
        }
        return result;
    }
}