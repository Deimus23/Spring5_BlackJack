package itacademy.blackjackspring5.repository.mongodb;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Card extends ReactiveMongoRepository<Card, String> {
}
