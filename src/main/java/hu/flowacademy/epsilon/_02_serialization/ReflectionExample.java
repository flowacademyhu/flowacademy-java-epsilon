package hu.flowacademy.epsilon._02_serialization;

import java.lang.reflect.Field;

public class ReflectionExample {
    public static void main(String[] args) throws Exception {
        Object p = new PhoneNumber("36", "30", "26567874");
        Class<?> clazz = PhoneNumber.class;
        Field numberField = clazz.getDeclaredField("number");
        numberField.setAccessible(true);
        numberField.set(p, "abrakadabra");
        System.out.println(numberField.get(p));
        System.out.println(p.toString());
    }
}
