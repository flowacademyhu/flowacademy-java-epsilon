package hu.flowacademy.epsilon._07_streams;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Demonstrates uses of enums, as well as either iterative for loops or streams
 * with flatMap to generate all combinations in a deck of cards.
 */
public class Cards {
    public enum Suit {
        HEARTS('\u2665', '\u2661'),
        DIAMONDS('\u2666', '\u2662'),
        SPADES('\u2660', '\u2664'),
        CLUBS('\u2663', '\u2667');

        // Enums can have fields
        private final char blackSign;
        private final char whiteSign;

        // Fields are normally initialized in the constructor
        // and constructors invoked when enumeration values
        // are listed at the top of the enum.
        Suit(char blackSign, char whiteSign) {
            this.blackSign = blackSign;
            this.whiteSign = whiteSign;
        }

        public char getBlackSign() {
            return blackSign;
        }
        public char getWhiteSign() {
            return whiteSign;
        }
    }

    public enum Rank {
        ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING
    }

    static class Card {
        final Suit suit;
        final Rank rank;

        Card(Suit suit, Rank rank) {
            this.suit = suit;
            this.rank = rank;
        }
    }

    static List<Card> deck() {
        List<Card> deck = new ArrayList<>();
        for(var suit: Suit.values()) {
            for (var rank: Rank.values()) {
                deck.add(new Card(suit, rank));
            }
        }
        return deck;
    }

    static List<Card> deckWithStreams() {
        return Stream.of(Suit.values())
            .flatMap(suit ->
                Stream.of(Rank.values())
                    .map(rank -> new Card(suit, rank)))
            .collect(Collectors.toList());
    }
}
