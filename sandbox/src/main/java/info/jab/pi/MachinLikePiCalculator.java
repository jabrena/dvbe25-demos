package info.jab.pi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Pi calculation using Machin's formula: π = 16 * arctan(1/5) - 4 * arctan(1/239)
 * This is a well-known formula discovered by John Machin in 1706.
 * 
 * Implementation follows functional programming principles with pure functions and immutability.
 */
public final class MachinLikePiCalculator implements HighPrecisionPiCalculator {

    // Immutable constants for Machin's formula
    private static final BigDecimal SIXTEEN = new BigDecimal(16);
    private static final BigDecimal FOUR = new BigDecimal(4);
    private static final BigDecimal FIVE = new BigDecimal(5);
    private static final BigDecimal TWO_THREE_NINE = new BigDecimal(239);

    @Override
    public BigDecimal calculatePiHighPrecision(int precision) {
        return createMachinsFormulaCalculation(precision).apply(createWorkingContext(precision));
    }

    /**
     * Creates a pure function that calculates Pi using Machin's formula.
     * This function composition approach separates concerns and enables better testing.
     */
    private Function<MathContext, BigDecimal> createMachinsFormulaCalculation(int targetPrecision) {
        return mathContext -> {
            // Pure function composition for Machin's formula: π = 16 * arctan(1/5) - 4 * arctan(1/239)
            Function<BigDecimal, BigDecimal> arctanCalculator = x -> calculateArctan(x, mathContext);
            
            BigDecimal arctan1_5 = arctanCalculator.apply(BigDecimal.ONE.divide(FIVE, mathContext));
            BigDecimal arctan1_239 = arctanCalculator.apply(BigDecimal.ONE.divide(TWO_THREE_NINE, mathContext));
            
            BigDecimal pi = SIXTEEN.multiply(arctan1_5, mathContext)
                                  .subtract(FOUR.multiply(arctan1_239, mathContext), mathContext);
            
            return pi.setScale(targetPrecision, RoundingMode.HALF_UP);
        };
    }

    /**
     * Creates working precision context - pure function with no side effects.
     */
    private MathContext createWorkingContext(int precision) {
        int workingPrecision = precision + 20;
        return new MathContext(workingPrecision, RoundingMode.HALF_UP);
    }

    /**
     * Pure function to calculate arctan(x) using Taylor series.
     * Uses functional stream-based approach for term generation.
     */
    private BigDecimal calculateArctan(BigDecimal x, MathContext mc) {
        BigDecimal xSquared = x.multiply(x, mc);
        BigDecimal convergenceThreshold = createConvergenceThreshold(mc);
        
        return generateTaylorSeriesTerms(x, xSquared, mc)
            .takeWhile(term -> termAboveThreshold(term.evaluatedTerm(), convergenceThreshold))
            .map(TaylorTerm::evaluatedTerm)
            .reduce(BigDecimal.ZERO, (sum, term) -> sum.add(term, mc));
    }

    /**
     * Generates infinite stream of Taylor series terms for arctan calculation.
     * This demonstrates functional programming with lazy evaluation.
     */
    private Stream<TaylorTerm> generateTaylorSeriesTerms(BigDecimal x, BigDecimal xSquared, MathContext mc) {
        // Start with first term: x/1 (with sign 1)
        TaylorTerm firstTerm = new TaylorTerm(x, x.divide(BigDecimal.ONE, mc), 1, 1);
        
        return Stream.iterate(
            firstTerm,
            term -> computeNextTaylorTerm(term, xSquared, mc)
        );
    }

    /**
     * Pure function to compute the next Taylor series term.
     */
    private TaylorTerm computeNextTaylorTerm(TaylorTerm current, BigDecimal xSquared, MathContext mc) {
        BigDecimal nextTerm = current.term().multiply(xSquared, mc);
        int nextDenominator = current.denominator() + 2;
        int nextSign = current.sign() * -1;
        BigDecimal nextEvaluatedTerm = nextTerm.divide(new BigDecimal(nextDenominator), mc)
                                              .multiply(new BigDecimal(nextSign), mc);
        
        return new TaylorTerm(nextTerm, nextEvaluatedTerm, nextDenominator, nextSign);
    }

    /**
     * Pure predicate function to check if term is above convergence threshold.
     */
    private boolean termAboveThreshold(BigDecimal term, BigDecimal threshold) {
        return term.abs().compareTo(threshold) > 0;
    }

    /**
     * Pure function to create convergence threshold.
     */
    private BigDecimal createConvergenceThreshold(MathContext mc) {
        return BigDecimal.ONE.divide(BigDecimal.TEN.pow(mc.getPrecision() - 5), mc);
    }

    /**
     * Immutable value object representing a Taylor series term.
     * Uses Java 14+ record for immutability and value semantics.
     */
    private record TaylorTerm(
        BigDecimal term,
        BigDecimal evaluatedTerm, 
        int denominator,
        int sign
    ) {}
}