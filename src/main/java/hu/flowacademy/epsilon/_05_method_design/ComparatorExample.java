package hu.flowacademy.epsilon._05_method_design;

import java.util.*;

public class ComparatorExample {
    // Most of the time, don't write your own comparator. Use factory methods in
    // Comparator interface. Here's a null-tolerant natural ordering for Integers.
    // NOTE: "natural ordering" means that it order instances of classes implementing
    // Comparable interface using their Comparable.compareTo() implementation.
    // NOTE: most comparators can't compare nulls. That's why Comparator.nullsLast and
    // Comparator.nullsFirst exist; they can wrap another comparator and provide an
    // ordering strategy for null.
    // Look into the {@link Period} class for further examples of comparators.
    private static final Comparator<Integer> INTEGER_COMPARATOR =
            Comparator.nullsLast(Comparator.naturalOrder());

    public static void main(String[] args) {
        // Here we can order these Integers in a natural order, and also have nulls
        // sorted appropriately.
        var a = new ArrayList<>(List.of(95, 13, 42, 4));
        a.add(null);
        Collections.sort(a, INTEGER_COMPARATOR);
        System.out.println(a);

        var periods = new ArrayList<>(List.of(
                new Period(new Date(7, 2, 3), new Date(9, 4, 5)),
                new Period(new Date(1, 2, 3), new Date(9, 4, 5)),
                new Period(new Date(3, 2, 3), new Date(9, 4, 5))

        ));
        // Look into the Period class for example of some more comparators.
        Collections.sort(periods, Period.START_COMPARATOR);
        System.out.println(periods);
    }
}
