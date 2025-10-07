package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.stream.IntStream;

/**
* Implementation of Pi calculation using Machin-like formulas.
* Uses the formula: π/4 = 4*arctan(1/5) - arctan(1/239)
*/
public class MachinLikePiCalculator implements HighPrecisionPiCalculator {

    private static final BigDecimal FOUR = new BigDecimal("4");
    private static final BigDecimal FIVE = new BigDecimal("5");
    private static final BigDecimal TWO_HUNDRED_THIRTY_NINE = new BigDecimal("239");

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        MathContext mc = new MathContext(precision + 2000, RoundingMode.HALF_UP);
        
        return calculateArctan(BigDecimal.ONE.divide(FIVE, mc), mc)
            .multiply(FOUR, mc)
            .subtract(calculateArctan(BigDecimal.ONE.divide(TWO_HUNDRED_THIRTY_NINE, mc), mc), mc)
            .multiply(FOUR, mc)
            .setScale(precision, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate arctan(x) using Taylor series: arctan(x) = x - x³/3 + x⁵/5 - x⁷/7 + ...
     */
    private BigDecimal calculateArctan(BigDecimal x, MathContext mc) {
        BigDecimal xSquared = x.multiply(x, mc);
        int maxTerms = Math.max(2000000, mc.getPrecision() * 2000);
        
        return IntStream.rangeClosed(1, maxTerms)
            .filter(i -> i % 2 == 1)
            .mapToObj(i -> calculateArctanTerm(x, xSquared, i, mc))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private BigDecimal calculateArctanTerm(BigDecimal x, BigDecimal xSquared, int i, MathContext mc) {
        BigDecimal term = x;
        BigDecimal sign = BigDecimal.ONE;
        
        for (int j = 1; j < i; j += 2) {
            term = term.multiply(xSquared, mc);
            sign = sign.negate();
        }
        
        return term.multiply(sign, mc);
    }
}