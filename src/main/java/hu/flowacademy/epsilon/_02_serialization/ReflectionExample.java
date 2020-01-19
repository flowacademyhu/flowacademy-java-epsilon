package hu.flowacademy.epsilon._02_serialization;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ReflectionExample {
    private static final void reflectiveAccessOnPrivateFields() throws Exception {
        Object p = new PhoneNumber("36", "30", "26567874");

        Class<?> clazz = p.getClass();
        Field numberField = clazz.getDeclaredField("number");
        numberField.setAccessible(true);

        System.out.println("Reflectively reading the number: " + numberField.get(p));

        numberField.set(p, "garbage");
        System.out.println("After reflectively writing the number: " + p.toString());
    }

    @Color({255, 255, 0})
    public static class ColoredObject {
        // This class is empty, it just shows us how can we put our custome
        // annotation on a class
    }

    private static final void checkForColorAnnotation(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Color.class)) {
            Color c = clazz.getAnnotation(Color.class);
            System.out.println("Color of " + clazz.getSimpleName() + " is " + Arrays.toString(c.value()));
        } else {
            System.out.println(clazz.getSimpleName() + " is colorless.");
        }
    }

    private static final void checkForColors() {
        checkForColorAnnotation(ColoredObject.class);
        checkForColorAnnotation(PhoneNumber.class);
    }

    public static void main(String[] args) throws Exception {
        reflectiveAccessOnPrivateFields();
        checkForColors();
    }
}
