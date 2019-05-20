package hu.flowacademy.epsilon._05_method_design;

import java.util.Objects;

public class Employee {
    private final String name;

    public Employee(String name) {
        this.name = Objects.requireNonNull(name);
        if (this.name.isBlank()) {
            throw new IllegalArgumentException("name is blank");
        }
    }

    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return "Employee(" + name + ")";
    }
}
