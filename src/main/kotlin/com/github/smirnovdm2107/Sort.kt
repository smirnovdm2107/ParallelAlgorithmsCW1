package com.github.smirnovdm2107

import java.util.Stack

object Sort {

    fun <T> sequentialQuickSort(
        arr: MutableList<T>,
        comparator: Comparator<in T>,
        stack: Stack<MutableList<T>> = Stack<MutableList<T>>()
    ) {
        if (arr.size <= 1) {
            return
        }
        val m = partition(arr, comparator)
        if (arr.size == 2) {
            return
        }
        sequentialQuickSort(arr.subList(0, m + 1), comparator, stack)
        sequentialQuickSort(arr.subList(m + 1, arr.size), comparator, stack)
    }

    fun <T> parallelQuickSort(
        arr: MutableList<T>,
        comparator: Comparator<in T>,
        blockSize: Int
    ) {
        if (arr.size <= blockSize) {
            sequentialQuickSort(arr, comparator)
            return
        }

        val m = partition(arr, comparator)
        if (arr.size == 2) {
            return
        }
        val sortedLeftFuture = forkJoin.submit { parallelQuickSort(arr.subList(0, m + 1), comparator, blockSize) }
        val sortedRightFuture = forkJoin.submit { parallelQuickSort(arr.subList(m + 1, arr.size), comparator, blockSize) }

        sortedLeftFuture.join()
        sortedRightFuture.join()
    }

    private fun <T> partition(arr: MutableList<T>, comparator: Comparator<in T>): Int {
        val t = arr[arr.size / 2]
        var i = 0
        var j = arr.size - 1
        while (i <= j) {
            while (comparator.compare(arr[i], t) < 0) {
                i++
            }
            while (comparator.compare(arr[j], t) > 0) {
                j--
            }
            if (i >= j) {
                break
            }
            val tmp = arr[i]
            arr[i] = arr[j]
            arr[j] = tmp
            i++
            j--
        }
        return j
    }

}