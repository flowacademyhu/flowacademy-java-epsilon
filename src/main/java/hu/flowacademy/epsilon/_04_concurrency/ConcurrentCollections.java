package hu.flowacademy.epsilon._04_concurrency;

import java.util.ArrayList;
import java.util.Collections;
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

// This example shows the correctness and performance of various strategies for incrementing a shared
// integer variable in a concurrent setting: no coordination, synchronization, AtomicInt and finally
// LongAdder. It also shows three various strategies for concurrently executing tasks: direct control
// of threads, using an executor service and wait/notify, and finally using an executor service and
// CountdownLatch.
public class ConcurrentCollections {
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    // Uses direct thread control to run tasks in parallel
    private static void runWithThreads(int threadCount, Runnable r, Supplier<Object> value) throws InterruptedException {
        Thread[] threads = new Thread[threadCount];
        for(int i = 0; i < threads.length; ++i) {
            threads[i] = new Thread(r);
        }
        // Always track elapsed time using nanoTime
        long t1 = System.nanoTime();

        for(Thread t: threads) {
            t.start();
        }
        for(Thread t: threads) {
            t.join();
        }
        long t2 = System.nanoTime();
        System.out.println((t2 - t1) + "\t" + value.get());
    }

    private static final class Coordination {
        private int ready;
        private boolean go;
        private int done;

        Coordination(int tasks) {
            ready = tasks;
            go = false;
            done = tasks;
        }

        synchronized void markReady() {
            if (--ready == 0) {
                notifyAll();
            }
        }

        synchronized void waitReady() throws InterruptedException {
            while (ready > 0) {
                wait();
            }
        }

        synchronized void markGo() {
            go = true;
            notifyAll();
        }

        synchronized void waitGo() throws InterruptedException {
            while (!go) {
                wait();
            }
        }

        synchronized void markDone() {
            if (--done == 0) {
                notifyAll();
            }
        }

        synchronized void waitDone() throws InterruptedException {
            while (done > 0) {
                wait();
            }
        }
    }

    // Uses an executor service to run tasks in parallel, and uses wait/notify to coordinate start
    private static void runWithExecServiceAndWaitNotify(int taskCount, Runnable r, Supplier<Object> value) throws InterruptedException {
        Coordination coord = new Coordination(taskCount);

        Runnable r2 = () -> {
            coord.markReady();
            try {
                coord.waitGo();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            try {
                r.run();
            } finally {
                coord.markDone();
            }
        };
        for (int i = 0; i < taskCount; ++i) {
            executorService.execute(r2);
        }
        coord.waitReady();
        long t1 = System.nanoTime();
        coord.markGo();
        coord.waitDone();
        long t2 = System.nanoTime();
        System.out.println((t2 - t1) + "\t" + value.get());
    }

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

    private static final class MutableInt {
        private int i = 0;

        void increment() {
            i++;
        }

        void syncedIncrement() {
            synchronized (this) {
                i++;
            }
        }

        int get() {
            return i;
        }
    }

    private static final void populateMap(Map<Integer, LongAdder> map) {
        var rnd = ThreadLocalRandom.current();
        var key = rnd.nextInt(1000);
        map.computeIfAbsent(key, x -> new LongAdder()).increment();
    }

    private static final void populateMapCompute(Map<Integer, Integer> map) {
        var rnd = ThreadLocalRandom.current();
        var key = rnd.nextInt(1000);
        map.compute(key, (x, oldValue) -> {
            if (oldValue == null) {
                return 1;
            } else {
                return oldValue + 1;
            }
        });
    }

    private static final void populateMapNonAtomic(Map<Integer, LongAdder> map) {
        var rnd = ThreadLocalRandom.current();
        var key = rnd.nextInt(1000);
        var adder = map.get(key);
        if (adder == null) {
            adder = new LongAdder();
            map.put(key, adder);
        }
        adder.increment();
    }

    private static final long summarizeValues(Map<Integer, LongAdder> map) {
        return map.values().stream().mapToLong(LongAdder::longValue).sum();
    }
    private static final long summarizeValues2(Map<Integer, Integer> map) {
        return map.values().stream().mapToLong(Integer::longValue).sum();
    }

    public static void main(String[] args) throws Exception {
/*
        var concurrency = 10;
        run(concurrency, () -> {}, () -> 0); // noop, warmup

        var map1 = new HashMap<Integer, LongAdder>();
        run(concurrency, () -> populateMapNonAtomic(map1), () -> summarizeValues(map1));

        var map2 = Collections.synchronizedMap(new HashMap<Integer, LongAdder>());
        run(concurrency, () -> populateMapNonAtomic(map2), () -> summarizeValues(map2));

        var map3 = Collections.synchronizedMap(new HashMap<Integer, LongAdder>());
        run(concurrency, () -> populateMap(map3), () -> summarizeValues(map3));

        var map4 = new ConcurrentHashMap<Integer, LongAdder>();
        run(concurrency, () -> populateMapNonAtomic(map4), () -> summarizeValues(map4));

        var map5 = new ConcurrentHashMap<Integer, LongAdder>();
        run(concurrency, () -> populateMap(map5), () -> summarizeValues(map5));

        var map6 = new ConcurrentHashMap<Integer, Integer>();
        run(concurrency, () -> populateMapCompute(map6), () -> summarizeValues2(map6));
*/

        var a = new CopyOnWriteArrayList<Integer>();
        a.add(1);
        a.add(2);
        a.add(3);
        for(var i: a) {
            System.out.println(i);
            a.add(i + 3);
        }
        for(var i: a) {
            System.out.println(i);
            a.add(i + 3);
        }

        executorService.shutdown();
    }
}
