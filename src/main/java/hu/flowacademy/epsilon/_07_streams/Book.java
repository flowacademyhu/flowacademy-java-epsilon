package hu.flowacademy.epsilon._07_streams;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// Demonstrates some more stream operations, specifically using
// sorting with Comparator.comparingInt
public class Book {
    public String author;
    public String title;
    public int yearPublished;
    public int copiesSold;

    public int getCopiesSold() {
        return copiesSold;
    }

    public static List<String> fiveBestSellersInPastTwoYears(List<Book> books, int currentYear) {
        return books.stream()
            .filter(b -> currentYear - b.yearPublished <= 2)
            .sorted(Comparator.comparingInt(Book::getCopiesSold).reversed())
            .limit(5)
            .map(b -> b.author + ": " + b.title)
            .collect(Collectors.toList());
    }
}
