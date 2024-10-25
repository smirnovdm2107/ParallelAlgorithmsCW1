package com.github.smirnovdm2107

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.Collections
import java.util.Random
import java.util.function.UnaryOperator
import java.util.stream.Stream
import kotlin.streams.toList

class QuickSortTest {


    @ParameterizedTest
    @MethodSource("sortMethods")
    fun `test sorted array`(sort: Sorter) {
        val list = listOf(1, 2, 3)
        Assertions.assertEquals(
            list,
            sort(list)
        )
    }

    @ParameterizedTest
    @MethodSource("sortMethods")
    fun `test reverse order`(sort: Sorter) {
        val list = listOf(3, 2, 1)
        Assertions.assertEquals(
            list.sorted(),
            sort(list)
        )
    }

    @ParameterizedTest
    @MethodSource("sortMethods")
    fun `test sorted with duplicate`(sort: Sorter) {
        val list = listOf(1, 2, 2, 3)
        Assertions.assertEquals(
            list,
            sort(list)
        )
    }

    @ParameterizedTest
    @MethodSource("sortMethods")
    fun `test reversed with duplicate`(sort: Sorter) {
        val list = listOf(3, 2, 2, 1)
        Assertions.assertEquals(
            list.sorted(),
            sort(list)
        )
    }

    @ParameterizedTest
    @MethodSource("sortMethods")
    fun `test unordered with duplicate`(sort: Sorter) {
        val list = listOf(2, 2, 3, 1)
        Assertions.assertEquals(
            list.sorted(),
            sort(list)
        )
    }

    @RepeatedTest(100)
    fun `test random from 1 to 100_000`() {
        val list = Random().ints(1, 10_000).limit(10_000).toList()
        val sorted = list.sorted()
        for (sort in sortMethods()) {
            Assertions.assertEquals(
                sorted,
                sort(list)
            )
        }
    }



    companion object {
        @JvmStatic
        fun sortMethods(): Stream<Sorter> = Stream.of(
            Sorter.SequentialSorter,
            Sorter.ParallelSorter(4, 1),
            Sorter.ParallelSorter(4, 2)
        )
    }

    sealed class Sorter {
        abstract operator fun invoke(list: List<Int>): List<Int>

        data object SequentialSorter : Sorter() {
            override fun invoke(list: List<Int>): List<Int> = Sort.sequentialQuickSort(list, Comparator.naturalOrder())
        }

        data class ParallelSorter(
            val parallelism: Int,
            val blockSize: Int
        ) : Sorter() {
            override fun invoke(list: List<Int>): List<Int> = ParallelContext(parallelism).use { it.parallelQuickSort(list, Comparator.naturalOrder(), blockSize) }
        }
    }
}