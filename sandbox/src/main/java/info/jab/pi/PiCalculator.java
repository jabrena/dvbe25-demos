package info.jab.pi;

/**
 * Base interface for Pi calculation algorithms
 */
public interface PiCalculator {
    
    /**
     * Calculate Pi using the default number of iterations
     * @return Pi value as double
     */
    double calculatePi();
    
    /**
     * Calculate Pi using specified number of iterations
     * @param iterations number of iterations for the algorithm
     * @return Pi value as double
     */
    double calculatePi(int iterations);
}