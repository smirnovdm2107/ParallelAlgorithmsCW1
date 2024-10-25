package com.github.smirnovdm2107

import java.util.Collections
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit
import kotlin.math.sqrt


class ParallelContext(
    parallelism: Int
): AutoCloseable {
    private val forkJoin = ForkJoinPool(parallelism)  //ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, false, parallelism, parallelism, parallelism, null, 60, TimeUnit.SECONDS)


    fun parallelFor(n: Int, blockSize: Int = sqrt(n.toDouble()).toInt(), block: (Int) -> Unit) {
        parallelFor(0, n, blockSize, block)
    }

    private fun parallelFor(l: Int, r: Int, blockSize: Int = sqrt((r - l).toDouble()).toInt(), block: (Int) -> Unit) {
        if (r - l <= blockSize) {
            for (i in l until r) {
                block(i)
            }
            return
        }
        val m = (l + r) / 2
        val f1 = forkJoin.submit {
            parallelFor(l, m, blockSize, block)
        }
        val f2 = forkJoin.submit {
            parallelFor(m, r, blockSize, block)
        }
        f1.join()
        f2.join()
    }

    fun <T> parallelReduce(arr: List<T>, blockSize: Int = sqrt(arr.size.toDouble()).toInt(), reduce: (T, T) -> T): T {
        return parallelReduce(arr, 0, arr.size, blockSize, reduce)
    }

    private fun <T> parallelReduce(
        arr: List<T>,
        l: Int,
        r: Int,
        blockSize: Int = sqrt(arr.size.toDouble()).toInt(),
        reduce: (T, T) -> T
    ): T {
        if (r - l <= blockSize) {
            var res = arr[l]
            for (i in (l + 1) until r) {
                res = reduce(res, arr[i])
            }
            return res
        }
        val m = (l + r) / 2
        val f1 = forkJoin.submit<T> {
            parallelReduce(arr, l, m, blockSize, reduce)
        }
        val f2 = forkJoin.submit<T> {
            parallelReduce(arr, m, r, blockSize, reduce)
        }
        return reduce(f1.join(), f2.join())
    }

    fun <T, R> parallelMap(
        arr: List<T>,
        blockSize: Int = sqrt(arr.size.toDouble()).toInt(),
        mapper: (T) -> R
    ): List<R> {
        val result: MutableList<Any?> = (arrayOfNulls<Any?>(arr.size)).toMutableList()
        parallelMap(arr, 0, arr.size, mapper, result, blockSize)
        return result as List<R>
    }

    private fun <T, R> parallelMap(
        arr1: List<T>,
        l: Int,
        r: Int,
        mapper: (T) -> R,
        arr2: MutableList<R>,
        blockSize: Int = sqrt(arr1.size.toDouble()).toInt()
    ) {
        if (r - l <= blockSize) {
            for (i in l until r) {
                arr2[i] = mapper(arr1[i])
            }
            return
        }
        val m = (l + r) / 2
        val f1 = forkJoin.submit {
            parallelMap(arr1, l, m, mapper, arr2)
        }
        val f2 = forkJoin.submit {
            parallelMap(arr1, m, r, mapper, arr2)
        }
        f1.join()
        f2.join()
    }

    fun <T> parallelScan(
        arr: List<T>,
        zero: T,
        blockSize: Int = sqrt(arr.size.toDouble()).toInt(),
        reduce: (T, T) -> T
    ): List<T> {
        val result: MutableList<Any?> = arrayOfNulls<Any>(arr.size).toMutableList()
        parallelFor(arr.size, blockSize) {
            result[it] = arr[it]
        }
        val resultT = result as MutableList<T>
        upScan(resultT, 0, arr.size, blockSize, reduce)
        result[arr.size - 1] = zero
        downScan(resultT, 0, arr.size, blockSize, reduce)
        return resultT
    }

    fun <T> upScan(
        arr: MutableList<T>,
        l: Int,
        r: Int,
        blockSize: Int = sqrt(arr.size.toDouble()).toInt(),
        reduce: (T, T) -> T
    ): T {
        if (r - l == 1) {
            return arr[l]
        }
        val m = (l + r) / 2
        val left: T
        val right: T
        if (r - l < blockSize) {
            left = upScan(arr, l, m, blockSize, reduce)
            right = upScan(arr, m, r, blockSize, reduce)
        } else {
            val leftF = forkJoin.submit<T> {
                upScan(arr, l, m, blockSize, reduce)
            }
            val rightF = forkJoin.submit<T> {
                upScan(arr, m, r, blockSize, reduce)
            }
            left = leftF.join()
            right = rightF.join()
        }
        arr[r - 1] = reduce(left, right)
        return arr[r - 1]
    }

    fun <T> downScan(
        arr: MutableList<T>,
        l: Int,
        r: Int,
        blockSize: Int = sqrt(arr.size.toDouble()).toInt(),
        reduce: (T, T) -> T
    ) {
        if (r - l == 1) {
            return
        }
        val m = (l + r) / 2
        val tmp = arr[m - 1]
        arr[m - 1] = arr[r - 1]
        arr[r - 1] = reduce(arr[r - 1], tmp)
        if (r - l < blockSize) {
            downScan(arr, l, m, blockSize, reduce)
            downScan(arr, m, r, blockSize, reduce)
        } else {
            val leftF = forkJoin.submit {
                downScan(arr, l, m, blockSize, reduce)
            }
            val rightF = forkJoin.submit {
                downScan(arr, m, r, blockSize, reduce)
            }
            leftF.join()
            rightF.join()
        }
    }

    fun <T> parallelFilter(
        arr: List<T>,
        blockSize: Int = sqrt(arr.size.toDouble()).toInt(),
        block: (T) -> Boolean
    ): List<T> {
        val mapped: List<Int> = parallelMap(arr, blockSize) { if (block(it)) 1 else 0 }
        val prefix: List<Int> = parallelScan(mapped, 0, blockSize) { a, b -> a + b }
        val filteredSize = prefix[prefix.size - 1] + mapped[prefix.size - 1]
        val result = arrayOfNulls<Any?>(filteredSize).toMutableList()
        parallelFor(prefix.size - 1, blockSize) {
            if (prefix[it + 1] == prefix[it] + 1) {
                result[prefix[it]] = arr[it]
            }
        }
        if (mapped[mapped.size - 1] == 1) {
            result[result.size - 1] = arr[arr.size - 1]
        }
        return result as List<T>
    }

    fun <T>     parallelQuickSort(arr: List<T>, comparator: Comparator<in T>, blockSize: Int): List<T> {
        if (arr.size <= blockSize) {
            Collections.sort(arr, comparator)
            return arr
        }
        val x = arr[ThreadLocalRandom.current().nextInt(arr.size)]
        val leftFuture = forkJoin.submit<List<T>> { parallelFilter(arr, blockSize) { comparator.compare(it, x) < 0 } }
        val middleFuture = forkJoin.submit<List<T>> { parallelFilter(arr, blockSize) { comparator.compare(it, x) == 0 } }
        val rightFuture = forkJoin.submit<List<T>> { parallelFilter(arr, blockSize) { comparator.compare(it, x) > 0 } }

        val left = leftFuture.join()
        val middle = middleFuture.join()
        val right = rightFuture.join()

        val sortedLeftFuture = forkJoin.submit<List<T>> { parallelQuickSort(left, comparator, blockSize) }
        val sortedRightFuture = forkJoin.submit<List<T>> { parallelQuickSort(right, comparator, blockSize) }

        return sortedLeftFuture.join() + middle + sortedRightFuture.join()
    }

    override fun close() {
        forkJoin.close()
    }
}