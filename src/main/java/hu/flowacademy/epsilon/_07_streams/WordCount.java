package hu.flowacademy.epsilon._07_streams;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;

/**
 * A fairly complete example that takes a file name from args[0] and finds the
 * 30 most frequently occurring words in the file.
 */
public class WordCount {
    public static List<String> mostFrequentWords(File f, int limit) throws FileNotFoundException {
        try (Scanner s = new Scanner(f)) {
            var words = s.tokens().collect(groupingBy(String::toLowerCase, counting()));

            return words.keySet().stream()
                .sorted(comparing(words::get).reversed())
                .limit(limit)
                .collect(Collectors.toList());
        }
    }

    public static void main(String[] args) throws Exception {
        File f = new File(args[0]);
        mostFrequentWords(f, 30).stream()
            .sorted()
            .forEach(System.out::println);
    }
}
