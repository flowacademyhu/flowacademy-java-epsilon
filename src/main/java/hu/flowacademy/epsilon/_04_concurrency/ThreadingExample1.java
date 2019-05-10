package hu.flowacademy.epsilon._04_concurrency;

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
        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Finished");
    }
}
