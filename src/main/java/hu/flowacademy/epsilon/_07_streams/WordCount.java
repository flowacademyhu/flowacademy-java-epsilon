package hu.flowacademy.epsilon._07_streams;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * A fairly complete example that takes a file name from args[0] and finds the
 * args[1] most frequently occurring words in the file.
 */
public class WordCount {
    public static List<String> mostFrequentWords(File f, int limit) throws FileNotFoundException {
        try (Scanner s = new Scanner(f)) {
            Map<String, Long> words = s.tokens()
                .collect(Collectors.groupingBy(
                    String::toLowerCase,
                    // .counting() is a collector that only collects the number of elements it sees.
                    Collectors.counting()));

            return words.entrySet().stream()
                // Sort by reverse value of entries; values are counts of occurrences
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                // Get the keys of entries, which are the actual words
                .map(Map.Entry::getKey)
                // avoid short words such as "and", "or", "with" etc.
                .filter(k -> k.length() > 5)
                .limit(limit)
                .collect(Collectors.toList());
        }
    }

    public static void main(String[] args) throws Exception {
        File f = new File(args[0]);
        mostFrequentWords(f, Integer.parseInt(args[1])).stream()
            .sorted()
            .forEach(System.out::println);
    }
}
