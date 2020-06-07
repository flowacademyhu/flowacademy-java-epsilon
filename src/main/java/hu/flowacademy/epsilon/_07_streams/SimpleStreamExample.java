package hu.flowacademy.epsilon._07_streams;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleStreamExample {
    public static void orderOfEvaluationExample() {
        /*
            NOTE: below example would ordinarily be written as:
                Stream.of("a", "b", "c", "d", "e", "f", "g")
                    .map(String::toUpperCase)
                    .filter(x -> x.charAt(0) % 2 == 0)
                    .limit(2)
                    .forEach(System.out::println);
            We're breaking it out for didactic purposes.
         */

        Stream<String> s = Stream.of("a", "b", "c", "d", "e", "f", "g");

        Stream<String> u = s.map(x -> {
            System.out.println("mapping " + x);
            return x.toUpperCase();
        });

        var f = u.filter(x -> x.charAt(0) % 2 == 0);
        var l = f.limit(2);

        // This will be printed first
        System.out.println("---");
        // This will be printed third. forEach triggers evaluation of the stream pipeline.
        l.forEach(System.out::println);
    }

    private static void randomInts() {
        // Random.ints(), Random.longs(), and Random.doubles() provide
        // infinite streams of random numbers.
        var rs = new Random().ints().limit(10);
        rs.forEach(System.out::println);
    }

    static final class Fib {
        final BigInteger previous;
        final BigInteger current;
        Fib(BigInteger previous, BigInteger current) {
            this.previous = previous;
            this.current = current;
        }

        Fib next() {
            return new Fib(current, current.add(previous));
        }

        BigInteger getValue() {
            return current;
        }
    }

    public static void fibIterate() {
        // Demonstrates using Stream.iterate to generate an infinites stream
        // from an initial element and a function producing the next element
        // from a previou one.
        List<BigInteger> l1 = Stream.iterate(
            new Fib(BigInteger.ZERO, BigInteger.ONE), // initial element
            Fib::next // function to produce the next element
        )
            .map(Fib::getValue)
            .limit(100)
            .collect(Collectors.toList());

        System.out.println(l1);
    }

    public static void fibIterateGroupBy() {
        // Demonstrates using Collectors.groupingBy. Here we group elements of
        // the Fibonacci sequence according to their remainder when divided by 3.
        var THREE = BigInteger.valueOf(3);

        Map<BigInteger, List<BigInteger>> m =
            Stream.iterate(new Fib(BigInteger.ZERO, BigInteger.ONE), Fib::next)
                .map(Fib::getValue)
                .limit(20)
                .collect(Collectors.groupingBy(
                    x -> x.mod(THREE),
                    Collectors.toList()
                ));


        System.out.println(m);
    }


    public static void fibGenerate() {
        // Demonstrates using Stream.generate to generate an infinite stream
        // by repeatedly invoking a Supplier. Note that we need to do state
        // management within the Supplier; in this particular case
        // Stream.iterate would be a better choice, compare with fibIterate().
        class FibSupplier implements Supplier<BigInteger> {
            private Fib f = new Fib(BigInteger.ZERO, BigInteger.ONE);

            @Override public BigInteger get() {
                var value = f.getValue();
                f = f.next();
                return value;
            }
        }

        var s = Stream.generate(new FibSupplier());

        s.limit(50).forEach(x -> System.out.println("--> " + x));
    }

    public static void main(String[] args) {
        orderOfEvaluationExample();
        randomInts();

        fibIterate();
        fibGenerate();
    }
}
