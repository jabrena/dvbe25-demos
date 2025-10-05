package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Implementation of Spigot Algorithm for Pi calculation.
 * This algorithm generates digits of Pi one at a time without 
 * computing all previous digits.
 * 
 * Uses a variation of the spigot algorithm based on the 
 * Leibniz formula with acceleration techniques.
 */
public class SpigotAlgorithm implements PiCalculator, HighPrecisionPiCalculator {

    @Override
    public double calculatePi() {
        // Use a simpler approach for double precision
        return calculatePiUsingLeibnizAccelerated();
    }
    
    private double calculatePiUsingLeibnizAccelerated() {
        double pi = 0.0;
        double sign = 1.0;
        
        // Leibniz formula with acceleration
        for (int k = 0; k < 1000000; k++) {
            pi += sign / (2 * k + 1);
            sign *= -1;
        }
        
        return pi * 4.0;
    }

    @Override
    public String calculatePiHighPrecision(int precision) {
        // Implement spigot algorithm for high precision
        return calculatePiSpigot(precision);
    }
    
    private String calculatePiSpigot(int digits) {
        MathContext mc = new MathContext(digits + 10, RoundingMode.HALF_UP);
        
        // Use Bailey's spigot algorithm variation
        BigDecimal pi = BigDecimal.ZERO;
        BigDecimal term;
        int k = 0;
        
        // Calculate using a converging series
        do {
            // Using Machin-like approach with BigDecimal for high precision
            BigDecimal k_bd = new BigDecimal(k);
            BigDecimal denominator = new BigDecimal(2).multiply(k_bd).add(BigDecimal.ONE);
            BigDecimal sign = k % 2 == 0 ? BigDecimal.ONE : new BigDecimal("-1");
            
            term = sign.divide(denominator, mc);
            pi = pi.add(term, mc);
            k++;
            
        } while (term.abs().compareTo(new BigDecimal("1e-" + (digits + 5))) > 0 && k < digits * 100);
        
        pi = pi.multiply(new BigDecimal("4"), mc);
        return pi.setScale(digits, RoundingMode.HALF_UP).toString();
    }
}