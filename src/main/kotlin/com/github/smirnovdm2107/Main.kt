package com.github.smirnovdm2107

import java.util.Collections
import java.util.concurrent.ForkJoinPool
import kotlin.math.sqrt

fun main() {
    val arrSize = 100_000_000
    val arr = (1..arrSize).shuffled()
    val iters = 5
    ForkJoinPool.commonPool().setParallelism(4)

    val blockSize = sqrt(arrSize.toDouble()).toInt()
    val results = SortMeter.meterAll(
        arr,
        iters,
        listOf(
            // {
            //     it.sort()
            // },
            { Sort.sequentialQuickSort(it, Integer::compare) },
            { Sort.parallelQuickSort(it, Integer::compare, 100000) },
        )
    )
    println(results)
}


