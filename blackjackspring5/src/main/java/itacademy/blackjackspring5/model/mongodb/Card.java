package itacademy.blackjackspring5.model.mongodb;
import  itacademy.blackjackspring5.model.mongodb.enums.*;
import lombok.Data;

@Data
public class Card {
    private Suit suit;
    private Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    @Override
    public String toString() {
        return rank.name() + "-" + suit.getSymbol();
    }
}
