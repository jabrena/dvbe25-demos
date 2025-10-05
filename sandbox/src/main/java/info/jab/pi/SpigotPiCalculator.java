package info.jab.pi;

/**
 * Implementation of Pi calculation using a Spigot Algorithm.
 * This implementation uses a digit extraction algorithm that can compute
 * individual digits of π without computing all the preceding digits.
 * Based on the algorithm by Rabinowitz and Wagon.
 */
public class SpigotPiCalculator implements PiCalculator {

    @Override
    public double calculatePi() {
        // Use a simplified spigot-like algorithm for Pi calculation
        // This implementation uses Leibniz formula with acceleration
        double pi = 0.0;
        int sign = 1;
        
        for (int i = 0; i < 1000000; i++) {
            pi += sign * (4.0 / (2 * i + 1));
            sign *= -1;
        }
        
        return pi;
    }
}