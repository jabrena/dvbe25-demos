package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.stream.IntStream;
import java.util.function.Function;

/**
* Implementation of Pi calculation using Machin-like formulas.
* Uses the formula: π/4 = 4*arctan(1/5) - arctan(1/239)
*/
public class MachinLikePiCalculator implements HighPrecisionPiCalculator {

    private static final BigDecimal FIVE = BigDecimal.valueOf(5);
    private static final BigDecimal TWO_HUNDRED_THIRTY_NINE = BigDecimal.valueOf(239);
    private static final BigDecimal FOUR = BigDecimal.valueOf(4);

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        MathContext mc = createMathContext.apply(precision);
        return calculateMachinFormula(mc, precision);
    }

    private Function<Integer, MathContext> createMathContext = 
            precision -> new MathContext(precision + 10, RoundingMode.HALF_UP);

    private BigDecimal calculateMachinFormula(MathContext mc, int precision) {
        BigDecimal arctan5 = calculateArctan(BigDecimal.ONE.divide(FIVE, mc), precision);
        BigDecimal arctan239 = calculateArctan(BigDecimal.ONE.divide(TWO_HUNDRED_THIRTY_NINE, mc), precision);
        
        return arctan5.multiply(FOUR, mc)
                .subtract(arctan239, mc)
                .multiply(FOUR, mc)
                .setScale(precision, RoundingMode.HALF_UP);
    }

    /**
     * Calculate arctan(x) using Taylor series: arctan(x) = x - x³/3 + x⁵/5 - x⁷/7 + ...
     * Uses functional approach with streams for convergence
     */
    private BigDecimal calculateArctan(BigDecimal x, int precision) {
        MathContext mc = new MathContext(precision + 10, RoundingMode.HALF_UP);
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal xSquared = x.multiply(x, mc);
        BigDecimal term = x;
        BigDecimal sign = BigDecimal.ONE;
        
        // Use enough terms to achieve desired precision
        int maxTerms = precision * 2;
        
        for (int i = 1; i <= maxTerms; i += 2) {
            BigDecimal currentTerm = term.multiply(sign, mc).divide(BigDecimal.valueOf(i), mc);
            result = result.add(currentTerm, mc);
            
            // Check if we've converged (term is smaller than our precision)
            if (currentTerm.abs().compareTo(BigDecimal.ONE.scaleByPowerOfTen(-precision)) < 0) {
                break;
            }
            
            term = term.multiply(xSquared, mc);
            sign = sign.negate();
        }
        
        return result;
    }
}