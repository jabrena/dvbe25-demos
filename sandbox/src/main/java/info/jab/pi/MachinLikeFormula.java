package info.jab.pi;

/**
 * Implementation of Machin-like Formula for Pi calculation.
 * Uses the formula: π/4 = 4*arctan(1/5) - arctan(1/239)
 * This is John Machin's formula from 1706.
 */
public class MachinLikeFormula implements PiCalculator {

    @Override
    public double calculatePi() {
        // Machin's formula: π/4 = 4*arctan(1/5) - arctan(1/239)
        // Therefore: π = 16*arctan(1/5) - 4*arctan(1/239)
        return 16.0 * arctan(1.0/5.0) - 4.0 * arctan(1.0/239.0);
    }
    
    /**
     * Calculate arctan using Taylor series expansion:
     * arctan(x) = x - x³/3 + x⁵/5 - x⁷/7 + ...
     */
    private double arctan(double x) {
        double result = 0.0;
        double term = x;
        int n = 1;
        
        // Continue until term becomes negligibly small
        while (Math.abs(term) > 1e-15) {
            result += term / n;
            term *= -x * x;
            n += 2;
        }
        
        return result;
    }
}