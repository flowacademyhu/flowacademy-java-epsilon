package hu.flowacademy.epsilon._06_enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Shows use of EnumMap, and incidentally also shows how to use Optional as a
 * return value (see WeeklyPlan.getFirstTaskOfTheDay) and how to treat such
 * Optional when returned from a method.
 */
public class EnumMapExample {
    enum Day {
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        SUNDAY
    }

    public static class Task {
        final String todo;

        Task(String todo) {
            this.todo = Objects.requireNonNull(todo);
        }
    }

    public static class WeeklyPlan {
        final Map<Day, List<Task>> plan = new EnumMap<>(Day.class);

        public void addTask(Day day, Task task) {
            plan.computeIfAbsent(day, d -> new ArrayList<>()).add(task);
        }

        public List<Task> getDailyPlan(Day day) {
            return Collections.unmodifiableList(plan.getOrDefault(day, List.of()));
        }

        public Optional<Task> getFirstTaskOfTheDay(Day day) {
            return getDailyPlan(day).stream().findFirst();
        }
    }

    public static WeeklyPlan getWeeklyPlan() {
        WeeklyPlan plan = new WeeklyPlan();
        plan.addTask(Day.MONDAY, new Task("Buy groceries"));
        plan.addTask(Day.MONDAY, new Task("Pick up dry cleaning"));
        plan.addTask(Day.TUESDAY, new Task("Take cat to vet"));
        plan.addTask(Day.WEDNESDAY, new Task("Take out the trash"));
        plan.addTask(Day.WEDNESDAY, new Task("Do the washing"));
        return plan;
    }

    public static void main(String[] args) {
        WeeklyPlan week = getWeeklyPlan();
        System.out.println(week.getFirstTaskOfTheDay(Day.MONDAY)
            .map(t -> t.todo)
            .orElse("No tasks today")
        );
    }
}
