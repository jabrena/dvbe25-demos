package info.jab.pi;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Test class for Spigot Algorithm Pi calculation.
 * Spigot algorithms generate digits of π one at a time without needing to store intermediate results.
 */
public class SpigotAlgorithmTest {
    
    private static final double EPSILON = 1e-10;
    
    @Test
    public void testCalculatePiWithDefaultDigits() {
        SpigotAlgorithm calculator = new SpigotAlgorithm();
        double pi = calculator.calculatePi();
        
        assertEquals(Math.PI, pi, EPSILON, "Pi calculation should be close to Math.PI");
        assertTrue(pi > 3.14159, "Pi should be greater than 3.14159");
        assertTrue(pi < 3.14160, "Pi should be less than 3.14160");
    }
    
    @Test
    public void testCalculatePiWithSpecificDigits() {
        SpigotAlgorithm calculator = new SpigotAlgorithm();
        
        double pi5 = calculator.calculatePi(5);
        double pi10 = calculator.calculatePi(10);
        double pi20 = calculator.calculatePi(20);
        
        assertNotEquals(0.0, pi5, "Pi with 5 digits should not be zero");
        assertNotEquals(0.0, pi10, "Pi with 10 digits should not be zero");
        assertNotEquals(0.0, pi20, "Pi with 20 digits should not be zero");
        
        // More digits should give better accuracy
        assertTrue(Math.abs(pi20 - Math.PI) <= Math.abs(pi10 - Math.PI),
            "More digits should provide better accuracy");
        assertTrue(Math.abs(pi10 - Math.PI) <= Math.abs(pi5 - Math.PI),
            "More digits should provide better accuracy");
    }
    
    @Test
    public void testGenerateDigitsSequence() {
        SpigotAlgorithm calculator = new SpigotAlgorithm();
        
        String digits = calculator.generateDigits(10);
        
        assertNotNull(digits, "Generated digits should not be null");
        assertEquals(10, digits.length(), "Should generate requested number of digits");
        
        // All characters should be digits
        for (char c : digits.toCharArray()) {
            assertTrue(Character.isDigit(c), "All characters should be digits");
        }
        
        // First digit should be 3
        assertEquals('3', digits.charAt(0), "First digit of π should be 3");
    }
    
    @Test
    public void testGenerateDigitsWithDecimalPoint() {
        SpigotAlgorithm calculator = new SpigotAlgorithm();
        
        String piString = calculator.generatePiString(10);
        
        assertNotNull(piString, "Pi string should not be null");
        assertTrue(piString.startsWith("3."), "Pi string should start with '3.'");
        assertTrue(piString.contains("14159"), "Pi string should contain known digits");
    }
    
    @Test
    public void testGetSingleDigit() {
        SpigotAlgorithm calculator = new SpigotAlgorithm();
        
        int digit0 = calculator.getDigit(0); // Should be 3
        int digit1 = calculator.getDigit(1); // Should be 1 (first digit after decimal)
        int digit2 = calculator.getDigit(2); // Should be 4
        int digit3 = calculator.getDigit(3); // Should be 1
        int digit4 = calculator.getDigit(4); // Should be 5
        int digit5 = calculator.getDigit(5); // Should be 9
        
        assertEquals(3, digit0, "0th digit should be 3");
        assertEquals(1, digit1, "1st digit should be 1");
        assertEquals(4, digit2, "2nd digit should be 4");
        assertEquals(1, digit3, "3rd digit should be 1");
        assertEquals(5, digit4, "4th digit should be 5");
        assertEquals(9, digit5, "5th digit should be 9");
    }
    
    @Test
    public void testCalculatePiWithHighPrecision() {
        SpigotAlgorithm calculator = new SpigotAlgorithm();
        MathContext precision = new MathContext(50);
        
        BigDecimal pi = calculator.calculatePi(precision, 100);
        
        assertNotNull(pi, "High precision Pi calculation should not return null");
        assertTrue(pi.compareTo(BigDecimal.valueOf(3.141592653)) > 0, 
            "Pi should be greater than 3.141592653");
        assertTrue(pi.compareTo(BigDecimal.valueOf(3.141592654)) < 0, 
            "Pi should be less than 3.141592654");
    }
    
    @Test
    public void testSpigotPerformance() {
        SpigotAlgorithm calculator = new SpigotAlgorithm();
        
        long startTime = System.currentTimeMillis();
        String digits = calculator.generateDigits(100);
        long endTime = System.currentTimeMillis();
        
        assertNotNull(digits, "Should generate digits in reasonable time");
        assertEquals(100, digits.length(), "Should generate all requested digits");
        
        // Should complete reasonably quickly (less than 5 seconds for 100 digits)
        assertTrue(endTime - startTime < 5000, "Should complete in reasonable time");
    }
    
    @Test
    public void testDigitAccuracy() {
        SpigotAlgorithm calculator = new SpigotAlgorithm();
        
        String knownPi = "31415926535897932384626433832795028841971693993751";
        String generated = calculator.generateDigits(knownPi.length());
        
        assertNotNull(generated, "Generated digits should not be null");
        
        // At least the first 10 digits should match
        String first10Known = knownPi.substring(0, Math.min(10, knownPi.length()));
        String first10Generated = generated.substring(0, Math.min(10, generated.length()));
        
        assertEquals(first10Known, first10Generated, 
            "First 10 digits should match known value of π");
    }
}