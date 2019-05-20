package hu.flowacademy.epsilon._05_method_design;

import java.util.ArrayList;

public class UseDepartment {
    public static void main(String[] args) {
        var a = new ArrayList<Employee>();
        a.add(new Employee("Tom"));
        a.add(new Employee("Harry"));
        a.add(new Employee("Joe"));
        var d = new Department(a);
        System.out.println(d);
        a.remove(1);
        System.out.println(d);

        var emps = d.getEmployees();
        System.out.println(emps);
    }
}
