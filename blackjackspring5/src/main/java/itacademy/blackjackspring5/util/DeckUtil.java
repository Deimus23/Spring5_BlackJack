package itacademy.blackjackspring5.util;

import itacademy.blackjackspring5.model.mongodb.Card;
import itacademy.blackjackspring5.model.mongodb.enums.Rank;
import itacademy.blackjackspring5.model.mongodb.enums.Suit;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DeckUtils {

    // Crea un mazo completo sin barajar (52 cartas)
    public static List<Card> createFullDeck() {
        return Stream.of(Suit.values())
                .flatMap(suit ->
                        Stream.of(Rank.values())
                                .map(rank -> new Card(suit, rank))
                                .collect(Collectors.toList());
    }

    // Crea un mazo nuevo y barajado
    public static List<Card> createShuffledDeck() {
        List<Card> deck = createFullDeck();
        Collections.shuffle(deck);
        return deck;
    }

    // Convierte un mazo de Cards a lista de strings (para persistencia)
    public static List<String> serializeDeck(List<Card> deck) {
        return deck.stream()
                .map(card -> card.getRank().name() + "-" + card.getSuit().name())
                .collect(Collectors.toList());
    }

    // Convierte una lista de strings a objetos Card
    public static List<Card> deserializeDeck(List<String> deckStrings) {
        return deckStrings.stream()
                .filter(s -> !s.equals("HIDDEN")) // Ignorar carta oculta del dealer
                .map(s -> {
                    String[] parts = s.split("-");
                    return new Card(
                            Suit.valueOf(parts[1]),
                            Rank.valueOf(parts[0])
                    );
                })
                .collect(Collectors.toList());
    }

    // Calcula el valor de una mano (lista de cartas en formato string)
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

        // Ajustar Ases si hay overflow
        while (total > 21 && aces > 0) {
            total -= 10;
            aces--;
        }
        return total;
    }

    // Dibuja N cartas del mazo (y las elimina del mismo)
    public static List<String> drawCards(List<Card> deck, int count) {
        return Stream.generate(() -> deck.remove(0))
                .limit(count)
                .map(Card::toString)
                .collect(Collectors.toList());
    }
}