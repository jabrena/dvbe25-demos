package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Implements Pi calculation using the Bailey-Borwein-Plouffe (BBP) Formula.
 * The BBP formula allows computation of hexadecimal digits of π without 
 * requiring the computation of preceding digits.
 * Formula: π = Σ(k=0 to ∞) [1/16^k * (4/(8k+1) - 2/(8k+4) - 1/(8k+5) - 1/(8k+6))]
 */
public class BaileyBorweinPlouffeCalculator implements HighPrecisionPiCalculator {

    @Override
    public double calculatePi() {
        double pi = 0.0;
        double sixteenPower = 1.0;
        
        for (int k = 0; k < 100; k++) {
            double term = (1.0 / sixteenPower) * (
                4.0 / (8 * k + 1) - 
                2.0 / (8 * k + 4) - 
                1.0 / (8 * k + 5) - 
                1.0 / (8 * k + 6)
            );
            
            pi += term;
            sixteenPower *= 16.0;
            
            if (Math.abs(term) < 1e-15) {
                break;
            }
        }
        
        return pi;
    }

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        MathContext mc = new MathContext(precision + 10, RoundingMode.HALF_UP);
        
        BigDecimal pi = BigDecimal.ZERO;
        BigDecimal sixteen = new BigDecimal("16");
        BigDecimal sixteenPower = BigDecimal.ONE;
        
        BigDecimal epsilon = BigDecimal.ONE.divide(BigDecimal.TEN.pow(precision + 5), mc);
        
        for (int k = 0; k < precision * 2; k++) {
            BigDecimal eightK = new BigDecimal(8L * k);
            
            BigDecimal term1 = new BigDecimal("4").divide(eightK.add(BigDecimal.ONE), mc);
            BigDecimal term2 = new BigDecimal("2").divide(eightK.add(new BigDecimal("4")), mc);
            BigDecimal term3 = BigDecimal.ONE.divide(eightK.add(new BigDecimal("5")), mc);
            BigDecimal term4 = BigDecimal.ONE.divide(eightK.add(new BigDecimal("6")), mc);
            
            BigDecimal sumTerms = term1.subtract(term2, mc).subtract(term3, mc).subtract(term4, mc);
            BigDecimal term = sumTerms.divide(sixteenPower, mc);
            
            pi = pi.add(term, mc);
            
            if (term.abs().compareTo(epsilon) < 0) {
                break;
            }
            
            sixteenPower = sixteenPower.multiply(sixteen, mc);
        }
        
        return pi.setScale(precision, RoundingMode.HALF_UP);
    }
}