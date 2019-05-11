package hu.flowacademy.epsilon._04_concurrency;

// Simples example showing execution running on two threads in parallel.
public class ThreadingExample1 {
    public static void main(String[] args) throws Exception {

        var t1 = new Thread(new Runnable() {
            @Override public void run() {
                for(int i = 0; i < 10; ++i) {
                    System.out.println("Pom");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        ;
                    }
                }
            }
        });

        var t2 = new Thread(new Runnable() {
            @Override public void run() {
                try {
                    for(int i = 0; i < 3; ++i) {
                        System.out.println("Pom pom");
                        Thread.sleep(3000);
                    }
                } catch (InterruptedException e) {
                    ;
                }
            }
        });

        // Threads don't start running automatically, we need to start them.
        t1.start();
        t2.start();

        // We can - but don't have to - wait until threads we started terminate.
        // We do that with the .join() method on running threads. Removing these
        // join commands will let main() exit immediately after it started the
        // threads, but you'll notice that the program doesn't exit then - it will
        // keep running until the two threads we started are done. As a general
        // rule, a Java program runs until all of its threads have exited.
        t1.join();
        t2.join();

        System.out.println("Finished");
    }
}
