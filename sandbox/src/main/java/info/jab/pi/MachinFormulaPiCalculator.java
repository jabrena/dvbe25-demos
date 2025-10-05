package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Machin-like Formula Pi Calculator
 * Uses Machin's formula: π/4 = 4*arctan(1/5) - arctan(1/239)
 */
public class MachinFormulaPiCalculator implements HighPrecisionPiCalculator {
    
    private static final int DEFAULT_ITERATIONS = 50;
    
    @Override
    public double calculatePi() {
        return calculatePi(DEFAULT_ITERATIONS);
    }
    
    @Override
    public double calculatePi(int iterations) {
        // Machin's formula: π/4 = 4*arctan(1/5) - arctan(1/239)
        double arctan1_5 = arctan(1.0/5.0, iterations);
        double arctan1_239 = arctan(1.0/239.0, iterations);
        return 4.0 * (4.0 * arctan1_5 - arctan1_239);
    }
    
    @Override
    public String calculatePiHighPrecision(int precision) {
        MathContext mc = new MathContext(precision + 10, RoundingMode.HALF_UP);
        BigDecimal five = new BigDecimal(5, mc);
        BigDecimal twoThreeNine = new BigDecimal(239, mc);
        
        BigDecimal arctan1_5 = arctanBigDecimal(BigDecimal.ONE.divide(five, mc), precision, mc);
        BigDecimal arctan1_239 = arctanBigDecimal(BigDecimal.ONE.divide(twoThreeNine, mc), precision, mc);
        
        BigDecimal four = new BigDecimal(4, mc);
        BigDecimal pi = four.multiply(four.multiply(arctan1_5).subtract(arctan1_239), mc);
        
        return pi.setScale(precision, RoundingMode.HALF_UP).toString();
    }
    
    private double arctan(double x, int iterations) {
        double result = 0.0;
        double term = x;
        double xSquared = x * x;
        
        for (int n = 0; n < iterations; n++) {
            if (n % 2 == 0) {
                result += term / (2 * n + 1);
            } else {
                result -= term / (2 * n + 1);
            }
            term *= xSquared;
        }
        return result;
    }
    
    private BigDecimal arctanBigDecimal(BigDecimal x, int precision, MathContext mc) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal term = x;
        BigDecimal xSquared = x.multiply(x, mc);
        int iterations = precision + 10;
        
        for (int n = 0; n < iterations; n++) {
            BigDecimal denominator = new BigDecimal(2 * n + 1, mc);
            if (n % 2 == 0) {
                result = result.add(term.divide(denominator, mc), mc);
            } else {
                result = result.subtract(term.divide(denominator, mc), mc);
            }
            term = term.multiply(xSquared, mc);
            
            // Early termination if term becomes negligible
            if (term.abs().compareTo(BigDecimal.ONE.divide(BigDecimal.TEN.pow(precision + 5), mc)) < 0) {
                break;
            }
        }
        return result;
    }
}