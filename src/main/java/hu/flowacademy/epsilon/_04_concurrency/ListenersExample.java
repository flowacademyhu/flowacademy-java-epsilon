package hu.flowacademy.epsilon._04_concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class ListenersExample {
    private final List<Consumer<String>> listeners = new CopyOnWriteArrayList<>();

    public void addListener(Consumer<String> l) {
        listeners.add(l);
    }

    public void removeListener(Consumer<String> l) {
        listeners.remove(l);
    }

    public void fireEvent(String event) {
        for(var l: listeners) {
            l.accept(event);
        }
    }

    public static void main(String[] args) {
        var ex = new ListenersExample();

        ex.addListener(new Consumer<String>() {
            @Override public void accept(String event) {
                if (event.equals("cake")) {
                    System.out.println("I got my cake, thank you!");
                    ex.removeListener(this);
                }
            }
        });

        ex.addListener(event -> {
            if (event.equals("drink")) {
                System.out.println("glug, glug");
            }
        });

        ex.addListener(event -> {
            if (event.equals("porridge")) {
                System.out.println("I really don't like this");
            }
        });

        ex.fireEvent("soup");
        ex.fireEvent("drink");
        ex.fireEvent("drink");
        ex.fireEvent("cake");
        ex.fireEvent("cake");
        ex.fireEvent("porridge");
    }
}
