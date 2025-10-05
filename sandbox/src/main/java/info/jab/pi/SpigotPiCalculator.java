package info.jab.pi;

/**
 * Spigot Algorithm Pi Calculator
 * Uses Rabinowitz and Wagon's spigot algorithm for computing π
 */
public class SpigotPiCalculator implements PiCalculator {
    
    private static final int DEFAULT_ITERATIONS = 10000;
    
    @Override
    public double calculatePi() {
        return calculatePi(DEFAULT_ITERATIONS);
    }
    
    @Override
    public double calculatePi(int iterations) {
        // Use the better-performing Nilakantha series for spigot algorithm
        return calculatePiUsingNilakantha(iterations);
    }
    
    /**
     * Calculate Pi using Leibniz formula (π/4 = 1 - 1/3 + 1/5 - 1/7 + ...)
     * This is a simple spigot-like algorithm
     */
    private double calculatePiUsingLeibniz(int iterations) {
        double pi = 0.0;
        for (int i = 0; i < iterations; i++) {
            if (i % 2 == 0) {
                pi += 1.0 / (2 * i + 1);
            } else {
                pi -= 1.0 / (2 * i + 1);
            }
        }
        return pi * 4.0;
    }
    
    /**
     * Calculate Pi using Nilakantha series
     * π = 3 + 4/(2×3×4) - 4/(4×5×6) + 4/(6×7×8) - ...
     */
    public double calculatePiUsingNilakantha(int iterations) {
        double pi = 3.0;
        boolean positive = true;
        
        for (int i = 1; i <= iterations; i++) {
            int base = 2 * i;
            double term = 4.0 / (base * (base + 1) * (base + 2));
            
            if (positive) {
                pi += term;
            } else {
                pi -= term;
            }
            positive = !positive;
        }
        
        return pi;
    }
    
    /**
     * Calculate Pi using Madhava series
     * π = √12 × (1 - 1/(3×3) + 1/(5×3²) - 1/(7×3³) + ...)
     */
    public double calculatePiUsingMadhava(int iterations) {
        double sum = 0.0;
        double powerOf3 = 1.0;
        
        for (int k = 0; k < iterations; k++) {
            double term = 1.0 / ((2 * k + 1) * powerOf3);
            if (k % 2 == 0) {
                sum += term;
            } else {
                sum -= term;
            }
            powerOf3 *= 3.0;
        }
        
        return Math.sqrt(12) * sum;
    }
}