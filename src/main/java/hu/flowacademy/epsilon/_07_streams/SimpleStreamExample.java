package hu.flowacademy.epsilon._07_streams;

import java.math.BigInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SimpleStreamExample {
    public static void orderOfEvaluationExample() {
        var s = Stream.of("a", "b", "c");
        var u = s.map(x -> {
            // This will be printed second, and only for 'b's
            System.out.println(x);
            return x.toUpperCase();
        });
        var f = u.filter(x -> x.charAt(0) % 2 == 0);
        var l = f.limit(1);
        // This will be printed first
        System.out.println("---");
        // This will be printed third. forEach triggers evaluation of the stream pipeline.
        l.forEach(System.out::println);
    }

    static final class Fib {
        final BigInteger m;
        final BigInteger n;
        Fib(BigInteger m, BigInteger n) {
            this.m = m;
            this.n = n;
        }

        Fib next() {
            return new Fib(n, m.add(n));
        }

        BigInteger getValue() {
            return m;
        }
    }

    public static void main(String[] args) {
        orderOfEvaluationExample();

        // Uncomment at most one of fibIterate and fibGenerate as they run infinitely
        fibIterate();
        fibGenerate();
    }

    public static void fibIterate() {
        var s = Stream.iterate(new Fib(BigInteger.ZERO, BigInteger.ONE), Fib::next).map(Fib::getValue);
        s.forEach(x -> System.out.println("--> " + x));
    }


    public static void fibGenerate() {
        class FibSupplier implements Supplier<BigInteger> {
            private Fib f = new Fib(BigInteger.ZERO, BigInteger.ONE);

            @Override public BigInteger get() {
                var value = f.getValue();
                f = f.next();
                return value;
            }
        }

        var s = Stream.generate(new FibSupplier());
        s.forEach(x -> System.out.println("--> " + x));
    }
}
