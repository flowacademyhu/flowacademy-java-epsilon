package hu.flowacademy.epsilon._04_concurrency;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Supplier;

// This example shows the correctness and performance of various strategies for incrementing a shared
// integer variable in a concurrent setting: no coordination, synchronization, AtomicInt and finally
// LongAdder. It also shows three various strategies for concurrently executing tasks: direct control
// of threads, using an executor service and wait/notify, and finally using an executor service and
// CountdownLatch.
public class ThreadingExample2 {
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

    public static void main(String[] args) throws Exception {
        run(10, () -> {}, () -> 0); // noop, warmup

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
        MutableInt mi1 = new MutableInt();
        run(10, mi1::increment, mi1::get);

        // This example shows that synchronizing produces correct results. We synchronize on each
        // iteration of the loop. We can see this is VERY costly. This is by far the slowest
        // example of them all.
        MutableInt mi2 = new MutableInt();
        run(10, mi2::syncedIncrement, mi2::get);

        // This example shows that we can often avoid synchronization using java.util.concurrent.Atomic* classes.
        AtomicInteger atomicInt = new AtomicInteger();
        run(10, atomicInt::incrementAndGet, atomicInt::get);

        // This example shows that we can get even better performance using LongAdder. (See also LongAccumulator.)
        LongAdder longAdder = new LongAdder();
        run(10, longAdder::increment, longAdder::longValue);

        executorService.shutdown();
    }
}
