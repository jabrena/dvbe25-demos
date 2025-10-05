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
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * JMH benchmark comparing Pi calculation performance between different algorithms.
 * 
 * This benchmark compares:
 * - MachinLikePiCalculator: Uses Machin's formula with Taylor series
 * - ChudnovskyPiCalculator: Uses Chudnovsky algorithm with rapid convergence
 * 
 * Benchmark configuration:
 * - Limited iterations to keep runtime under 30 seconds
 * - Tests different precision levels to understand performance characteristics
 * - Uses throughput mode to measure operations per time unit
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-Xms1G", "-Xmx1G"})
@Warmup(iterations = 2, time = 2)
@Measurement(iterations = 3, time = 3)
public class PiCalculationBenchmark {

    @Param({"5", "10", "15"}) // Different precision levels to test
    private int precision;

    private MachinLikePiCalculator machinCalculator;
    private ChudnovskyPiCalculator chudnovskyCalculator;

    @Setup
    public void setup() {
        machinCalculator = new MachinLikePiCalculator();
        chudnovskyCalculator = new ChudnovskyPiCalculator();
    }

    /**
     * Benchmark Machin-like formula Pi calculation.
     * Tests performance of functional stream-based Taylor series implementation.
     */
    @Benchmark
    public BigDecimal benchmarkMachinLikePiCalculation() {
        return machinCalculator.calculatePiHighPrecision(precision);
    }

    /**
     * Benchmark Chudnovsky algorithm Pi calculation.
     * Tests performance of functional implementation with trampoline factorial.
     */
    @Benchmark
    public BigDecimal benchmarkChudnovskyPiCalculation() {
        return chudnovskyCalculator.calculatePiHighPrecision(precision);
    }

    /**
     * Main method to run benchmarks with JSON output.
     * Configured to complete within 30 seconds maximum.
     */
    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
            .include(PiCalculationBenchmark.class.getSimpleName())
            .resultFormat(ResultFormatType.JSON)
            .result("sandbox/src/test/resources/benchmark/jmh-result.json")
            .shouldDoGC(true)
            .build();

        new Runner(options).run();
    }
}