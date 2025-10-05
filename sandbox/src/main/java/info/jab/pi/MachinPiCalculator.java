package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Implementation of Pi calculation using Machin-like Formula.
 * Uses the formula: π/4 = 4*arctan(1/5) - arctan(1/239)
 * This is John Machin's original formula from 1706.
 */
public class MachinPiCalculator implements HighPrecisionPiCalculator {

    @Override
    public double calculatePi() {
        // Use Machin's formula: π/4 = 4*arctan(1/5) - arctan(1/239)
        double arctan1_5 = arctan(1.0/5.0);
        double arctan1_239 = arctan(1.0/239.0);
        return 4.0 * (4.0 * arctan1_5 - arctan1_239);
    }

    @Override
    public String calculatePiHighPrecision(int decimalPlaces) {
        MathContext mc = new MathContext(decimalPlaces + 10, RoundingMode.HALF_UP);
        
        BigDecimal five = new BigDecimal("5");
        BigDecimal two39 = new BigDecimal("239");
        BigDecimal four = new BigDecimal("4");
        
        // Calculate 4*arctan(1/5) - arctan(1/239)
        BigDecimal arctan1_5 = arctanBigDecimal(BigDecimal.ONE.divide(five, mc), mc);
        BigDecimal arctan1_239 = arctanBigDecimal(BigDecimal.ONE.divide(two39, mc), mc);
        
        BigDecimal piOver4 = four.multiply(arctan1_5, mc).subtract(arctan1_239, mc);
        BigDecimal pi = four.multiply(piOver4, mc);
        
        return pi.setScale(decimalPlaces, RoundingMode.HALF_UP).toString();
    }

    private double arctan(double x) {
        double result = 0.0;
        double term = x;
        double xSquared = x * x;
        
        for (int n = 0; n < 100; n++) {
            double currentTerm = term / (2 * n + 1);
            if (n % 2 == 0) {
                result += currentTerm;
            } else {
                result -= currentTerm;
            }
            term *= xSquared;
            
            if (Math.abs(currentTerm) < 1e-15) {
                break;
            }
        }
        
        return result;
    }

    private BigDecimal arctanBigDecimal(BigDecimal x, MathContext mc) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal term = x;
        BigDecimal xSquared = x.multiply(x, mc);
        
        for (int n = 0; n < 200; n++) {
            BigDecimal denominator = new BigDecimal(2 * n + 1);
            BigDecimal currentTerm = term.divide(denominator, mc);
            
            if (n % 2 == 0) {
                result = result.add(currentTerm, mc);
            } else {
                result = result.subtract(currentTerm, mc);
            }
            
            term = term.multiply(xSquared, mc);
            
            // Check for convergence
            if (currentTerm.abs().compareTo(new BigDecimal("1E-" + (mc.getPrecision() - 5))) < 0) {
                break;
            }
        }
        
        return result;
    }
}