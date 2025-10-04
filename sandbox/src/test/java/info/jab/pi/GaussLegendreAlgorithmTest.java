package info.jab.pi;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Test class for Gauss-Legendre Algorithm Pi calculation.
 * The Gauss-Legendre algorithm is known for its quadratic convergence.
 */
public class GaussLegendreAlgorithmTest {
    
    private static final double EPSILON = 1e-14;
    
    @Test
    public void testCalculatePiWithDefaultIterations() {
        GaussLegendreAlgorithm calculator = new GaussLegendreAlgorithm();
        double pi = calculator.calculatePi();
        
        assertEquals(Math.PI, pi, EPSILON, "Pi calculation should be very close to Math.PI");
        assertTrue(pi > 3.141592, "Pi should be greater than 3.141592");
        assertTrue(pi < 3.141593, "Pi should be less than 3.141593");
    }
    
    @Test
    public void testCalculatePiWithSpecificIterations() {
        GaussLegendreAlgorithm calculator = new GaussLegendreAlgorithm();
        
        double pi1 = calculator.calculatePi(1);
        double pi2 = calculator.calculatePi(2);
        double pi3 = calculator.calculatePi(3);
        double pi5 = calculator.calculatePi(5);
        
        assertNotEquals(0.0, pi1, "Pi with 1 iteration should not be zero");
        assertNotEquals(0.0, pi2, "Pi with 2 iterations should not be zero");
        assertNotEquals(0.0, pi3, "Pi with 3 iterations should not be zero");
        assertNotEquals(0.0, pi5, "Pi with 5 iterations should not be zero");
        
        // Test quadratic convergence - each iteration should roughly double precision
        double error1 = Math.abs(pi1 - Math.PI);
        double error2 = Math.abs(pi2 - Math.PI);
        double error3 = Math.abs(pi3 - Math.PI);
        
        assertTrue(error3 < error2, "Error should decrease with more iterations");
        assertTrue(error2 < error1, "Error should decrease with more iterations");
    }
    
    @Test
    public void testCalculatePiWithHighPrecision() {
        GaussLegendreAlgorithm calculator = new GaussLegendreAlgorithm();
        MathContext precision = new MathContext(50);
        
        BigDecimal pi = calculator.calculatePi(precision, 4);
        
        assertNotNull(pi, "High precision Pi calculation should not return null");
        assertTrue(pi.compareTo(BigDecimal.valueOf(3.141592653)) > 0, 
            "Pi should be greater than 3.141592653");
        assertTrue(pi.compareTo(BigDecimal.valueOf(3.141592654)) < 0, 
            "Pi should be less than 3.141592654");
    }
    
    @Test
    public void testQuadraticConvergence() {
        GaussLegendreAlgorithm calculator = new GaussLegendreAlgorithm();
        
        // Test the quadratic convergence property
        double pi1 = calculator.calculatePi(1);
        double pi2 = calculator.calculatePi(2);
        double pi3 = calculator.calculatePi(3);
        
        double error1 = Math.abs(pi1 - Math.PI);
        double error2 = Math.abs(pi2 - Math.PI);
        double error3 = Math.abs(pi3 - Math.PI);
        
        // Each iteration should roughly square the error (quadratic convergence)
        assertTrue(error2 < error1, "Second iteration should be more accurate");
        assertTrue(error3 < error2, "Third iteration should be more accurate");
        
        // The improvement should be significant
        assertTrue(error2 < error1 * 0.1, "Quadratic convergence should show significant improvement");
    }
    
    @Test
    public void testArithmeticGeometricMean() {
        GaussLegendreAlgorithm calculator = new GaussLegendreAlgorithm();
        MathContext precision = new MathContext(20);
        
        BigDecimal a = BigDecimal.ONE;
        BigDecimal b = new BigDecimal("0.707106781186547524"); // 1/sqrt(2)
        
        BigDecimal agm = calculator.arithmeticGeometricMean(a, b, precision);
        
        assertNotNull(agm, "Arithmetic-Geometric Mean should not be null");
        assertTrue(agm.compareTo(b) > 0, "AGM should be greater than the smaller input");
        assertTrue(agm.compareTo(a) < 0, "AGM should be less than the larger input");
    }
    
    @Test
    public void testSqrtFunction() {
        GaussLegendreAlgorithm calculator = new GaussLegendreAlgorithm();
        MathContext precision = new MathContext(20);
        
        BigDecimal sqrt2 = calculator.sqrt(BigDecimal.valueOf(2), precision);
        BigDecimal sqrt4 = calculator.sqrt(BigDecimal.valueOf(4), precision);
        
        assertNotNull(sqrt2, "Square root calculation should not return null");
        assertNotNull(sqrt4, "Square root calculation should not return null");
        
        // sqrt(4) should be very close to 2
        assertTrue(Math.abs(sqrt4.doubleValue() - 2.0) < 1e-10, "sqrt(4) should be close to 2");
        
        // sqrt(2) should be close to 1.414...
        assertTrue(Math.abs(sqrt2.doubleValue() - Math.sqrt(2)) < 1e-10, 
            "sqrt(2) should be close to expected value");
    }
    
    @Test
    public void testInitialValues() {
        GaussLegendreAlgorithm calculator = new GaussLegendreAlgorithm();
        
        // Test that the algorithm starts with correct initial values
        double pi1 = calculator.calculatePi(1);
        
        assertTrue(pi1 > 3.0, "First iteration should give a reasonable approximation");
        assertTrue(pi1 < 4.0, "First iteration should give a reasonable approximation");
    }
}