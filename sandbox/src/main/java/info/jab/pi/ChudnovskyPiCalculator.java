package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.stream.IntStream;
import java.util.function.Function;
import java.util.function.Predicate;

/**
* Implementation of Pi calculation using the Chudnovsky algorithm.
* This algorithm is known for its rapid convergence and high precision.
*/
public class ChudnovskyPiCalculator implements HighPrecisionPiCalculator {

    // Constants for Chudnovsky algorithm
    private static final BigDecimal C = new BigDecimal("426880");
    private static final BigDecimal A = new BigDecimal("13591409");
    private static final BigDecimal B = new BigDecimal("545140134");
    private static final BigDecimal C_CONST = new BigDecimal("640320");
    private static final BigDecimal SQRT_VALUE = new BigDecimal("10005");
    private static final BigDecimal TWO = BigDecimal.valueOf(2);
    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal ONE = BigDecimal.ONE;

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        MathContext mc = createMathContext(precision);
        BigDecimal sum = calculateChudnovskySum(mc, precision);
        return calculateFinalPi(sum, precision);
    }

    private MathContext createMathContext(int precision) {
        return new MathContext(precision + 20, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateChudnovskySum(MathContext mc, int precision) {
        int maxIterations = Math.max(10, precision / 14);
        BigDecimal precisionThreshold = ONE.scaleByPowerOfTen(-precision - 5);
        
        return IntStream.range(0, maxIterations)
                .mapToObj(k -> calculateChudnovskyTerm(k, mc))
                .takeWhile(term -> term.abs().compareTo(precisionThreshold) >= 0)
                .reduce(ZERO, (sum, term) -> sum.add(term, mc));
    }

    private BigDecimal calculateFinalPi(BigDecimal sum, int precision) {
        MathContext mc = new MathContext(precision + 20, RoundingMode.HALF_UP);
        BigDecimal sqrt10005 = calculateSqrt(SQRT_VALUE, precision + 10);
        return C.multiply(sqrt10005, mc)
                .divide(sum, mc)
                .setScale(precision, RoundingMode.HALF_UP);
    }

    /**
     * Calculate the k-th term in the Chudnovsky series using functional composition
     */
    private BigDecimal calculateChudnovskyTerm(int k, MathContext mc) {
        BigDecimal numerator = calculateNumerator(k, mc);
        BigDecimal denominator = calculateDenominator(k, mc);
        return numerator.divide(denominator, mc);
    }

    private BigDecimal calculateNumerator(int k, MathContext mc) {
        BigDecimal sixKFactorial = factorialTrampoline(BigDecimal.valueOf(6 * k), mc);
        BigDecimal linearTerm = B.multiply(BigDecimal.valueOf(k), mc).add(A, mc);
        return sixKFactorial.multiply(linearTerm, mc);
    }

    private BigDecimal calculateDenominator(int k, MathContext mc) {
        BigDecimal threeKFactorial = factorialTrampoline(BigDecimal.valueOf(3 * k), mc);
        BigDecimal kFactorial = factorialTrampoline(BigDecimal.valueOf(k), mc);
        BigDecimal kFactorialCubed = kFactorial.pow(3, mc);
        
        BigDecimal cTo3k = C_CONST.pow(3 * k, mc);
        BigDecimal signedCTo3k = k % 2 == 1 ? cTo3k.negate() : cTo3k;
        
        return threeKFactorial.multiply(kFactorialCubed, mc)
                .multiply(signedCTo3k, mc);
    }

    /**
     * Calculate factorial using trampoline pattern to avoid stack overflow
     */
    private BigDecimal factorialTrampoline(BigDecimal n, MathContext mc) {
        if (n.compareTo(ZERO) <= 0) {
            return ONE;
        }
        
        return IntStream.rangeClosed(1, n.intValue())
                .mapToObj(i -> BigDecimal.valueOf(i))
                .reduce(ONE, (result, i) -> result.multiply(i, mc));
    }

    /**
     * Calculate square root using Newton's method with functional approach
     */
    private BigDecimal calculateSqrt(BigDecimal value, int precision) {
        MathContext mc = new MathContext(precision + 10, RoundingMode.HALF_UP);
        
        if (value.compareTo(ZERO) == 0) {
            return ZERO;
        }
        
        BigDecimal initialGuess = value.divide(TWO, mc);
        BigDecimal tolerance = ONE.scaleByPowerOfTen(-precision);
        
        return newtonIteration(value, initialGuess, tolerance, mc, 0);
    }

    private BigDecimal newtonIteration(BigDecimal value, BigDecimal x, BigDecimal tolerance, MathContext mc, int iteration) {
        if (iteration >= 50) {
            return x;
        }
        
        BigDecimal xNew = x.add(value.divide(x, mc), mc).divide(TWO, mc);
        
        if (xNew.subtract(x, mc).abs().compareTo(tolerance) < 0) {
            return xNew;
        }
        
        return newtonIteration(value, xNew, tolerance, mc, iteration + 1);
    }
}