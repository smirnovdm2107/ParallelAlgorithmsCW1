package sort

import com.github.smirnovdm2107.Sort.parallelQuickSort2
import org.openjdk.jmh.annotations.Benchmark
import java.util.concurrent.ForkJoinPool

open class ParallelQuickSort2Benchmark : RandomArrayBenchmark() {
    private val parallelism = 4

    @Benchmark
    fun parallelQuickSortBenchmark() {
        ForkJoinPool(parallelism).parallelQuickSort2(arr, 1000)
    }
}