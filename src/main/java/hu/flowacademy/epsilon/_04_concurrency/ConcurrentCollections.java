package hu.flowacademy.epsilon._04_concurrency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Supplier;

// This example shows the correctness and performance of using various maps in a concurrent setting:
// ordinary HashMap, a Collections.synchronizedMap, and a ConcurrentHashMap. It also shows the
// difference between using operations with atomic guarantees versus manually composing operations.
public class ConcurrentCollections {
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    // Uses an executor service to run tasks in parallel
    private static void runWithExecServiceAndLatch(int taskCount, Runnable r, Supplier<Object> value) throws InterruptedException {
        CountDownLatch ready = new CountDownLatch(taskCount);
        CountDownLatch go = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(taskCount);

        Runnable r2 = () -> {
            ready.countDown();
            try {
                go.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            try {
                r.run();
            } finally {
                done.countDown();
            }
        };
        for (int i = 0; i < taskCount; ++i) {
            executorService.execute(r2);
        }

        ready.await();
        long t1 = System.nanoTime();
        go.countDown();
        done.await();
        long t2 = System.nanoTime();
        System.out.println((t2 - t1) + "\t" + value.get());
    }

    // This is also a prime example of using Runnable as a Command pattern.
    private static Runnable doManyTimes(Runnable r) {
        return () -> {
            for(int i = 0; i < 3000000; ++i) {
                r.run();
            }
        };
    }

    private static void run(int taskCount, Runnable r, Supplier<Object> value) throws InterruptedException {
        runWithExecServiceAndLatch(taskCount, doManyTimes(r), value);
    }

    // This strategy populates a map using a Map.get followed by a Map.put if
    // the value is not found. This is not recommended, and using it we see that
    // even ConcurrentHashMap won't give the expected results.
    private static final void populateMapNonAtomic(Map<Integer, LongAdder> map) {
        var key = ThreadLocalRandom.current().nextInt(1000);
        var adder = map.get(key);
        if (adder == null) {
            adder = new LongAdder();
            map.put(key, adder);
        }
        adder.increment();
    }

    // This strategy populates a map using Map.computeIfAbsent and uses
    // LongAdder as values. It works correctly for synchronized maps and
    // for ConcurrentHashMap.
    private static final void populateMap(Map<Integer, LongAdder> map) {
        var key = ThreadLocalRandom.current().nextInt(1000);
        map.computeIfAbsent(key, x -> new LongAdder()).increment();
    }

    // This strategy uses Map.merge to both compute initial values and
    // increment values. Instead of a LongAdder it uses ordinary Integer
    // values for its values. It works correctly for synchronized maps and
    // for ConcurrentHashMap. In our example, {@link #populateMap} will
    // actually be faster as contention on the map will only happen when the
    // key was absent, and also LongAdder can allow multiple concurrent updates.
    // This variant is included to show that even with a non-concurrent class as
    // the value (Integer vs. LongAdder) this strategy still works.
    private static final void populateMapCompute(Map<Integer, Integer> map) {
        var key = ThreadLocalRandom.current().nextInt(1000);
        map.merge(key, 1, Integer::sum);
    }

    private static final long summarizeValues(Map<Integer, LongAdder> map) {
        return map.values().stream().mapToLong(LongAdder::longValue).sum();
    }
    private static final long summarizeValues2(Map<Integer, Integer> map) {
        return map.values().stream().mapToLong(Integer::longValue).sum();
    }

    public static void main(String[] args) throws Exception {
        var concurrency = 10;
        run(concurrency, () -> {}, () -> 0); // noop, warmup

        // Using an ordinary HashMap will get us wrong results. HashMap is not safe
        // for concurrent use.
        var map1 = new HashMap<Integer, LongAdder>();
        run(concurrency, () -> populateMapNonAtomic(map1), () -> summarizeValues(map1));

        // NOTE: we didn't run an ordinary HashMap with populateMap(). The reason
        // for this is that HashMap.computeIfAbsent() will throw a
        // ConcurrentModificationException if the map is modified while it executes
        // the method.

        // Just wrapping a HashMap in Collections.synchronizedMap will still not work,
        // because get() and put() will happen in separate synchronized blocks so the
        // get+put composite operation won't be atomic.
        var map2 = Collections.synchronizedMap(new HashMap<Integer, LongAdder>());
        run(concurrency, () -> populateMapNonAtomic(map2), () -> summarizeValues(map2));

        // Wrapping a HashMap in Collections.synchronizedMap and using computeIfAbsent
        // works correctly. It will be slow, though.
        var map3 = Collections.synchronizedMap(new HashMap<Integer, LongAdder>());
        run(concurrency, () -> populateMap(map3), () -> summarizeValues(map3));

        // Using ConcurrentHashMap with non-atomic operation also doesn't work correctly
        // for the same reason it didn't with Collections.synchronizedMap
        var map4 = new ConcurrentHashMap<Integer, LongAdder>();
        run(concurrency, () -> populateMapNonAtomic(map4), () -> summarizeValues(map4));

        // Using ConcurrentHashMap with computeIfAbsent works. This variant with
        // using LongAdder to contain the values is the best of all in a concurrent
        // environment.
        var map5 = new ConcurrentHashMap<Integer, LongAdder>();
        run(concurrency, () -> populateMap(map5), () -> summarizeValues(map5));

        // Using ConcurrentHashMap with compute() works with Integer keys too and not
        // just with LongAdder. This variant shows that atomicity of compute() protects
        // correctness even if the class of the values (Integer) is itself not
        // concurrency-aware. However, the previous example (map5) is the fastest in a
        // concurrent environment.
        var map6 = new ConcurrentHashMap<Integer, Integer>();
        run(concurrency, () -> populateMapCompute(map6), () -> summarizeValues2(map6));

        executorService.shutdown();
    }
}
