package itacademy.blackjackspring5.repository.mongodb;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface Card extends ReactiveMongoRepository<Card, String> {
}
