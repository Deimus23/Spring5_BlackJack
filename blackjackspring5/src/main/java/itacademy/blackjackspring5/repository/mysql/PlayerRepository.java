package itacademy.blackjackspring5.repository.mysql;
import itacademy.blackjackspring5.model.mysql.Player;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Repository
public interface PlayerRepository extends R2dbcRepository<Player, Long> {
    @Query("SELECT * FROM players WHERE name = :name LIMIT 1")
    Mono<Player> findFirstByName(String name);

    Flux<Player> findAllByOrderByScoreDesc();
}
