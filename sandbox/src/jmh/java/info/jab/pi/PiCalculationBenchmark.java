package info.jab.pi;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * JMH Benchmark for Pi calculation algorithms comparing performance at different precision levels.
 * 
 * This benchmark compares:
 * - MachinLikePiCalculator: Using Machin's formula with Taylor series
 * - ChudnovskyPiCalculator: Using Chudnovsky algorithm for fast convergence
 * 
 * Tests are performed at precision levels: 20, 50, 100 decimal places
 * to evaluate performance characteristics under different accuracy requirements.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-Xms2G", "-Xmx2G"})
@Warmup(iterations = 1, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 2, timeUnit = TimeUnit.SECONDS)
public class PiCalculationBenchmark {

    @Param({"20", "50", "100"})
    private int precision;

    private MachinLikePiCalculator machinCalculator;
    private ChudnovskyPiCalculator chudnovskyCalculator;

    @Setup
    public void setup() {
        machinCalculator = new MachinLikePiCalculator();
        chudnovskyCalculator = new ChudnovskyPiCalculator();
    }

    /**
     * Benchmark MachinLike Pi calculator performance.
     * Tests Machin's formula: π/4 = 4*arctan(1/5) - arctan(1/239)
     * with Taylor series expansion for arctan calculation.
     * 
     * @return Calculated Pi value as BigDecimal
     */
    @Benchmark
    public BigDecimal benchmarkMachinLikePiCalculator() {
        return machinCalculator.calculatePiHighPrecision(precision);
    }

    /**
     * Benchmark Chudnovsky Pi calculator performance.
     * Tests Chudnovsky algorithm using rapidly converging series
     * with factorial calculations and square root operations.
     * 
     * @return Calculated Pi value as BigDecimal
     */
    @Benchmark
    public BigDecimal benchmarkChudnovskyPiCalculator() {
        return chudnovskyCalculator.calculatePiHighPrecision(precision);
    }

    /**
     * Baseline benchmark for comparison - calculates both algorithms
     * to measure relative performance difference.
     * 
     * @return Array containing results from both calculators
     */
    @Benchmark
    public BigDecimal[] benchmarkBothCalculators() {
        return new BigDecimal[]{
            machinCalculator.calculatePiHighPrecision(precision),
            chudnovskyCalculator.calculatePiHighPrecision(precision)
        };
    }
}