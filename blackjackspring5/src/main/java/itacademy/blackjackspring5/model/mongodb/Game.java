package itacademy.blackjackspring5.model.mongodb;

import itacademy.blackjackspring5.model.mongodb.enums.GameResult;
import itacademy.blackjackspring5.model.mongodb.enums.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Date;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection= "games")
public class Game {


    @Id
    private Long  id;
    private Long playerId;
    private List<Card> deck;
    private List<Card> playerHand;
    private List<Card> dealerHand;
    private int playerScore;
    private GameStatus status;
    private GameResult result;
    private Date timestamp= Date.from(Instant.now());
}
