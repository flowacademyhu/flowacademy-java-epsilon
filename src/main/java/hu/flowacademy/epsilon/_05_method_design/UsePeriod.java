package hu.flowacademy.epsilon._05_method_design;

import java.util.*;

public class UsePeriod {
    public static void main(String[] args) {
        var now = new Date();
        var now2 = (Date) now.clone();
        var p = new Period(new Date(89, Calendar.MAY, 17), now);

        System.out.println(p);
        System.out.println(p.durationMillis());

        Map<Period, String> periods = new HashMap<>();
        periods.put(p, "From 30 years ago");

        // NOTE: since we made the Period class essentially immutable and are
        // defensively copying input to its constructor and the output from its
        // getters, we can't cheat it into changing its internal state by calling
        // setTime either on an input parameter to its constructor (now) or on
        // a value we get back from its setter (p.getEnd()). If we didn't do all the
        // defensive copying, both of these would be able to mess with p's state.
        now.setTime(445765679568L);
        p.getEnd().setTime(465785679568L);

        System.out.println(periods.get(new Period(new Date(89, Calendar.MAY, 17), now2)));
    }
}
