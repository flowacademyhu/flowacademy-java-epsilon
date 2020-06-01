package hu.flowacademy.epsilon._01_collections;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

// Demonstrates one possible use of a sorted map. Simulates scheduling of an
// airport runway, not allowing two flights to land too close in time to each
// other.
public class Runway {
    private final TreeMap<Integer, String> arrivals = new TreeMap<>();
    private final int safetyInterval;

    public Runway(int safetyInterval) {
        this.safetyInterval = safetyInterval;
    }

    public boolean tryToLand(int when, String flightName) {
        var prev = arrivals.floorKey(when);
        var next = arrivals.ceilingKey(when);
        if ((prev == null || when - prev >= safetyInterval) &&
            (next == null || next - when >= safetyInterval)) {
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
