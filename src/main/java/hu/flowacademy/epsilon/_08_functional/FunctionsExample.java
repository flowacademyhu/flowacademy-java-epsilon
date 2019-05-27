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

    public static void main(String[] args) {
        System.out.println(TRICE_SQUARED.applyAsInt(10));
        System.out.println(SQUARED_TRICE.applyAsInt(10));

        for (int i = 0; i < 200; ++i) {
            // Only effective finals
            final int j = i;
            logDebug(() -> "Started up at " + new Date() + " " + j);
        }
    }
}
