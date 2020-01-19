package hu.flowacademy.epsilon._04_concurrency;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * This example demonstrates use of {@link CopyOnWriteArrayList} in a setting
 * where it can be concurrently modified. Beware that CopyOnWriteArrayList
 * copies the whole underlying array on every modification, so it has terrible
 * performance if it is modified frequently. Its best uses are in a situation
 * where the list is modified rarely but read often.
 */
public class ListenersExample {
    private static class Listeners<T> {
        private final List<Consumer<T>> listeners;

        Listeners(List<Consumer<T>> listeners) {
            this.listeners = listeners;
        }

        public void addListener(Consumer<T> l) {
            listeners.add(l);
        }

        public void removeListener(Consumer<T> l) {
            listeners.remove(l);
        }

        public void fireEvent(T event) {
            for(var l: listeners) {
                l.accept(event);
            }
        }
    }

    // In this example, we want to only have one piece of cake, so the cake
    // listener will remove itself once it saw the first occurrence of cake.
    // However, if we do this using an ordinary ArrayList for listeners, we'll
    // suffer a ConcurrentModificationException from Listeners.fireEvent() as
    // the for loop doesn't allow for the list to be modified while iterating
    // over its elements. Using CopyOnWriteArrayList fixes the problem. Note
    // we aren't even using multithreading here at all! All we needed for this
    // to happen is to try to modify the listener list while an event was being
    // processed by the listeners.
    private static void example(Listeners<String> foodStall) {
        foodStall.addListener(new Consumer<String>() {
            @Override public void accept(String event) {
                if (event.equals("cake")) {
                    System.out.println("I got my cake, thank you!");
                    // This will cause trouble with a non-concurrent
                    // List implementation.
                    foodStall.removeListener(this);
                }
            }
        });

        foodStall.addListener(event -> {
            if (event.equals("drink")) {
                System.out.println("glug, glug");
            }
        });

        foodStall.addListener(event -> {
            if (event.equals("porridge")) {
                System.out.println("I really don't like this");
            }
        });

        foodStall.fireEvent("soup");
        foodStall.fireEvent("drink");
        foodStall.fireEvent("cake");
        foodStall.fireEvent("drink");
        foodStall.fireEvent("cake");
        foodStall.fireEvent("porridge");
    }

    public static void main(String[] args) {
        // This must fail with an ordinary ArrayList
        System.out.println("--- With ArrayList");
        try {
            example(new Listeners<String>(new ArrayList<>()));
            throw new IllegalStateException("This should have failed!");
        } catch (ConcurrentModificationException e) {
            System.out.println("Got ConcurrentModificationException as expected.");
        }

        System.out.println("--- With CopyOnWriteArrayList");
        // It does work with a CopyOnWriteArrayList
        example(new Listeners<String>(new CopyOnWriteArrayList<>()));
    }
}
