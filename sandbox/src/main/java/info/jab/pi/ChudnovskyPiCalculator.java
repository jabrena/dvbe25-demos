package info.jab.pi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Pi calculation using the Chudnovsky algorithm.
 * This algorithm converges extremely rapidly and is used by many computer programs
 * to calculate π to billions of digits.
 * 
 * Implementation follows functional programming principles with pure functions,
 * immutability, and trampoline pattern for stack-safe recursion.
 * 
 * Formula: 1/π = 12 * Σ(k=0 to ∞) [(-1)^k * (6k)! * (545140134*k + 13591409)] / [(3k)! * (k!)^3 * 640320^(3k+3/2)]
 */
public final class ChudnovskyPiCalculator implements HighPrecisionPiCalculator {

    // Immutable constants for Chudnovsky formula
    private static final BigDecimal CONSTANT_426880 = new BigDecimal(426880);
    private static final BigDecimal CONSTANT_10005 = new BigDecimal(10005);
    private static final BigInteger CONSTANT_545140134 = BigInteger.valueOf(545140134L);
    private static final BigInteger CONSTANT_13591409 = BigInteger.valueOf(13591409L);
    private static final BigInteger CONSTANT_640320 = BigInteger.valueOf(640320L);
    private static final BigDecimal TWO = new BigDecimal(2);

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        MathContext workingContext = createWorkingContext(precision);
        return createChudnovskyCalculation(precision).apply(workingContext);
    }

    /**
     * Creates a pure function that performs the complete Chudnovsky Pi calculation.
     */
    private Function<MathContext, BigDecimal> createChudnovskyCalculation(int precision) {
        return mathContext -> {
            BigDecimal c = CONSTANT_426880.multiply(calculateSquareRoot(CONSTANT_10005, mathContext), mathContext);
            
            BigDecimal seriesSum = generateChudnovskyTerms(precision, mathContext)
                .reduce(BigDecimal.ZERO, (sum, term) -> sum.add(term, mathContext));
            
            BigDecimal pi = c.divide(seriesSum, mathContext);
            return pi.setScale(precision, RoundingMode.HALF_UP);
        };
    }

    /**
     * Generates stream of Chudnovsky series terms using functional approach.
     */
    private Stream<BigDecimal> generateChudnovskyTerms(int precision, MathContext mc) {
        int maxTerms = precision / 14 + 5;
        
        return Stream.iterate(0, k -> k + 1)
            .limit(maxTerms)
            .map(k -> calculateChudnovskyTerm(k, mc))
            .takeWhile(createConvergenceChecker(mc));
    }

    /**
     * Creates a pure convergence checker function.
     */
    private java.util.function.Predicate<BigDecimal> createConvergenceChecker(MathContext mc) {
        BigDecimal convergenceThreshold = BigDecimal.ONE.divide(
            BigDecimal.TEN.pow(mc.getPrecision() - 10), mc);
        
        return term -> term.abs().compareTo(convergenceThreshold) >= 0;
    }

    /**
     * Pure function to calculate a single Chudnovsky series term.
     */
    private BigDecimal calculateChudnovskyTerm(int k, MathContext mc) {
        ChudnovskyComponents components = computeChudnovskyComponents(k);
        
        BigInteger numerator = components.factorial6k()
            .multiply(components.numeratorLinear());
        
        if (components.sign() < 0) {
            numerator = numerator.negate();
        }
        
        BigInteger denominator = components.factorial3k()
            .multiply(components.factorialK().pow(3))
            .multiply(components.power640320_3k());
        
        return new BigDecimal(numerator).divide(new BigDecimal(denominator), mc);
    }

    /**
     * Pure function to compute all components needed for Chudnovsky term calculation.
     */
    private ChudnovskyComponents computeChudnovskyComponents(int k) {
        return new ChudnovskyComponents(
            calculateFactorialTrampoline(6 * k),
            calculateFactorialTrampoline(3 * k),
            calculateFactorialTrampoline(k),
            CONSTANT_545140134.multiply(BigInteger.valueOf(k)).add(CONSTANT_13591409),
            (k % 2 == 0) ? 1 : -1,
            CONSTANT_640320.pow(3 * k)
        );
    }

    /**
     * Stack-safe factorial calculation using trampoline pattern.
     * This prevents StackOverflowError for large values.
     */
    private BigInteger calculateFactorialTrampoline(int n) {
        return trampolineFactorial(n, BigInteger.ONE).invoke();
    }

    /**
     * Trampoline implementation for factorial calculation.
     */
    private Trampoline<BigInteger> trampolineFactorial(int n, BigInteger accumulator) {
        if (n <= 1) {
            return Trampoline.done(accumulator);
        }
        return Trampoline.more(() -> trampolineFactorial(n - 1, accumulator.multiply(BigInteger.valueOf(n))));
    }

    /**
     * Pure function for square root calculation using Newton's method.
     */
    private BigDecimal calculateSquareRoot(BigDecimal n, MathContext mc) {
        if (n.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }
        
        return generateNewtonIterations(n, mc)
            .reduce((prev, current) -> current)
            .orElse(n);
    }

    /**
     * Generates stream of Newton method iterations for square root.
     */
    private Stream<BigDecimal> generateNewtonIterations(BigDecimal n, MathContext mc) {
        BigDecimal convergenceThreshold = BigDecimal.ONE.divide(
            BigDecimal.TEN.pow(mc.getPrecision() - 5), mc);
        
        return Stream.iterate(n, x -> computeNewtonIteration(x, n, mc))
            .limit(mc.getPrecision()) // Prevent infinite iterations
            .takeWhile(createNewtonConvergenceChecker(convergenceThreshold));
    }

    /**
     * Pure function for single Newton method iteration.
     */
    private BigDecimal computeNewtonIteration(BigDecimal x, BigDecimal n, MathContext mc) {
        return x.add(n.divide(x, mc), mc).divide(TWO, mc);
    }

    /**
     * Creates convergence checker for Newton method.
     */
    private java.util.function.Predicate<BigDecimal> createNewtonConvergenceChecker(BigDecimal threshold) {
        final BigDecimal[] previousValue = {null};
        
        return current -> {
            if (previousValue[0] == null) {
                previousValue[0] = current;
                return true;
            }
            
            BigDecimal difference = current.subtract(previousValue[0]).abs();
            previousValue[0] = current;
            return difference.compareTo(threshold) > 0;
        };
    }

    /**
     * Creates working precision context - pure function.
     */
    private MathContext createWorkingContext(int precision) {
        int workingPrecision = precision + 50;
        return new MathContext(workingPrecision, RoundingMode.HALF_UP);
    }

    /**
     * Immutable value object for Chudnovsky calculation components.
     */
    private record ChudnovskyComponents(
        BigInteger factorial6k,
        BigInteger factorial3k,
        BigInteger factorialK,
        BigInteger numeratorLinear,
        int sign,
        BigInteger power640320_3k
    ) {}

    /**
     * Trampoline implementation for stack-safe recursion.
     * This is a functional programming pattern to avoid stack overflow.
     */
    private sealed interface Trampoline<T> permits Done, More {
        T invoke();
        
        static <T> Trampoline<T> done(T value) {
            return new Done<>(value);
        }
        
        static <T> Trampoline<T> more(Supplier<Trampoline<T>> supplier) {
            return new More<>(supplier);
        }
    }

    private record Done<T>(T value) implements Trampoline<T> {
        @Override
        public T invoke() {
            return value;
        }
    }

    private record More<T>(Supplier<Trampoline<T>> supplier) implements Trampoline<T> {
        @Override
        public T invoke() {
            Trampoline<T> trampoline = supplier.get();
            while (trampoline instanceof More<T> more) {
                trampoline = more.supplier().get();
            }
            return ((Done<T>) trampoline).value();
        }
    }
}