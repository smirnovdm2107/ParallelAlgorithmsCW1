package com.github.smirnovdm2107

object SortMeter {

    fun <T> meterAll(
        arraySupplier: () -> List<T>,
        iter: Int,
        sorters: List<(List<T>) -> List<T>>
    ): List<Double> {
        return sorters.map { meter(arraySupplier, iter, it) }
    }

    // return avg nanos
    private fun <T> meter(
        arraySupplier: () -> List<T>,
        iter: Int,
        sorter: (List<T>) -> List<T>
    ): Double {
        var sum = 0L
        // warming up the goys
        meter(arraySupplier, sorter)
        repeat(iter) {
            println(it)
            sum += meter(arraySupplier, sorter)
        }
        return sum.toDouble().div(iter)
    }

    // return nanos
    private fun <T> meter(
        arraySupplier: () -> List<T>,
        sorter: (List<T>) -> List<T>
    ): Long {
        val arr = arraySupplier()
        val startTs = System.nanoTime()
        sorter(arr)
        val endTs = System.nanoTime()
        return endTs - startTs
    }
}