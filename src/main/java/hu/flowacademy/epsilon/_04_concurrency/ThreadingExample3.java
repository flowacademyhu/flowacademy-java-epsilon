package hu.flowacademy.epsilon._04_concurrency;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import java.util.function.Supplier;

// This example shows the correctness and performance of various strategies for incrementing a shared
// integer variable in a concurrent setting: no coordination, synchronization, AtomicInt and finally
// LongAdder. It also shows three various strategies for concurrently executing tasks: direct control
// of threads, using an executor service and wait/notify, and finally using an executor service and
// CountdownLatch.
public class ThreadingExample3 {
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

    private static void observeMaximum(Consumer<Long> accumulator) {
        // ThreadLocalRandom is much better than Random.
        Random r = ThreadLocalRandom.current();
        for(int i = 0; i < 3000000; ++i) {
            var l = r.nextLong();
            accumulator.accept(l);
        }
    }

    private static void run(int taskCount, Consumer<Long> accumulator, Supplier<Object> value) throws InterruptedException {
        runWithExecServiceAndLatch(taskCount, () -> observeMaximum(accumulator), value);
    }

    private static final class MutableLong {
        private long l = Long.MIN_VALUE;

        void max(long l2) {
            if (l2 > l) {
                l = l2;
            }
        }

        synchronized void syncedMax(long l2) {
            max(l2);
        }

        long get() {
            return l;
        }
    }

    public static void main(String[] args) throws Exception {
        var concurrency = 20;

        run(concurrency, (x) -> {}, () -> 0); // noop, warmup

        // This example shows you how concurrent execution is much harder to write
        // correctly than a single-threaded execution. We start 10 threads, each of
        // them increments a shared variable 3 million times. The end result, however,
        // will not be 30 million, rather it will be some random number between 0 and
        // 30 million. The reasons for this are twofold. First: the increment operation is
        // NOT ATOMIC; rather every thread reads the counter, increments it, and writes
        // it back. So it's easy to have multiple threads read the same value, increment
        // it and write it back. The counter can even go backwards if the operating
        // system suspends a thread holding an old value and then resumes it later.
        // The second reason increments are not consistent is MEMORY HIERARCHY.
        // In absence of instructions of how to handle concurrency, the "count" value
        // on every thread is allowed to be stored in a CPU register, or L1 or L2 cache
        // indefinitely, and there is no requirement that every thread writes it back all
        // the way to main RAM all the time.
        MutableLong ml1 = new MutableLong();
        run(concurrency, ml1::max, ml1::get);

        // This example shows that synchronizing produces correct results. We synchronize on each
        // iteration of the loop. We can see this is VERY costly. This is by far the slowest
        // example of them all.
        MutableLong ml2 = new MutableLong();
        run(concurrency, ml2::syncedMax, ml2::get);

        // This example shows updating to maximum with compareAndExchange
        AtomicLong al1 = new AtomicLong(Long.MIN_VALUE);
        run(concurrency, (l) -> {
            var lc = al1.get();
            while (Long.max(lc, l) != lc) {
                lc = al1.compareAndExchange(lc, l);
            }
        }, al1::get);

        // This example shows updating to maximum with AtomicLong.accumulateAndGet
        AtomicLong al2 = new AtomicLong(Long.MIN_VALUE);
        run(concurrency, (l) -> al2.accumulateAndGet(l, Long::max), al2::get);

        // This example shows updating to maximum with LongAccumulator
        LongAccumulator lacc = new LongAccumulator(Long::max, Long.MIN_VALUE);
        run(concurrency, lacc::accumulate, lacc::longValue);

        executorService.shutdown();
    }
}
