package itacademy.blackjackspring5.service;

import itacademy.blackjackspring5.model.mysql.Player;

import itacademy.blackjackspring5.repository.mysql.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    public Mono<Player> createOrUpdatePlayer(String name) {
        return playerRepository.findFirstByName(name)
                .flatMap(existingPlayer -> {
                    existingPlayer.setGamesPlayed(existingPlayer.getGamesPlayed() + 1);
                    return playerRepository.save(existingPlayer);
                })
                .switchIfEmpty(
                        playerRepository.save(new Player(null, name, 0, 0, 0))
                                .onErrorMap(e -> new RuntimeException("Error al crear/usar jugador", e)));
    }

    public Mono<Player> updatePlayerName(Long id, String newName) {
        return playerRepository.findById(id)
                .flatMap(player -> {
                    player.setName(newName);
                    return playerRepository.save(player);
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Jugador no encontrado")));
    }

    public Flux<Player> getRanking() {
        return playerRepository.findAllByOrderByScoreDesc()
                .onErrorResume(e -> Flux.error(new RuntimeException("Error al obtener ranking", e)));
    }

    public Mono<Player> updatePlayerStats(String playerId, boolean won) {
        return playerRepository.findFirstByName(playerId)
                .flatMap(player -> {
                    player.setGamesPlayed(player.getGamesPlayed() + 1);
                    if (won) {
                        player.setScore(player.getScore() + 1);
                        player.setRankingPoints(player.getRankingPoints() + 10);
                    }
                    return playerRepository.save(player);
                });
    }
}