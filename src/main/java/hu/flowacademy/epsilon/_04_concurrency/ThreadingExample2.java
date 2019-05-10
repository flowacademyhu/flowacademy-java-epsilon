package hu.flowacademy.epsilon._04_concurrency;

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
