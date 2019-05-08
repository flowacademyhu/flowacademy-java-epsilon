package hu.flowacademy.epsilon._02_serialization;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PhoneDirectory implements Serializable {
    private final Map<String, Set<PhoneNumber>> dir = new HashMap<>();

    public void add(String name, PhoneNumber number) {
        dir.computeIfAbsent(name, n -> new HashSet<>()).add(number);
    }

    public Set<PhoneNumber> lookup(String name) {
        return Collections.unmodifiableSet(dir.getOrDefault(name, Set.of()));
    }

    public Set<String> reverseLookupX(PhoneNumber number) {
        var names = new HashSet<String>();
        for(var e: dir.entrySet()) {
            if (e.getValue().contains(number)) {
                names.add(e.getKey());
            }
        }
        return names;
    }

    public Set<String> reverseLookup(PhoneNumber number) {
        return dir.entrySet().stream()
            .filter(e -> e.getValue().contains(number))
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
    }

    protected Map<String, Set<PhoneNumber>> getEntries() {
        return Collections.unmodifiableMap(dir);
    }
}
