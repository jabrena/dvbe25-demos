package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Implementation of Pi calculation using the Chudnovsky Algorithm.
 * This is one of the fastest known algorithms for calculating Pi.
 * Uses the series discovered by David and Gregory Chudnovsky.
 */
public class ChudnovskyAlgorithmPiCalculator implements HighPrecisionPiCalculator {

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        // Set math context with extra precision for intermediate calculations
        int workingPrecision = precision + 20;
        MathContext mc = new MathContext(workingPrecision, RoundingMode.HALF_UP);
        
        // Chudnovsky algorithm constants
        BigDecimal C = new BigDecimal("426880").multiply(sqrt(new BigDecimal("10005"), mc), mc);
        
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal numeratorFactor = BigDecimal.ONE;
        BigDecimal denominatorFactor = BigDecimal.ONE;
        
        // Calculate the series until convergence
        for (int k = 0; k < precision / 14 + 5; k++) { // Each term gives ~14.18 decimal digits
            // Calculate (6k)! 
            BigDecimal factorial6k = factorial(6 * k, mc);
            
            // Calculate (3k)!
            BigDecimal factorial3k = factorial(3 * k, mc);
            
            // Calculate k!
            BigDecimal factorialK = factorial(k, mc);
            
            // Calculate the numerator: (6k)! * (13591409 + 545140134*k)
            BigDecimal numerator = factorial6k.multiply(
                new BigDecimal("13591409").add(new BigDecimal("545140134").multiply(new BigDecimal(k), mc), mc), mc);
            
            // Calculate the denominator: (3k)! * (k!)^3 * (-262537412640768000)^k
            BigDecimal denominator = factorial3k.multiply(factorialK.pow(3, mc), mc);
            BigDecimal base = new BigDecimal("-262537412640768000");
            if (k > 0) {
                denominator = denominator.multiply(base.pow(k, mc), mc);
            }
            
            // Add term to sum
            BigDecimal term = numerator.divide(denominator, mc);
            sum = sum.add(term, mc);
            
            // Check for convergence
            if (term.abs().compareTo(BigDecimal.ONE.divide(BigDecimal.TEN.pow(workingPrecision - 5), mc)) < 0) {
                break;
            }
        }
        
        // Calculate Pi = C / sum
        BigDecimal pi = C.divide(sum, mc);
        
        // Round to requested precision
        return pi.setScale(precision, RoundingMode.HALF_UP);
    }

    /**
     * Calculate factorial of n using BigDecimal.
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
     * Calculate square root using Newton's method.
     */
    private BigDecimal sqrt(BigDecimal x, MathContext mc) {
        if (x.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal guess = x.divide(new BigDecimal("2"), mc);
        BigDecimal prevGuess;
        
        do {
            prevGuess = guess;
            guess = guess.add(x.divide(guess, mc), mc).divide(new BigDecimal("2"), mc);
        } while (guess.subtract(prevGuess, mc).abs().compareTo(BigDecimal.ONE.divide(BigDecimal.TEN.pow(mc.getPrecision() - 5), mc)) > 0);
        
        return guess;
    }
}