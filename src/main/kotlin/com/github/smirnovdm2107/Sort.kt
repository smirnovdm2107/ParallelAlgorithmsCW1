package com.github.smirnovdm2107

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.ThreadLocalRandom
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
        blockSize: Int
    ) {
        parallelQuickSort2(arr, 0, arr.size, blockSize)
    }

    private fun ForkJoinPool.parallelQuickSort2(
        arr: IntArray,
        l: Int,
        r: Int,
        blockSize: Int
    ) {
        if (r - l <= blockSize) {
            sequentialQuickSort(arr, l, r)
            return
        }
        val pivot = arr[Random.nextInt(l, r)]
        val left = parallelFilter(arr, l, r, blockSize) { it < pivot }
        val middle = parallelFilter(arr, l, r, blockSize) { it == pivot }
        val right = parallelFilter(arr, l, r, blockSize) { it > pivot }
        if (r - l == 2) {
            return
        }
        parallelFor(r - l) {
            if (it < left.size) {
                arr[l + it] = left[it]
            } else if (it < left.size + middle.size) {
                arr[l + it] = middle[it - left.size]
            } else {
                arr[l + it] = right[it - left.size - middle.size]
            }
        }
        val sortedLeftFuture = submit { parallelQuickSort(arr, l, l + left.size, blockSize) }
        val sortedRightFuture = submit { parallelQuickSort(arr, r - right.size, r, blockSize) }

        sortedLeftFuture.join()
        sortedRightFuture.join()
    }

    private fun partition2(
        arr: IntArray,
        l: Int,
        r: Int
    ) {

    }

}