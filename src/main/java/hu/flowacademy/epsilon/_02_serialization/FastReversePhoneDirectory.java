package hu.flowacademy.epsilon._02_serialization;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FastReversePhoneDirectory extends PhoneDirectory {
    private final Map<PhoneNumber, Set<String>> reverse = new HashMap<>();

    @Override public void add(String name, PhoneNumber number) {
        super.add(name, number);
        reverse.computeIfAbsent(number, n -> new HashSet<>()).add(name);
    }

    @Override public Set<String> reverseLookup(PhoneNumber number) {
        return Collections.unmodifiableSet(reverse.getOrDefault(number, Set.of()));
    }
}
