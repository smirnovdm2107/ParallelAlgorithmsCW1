
Comparasion of 3 algorithms wit sorting array of ints (size = 10e8):
1) ParallelQuickSort - parallel quick sort with 4 threads and sequential partition
2) ParallelQuickSort - parallel quick sort with 4 threads and parallel partition
3) SequentialQuickSort - sequential quick sort

| Benchmark                   | Mode                    | Cnt  | Score | Error | Units|
|-----------------------------|-------------------------|------|-------|-------|------|
 | ParallelQuickSort2          | avgt                    | 5    |   5553.630 ±  | 669.311 | ms/op |
| ParallelQuickSort           | avgt |    5 |   4066.204 ± |  1267.669 |  ms/op |
| SequentialQuickSort         |  avgt |    5 |  11710.973 ± |  164.767 |  ms/op |

To start benchmark test use `./gradlew jmh`
