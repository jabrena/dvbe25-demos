package info.jab.pi;

/**
 * Functional interface for Pi calculation algorithms.
 * Represents a pure function that calculates Pi with standard double precision.
 */
@FunctionalInterface
public interface PiCalculator {
    
    /**
     * Calculates Pi using the implemented algorithm.
     * This should be a pure function with no side effects.
     * 
     * @return Pi value as a double with standard precision
     */
    double calculatePi();
}