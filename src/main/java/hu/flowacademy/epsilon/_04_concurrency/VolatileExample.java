package hu.flowacademy.epsilon._04_concurrency;

public class VolatileExample {
    private volatile boolean stop = false;
    private long i = 0;

    public static void main(String[] args) throws Exception {
        new VolatileExample().run();
    }

    public void loop(){
        while(!stop) {
            i += 1;
        }
        System.out.print("stopped");
    }

    public void loop2() {
        while(!stop) {
            System.out.println(i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
        System.out.print("stopped counter");
    }

    public void run() throws InterruptedException {
        new Thread(this::loop).start();
        new Thread(this::loop2).start();
        Thread.sleep(10000);
        stop = true;
    }
}
