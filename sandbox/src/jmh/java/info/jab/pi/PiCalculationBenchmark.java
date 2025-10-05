package info.jab.pi;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * JMH Benchmark for Pi calculation implementations.
 * Benchmarks the winning MachinLikePiCalculator implementation.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Warmup(iterations = 1, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class PiCalculationBenchmark {

    private MachinLikePiCalculator machinCalculator;
    
    private static final int PRECISION_10 = 10;
    private static final int PRECISION_20 = 20;
    private static final int PRECISION_50 = 50;

    @Setup
    public void setup() {
        machinCalculator = new MachinLikePiCalculator();
    }

    // Machin-like Formula Benchmarks (Winner)
    @Benchmark
    public BigDecimal machinFormula10Digits() {
        return machinCalculator.calculatePiHighPrecision(PRECISION_10);
    }

    @Benchmark
    public BigDecimal machinFormula20Digits() {
        return machinCalculator.calculatePiHighPrecision(PRECISION_20);
    }

    @Benchmark
    public BigDecimal machinFormula50Digits() {
        return machinCalculator.calculatePiHighPrecision(PRECISION_50);
    }

    /**
     * Main method to run the benchmark programmatically.
     * This is useful for IDE execution or custom configurations.
     */
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(PiCalculationBenchmark.class.getSimpleName())
                .warmupIterations(1)
                .warmupTime(org.openjdk.jmh.runner.options.TimeValue.seconds(2))
                .measurementIterations(3)
                .measurementTime(org.openjdk.jmh.runner.options.TimeValue.seconds(2))
                .forks(1)
                .resultFormat(org.openjdk.jmh.results.format.ResultFormatType.JSON)
                .result("sandbox/src/test/resources/benchmark/jmh-result.json")
                .build();

        new Runner(opt).run();
    }
}