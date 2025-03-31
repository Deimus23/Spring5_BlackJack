package itacademy.blackjackspring5.model.mongodb;

import com.fasterxml.jackson.annotation.JsonIgnore;
import itacademy.blackjackspring5.model.mongodb.enums.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.util.Date;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection= "games")
public class Game {


    @Id
    @MongoId
    private String  id;
    private Long playerId;
    private int playerBet;
    private List<Card> deck;
    private List<Card> playerHand;
    private List<Card> dealerHand;
    private int playerScore;
    private GameStatus status= GameStatus.IN_PROGRES;
    private String result;
    private Date timestamp= Date.from(Instant.now());
}
