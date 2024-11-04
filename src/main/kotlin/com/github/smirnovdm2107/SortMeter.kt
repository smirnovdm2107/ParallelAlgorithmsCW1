package com.github.smirnovdm2107

object SortMeter {

    fun <T> meterAll(
        arr: List<T>,
        iter: Int,
        sorters: List<(MutableList<T>) -> Unit>
    ): List<Double> {
        return sorters.map { meter(arr, iter, it) }
    }

    // return avg nanos
    private fun <T> meter(
        arr: List<T>,
        iter: Int,
        sorter: (MutableList<T>) -> Unit
    ): Double {
        var sum = 0L
        System.gc()
        Thread.sleep(1000)
        // warming up the goys
        println("start warmup")
        meter(arr, sorter)
        repeat(iter) {
            println(it)
            sum += meter(arr, sorter)
        }
        return sum.toDouble().div(iter)
    }

    // return nanos
    private fun <T> meter(
        arr: List<T>,
        sorter: (MutableList<T>) -> Unit
    ): Long {
        val startTs = System.currentTimeMillis()
        sorter(arr.toMutableList())
        val endTs = System.currentTimeMillis()
        return endTs - startTs
    }
}