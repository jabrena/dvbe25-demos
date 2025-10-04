package info.jab.pi;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Test class for Bailey-Borwein-Plouffe (BBP) Formula Pi calculation.
 * BBP formula allows computation of hexadecimal digits of π without calculating preceding digits.
 */
public class BBPFormulaTest {
    
    private static final double EPSILON = 1e-12;
    
    @Test
    public void testCalculatePiWithDefaultIterations() {
        BBPFormula calculator = new BBPFormula();
        double pi = calculator.calculatePi();
        
        assertEquals(Math.PI, pi, EPSILON, "Pi calculation should be close to Math.PI");
        assertTrue(pi > 3.14159, "Pi should be greater than 3.14159");
        assertTrue(pi < 3.14160, "Pi should be less than 3.14160");
    }
    
    @Test
    public void testCalculatePiWithSpecificIterations() {
        BBPFormula calculator = new BBPFormula();
        
        double pi10 = calculator.calculatePi(10);
        double pi50 = calculator.calculatePi(50);
        double pi100 = calculator.calculatePi(100);
        
        assertNotEquals(0.0, pi10, "Pi with 10 iterations should not be zero");
        assertNotEquals(0.0, pi50, "Pi with 50 iterations should not be zero");
        assertNotEquals(0.0, pi100, "Pi with 100 iterations should not be zero");
        
        // More iterations should give better accuracy
        assertTrue(Math.abs(pi100 - Math.PI) <= Math.abs(pi50 - Math.PI),
            "More iterations should provide better accuracy");
        assertTrue(Math.abs(pi50 - Math.PI) <= Math.abs(pi10 - Math.PI),
            "More iterations should provide better accuracy");
    }
    
    @Test
    public void testCalculatePiWithHighPrecision() {
        BBPFormula calculator = new BBPFormula();
        MathContext precision = new MathContext(50);
        
        BigDecimal pi = calculator.calculatePi(precision, 100);
        
        assertNotNull(pi, "High precision Pi calculation should not return null");
        assertTrue(pi.compareTo(BigDecimal.valueOf(3.141592)) > 0, 
            "Pi should be greater than 3.141592");
        assertTrue(pi.compareTo(BigDecimal.valueOf(3.141593)) < 0, 
            "Pi should be less than 3.141593");
    }
    
    @Test
    public void testCalculateHexDigit() {
        BBPFormula calculator = new BBPFormula();
        
        // Test calculation of specific hexadecimal digits
        int digit0 = calculator.calculateHexDigit(0);
        int digit1 = calculator.calculateHexDigit(1);
        
        assertTrue(digit0 >= 0 && digit0 <= 15, "Hex digit should be between 0 and 15");
        assertTrue(digit1 >= 0 && digit1 <= 15, "Hex digit should be between 0 and 15");
    }
    
    @Test
    public void testCalculateHexDigitsSequence() {
        BBPFormula calculator = new BBPFormula();
        
        String hexDigits = calculator.calculateHexDigits(0, 10);
        
        assertNotNull(hexDigits, "Hex digits string should not be null");
        assertEquals(10, hexDigits.length(), "Should return requested number of digits");
        
        // All characters should be valid hex digits
        for (char c : hexDigits.toCharArray()) {
            assertTrue(Character.isDigit(c) || (c >= 'A' && c <= 'F'), 
                "All characters should be valid hex digits");
        }
    }
    
    @Test
    public void testModularExponentiation() {
        BBPFormula calculator = new BBPFormula();
        
        // Test the modular exponentiation function used in BBP
        double result = calculator.modularExp(16, 5, 7);
        
        assertTrue(result >= 0, "Modular exponentiation result should be non-negative");
        assertTrue(result < 7, "Modular exponentiation result should be less than modulus");
    }
    
    @Test
    public void testConvergence() {
        BBPFormula calculator = new BBPFormula();
        
        double pi1 = calculator.calculatePi(1);
        double pi10 = calculator.calculatePi(10);
        double pi50 = calculator.calculatePi(50);
        
        double error1 = Math.abs(pi1 - Math.PI);
        double error10 = Math.abs(pi10 - Math.PI);
        double error50 = Math.abs(pi50 - Math.PI);
        
        assertTrue(error50 <= error10, "Error should not increase with more iterations");
        assertTrue(error10 <= error1, "Error should not increase with more iterations");
    }
}