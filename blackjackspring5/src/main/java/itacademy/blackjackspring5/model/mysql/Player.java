package itacademy.blackjackspring5.model.mysql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("players")
public class Player {
    @Id
    private Long id;
    private String name;
    private int gamesPlayed;
    private int gamesWon;
    private int rankingPoints;
}

