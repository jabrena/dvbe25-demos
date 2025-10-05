package info.jab.pi;

/**
 * Implementation of Pi calculation using the Gauss-Legendre Algorithm.
 * This algorithm has quadratic convergence, meaning it roughly doubles 
 * the number of correct digits with each iteration.
 */
public class GaussLegendrePiCalculator implements PiCalculator {

    @Override
    public double calculatePi() {
        double a = 1.0;
        double b = 1.0 / Math.sqrt(2.0);
        double t = 1.0 / 4.0;
        double p = 1.0;
        
        // Iterate until convergence
        for (int i = 0; i < 10; i++) {
            double aNext = (a + b) / 2.0;
            double bNext = Math.sqrt(a * b);
            double tNext = t - p * (a - aNext) * (a - aNext);
            double pNext = 2.0 * p;
            
            a = aNext;
            b = bNext;
            t = tNext;
            p = pNext;
            
            // Check for convergence
            if (Math.abs(a - b) < 1e-15) {
                break;
            }
        }
        
        return (a + b) * (a + b) / (4.0 * t);
    }
}