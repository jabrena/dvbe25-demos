package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Implementation of the Chudnovsky Algorithm for Pi calculation.
 * This is one of the fastest known algorithms for calculating Pi.
 * Uses the formula discovered by David and Gregory Chudnovsky in 1988.
 */
public class ChudnovskyAlgorithm implements PiCalculator, HighPrecisionPiCalculator {

    @Override
    public double calculatePi() {
        return Double.parseDouble(calculatePiHighPrecision(15));
    }

    @Override
    public String calculatePiHighPrecision(int precision) {
        MathContext mc = new MathContext(precision + 10, RoundingMode.HALF_UP);
        
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal c = new BigDecimal("426880").multiply(sqrt(new BigDecimal("10005"), mc), mc);
        
        // Calculate sufficient iterations for desired precision
        int iterations = precision / 14 + 2;
        
        for (int k = 0; k < iterations; k++) {
            BigDecimal numerator = factorial(6 * k).multiply(
                new BigDecimal("545140134").multiply(new BigDecimal(k)).add(new BigDecimal("13591409"))
            );
            
            BigDecimal denominator = factorial(3 * k).multiply(factorial(k).pow(3)).multiply(
                new BigDecimal("-262537412640768000").pow(k)
            );
            
            sum = sum.add(numerator.divide(denominator, mc), mc);
        }
        
        BigDecimal pi = c.divide(sum, mc);
        return pi.setScale(precision, RoundingMode.HALF_UP).toString();
    }
    
    private BigDecimal factorial(int n) {
        BigDecimal result = BigDecimal.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(new BigDecimal(i));
        }
        return result;
    }
    
    private BigDecimal sqrt(BigDecimal value, MathContext mc) {
        BigDecimal x = new BigDecimal("1");
        BigDecimal last;
        
        do {
            last = x;
            x = value.divide(x, mc).add(x).divide(new BigDecimal("2"), mc);
        } while (x.subtract(last).abs().compareTo(new BigDecimal("1e-" + (mc.getPrecision() - 1))) > 0);
        
        return x;
    }
}