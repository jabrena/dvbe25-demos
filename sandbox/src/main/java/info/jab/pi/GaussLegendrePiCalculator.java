package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Pi calculation using Gauss-Legendre algorithm
 * This algorithm has quadratic convergence
 */
public class GaussLegendrePiCalculator implements HighPrecisionPiCalculator {
    
    @Override
    public double calculatePi() {
        double a = 1.0;
        double b = 1.0 / Math.sqrt(2.0);
        double t = 0.25;
        double p = 1.0;
        
        for (int i = 0; i < 10; i++) {
            double aNext = (a + b) / 2.0;
            double bNext = Math.sqrt(a * b);
            double tNext = t - p * (a - aNext) * (a - aNext);
            double pNext = 2.0 * p;
            
            a = aNext;
            b = bNext;
            t = tNext;
            p = pNext;
        }
        
        return (a + b) * (a + b) / (4.0 * t);
    }
    
    @Override
    public String calculatePiHighPrecision(int precision) {
        MathContext mc = new MathContext(precision + 10, RoundingMode.HALF_UP);
        
        BigDecimal a = BigDecimal.ONE;
        BigDecimal b = BigDecimal.ONE.divide(sqrt(new BigDecimal("2"), mc), mc);
        BigDecimal t = new BigDecimal("0.25");
        BigDecimal p = BigDecimal.ONE;
        
        for (int i = 0; i < precision / 10 + 5; i++) {
            BigDecimal aNext = a.add(b, mc).divide(new BigDecimal("2"), mc);
            BigDecimal bNext = sqrt(a.multiply(b, mc), mc);
            BigDecimal diff = a.subtract(aNext, mc);
            BigDecimal tNext = t.subtract(p.multiply(diff.multiply(diff, mc), mc), mc);
            BigDecimal pNext = p.multiply(new BigDecimal("2"), mc);
            
            BigDecimal prevA = a;
            a = aNext;
            b = bNext;
            t = tNext;
            p = pNext;
            
            // Check for convergence
            if (prevA.subtract(a, mc).abs().compareTo(new BigDecimal("1E-" + (precision + 5))) < 0) {
                break;
            }
        }
        
        BigDecimal numerator = a.add(b, mc).pow(2, mc);
        BigDecimal denominator = new BigDecimal("4").multiply(t, mc);
        BigDecimal pi = numerator.divide(denominator, mc);
        
        return pi.setScale(precision, RoundingMode.HALF_UP).toPlainString();
    }
    
    private BigDecimal sqrt(BigDecimal value, MathContext mc) {
        BigDecimal x = value;
        BigDecimal previous;
        
        do {
            previous = x;
            x = x.add(value.divide(x, mc), mc).divide(new BigDecimal("2"), mc);
        } while (x.subtract(previous, mc).abs().compareTo(new BigDecimal("1E-" + mc.getPrecision())) > 0);
        
        return x;
    }
}