package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Pi calculator using the Chudnovsky algorithm
 */
public class ChudnovskyPiCalculator implements HighPrecisionPiCalculator {

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        MathContext mc = new MathContext(precision + 20, RoundingMode.HALF_UP);
        
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal constant = new BigDecimal("426880").multiply(sqrt(new BigDecimal("10005"), mc), mc);
        
        for (int k = 0; k < precision / 14 + 10; k++) {
            BigDecimal numerator = factorial(6 * k, mc)
                    .multiply(new BigDecimal("13591409").add(new BigDecimal("545140134").multiply(new BigDecimal(k), mc), mc), mc);
            
            BigDecimal denominator = factorial(3 * k, mc)
                    .multiply(factorial(k, mc).pow(3), mc)
                    .multiply(new BigDecimal("640320").pow(3 * k), mc);
            
            BigDecimal term = numerator.divide(denominator, mc);
            
            if (k % 2 == 1) {
                term = term.negate();
            }
            
            sum = sum.add(term, mc);
            
            // Check for convergence
            if (term.abs().compareTo(new BigDecimal("1E-" + (precision + 10))) < 0 && k > 0) {
                break;
            }
        }
        
        BigDecimal pi = constant.divide(sum, mc);
        
        return pi.setScale(precision, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate factorial using BigDecimal
     */
    private BigDecimal factorial(int n, MathContext mc) {
        if (n <= 1) return BigDecimal.ONE;
        
        BigDecimal result = BigDecimal.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(new BigDecimal(i), mc);
        }
        return result;
    }
    
    /**
     * Calculate square root using Newton's method
     */
    private BigDecimal sqrt(BigDecimal n, MathContext mc) {
        if (n.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal x = n;
        BigDecimal lastX;
        
        do {
            lastX = x;
            x = x.add(n.divide(x, mc)).divide(new BigDecimal("2"), mc);
        } while (x.subtract(lastX).abs().compareTo(new BigDecimal("1E-" + (mc.getPrecision() - 5))) > 0);
        
        return x;
    }
}