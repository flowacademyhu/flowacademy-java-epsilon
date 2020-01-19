package hu.flowacademy.epsilon._08_functional;

import java.util.Date;
import java.util.List;
import java.util.function.*;
import java.util.logging.*;


public class FunctionsExample {
    // Don't use boxed primitive
    private static final Function<Integer, Integer> TRICE_BOXED = x -> (3 * x);
    private static final IntUnaryOperator TRICE = x -> (3 * x);
    private static final IntUnaryOperator SQUARED = x -> (x * x);

    private static final IntUnaryOperator TRICE_SQUARED = SQUARED.andThen(TRICE);
    private static final IntUnaryOperator SQUARED_TRICE = TRICE.andThen(SQUARED);

    private static final Logger logger = Logger.getLogger(FunctionsExample.class.getName());

    public static void logDebug(Supplier<String> message) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, message.get());
        }
    }

    // Here's an interface we declared ourselves that's similar to Function
    public interface MyFunction<T, R> {
        R foo(T x);
    }

    private static Date now() {
        System.out.println("now invoked");
        return new Date();
    }

    // Here's a method that uses our special MyFunction
    public static <T, R> R useMyFunction(MyFunction<T, R> f, T x) {
        return f.foo(x);
    }

    public static void main(String[] args) {
        // Here we can see how our own interface can also be defined
        // with a method reference. It could also be defined with a lambda.
        var x = useMyFunction(String::toUpperCase, "abrakadabra");
        System.out.println("Using our own MyFunction to uppercase a string: " + x);

        // Test out various compositions:
        System.out.println("TRICE_SQUARED(10): " + TRICE_SQUARED.applyAsInt(10));
        System.out.println("SQUARED_TRICE(10): " + SQUARED_TRICE.applyAsInt(10));

        // LogDebug is taking a supplier; new Date() is only evaluated when needed.
        for (int i = 0; i < 200; ++i) {
            // Only effective finals
            final int j = i;
            logDebug(() -> "Started up at " + now() + " " + j);
        }
    }
}
