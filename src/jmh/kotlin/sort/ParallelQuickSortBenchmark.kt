package sort

import com.github.smirnovdm2107.Sort.parallelQuickSort
import org.openjdk.jmh.annotations.Benchmark
import java.util.concurrent.ForkJoinPool

open class ParallelQuickSortBenchmark : RandomArrayBenchmark() {
    private val parallelism = 4

    @Benchmark
    fun parallelQuickSort2Benchmark() {
        ForkJoinPool(parallelism).parallelQuickSort(arr, 1000)
    }
}