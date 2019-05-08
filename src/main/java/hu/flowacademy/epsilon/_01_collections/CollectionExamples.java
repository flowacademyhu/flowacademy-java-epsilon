package hu.flowacademy.epsilon._01_collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;

public class CollectionExamples {
    public static void arrayListTraversalWithIterator() {
        var l = new ArrayList<String>();
        l.add("a");
        l.add("b");
        l.add("c");

        // We can traverse the list with an iterator, using hasNext/next.
        // This is the same as walking it with "for(String s: l) { ... }"
        // except here you can also use ListIterator's add/set/remove methods
        // to edit the list as you traverse it.
        for (ListIterator<String> it = l.listIterator(); it.hasNext();) {
            String s = it.next();
            System.err.println(s);
        }
    }

    public static void arrayListBackwardsTraversal() {
        var l = new ArrayList<String>();
        l.add("a");
        l.add("b");
        l.add("c");

        // We can walk the list backwards with listIterator. Note we initialize
        // the iterator with l.size() to start at the end, and then use hasPrevious/previous
        for (ListIterator<String> it = l.listIterator(l.size()); it.hasPrevious();) {
            String s = it.previous();
            System.err.println(s);
        }
    }

    public static void arrayListTraversalWithForEach() {
        var l = new ArrayList<String>();
        l.add("a");
        l.add("b");
        l.add("c");

        l.forEach(s -> System.err.println(s));

        // NOTE that x -> obj.method(x) lambdas can be replaced with
        // METHOD REFERENCES: obj::method
        l.forEach(System.err::println);
    }

    public static void linkedHashMapsPreserveInsertionOrder() {
        var m = new LinkedHashMap<String, String>();
        m.put("z", "X");
        m.put("abcdef", "XXXXX");
        m.put("x", "X");
        m.put("xyzzy", "asdf");
        m.put("y", "Y");

        // Traverse keys only. You should notice we get back the
        // keys in exactly the same order we put them in. With an ordinary
        // HashMap you don't have that guarantee.
        for(String k: m.keySet()) {
            System.err.println(k);
        }
        // or:
        m.keySet().forEach(k -> System.err.println(k));
        // or:
        m.keySet().forEach(System.err::println);

        // Traverse values only with values(). Again, insertion order is preserved.
        for(String v: m.values()) {
            System.err.println(v);
        }

        // Traverse keys and values with entrySet(). Again, insertion order is preserved.
        for(Map.Entry<String, String> e: m.entrySet()) {
            System.err.println(e.getKey() + " -> " + e.getValue());
        }
        // This one looks different with a lambda, note lambda takes two
        // parameters.
        m.forEach((k, v) -> System.err.println(k + " -> " + v));

        // The reason this lambda takes two parameters is that forEach takes
        // BiConsumer as its parameter, which as its name suggests consumes
        // two values. Above is equivalent to: (Don't use this, use lambdas;
        // this is just illustrative):
        m.forEach(new BiConsumer<String, String>() {
            @Override public void accept(String k, String v) {
                System.err.println(k + " -> " + v);
            }
        });

        // Not pictured, but remember you can subclass LinkedHashMap and
        // provide an implementation of its removeEldestEntry() method to
        // implement size-limited Most Recently Used caches.
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
        runway();
    }
}
