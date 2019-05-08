package hu.flowacademy.epsilon._02_serialization;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FastSmallReversePhoneDirectory extends PhoneDirectory {
    private transient Map<PhoneNumber, Set<String>> reverse = new HashMap<>();

    @Override public void add(String name, PhoneNumber number) {
        super.add(name, number);
        addReverse(name, number);
    }

    private void addReverse(String name, PhoneNumber number) {
        reverse.computeIfAbsent(number, n -> new HashSet<>()).add(name);
    }

    @Override public Set<String> reverseLookup(PhoneNumber number) {
        return Collections.unmodifiableSet(reverse.getOrDefault(number, Set.of()));
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        reverse = new HashMap<>();
        getEntries().forEach((name, numbers) ->
            numbers.forEach(number ->
                addReverse(name, number)
            )
        );
    }
}
