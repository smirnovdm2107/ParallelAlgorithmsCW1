package com.github.smirnovdm2107

import com.github.smirnovdm2107.Sort.parallelQuickSort
import com.github.smirnovdm2107.Sort.parallelQuickSort2
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.Random
import java.util.concurrent.ForkJoinPool
import java.util.stream.Stream
import kotlin.streams.toList


class QuickSortTest {


    @ParameterizedTest
    @MethodSource("sortMethods")
    fun `test sorted array`(sort: Sorter) {
        val list = listOf(1, 2, 3)
        checkSort(sort, list)
    }

    @ParameterizedTest
    @MethodSource("sortMethods")
    fun `test reverse order`(sort: Sorter) {
        val list = listOf(3, 2, 1)
        checkSort(sort, list)
    }

    @ParameterizedTest
    @MethodSource("sortMethods")
    fun `test sorted with duplicate`(sort: Sorter) {
        val list = listOf(1, 2, 2, 3)
        checkSort(sort, list)
    }

    @ParameterizedTest
    @MethodSource("sortMethods")
    fun `test reversed with duplicate`(sort: Sorter) {
        val list = listOf(3, 2, 2, 1)
        checkSort(sort, list)
    }

    @ParameterizedTest
    @MethodSource("sortMethods")
    fun `test unordered with duplicate`(sort: Sorter) {
        val list = listOf(2, 2, 3, 1)
        checkSort(sort, list)
    }

    @RepeatedTest(100)
    fun `test random from 1 to 10_000`() {
        val list = Random().ints(1, 10_000).limit(10_000).toList()
        for (sort in sortMethods()) {
            checkSort(sort, list)
        }
    }


    private fun checkSort(
        sorter: Sorter,
        list: List<Int>
    ) {
        val arr = list.toIntArray()
        val sorted = arr.sorted().toIntArray()
        sorter(arr)
        Assertions.assertArrayEquals(
            sorted,
            arr
        )
    }

    companion object {
        @JvmStatic
        fun sortMethods(): Stream<Sorter> = Stream.of(
            Sorter.SequentialSorter,
            Sorter.ParallelSorter(4, 1),
            Sorter.ParallelSorter(4, 2),
            Sorter.ParallelSorter2(4, 1),
            Sorter.ParallelSorter2(4, 2)
        )
    }

    sealed class Sorter {
        abstract operator fun invoke(arr: IntArray)

        data object SequentialSorter : Sorter() {
            override fun invoke(arr: IntArray) = Sort.sequentialQuickSort(arr)
        }

        data class ParallelSorter(
            val parallelism: Int,
            val blockSize: Int
        ) : Sorter() {
            override fun invoke(arr: IntArray) {
                val pool = ForkJoinPool(parallelism)
                pool.parallelQuickSort(arr, blockSize)
            }
        }

        data class ParallelSorter2(
            val parallelism: Int,
            val blockSize: Int
        ): Sorter() {
            override fun invoke(arr: IntArray) {
                val pool = ForkJoinPool(parallelism)
                pool.parallelQuickSort2(arr, blockSize)
            }
        }
    }
}