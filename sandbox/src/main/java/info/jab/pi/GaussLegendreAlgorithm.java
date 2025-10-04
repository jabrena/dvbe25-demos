package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Implementation of the Gauss-Legendre Algorithm for calculating Pi.
 * 
 * This algorithm has quadratic convergence, meaning that each iteration
 * roughly doubles the number of correct digits. It's based on the 
 * arithmetic-geometric mean and was historically important for computing
 * high-precision values of π.
 * 
 * The algorithm converges to π very rapidly but requires more memory
 * than some other methods due to the need to maintain several variables.
 */
public class GaussLegendreAlgorithm {
    
    private static final int DEFAULT_ITERATIONS = 10;
    
    /**
     * Calculate Pi using default number of iterations.
     * @return Pi as a double value
     */
    public double calculatePi() {
        return calculatePi(DEFAULT_ITERATIONS);
    }
    
    /**
     * Calculate Pi with specified number of iterations.
     * @param iterations Number of iterations to perform
     * @return Pi as a double value
     */
    public double calculatePi(int iterations) {
        MathContext precision = new MathContext(50);
        BigDecimal pi = calculatePi(precision, iterations);
        return pi.doubleValue();
    }
    
    /**
     * Calculate Pi using the Gauss-Legendre algorithm with high precision.
     * 
     * Algorithm:
     * 1. Initialize: a₀ = 1, b₀ = 1/√2, t₀ = 1/4, p₀ = 1
     * 2. Iterate: aₙ₊₁ = (aₙ + bₙ)/2
     *            bₙ₊₁ = √(aₙ × bₙ)
     *            tₙ₊₁ = tₙ - pₙ(aₙ - aₙ₊₁)²
     *            pₙ₊₁ = 2pₙ
     * 3. π ≈ (aₙ + bₙ)² / (4tₙ)
     * 
     * @param precision The precision context
     * @param iterations Number of iterations
     * @return Pi as a BigDecimal
     */
    public BigDecimal calculatePi(MathContext precision, int iterations) {
        // Initialize variables
        BigDecimal a = BigDecimal.ONE;
        BigDecimal b = BigDecimal.ONE.divide(sqrt(BigDecimal.valueOf(2), precision), precision);
        BigDecimal t = BigDecimal.valueOf(0.25);
        BigDecimal p = BigDecimal.ONE;
        
        for (int i = 0; i < iterations; i++) {
            BigDecimal aNext = a.add(b, precision).divide(BigDecimal.valueOf(2), precision);
            BigDecimal bNext = sqrt(a.multiply(b, precision), precision);
            
            BigDecimal aDiff = a.subtract(aNext, precision);
            BigDecimal tNext = t.subtract(p.multiply(aDiff.multiply(aDiff, precision), precision), precision);
            BigDecimal pNext = p.multiply(BigDecimal.valueOf(2), precision);
            
            a = aNext;
            b = bNext;
            t = tNext;
            p = pNext;
        }
        
        // Calculate π = (a + b)² / (4t)
        BigDecimal numerator = a.add(b, precision).pow(2, precision);
        BigDecimal denominator = BigDecimal.valueOf(4).multiply(t, precision);
        
        return numerator.divide(denominator, precision);
    }
    
    /**
     * Calculate the arithmetic-geometric mean of two numbers.
     * This is used as a helper function in the Gauss-Legendre algorithm.
     * 
     * @param a First number
     * @param b Second number
     * @param precision The precision context
     * @return The arithmetic-geometric mean
     */
    public BigDecimal arithmeticGeometricMean(BigDecimal a, BigDecimal b, MathContext precision) {
        BigDecimal currentA = a;
        BigDecimal currentB = b;
        
        while (currentA.subtract(currentB, precision).abs().compareTo(
                BigDecimal.valueOf(Math.pow(10, -precision.getPrecision()))) > 0) {
            
            BigDecimal nextA = currentA.add(currentB, precision).divide(BigDecimal.valueOf(2), precision);
            BigDecimal nextB = sqrt(currentA.multiply(currentB, precision), precision);
            
            currentA = nextA;
            currentB = nextB;
        }
        
        return currentA;
    }
    
    /**
     * Calculate square root using Newton's method.
     * This provides high-precision square root calculation needed for the algorithm.
     * 
     * @param value The value to calculate square root of
     * @param precision The precision context
     * @return Square root of the value
     */
    public BigDecimal sqrt(BigDecimal value, MathContext precision) {
        if (value.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }
        
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new ArithmeticException("Cannot calculate square root of negative number");
        }
        
        // Initial guess
        BigDecimal x = value.divide(BigDecimal.valueOf(2), precision);
        BigDecimal previousX;
        
        int maxIterations = precision.getPrecision() * 2; // Prevent infinite loops
        int iterations = 0;
        
        do {
            previousX = x;
            // Newton's method: x_{n+1} = (x_n + value/x_n) / 2
            x = x.add(value.divide(x, precision), precision)
                 .divide(BigDecimal.valueOf(2), precision);
            iterations++;
        } while (x.subtract(previousX).abs().compareTo(
                BigDecimal.valueOf(Math.pow(10, -precision.getPrecision()))) > 0 && 
                iterations < maxIterations);
        
        return x;
    }
}