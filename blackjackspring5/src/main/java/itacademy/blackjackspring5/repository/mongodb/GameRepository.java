package itacademy.blackjackspring5.repository.mongodb;

import itacademy.blackjackspring5.model.mongodb.Game;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface GameRepository extends ReactiveMongoRepository<Game, String> {
}
