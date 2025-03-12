package itacademy.blackjackspring5.model.mongodb;

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
    public enum GameStatus{
        IN_PROGRES,
        FINISHED
    }
    public enum GameResult{
        WIN,
        LOSSE,
        DRAW
    }
    @Id
    private String id;
    private String playerId;
    private List<Card> cards;
    private GameStatus status;
    private GameResult result;
    private Date timestamp= Date.from(Instant.now());
}
