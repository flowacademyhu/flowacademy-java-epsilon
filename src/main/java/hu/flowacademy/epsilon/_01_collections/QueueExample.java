package hu.flowacademy.epsilon._01_collections;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

// This example shows you how you can use a queue to add new items of
// work in a loop while traversing some items. This example will traverse
// the organizational structure encoded as Employee objects, and print
// them level-by-level.
public class QueueExample {
    private static class Employee {
        private final String name;
        private final Employee[] reports;

        Employee(String name, Employee... reports) {
            this.name = Objects.requireNonNull(name);
            this.reports = reports.clone();
        }

        Employee(String name) {
            this(name, new Employee[0]);
        }
    }

    public static void main(String[] args) {
        var ann = new Employee("Ann",
            new Employee("Bob",
                new Employee("Clara",
                    new Employee("Dan"),
                    new Employee("Emily"),
                    new Employee("Frank")),
                new Employee("Gordon",
                    new Employee("Helen"),
                    new Employee("Ian"),
                    new Employee("John")),
                new Employee("Karen",
                    new Employee("Leslie"),
                    new Employee("Marie"),
                    new Employee("Nathan"))),
            new Employee("Oliver"),
            new Employee("Patricia"));

        Queue<Employee> q = new LinkedList<>();
        q.add(ann);

        while (!q.isEmpty()) {
            var employee = q.remove();
            System.out.println(employee.name);

            for(Employee report: employee.reports) {
                q.add(report);
            }
        }
    }
}
