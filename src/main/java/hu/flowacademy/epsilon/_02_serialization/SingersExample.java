package hu.flowacademy.epsilon._02_serialization;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class SingersExample {
    private static class SomeSinger implements Serializable, Singer {
        private final String name;

        public SomeSinger(String name) {
            this.name = name;
        }
    }

    public static void createSingers() throws Exception {
        Set<Singer> s = new HashSet<>();
        s.add(Elvis.INSTANCE);
        s.add(new SomeSinger("Aretha Franklin"));
        Set<Singer> s2 = (Set)Copy.copy(s);
        System.out.println(s2.contains(Elvis.INSTANCE));
    }

    public static void main(String[] args) throws Exception {
        createSingers();
    }
}
