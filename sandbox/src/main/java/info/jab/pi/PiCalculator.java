package info.jab.pi;

/**
 * Interface for Pi calculation implementations.
 * All Pi calculation algorithms must implement this interface.
 */
public interface PiCalculator {
    
    /**
     * Calculates the value of Pi using the specific algorithm implementation.
     * 
     * @return The calculated value of Pi as a double
     */
    double calculatePi();
}