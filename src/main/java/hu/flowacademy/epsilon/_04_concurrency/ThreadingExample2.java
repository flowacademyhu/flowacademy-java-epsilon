package hu.flowacademy.epsilon._04_concurrency;

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
public class ThreadingExample2 {
    private static int count = 0;

    private static void increment() {
        for(int i = 0; i < 3000000; ++i) {
            count = count + 1;
        }
    }
    public static void main(String[] args) throws Exception {
        Thread[] threads = new Thread[10];
        for(int i = 0; i < threads.length; ++i) {
            threads[i] = new Thread(ThreadingExample2::increment);
        }
        for(Thread t: threads) {
            t.start();
        }
        for(Thread t: threads) {
            t.join();
        }
        System.out.println(count);
    }
}
