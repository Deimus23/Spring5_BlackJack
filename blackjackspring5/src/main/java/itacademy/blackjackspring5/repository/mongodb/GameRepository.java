package itacademy.blackjackspring5.repository.mongodb;

import itacademy.blackjackspring5.model.mongodb.Game;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface GameRepository extends ReactiveMongoRepository<Game, String> {
}
