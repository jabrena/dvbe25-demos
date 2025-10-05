package info.jab.pi;

import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Immutable value object representing precision context for Pi calculations.
 * Provides type-safe wrapper for calculation precision parameters.
 */
public record PrecisionContext(int targetPrecision, int workingPrecision, MathContext mathContext) {

    /**
     * Creates a PrecisionContext with appropriate working precision buffer.
     *
     * @param targetPrecision The desired precision for the final result
     * @param precisionBuffer Additional precision to avoid rounding errors during calculation
     * @return A new PrecisionContext instance
     */
    public static PrecisionContext of(final int targetPrecision, final int precisionBuffer) {
        final int workingPrecision = targetPrecision + precisionBuffer;
        final MathContext mathContext = new MathContext(workingPrecision, RoundingMode.HALF_UP);
        return new PrecisionContext(targetPrecision, workingPrecision, mathContext);
    }

    /**
     * Creates a PrecisionContext with default precision buffer of 10.
     *
     * @param targetPrecision The desired precision for the final result
     * @return A new PrecisionContext instance with default buffer
     */
    public static PrecisionContext withDefaultBuffer(final int targetPrecision) {
        return of(targetPrecision, 10);
    }

    /**
     * Creates a PrecisionContext with extended precision buffer for complex calculations.
     *
     * @param targetPrecision The desired precision for the final result
     * @return A new PrecisionContext instance with extended buffer
     */
    public static PrecisionContext withExtendedBuffer(final int targetPrecision) {
        return of(targetPrecision, 20);
    }
}