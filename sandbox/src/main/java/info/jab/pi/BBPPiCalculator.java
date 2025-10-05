package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Bailey-Borwein-Plouffe (BBP) Formula Pi Calculator
 * Allows computation of binary digits of π without needing the preceding digits
 */
public class BBPPiCalculator implements HighPrecisionPiCalculator {
    
    private static final int DEFAULT_ITERATIONS = 500;
    
    @Override
    public double calculatePi() {
        return calculatePi(DEFAULT_ITERATIONS);
    }
    
    @Override
    public double calculatePi(int iterations) {
        double sum = 0.0;
        
        for (int k = 0; k < iterations; k++) {
            double r = 8 * k + 1;
            double s = 8 * k + 4;
            double t = 8 * k + 5;
            double u = 8 * k + 6;
            
            double term = (1.0 / Math.pow(16, k)) * 
                         (4.0 / r - 2.0 / s - 1.0 / t - 1.0 / u);
            sum += term;
        }
        
        return sum;
    }
    
    @Override
    public String calculatePiHighPrecision(int precision) {
        MathContext mc = new MathContext(precision + 10, RoundingMode.HALF_UP);
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal sixteen = new BigDecimal(16, mc);
        
        int iterations = precision + 50; // BBP needs more iterations for precision
        
        for (int k = 0; k < iterations; k++) {
            BigDecimal kBig = new BigDecimal(k, mc);
            BigDecimal r = new BigDecimal(8).multiply(kBig, mc).add(BigDecimal.ONE, mc);
            BigDecimal s = new BigDecimal(8).multiply(kBig, mc).add(new BigDecimal(4), mc);
            BigDecimal t = new BigDecimal(8).multiply(kBig, mc).add(new BigDecimal(5), mc);
            BigDecimal u = new BigDecimal(8).multiply(kBig, mc).add(new BigDecimal(6), mc);
            
            BigDecimal sixteenPowK = sixteen.pow(k, mc);
            BigDecimal oneDividedBySixteenPowK = BigDecimal.ONE.divide(sixteenPowK, mc);
            
            BigDecimal term = oneDividedBySixteenPowK.multiply(
                new BigDecimal(4, mc).divide(r, mc)
                    .subtract(new BigDecimal(2, mc).divide(s, mc), mc)
                    .subtract(BigDecimal.ONE.divide(t, mc), mc)
                    .subtract(BigDecimal.ONE.divide(u, mc), mc), mc);
            
            sum = sum.add(term, mc);
            
            // Early termination if term becomes negligible
            if (term.abs().compareTo(BigDecimal.ONE.divide(BigDecimal.TEN.pow(precision + 5), mc)) < 0) {
                break;
            }
        }
        
        return sum.setScale(precision, RoundingMode.HALF_UP).toString();
    }
}