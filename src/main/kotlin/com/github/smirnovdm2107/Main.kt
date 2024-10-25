package com.github.smirnovdm2107

import java.util.Collections
import kotlin.math.sqrt

fun main() {
    val arrSize = 10_000_000
    val arrSupplier = { (1..arrSize).shuffled() }
    val iters = 5
    val parallelContext = ParallelContext(4)
    val blockSize = sqrt(arrSize.toDouble()).toInt()

    val results = SortMeter.meterAll(
        arrSupplier,
        iters,
        listOf(
            { Sort.sequentialQuickSort(it, Integer::compare) },
            { parallelContext.parallelQuickSort(it, Integer::compare, blockSize) }
        )
    )
    println(results)
}


