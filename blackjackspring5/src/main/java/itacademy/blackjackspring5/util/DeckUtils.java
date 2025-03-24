package itacademy.blackjackspring5.util;

import itacademy.blackjackspring5.model.mongodb.Card;
import itacademy.blackjackspring5.model.mongodb.enums.Rank;
import itacademy.blackjackspring5.model.mongodb.enums.Suit;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DeckUtils {


    public static List<Card> createFullDeck() {
        return Stream.of(Suit.values())
                .flatMap(suit -> Stream.of(Rank.values())
                        .map(rank -> new Card(suit, rank))
                ).toList();
    }


    public static List<Card> createShuffledDeck() {
        List<Card> deck = createFullDeck();
        Collections.shuffle(deck);
        return deck;
    }


    public static List<Card> serializeDeck(List<Card> deck) {
        return deck.stream()
                .map(card -> card.getRank().name() + "-" + card.getSuit().name())
                .collect(Collectors.toList());
    }


    public static List<Card> deserializeDeck(List<Card> serializedDeck) {
        return serializedDeck.stream()
                .map(serializedCard -> {
                    try {
                        String[] parts = serializedCard.split("-");
                        if (parts.length != 2) {
                            throw new IllegalArgumentException("Formato inválido. Se esperaba 'RANK-SUIT'");
                        }

                        return new Card(
                                Suit.valueOf(parts[1].trim().toUpperCase()),
                                Rank.valueOf(parts[0].trim().toUpperCase())
                        );
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Error al deserializar: " + serializedCard, e);
                    }
                })
                .collect(Collectors.toList());
    }

    public static int calculateHandValue(List<String> hand) {
        int total = 0;
        int aces = 0;

        for (String card : hand) {
            if (card.equals("HIDDEN")) continue;

            String[] parts = card.split("-");
            Rank rank = Rank.valueOf(parts[0]);
            total += rank.getValue();
            if (rank == Rank.ACE) aces++;
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
                .map(Card::toString)
                .collect(Collectors.toList());
    }
}