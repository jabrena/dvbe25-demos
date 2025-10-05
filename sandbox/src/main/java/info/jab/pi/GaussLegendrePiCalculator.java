package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Gauss-Legendre Algorithm Pi Calculator
 * Uses the arithmetic-geometric mean iteration to compute π
 */
public class GaussLegendrePiCalculator implements HighPrecisionPiCalculator {
    
    private static final int DEFAULT_ITERATIONS = 10;
    
    @Override
    public double calculatePi() {
        return calculatePi(DEFAULT_ITERATIONS);
    }
    
    @Override
    public double calculatePi(int iterations) {
        double a = 1.0;
        double b = 1.0 / Math.sqrt(2.0);
        double t = 0.25;
        double p = 1.0;
        
        for (int i = 0; i < iterations; i++) {
            double a_next = (a + b) / 2.0;
            double b_next = Math.sqrt(a * b);
            double t_next = t - p * Math.pow(a - a_next, 2);
            double p_next = 2.0 * p;
            
            a = a_next;
            b = b_next;
            t = t_next;
            p = p_next;
        }
        
        return Math.pow(a + b, 2) / (4.0 * t);
    }
    
    @Override
    public String calculatePiHighPrecision(int precision) {
        MathContext mc = new MathContext(precision + 10, RoundingMode.HALF_UP);
        
        BigDecimal a = BigDecimal.ONE;
        BigDecimal b = BigDecimal.ONE.divide(sqrt(new BigDecimal(2), mc), mc);
        BigDecimal t = new BigDecimal(0.25, mc);
        BigDecimal p = BigDecimal.ONE;
        
        int iterations = Math.max(precision / 50 + 5, 10); // Gauss-Legendre converges quadratically
        
        for (int i = 0; i < iterations; i++) {
            BigDecimal a_next = a.add(b, mc).divide(new BigDecimal(2), mc);
            BigDecimal b_next = sqrt(a.multiply(b, mc), mc);
            BigDecimal diff = a.subtract(a_next, mc);
            BigDecimal t_next = t.subtract(p.multiply(diff.multiply(diff, mc), mc), mc);
            BigDecimal p_next = p.multiply(new BigDecimal(2), mc);
            
            a = a_next;
            b = b_next;
            t = t_next;
            p = p_next;
        }
        
        BigDecimal sum = a.add(b, mc);
        BigDecimal pi = sum.multiply(sum, mc).divide(new BigDecimal(4).multiply(t, mc), mc);
        
        return pi.setScale(precision, RoundingMode.HALF_UP).toString();
    }
    
    private BigDecimal sqrt(BigDecimal value, MathContext mc) {
        if (value.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal x = value;
        BigDecimal lastX = BigDecimal.ZERO;
        BigDecimal two = new BigDecimal(2);
        
        int maxIterations = mc.getPrecision() + 5;
        for (int i = 0; i < maxIterations && !x.equals(lastX); i++) {
            lastX = x;
            x = x.add(value.divide(x, mc)).divide(two, mc);
        }
        
        return x;
    }
}