package itacademy.blackjackspring5.util;

import itacademy.blackjackspring5.model.mongodb.Card;
import itacademy.blackjackspring5.model.mongodb.enums.Rank;
import itacademy.blackjackspring5.model.mongodb.enums.Suit;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DeckUtils {

    public static List<Card> createFullDeck() {
        return Stream.of(Suit.values())
                .flatMap(suit -> Stream.of(Rank.values())
                        .filter(rank -> suit != Suit.HIDDEN && rank != Rank.HIDDEN)
                        .map(rank -> new Card(suit, rank))
                ).collect(Collectors.toList());
    }

    public static List<Card> createShuffledDeck() {
        List<Card> deck = new ArrayList<>(createFullDeck());
        Collections.shuffle(deck);
        return deck;
    }

    public static List<String> serializeDeck(List<Card> deck) {
        return deck.stream()
                .map(card -> card.getRank().name() + "-" + card.getSuit().name())
                .collect(Collectors.toList());
    }

    public static List<Card> deserializeDeck(List<String> serializedDeck) {
        return serializedDeck.stream()
                .map(serializedCard -> {
                    String normalized = serializedCard.trim().toUpperCase().replaceAll("\\s+", "_");
                    String[] parts = normalized.split("[-_]"); // Acepta "RANK SUIT" o "RANK-SUIT"
                    if (parts.length != 2) {
                        throw new IllegalArgumentException("Formato inválido: " + serializedCard);
                    }
                    try {
                        Rank rank = Rank.valueOf(parts[0]);
                        Suit suit = Suit.valueOf(parts[1]);
                        return new Card(suit, rank);
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Valores inválidos en: " + serializedCard);
                    }
                })
                .collect(Collectors.toList());
    }

    public static int calculateHandValue(List<String> hand) {
        int total = 0;
        int aces = 0;

        for (String cardStr : hand) {
            if (cardStr.contains("HIDDEN")) continue;

            String[] parts = cardStr.split("-");
            if (parts.length != 2) continue;

            try {
                Rank rank = Rank.valueOf(parts[0].trim().toUpperCase());
                total += rank.getValue();
                if (rank == Rank.ACE) aces++;
            } catch (IllegalArgumentException ignored) {
            }
        }

        while (total > 21 && aces > 0) {
            total -= 10;
            aces--;
        }

        return total;
    }

    public static List<String> drawCards(List<Card> deck, int count) {
        return Stream.generate(() -> deck.remove(0))
                .limit(count)
                .map(card -> card.getRank().name() + "-" + card.getSuit().name())
                .collect(Collectors.toList());
    }
}
