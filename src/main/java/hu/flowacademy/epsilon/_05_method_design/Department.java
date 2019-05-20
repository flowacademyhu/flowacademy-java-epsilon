package hu.flowacademy.epsilon._05_method_design;

import java.util.*;

//This class demonstrates the correct use of defensive copying in case of
// collections such as lists.
public class Department {
    private final List<Employee> employees;

    public Department(List<Employee> employees) {
        // Always check for null. It's cheap and explicitly expresses your
        // intent, even if List.copyOf would check it for you anyway.
        Objects.requireNonNull(employees);
        // Ideally, use {List|Set|Map}.copyOf to create a private copy of
        // a collection you are passed from the outside. If you subsequently
        // need to modify the list internally, use
        // "this.employees = new ArrayList<>(employees)" instead. In that case,
        // see the notes in getEmployees() too for creating a defensive copy of
        // it before handing it out to callers.
        this.employees = List.copyOf(employees);
    }

    public List<Employee> getEmployees() {
        // NOTE: we aren't making a copy, because List.copyOf created an
        // immutable copy for us already. You can freely hand it out, because
        // it can not be modified by the client code you returned it to.
        // If "employees" were not immutable (e.g. you created it with
        // "this.employees = new ArrayList<>(employees)" in the constructor)
        // then you would have to use either
        // "Collections.unmodifiableList(employees)" here to return a
        // cheap-to-construct view of your internal list, or
        // "List.copyOf(employees)": more costly, but it would protect the
        // caller from observing subsequent changes to your internal list.
        return employees;
    }

    @Override
    public String toString() {
        return "Employees(" + employees.toString() + ")";
    }
}
