package itacademy.blackjackspring5.service;

import itacademy.blackjackspring5.model.mongodb.enums.Rank;
import itacademy.blackjackspring5.model.mongodb.enums.Suit;
import itacademy.blackjackspring5.model.mongodb.Card;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeckService {

    public Flux<Card> createShuffledDeck() {
        List<Card> deck = createFullDeck();
        Collections.shuffle(deck);
        return Flux.fromIterable(deck);
    }

    private List<Card> createFullDeck() {
        return Arrays.stream(Suit.values())
                .flatMap(suit ->
                        Arrays.stream(Rank.values())
                                .map(rank -> new Card(suit, rank))
                )
                .collect(Collectors.toList());
    }
}