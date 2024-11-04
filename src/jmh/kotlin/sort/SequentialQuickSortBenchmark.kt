package sort

import com.github.smirnovdm2107.Sort.sequentialQuickSort
import org.openjdk.jmh.annotations.Benchmark


open class SequentialQuickSortBenchmark : RandomArrayBenchmark() {
    @Benchmark
    fun sequentialQuickSortBenchmark() {
        sequentialQuickSort(arr)
    }
}