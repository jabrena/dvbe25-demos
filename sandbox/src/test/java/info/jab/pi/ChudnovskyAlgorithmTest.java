package info.jab.pi;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Test class for Chudnovsky Algorithm Pi calculation.
 * The Chudnovsky algorithm is one of the fastest known algorithms for calculating π.
 */
public class ChudnovskyAlgorithmTest {
    
    private static final double EPSILON = 1e-6;
    
    @Test
    public void testCalculatePiWithDefaultIterations() {
        ChudnovskyAlgorithm calculator = new ChudnovskyAlgorithm();
        double pi = calculator.calculatePi();
        
        assertEquals(Math.PI, pi, EPSILON, "Pi calculation should be extremely close to Math.PI");
        assertTrue(pi > 3.141592, "Pi should be greater than 3.141592");
        assertTrue(pi < 3.141593, "Pi should be less than 3.141593");
    }
    
    @Test
    public void testCalculatePiWithSpecificIterations() {
        ChudnovskyAlgorithm calculator = new ChudnovskyAlgorithm();
        
        // Test with different iteration counts
        double pi1 = calculator.calculatePi(1);
        double pi2 = calculator.calculatePi(2);
        double pi5 = calculator.calculatePi(5);
        
        assertNotEquals(0.0, pi1, "Pi with 1 iteration should not be zero");
        assertNotEquals(0.0, pi2, "Pi with 2 iterations should not be zero");
        assertNotEquals(0.0, pi5, "Pi with 5 iterations should not be zero");
        
        // More iterations should give better accuracy
        assertTrue(Math.abs(pi5 - Math.PI) <= Math.abs(pi2 - Math.PI),
            "More iterations should provide better accuracy");
    }
    
    @Test
    public void testCalculatePiWithHighPrecision() {
        ChudnovskyAlgorithm calculator = new ChudnovskyAlgorithm();
        MathContext precision = new MathContext(100);
        
        BigDecimal pi = calculator.calculatePi(precision, 3);
        
        assertNotNull(pi, "High precision Pi calculation should not return null");
        assertTrue(pi.compareTo(BigDecimal.valueOf(3.14159)) > 0, 
            "Pi should be greater than 3.14159");
        assertTrue(pi.compareTo(BigDecimal.valueOf(3.14160)) < 0, 
            "Pi should be less than 3.14160");
    }
    
    @Test
    public void testFactorialFunction() {
        ChudnovskyAlgorithm calculator = new ChudnovskyAlgorithm();
        
        BigDecimal fact0 = calculator.factorial(0);
        BigDecimal fact1 = calculator.factorial(1);
        BigDecimal fact5 = calculator.factorial(5);
        
        assertEquals(BigDecimal.ONE, fact0, "0! should be 1");
        assertEquals(BigDecimal.ONE, fact1, "1! should be 1");
        assertEquals(BigDecimal.valueOf(120), fact5, "5! should be 120");
    }
    
    @Test
    public void testConvergenceRate() {
        ChudnovskyAlgorithm calculator = new ChudnovskyAlgorithm();
        
        // Chudnovsky algorithm should converge very fast
        double pi1 = calculator.calculatePi(1);
        double pi2 = calculator.calculatePi(2);
        
        // Each iteration should add about 14 decimal digits of precision
        double error1 = Math.abs(pi1 - Math.PI);
        double error2 = Math.abs(pi2 - Math.PI);
        
        assertTrue(error2 < error1, "Error should decrease with more iterations");
    }
}