package itacademy.blackjackspring5.model.mongodb;
import  itacademy.blackjackspring5.model.mongodb.enums.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection= "Cards")
public class Card {
    private Suit suit;
    private Rank rank;

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    public String toSplitString() {
        return this.rank.name() + "-" + this.suit.name();
    }

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    @Override
    public String toString() {
        return rank.name() + "-" + suit.getSymbol();
    }
}
