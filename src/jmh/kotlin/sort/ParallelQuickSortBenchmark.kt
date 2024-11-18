package sort

import com.github.smirnovdm2107.Sort.parallelQuickSort
import org.openjdk.jmh.annotations.Benchmark
import java.util.concurrent.ForkJoinPool

open class ParallelQuickSortBenchmark : RandomArrayBenchmark() {
    private val parallelism = 4

    @Benchmark
    fun parallelQuickSortBenchmark() {
        ForkJoinPool(parallelism).parallelQuickSort(arr, 1000)
    }
}