package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Pi calculation using Bailey-Borwein-Plouffe (BBP) formula
 * π = Σ(k=0 to ∞) [1/16^k * (4/(8k+1) - 2/(8k+4) - 1/(8k+5) - 1/(8k+6))]
 */
public class BBPPiCalculator implements HighPrecisionPiCalculator {
    
    @Override
    public double calculatePi() {
        double pi = 0.0;
        
        for (int k = 0; k < 100; k++) {
            double term1 = 4.0 / (8 * k + 1);
            double term2 = 2.0 / (8 * k + 4);
            double term3 = 1.0 / (8 * k + 5);
            double term4 = 1.0 / (8 * k + 6);
            
            double sum = term1 - term2 - term3 - term4;
            double power16 = Math.pow(16, -k);
            
            pi += power16 * sum;
        }
        
        return pi;
    }
    
    @Override
    public String calculatePiHighPrecision(int precision) {
        MathContext mc = new MathContext(precision + 10, RoundingMode.HALF_UP);
        
        BigDecimal pi = BigDecimal.ZERO;
        BigDecimal sixteen = new BigDecimal("16");
        BigDecimal power16 = BigDecimal.ONE;
        
        for (int k = 0; k < precision * 2; k++) {
            BigDecimal k8 = new BigDecimal(8 * k);
            
            BigDecimal term1 = new BigDecimal("4").divide(k8.add(BigDecimal.ONE), mc);
            BigDecimal term2 = new BigDecimal("2").divide(k8.add(new BigDecimal("4")), mc);
            BigDecimal term3 = BigDecimal.ONE.divide(k8.add(new BigDecimal("5")), mc);
            BigDecimal term4 = BigDecimal.ONE.divide(k8.add(new BigDecimal("6")), mc);
            
            BigDecimal sum = term1.subtract(term2, mc).subtract(term3, mc).subtract(term4, mc);
            BigDecimal termContribution = power16.multiply(sum, mc);
            
            pi = pi.add(termContribution, mc);
            
            // Update power for next iteration
            if (k > 0) {
                power16 = power16.divide(sixteen, mc);
            } else {
                power16 = BigDecimal.ONE.divide(sixteen, mc);
            }
            
            // Check for convergence
            if (termContribution.abs().compareTo(new BigDecimal("1E-" + (precision + 5))) < 0) {
                break;
            }
        }
        
        return pi.setScale(precision, RoundingMode.HALF_UP).toPlainString();
    }
}