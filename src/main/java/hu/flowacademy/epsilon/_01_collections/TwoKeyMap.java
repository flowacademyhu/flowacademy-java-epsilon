package hu.flowacademy.epsilon._01_collections;

import java.util.HashMap;
import java.util.Map;

public class TwoKeyMap<K1, K2, V> {
    private final Map<K1, Map<K2, V>> map = new HashMap<>();

    public void oldStylePut(K1 k1, K2 k2, V value) {
        var m = map.get(k1);
        if (m == null) {
            m = new HashMap<>();
            map.put(k1, m);
        }
        m.put(k2, value);
    }

    public V put(K1 k1, K2 k2, V value) {
        return map.computeIfAbsent(k1, i -> new HashMap<>()).put(k2, value);
    }

    public V get(K1 k1, K2 k2) {
        return map.getOrDefault(k1, Map.of()).get(k2);
    }
}
