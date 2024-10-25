package com.github.smirnovdm2107

import kotlin.random.Random

object Sort {
    fun <T> sequentialQuickSort(arr: List<T>, comparator: Comparator<in T>): List<T> {
        if (arr.size <= 1) {
            return arr
        }
        val x = arr[Random.nextInt(arr.size)]
        val left = arr.filter { comparator.compare(it, x) < 0 }
        val middle = arr.filter { comparator.compare(it, x) == 0 }
        val right = arr.filter { comparator.compare(it, x) > 0 }
        val sortedLeft = sequentialQuickSort(left, comparator)
        val sortedRight = sequentialQuickSort(right, comparator)
        return sortedLeft + middle + sortedRight
    }
}