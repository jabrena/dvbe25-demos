package info.jab.pi;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * JMH Benchmark for comparing Pi calculation algorithms.
 * Benchmarks the performance of Machin-like Formula vs Chudnovsky Algorithm.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms2G", "-Xmx2G"})
@Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
public class PiCalculationBenchmark {

    private MachinLikeFormulaPiCalculator machinCalculator;
    private ChudnovskyAlgorithmPiCalculator chudnovskyCalculator;

    @Setup
    public void setup() {
        machinCalculator = new MachinLikeFormulaPiCalculator();
        chudnovskyCalculator = new ChudnovskyAlgorithmPiCalculator();
    }

    @Benchmark
    public BigDecimal benchmarkMachinLikeFormula_5Digits() {
        return machinCalculator.calculatePiHighPrecision(5);
    }

    @Benchmark
    public BigDecimal benchmarkMachinLikeFormula_10Digits() {
        return machinCalculator.calculatePiHighPrecision(10);
    }

    @Benchmark
    public BigDecimal benchmarkMachinLikeFormula_20Digits() {
        return machinCalculator.calculatePiHighPrecision(20);
    }

    @Benchmark
    public BigDecimal benchmarkChudnovskyAlgorithm_5Digits() {
        return chudnovskyCalculator.calculatePiHighPrecision(5);
    }

    @Benchmark
    public BigDecimal benchmarkChudnovskyAlgorithm_10Digits() {
        return chudnovskyCalculator.calculatePiHighPrecision(10);
    }

    @Benchmark
    public BigDecimal benchmarkChudnovskyAlgorithm_20Digits() {
        return chudnovskyCalculator.calculatePiHighPrecision(20);
    }

    /**
     * Main method to run benchmarks with JSON output configuration.
     * Configured to run for approximately 30 seconds total.
     */
    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(PiCalculationBenchmark.class.getSimpleName())
                .resultFormat(ResultFormatType.JSON)
                .result("sandbox/src/test/resources/benchmark/jmh-result.json")
                .build();

        new Runner(options).run();
    }
}