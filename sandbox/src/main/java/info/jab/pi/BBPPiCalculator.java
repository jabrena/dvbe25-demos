package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Implementation of Pi calculation using the Bailey-Borwein-Plouffe (BBP) Formula.
 * This formula allows for the computation of hexadecimal digits of π without 
 * needing to compute the preceding digits.
 * Formula: π = Σ(k=0 to ∞) [1/16^k * (4/(8k+1) - 2/(8k+4) - 1/(8k+5) - 1/(8k+6))]
 */
public class BBPPiCalculator implements HighPrecisionPiCalculator {

    @Override
    public double calculatePi() {
        double pi = 0.0;
        double pow16 = 1.0;
        
        for (int k = 0; k < 500; k++) {
            double term = (1.0 / pow16) * 
                (4.0 / (8 * k + 1) - 2.0 / (8 * k + 4) - 1.0 / (8 * k + 5) - 1.0 / (8 * k + 6));
            
            pi += term;
            pow16 *= 16.0;
            
            if (Math.abs(term) < 1e-15) {
                break;
            }
        }
        
        return pi;
    }

    @Override
    public String calculatePiHighPrecision(int decimalPlaces) {
        MathContext mc = new MathContext(decimalPlaces + 10, RoundingMode.HALF_UP);
        
        BigDecimal pi = BigDecimal.ZERO;
        BigDecimal sixteen = new BigDecimal("16");
        BigDecimal pow16 = BigDecimal.ONE;
        
        // Calculate sufficient terms for desired precision
        int maxTerms = decimalPlaces / 2 + 50; // BBP converges relatively quickly
        
        for (int k = 0; k < maxTerms; k++) {
            BigDecimal k8 = new BigDecimal(8 * k);
            
            BigDecimal term1 = new BigDecimal("4").divide(k8.add(BigDecimal.ONE), mc);
            BigDecimal term2 = new BigDecimal("2").divide(k8.add(new BigDecimal("4")), mc);
            BigDecimal term3 = BigDecimal.ONE.divide(k8.add(new BigDecimal("5")), mc);
            BigDecimal term4 = BigDecimal.ONE.divide(k8.add(new BigDecimal("6")), mc);
            
            BigDecimal innerSum = term1.subtract(term2, mc).subtract(term3, mc).subtract(term4, mc);
            BigDecimal term = innerSum.divide(pow16, mc);
            
            pi = pi.add(term, mc);
            pow16 = pow16.multiply(sixteen, mc);
            
            // Check for convergence
            if (term.abs().compareTo(new BigDecimal("1E-" + (decimalPlaces + 5))) < 0) {
                break;
            }
        }
        
        return pi.setScale(decimalPlaces, RoundingMode.HALF_UP).toString();
    }
}