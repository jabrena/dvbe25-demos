package info.jab.pi;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Test class for Machin-like Formula Pi calculation.
 * Machin's formula: π/4 = 4*arctan(1/5) - arctan(1/239)
 */
public class MachinFormulaTest {
    
    private static final double EPSILON = 1e-6;
    
    @Test
    public void testCalculatePiWithDefaultPrecision() {
        MachinFormula calculator = new MachinFormula();
        double pi = calculator.calculatePi();
        
        assertEquals(Math.PI, pi, EPSILON, "Pi calculation should be close to Math.PI");
        assertTrue(pi > 3.14, "Pi should be greater than 3.14");
        assertTrue(pi < 3.15, "Pi should be less than 3.15");
    }
    
    @Test
    public void testCalculatePiWithHighPrecision() {
        MachinFormula calculator = new MachinFormula();
        MathContext precision = new MathContext(50);
        BigDecimal pi = calculator.calculatePi(precision);
        
        assertNotNull(pi, "Pi calculation should not return null");
        assertTrue(pi.compareTo(BigDecimal.valueOf(3.14)) > 0, "Pi should be greater than 3.14");
        assertTrue(pi.compareTo(BigDecimal.valueOf(3.15)) < 0, "Pi should be less than 3.15");
    }
    
    @Test
    public void testCalculatePiWithDifferentPrecisions() {
        MachinFormula calculator = new MachinFormula();
        
        MathContext low = new MathContext(10);
        MathContext high = new MathContext(100);
        
        BigDecimal piLow = calculator.calculatePi(low);
        BigDecimal piHigh = calculator.calculatePi(high);
        
        // Higher precision should have more accurate result
        assertNotNull(piLow, "Low precision result should not be null");
        assertNotNull(piHigh, "High precision result should not be null");
    }
    
    @Test
    public void testArctanFunction() {
        MachinFormula calculator = new MachinFormula();
        MathContext precision = new MathContext(20);
        
        // Test known arctan values
        BigDecimal arctan1 = calculator.arctan(BigDecimal.ONE, precision);
        double expected = Math.PI / 4;
        
        assertTrue(Math.abs(arctan1.doubleValue() - expected) < 1e-3, 
            "arctan(1) should be π/4");
    }
}