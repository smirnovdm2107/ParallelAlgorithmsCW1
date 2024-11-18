package com.github.smirnovdm2107

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.sqrt
import kotlin.random.Random

object Sort {


    fun sequentialQuickSort(
        arr: IntArray
    ) {
        sequentialQuickSort(arr, 0, arr.size)
    }

    private fun sequentialQuickSort(
        arr: IntArray,
        l: Int,
        r: Int
    ) {
        if (r - l <= 1) {
            return
        }
        val m = partition(arr, l, r)
        if (r - l == 2) {
            return
        }
        sequentialQuickSort(arr, l, m + 1)
        sequentialQuickSort(arr, m + 1, r)
    }

    fun ForkJoinPool.parallelQuickSort(
        arr: IntArray,
        blockSize: Int
    ) {
        parallelQuickSort(arr, 0, arr.size, blockSize)
    }

    private fun ForkJoinPool.parallelQuickSort(
        arr: IntArray,
        l: Int,
        r: Int,
        blockSize: Int
    ) {
        if (r - l <= blockSize) {
            sequentialQuickSort(arr, l, r)
            return
        }

        val m = partition(arr, l, r)
        if (r - l == 2) {
            return
        }
        val sortedLeftFuture = submit { parallelQuickSort(arr, l, m + 1, blockSize) }
        val sortedRightFuture = submit { parallelQuickSort(arr, m + 1, r, blockSize) }

        sortedLeftFuture.join()
        sortedRightFuture.join()
    }

    private fun partition(arr: IntArray, l: Int, r: Int): Int {
        val t = arr[(l + r) / 2]
        var i = l
        var j = r - 1
        while (i <= j) {
            while (i < arr.size && arr[i] < t) {
                i++
            }
            while (j >= 0 && arr[j] > t) {
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

    fun ForkJoinPool.parallelQuickSort2(
        arr: IntArray,
        sandbox: IntArray,
        blockSize: Int = sqrt(arr.size.toDouble()).toInt()
    ) {
        parallelQuickSort2(arr, 0, arr.size, blockSize, sandbox)
    }

    private fun ForkJoinPool.parallelQuickSort2(
        arr: IntArray,
        l: Int,
        r: Int,
        blockSize: Int,
        sandbox: IntArray
    ) {
        if (r - l <= blockSize) {
            sequentialQuickSort(arr, l, r)
            return
        }
        val pivot = arr[r - 1]
        val realSize = r - l
        val lessSize = parallelFilter2(arr, l, r, sandbox, l * 2, sandbox, l * 2 + realSize) { it < pivot }
        val eqSize = parallelFilter2(arr, l, r, sandbox, l * 2, sandbox, l * 2 + realSize + lessSize) { it == pivot }
        val moreSize = parallelFilter2(arr, l, r, sandbox, l * 2, sandbox, l * 2 + realSize + lessSize + eqSize) { it > pivot }
        parallelFor(r - l) {
            arr[l + it] = sandbox[l * 2 + realSize + it]
        }
        val sortedLeftFuture = submit { parallelQuickSort2(arr, l, l + lessSize, blockSize, sandbox) }
        val sortedRightFuture = submit { parallelQuickSort2(arr, r - moreSize, r, blockSize, sandbox) }

        sortedLeftFuture.join()
        sortedRightFuture.join()
    }

    private fun ForkJoinPool.parallelFilter2(
        arr: IntArray,
        l1: Int,
        r1: Int,
        sandbox: IntArray,
        l2: Int,
        result: IntArray,
        l3: Int,
        block: (Int) -> Boolean,
    ): Int {
        if (r1 - l1 == 0) {
            return 0
        }
        val realSize = r1 - l1
        parallelFor(realSize) {
            sandbox[l2 + it] = if (block(arr[l1 + it])) 1 else 0
        }
        val maxElem = parallelScan(sandbox, l2, l2 + realSize) { a, b -> a + b }
        if (maxElem == 0) {
            return 0
        }
        val last = block(arr[r1 - 1])
        parallelFor(realSize - 1) {
            if (sandbox[l2 + it + 1] == sandbox[l2 + it] + 1) {
                result[l3 + sandbox[l2 + it]] = arr[l1 + it]
            }
        }
        if (last) {
            result[l3 + maxElem - 1] = arr[r1 - 1]
        }
        return maxElem
    }
}