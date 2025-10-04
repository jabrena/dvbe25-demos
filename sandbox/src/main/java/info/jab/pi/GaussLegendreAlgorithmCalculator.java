package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Implements Pi calculation using the Gauss-Legendre Algorithm.
 * This is an iterative algorithm that converges quadratically to π.
 * Each iteration approximately doubles the number of correct digits.
 */
public class GaussLegendreAlgorithmCalculator implements HighPrecisionPiCalculator {

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
    public BigDecimal calculatePiHighPrecision(int precision) {
        MathContext mc = new MathContext(precision + 10, RoundingMode.HALF_UP);
        
        BigDecimal a = BigDecimal.ONE;
        BigDecimal b = BigDecimal.ONE.divide(sqrt(new BigDecimal("2"), mc), mc);
        BigDecimal t = new BigDecimal("0.25");
        BigDecimal p = BigDecimal.ONE;
        
        int iterations = (int) Math.ceil(Math.log(precision) / Math.log(2)) + 5;
        
        for (int i = 0; i < iterations; i++) {
            BigDecimal aNext = a.add(b, mc).divide(new BigDecimal("2"), mc);
            BigDecimal bNext = sqrt(a.multiply(b, mc), mc);
            BigDecimal diff = a.subtract(aNext, mc);
            BigDecimal tNext = t.subtract(p.multiply(diff.multiply(diff, mc), mc), mc);
            BigDecimal pNext = p.multiply(new BigDecimal("2"), mc);
            
            a = aNext;
            b = bNext;
            t = tNext;
            p = pNext;
        }
        
        BigDecimal sum = a.add(b, mc);
        BigDecimal result = sum.multiply(sum, mc).divide(new BigDecimal("4").multiply(t, mc), mc);
        
        return result.setScale(precision, RoundingMode.HALF_UP);
    }

    private BigDecimal sqrt(BigDecimal value, MathContext mc) {
        BigDecimal x = value;
        BigDecimal two = new BigDecimal("2");
        
        for (int i = 0; i < mc.getPrecision(); i++) {
            BigDecimal newX = x.add(value.divide(x, mc), mc).divide(two, mc);
            if (newX.subtract(x, mc).abs().compareTo(BigDecimal.ONE.divide(BigDecimal.TEN.pow(mc.getPrecision() - 5), mc)) < 0) {
                break;
            }
            x = newX;
        }
        
        return x;
    }
}