package itacademy.blackjackspring5.repository.mysql;
import itacademy.blackjackspring5.model.mysql.Player;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface PlayerRepostory extends R2dbcRepository<Player, Long> {
    Mono<Player> findByName(String name);
}
