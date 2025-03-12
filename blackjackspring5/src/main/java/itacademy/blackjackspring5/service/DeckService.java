package com.blackjack.service;


import itacademy.blackjackspring5.model.mongodb.enums.Rank;
import itacademy.blackjackspring5.model.mongodb.enums.Suit;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.smartcardio.Card;
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
        return Flux.fromArray(Suit.values())
                .flatMap(suit -> Flux.fromArray(Rank.values())
                        .map(rank -> new Card(suit, rank))
                        .collect(Collectors.toList())
                        .block(); // Solo en inicialización, no bloquea durante el juego
    }
}