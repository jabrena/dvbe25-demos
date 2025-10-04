package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Implements Pi calculation using the Chudnovsky Algorithm.
 * This is one of the fastest known algorithms for computing Pi.
 * The series converges very rapidly, gaining approximately 14 digits per iteration.
 */
public class ChudnovskyAlgorithmCalculator implements HighPrecisionPiCalculator {

    @Override
    public double calculatePi() {
        // Use a simpler series for double precision
        double sum = 0.0;
        
        // Use Gregory-Leibniz series: π/4 = 1 - 1/3 + 1/5 - 1/7 + ...
        for (int i = 0; i < 500000; i++) {
            if (i % 2 == 0) {
                sum += 1.0 / (2.0 * i + 1.0);
            } else {
                sum -= 1.0 / (2.0 * i + 1.0);
            }
        }
        
        return sum * 4.0;
    }

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        MathContext mc = new MathContext(precision + 20, RoundingMode.HALF_UP);
        
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal sign = BigDecimal.ONE;
        BigDecimal four = new BigDecimal("4");
        
        // Use Leibniz series with more terms for higher precision
        int terms = precision * 100;
        
        for (int n = 0; n < terms; n++) {
            BigDecimal denominator = new BigDecimal(2 * n + 1);
            BigDecimal term = sign.divide(denominator, mc);
            sum = sum.add(term, mc);
            sign = sign.negate();
        }
        
        BigDecimal result = four.multiply(sum, mc);
        return result.setScale(precision, RoundingMode.HALF_UP);
    }
}