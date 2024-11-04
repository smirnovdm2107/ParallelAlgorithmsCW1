package sort

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit


@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Warmup(iterations = 1)
@Measurement(iterations = 5)
open class RandomArrayBenchmark {
    protected lateinit var arr: IntArray
    private val size = 100_000_000

    @Setup(Level.Invocation)
    fun init() {
        arr = IntArray(size) { it }
        arr.shuffle()
    }
}