package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Implementation of the Bailey-Borwein-Plouffe (BBP) Formula for Pi calculation.
 * This formula allows computation of hexadecimal digits of Pi without 
 * computing preceding digits.
 * 
 * Formula: π = Σ(k=0 to ∞) [1/16^k * (4/(8k+1) - 2/(8k+4) - 1/(8k+5) - 1/(8k+6))]
 */
public class BaileyBorweinPlouffeFormula implements PiCalculator, HighPrecisionPiCalculator {

    @Override
    public double calculatePi() {
        double pi = 0.0;
        double term;
        int k = 0;
        
        do {
            term = (1.0 / Math.pow(16, k)) * (
                4.0 / (8 * k + 1) -
                2.0 / (8 * k + 4) -
                1.0 / (8 * k + 5) -
                1.0 / (8 * k + 6)
            );
            pi += term;
            k++;
        } while (Math.abs(term) > 1e-15);
        
        return pi;
    }

    @Override
    public String calculatePiHighPrecision(int precision) {
        MathContext mc = new MathContext(precision + 10, RoundingMode.HALF_UP);
        
        BigDecimal pi = BigDecimal.ZERO;
        BigDecimal sixteen = new BigDecimal("16");
        BigDecimal four = new BigDecimal("4");
        BigDecimal two = new BigDecimal("2");
        BigDecimal one = BigDecimal.ONE;
        
        // Calculate sufficient iterations for desired precision
        int iterations = (int)(precision * 0.8) + 10;
        
        for (int k = 0; k < iterations; k++) {
            BigDecimal k_bd = new BigDecimal(k);
            BigDecimal eight_k = new BigDecimal(8).multiply(k_bd);
            
            BigDecimal term1 = four.divide(eight_k.add(one), mc);
            BigDecimal term2 = two.divide(eight_k.add(four), mc);
            BigDecimal term3 = one.divide(eight_k.add(new BigDecimal("5")), mc);
            BigDecimal term4 = one.divide(eight_k.add(new BigDecimal("6")), mc);
            
            BigDecimal bracket = term1.subtract(term2).subtract(term3).subtract(term4);
            BigDecimal sixteen_power_k = sixteen.pow(k).setScale(mc.getPrecision(), mc.getRoundingMode());
            
            BigDecimal term = bracket.divide(sixteen_power_k, mc);
            pi = pi.add(term, mc);
        }
        
        return pi.setScale(precision, RoundingMode.HALF_UP).toString();
    }
}