package info.jab.pi;

/**
 * Interface for Pi calculation algorithms that provide standard precision results.
 */
public interface PiCalculator {
    /**
     * Calculate the value of Pi with standard double precision.
     * 
     * @return the calculated value of Pi as a double
     */
    double calculatePi();
}