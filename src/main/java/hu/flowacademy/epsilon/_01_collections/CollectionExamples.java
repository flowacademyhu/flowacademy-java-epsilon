package hu.flowacademy.epsilon._01_collections;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CollectionExamples {
    public static void arrayListTraversalWithIterator() {
        // List.of is immutable, but we want to mutate it therefore we
        // construct a mutable ArrayList as its copy.
        var l = new ArrayList<>(List.of("a", "b", "c"));

        // We can traverse the list with an iterator, using hasNext/next.
        // This is the same as walking it with "for(String s: l) { ... }"
        // except here you can also use ListIterator's add/set/remove methods
        // to edit the list as you traverse it.
        for (ListIterator<String> it = l.listIterator(); it.hasNext();) {
            String s = it.next();
            if (s.equals("c")) {
                it.set("C");
            } else if (s.equals("b")) {
                it.add("Z");
            }
            System.out.println(s);
        }
        System.out.println(l);
    }

    public static void arrayListBackwardsTraversal() {
        var l = List.of("a", "b", "c");

        // We can walk the list backwards with listIterator. Note we initialize
        // the iterator with l.size() to start at the end, and then use hasPrevious/previous
        for (ListIterator<String> it = l.listIterator(l.size()); it.hasPrevious();) {
            String s = it.previous();
            System.out.println(s);
        }
    }

    public static void arrayListTraversalWithForEach() {
        var l = List.of("a", "b", "c");

        // NOTE that x -> obj.method(x) lambdas can be replaced with
        // METHOD REFERENCES: obj::method
        l.forEach(System.out::println);

        // The above method reference is a more compact representation of this lambda:
        l.forEach(s -> System.out.println(s));

        // The above lambda is a more compact representation of this anonymous inner class:
        l.forEach(new Consumer<>() {
            @Override public void accept(String s) {
                System.out.println(s);
            }
        });

        // All three are functionally equivalent.
    }

    public static void linkedHashMapsPreserveInsertionOrder() {
        var m = new LinkedHashMap<String, String>(16, .75f, true) {
            @Override protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                return size() > 5;
            }
        };
        m.put("z", "X");
        m.put("abcdef", "XXXXX");
        m.put("x", "X");
        m.put("xyzzy", "asdf");
        m.put("y", "Y");

        // Traverse keys only. You should notice we get back the
        // keys in exactly the same order we put them in. With an ordinary
        // HashMap you don't have that guarantee.
        for(String k: m.keySet()) {
            System.out.println(k);
        }
        // or:
        m.keySet().forEach(k -> System.out.println(k));
        // or:
        m.keySet().forEach(System.out::println);

        // Traverse values only with values(). Again, insertion order is preserved.
        for(String v: m.values()) {
            System.out.println(v);
        }

        // Traverse keys and values with entrySet(). Again, insertion order is preserved.
        for(Map.Entry<String, String> e: m.entrySet()) {
            System.out.println(e.getKey() + " -> " + e.getValue());
        }
        // This one looks different with a lambda, note lambda takes two
        // parameters.
        m.forEach((k, v) -> System.out.println(k + " -> " + v));

        // The reason this lambda takes two parameters is that forEach takes
        // BiConsumer as its parameter, which as its name suggests consumes
        // two values. Above is equivalent to: (Don't use this, use lambdas;
        // this is just illustrative):
        m.forEach(new BiConsumer<String, String>() {
            @Override public void accept(String k, String v) {
                System.out.println(k + " -> " + v);
            }
        });
    }

    public static void linkedHashMapsCanProvideMostRecentlyUsedCaches() {
        // A Most Recently Used (MRU) cache is a temporary storage that has a
        // bounded size and throws away least-recently used entries to make space
        // for new entries when it reaches its maximum size.
        // LinkedHashMap can be used to provide a MRU cache by subclassing it and
        // overriding its removeEldestEntry method:
        var m = new LinkedHashMap<String, String>(16, .75f, true) {
            @Override protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                return size() > 5;
            }
        };

        m.put("z", "X");
        m.put("abcdef", "XXXXX");
        m.put("x", "X");
        m.put("xyzzy", "asdf");
        m.put("y", "Y");

        // Print all entries
        m.forEach((k, v) -> System.out.println(k + " -> " + v));

        // Get "z" so it'll become recently used:
        m.get("z");

        // Add a new entry:
        m.put("woo", "Hoo");

        System.out.println();
        // Print all entries again; we see "abcdef" was removed.
        m.forEach((k, v) -> System.out.println(k + " -> " + v));

    }

    public static void runway() {
        // Runway demonstrates an use for a sorted map
        var runway = new Runway(3);
        // These should all be cleared to land
        System.out.println(runway.tryToLand(4, "LH477"));
        System.out.println(runway.tryToLand(12, "WZ567"));
        System.out.println(runway.tryToLand(16, "XZ4433"));

        // This one can not land as it's too close to already scheduled
        // landings.
        System.out.println(runway.tryToLand(14, "FL1234"));

        // Let's print the arrivals:
        System.out.println("Time:\tFlight:");
        runway.getArrivals().forEach((when, flight) -> System.out.println(when + "\t" + flight));

        // Let's only print arrivals between minutes 11 (inclusive) and 20 (exclusive),
        // that is for `m` values where `11 <= m < 20`. Note tailMap is inclusive and
        // headMap is exclusive. This illustrates how a sorted map can be used for
        // range filtering.
        var after11 = runway.getArrivals().tailMap(11);
        var after11Before20 = after11.headMap(20);

        System.out.println("Between 11-20");
        System.out.println("Time:\tFlight:");
        after11Before20.forEach((when, flight) -> System.out.println(when + "\t" + flight));

        System.out.println(runway.tryToLand(19, "WV4789"));

        // Note that we're using the same headMap in after11Before20 variable.
        // It is a live view of the underlying collection.
        System.out.println("Between 11-20");
        System.out.println("Time:\tFlight:");
        after11Before20.forEach((when, flight) -> System.out.println(when + "\t" + flight));

    }

    public static void mapOfMaps() {
        // TwoKeyMap demonstrates the fact that maps can be values in other maps.
        var tkmap = new TwoKeyMap<Integer, Integer, String>();
        tkmap.put(3, 5, "foo");
        tkmap.put(11, 55, "bar");

        System.out.println(tkmap.get(11, 55));
        System.out.println(tkmap.get(3, 4)); // not found
    }

    public static void main(String[] args) {
        arrayListTraversalWithIterator();
        arrayListBackwardsTraversal();
        arrayListTraversalWithForEach();
        linkedHashMapsPreserveInsertionOrder();
        linkedHashMapsCanProvideMostRecentlyUsedCaches();
        runway();
        mapOfMaps();
    }
}
