package itacademy.blackjackspring5.repository.mongodb;

import itacademy.blackjackspring5.model.mongodb.Game;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends ReactiveMongoRepository<Game, String> {
}
