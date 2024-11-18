
Comparasion of 3 algorithms wit sorting array of ints (size = 10e8):
1) ParallelQuickSort - parallel quick sort with 4 threads and sequential partition
2) ParallelQuickSort2 - parallel quick sort with 4 threads and parallel partition
3) SequentialQuickSort - sequential quick sort

| Benchmark                   | Mode                    | Cnt  | Score | Error | Units|
|-----------------------------|-------------------------|------|-------|-------|------|
 | ParallelQuickSort2          | avgt                    | 5    |  42877.420 ± |  6385.146 | ms/op |
| ParallelQuickSort           | avgt |    5 |    3676.072 ± |  854.568 |  ms/op |
| SequentialQuickSort         |  avgt |    5 |   11853.940 ± |   210.409 |  ms/op |

To start benchmark test use `./gradlew jmh`
