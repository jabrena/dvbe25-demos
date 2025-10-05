package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Pi calculation using Machin-like formula.
 * Uses the formula: Pi/4 = 4*arctan(1/5) - arctan(1/239)
 * This is John Machin's famous formula from 1706.
 */
public class MachinLikePiCalculator implements HighPrecisionPiCalculator {

    @Override
    public double calculatePi() {
        // Using the basic Machin formula with double precision
        double arctan1_5 = arctan(1.0 / 5.0, 50);
        double arctan1_239 = arctan(1.0 / 239.0, 50);
        
        return 4.0 * (4.0 * arctan1_5 - arctan1_239);
    }

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        // Set precision with extra digits for intermediate calculations
        int workingPrecision = precision + 20;
        MathContext mc = new MathContext(workingPrecision, RoundingMode.HALF_UP);
        
        // Calculate arctan(1/5) and arctan(1/239) with high precision
        BigDecimal five = new BigDecimal("5", mc);
        BigDecimal twoThreeNine = new BigDecimal("239", mc);
        
        BigDecimal arctan1_5 = arctanHighPrecision(BigDecimal.ONE.divide(five, mc), mc);
        BigDecimal arctan1_239 = arctanHighPrecision(BigDecimal.ONE.divide(twoThreeNine, mc), mc);
        
        // Pi/4 = 4*arctan(1/5) - arctan(1/239)
        BigDecimal four = new BigDecimal("4", mc);
        BigDecimal piOver4 = four.multiply(arctan1_5, mc).subtract(arctan1_239, mc);
        
        // Pi = 4 * (Pi/4)
        BigDecimal result = four.multiply(piOver4, mc);
        
        // Return with requested precision
        return result.setScale(precision, RoundingMode.HALF_UP);
    }

    /**
     * Calculates arctan(x) using Taylor series for double precision.
     * arctan(x) = x - x³/3 + x⁵/5 - x⁷/7 + ...
     */
    private double arctan(double x, int terms) {
        double result = 0.0;
        double xSquared = x * x;
        double xPower = x;
        
        for (int n = 0; n < terms; n++) {
            int denominator = 2 * n + 1;
            double term = xPower / denominator;
            
            if (n % 2 == 0) {
                result += term;
            } else {
                result -= term;
            }
            
            xPower *= xSquared;
        }
        
        return result;
    }

    /**
     * Calculates arctan(x) using Taylor series for high precision.
     * arctan(x) = x - x³/3 + x⁵/5 - x⁷/7 + ...
     */
    private BigDecimal arctanHighPrecision(BigDecimal x, MathContext mc) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal xSquared = x.multiply(x, mc);
        BigDecimal xPower = x;
        BigDecimal tolerance = BigDecimal.ONE.divide(BigDecimal.TEN.pow(mc.getPrecision() - 5), mc);
        
        int n = 0;
        BigDecimal term;
        
        do {
            int denominator = 2 * n + 1;
            term = xPower.divide(new BigDecimal(denominator), mc);
            
            if (n % 2 == 0) {
                result = result.add(term, mc);
            } else {
                result = result.subtract(term, mc);
            }
            
            xPower = xPower.multiply(xSquared, mc);
            n++;
            
        } while (term.abs().compareTo(tolerance) > 0 && n < 10000); // Safety limit
        
        return result;
    }
}