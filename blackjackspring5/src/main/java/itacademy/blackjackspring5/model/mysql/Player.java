package itacademy.blackjackspring5.model.mysql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("players")
public class Player {
    @Id
    private Long id;

    @Column("name")
    private String name;

    @Column("games_played")
    private int gamesPlayed;

    @Column("score")
    private int score;

    @Column("ranking_points")
    private int rankingPoints;
}