package hu.flowacademy.epsilon._02_serialization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

// This is an example of an annotation
public @interface Color {
    int[] value();
}
