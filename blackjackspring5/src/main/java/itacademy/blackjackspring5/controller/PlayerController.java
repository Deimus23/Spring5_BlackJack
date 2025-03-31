package itacademy.blackjackspring5.controller;

import io.swagger.v3.oas.annotations.Operation;
import itacademy.blackjackspring5.model.mysql.Player;
import itacademy.blackjackspring5.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;
    @Operation(summary = "Obtener ranking de jugadores", description = "Devuelve una lista ordenada de jugadores por su puntaje.")
    @GetMapping("/ranking")
    public Flux<Player> getRanking() {
        return playerService.getRanking();
    }
}

