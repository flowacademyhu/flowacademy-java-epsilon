package hu.flowacademy.epsilon._01_collections;

import com.google.common.base.Function;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.UnaryOperator;

// Demonstrates one possible use of a sorted map. Simulates scheduling of an
// airport runway, not allowing two flights to land too close in time to each
// other.
public class Runway {
    private final SortedMap<Integer, String> arrivals = new TreeMap<>();
    private final int safetyInterval;

    public Runway(int safetyInterval) {
        this.safetyInterval = safetyInterval;
    }

    private Optional<Integer> key(UnaryOperator<SortedMap<Integer, String>> op, Function<SortedMap<Integer, String>, Integer> fn) {
        var opmap = op.apply(arrivals);
        if (opmap.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(fn.apply(opmap));
        }
    }

    public boolean tryToLand(int when, String flightName) {
        var prev = key(m -> m.headMap(when), SortedMap::lastKey);
        var next = key(m -> m.tailMap(when), SortedMap::firstKey);
        if (prev.map(p -> when - p >= safetyInterval).orElse(true) &&
            next.map(n -> n - when >= safetyInterval).orElse(true)) {
            arrivals.put(when, flightName);
            return true;
        } else {
            return false;
        }
    }

    public SortedMap<Integer, String> getArrivals() {
        return Collections.unmodifiableSortedMap(arrivals);
    }
}
