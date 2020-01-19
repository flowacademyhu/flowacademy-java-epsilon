package hu.flowacademy.epsilon._07_streams;

import java.util.Arrays;
import java.util.Random;

/**
 * Demonstrates using parallel streams. It demonstrates that in many cases,
 * parallelization can even slow things down. In other cases, you'll get a
 * speedup, but it will not be linear (e.g. using 4 cores you might get only
 * 2x the throughput.)
 */
public class ParallelStreamExample {
    public static void main(String[] args) {
        var l = new long[200_000_000];
        var r = new Random(1579464016115L);
        for(int i = 0; i < l.length; ++i) {
            l[i] = r.nextLong();
        }
        for (;;) {
            var t1 = System.nanoTime();
            var avg = Arrays.stream(l).parallel().filter(x -> x % 100000L == 0).findFirst();
            var t2 = System.nanoTime();
            System.out.println(t2 - t1 + "\t" + avg);
        }
    }
}
