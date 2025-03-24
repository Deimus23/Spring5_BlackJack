package itacademy.blackjackspring5.controller;

import io.swagger.v3.oas.annotations.Operation;
import itacademy.blackjackspring5.model.mongodb.Card;
import itacademy.blackjackspring5.model.mongodb.Game;
import itacademy.blackjackspring5.service.GameService;
import itacademy.blackjackspring5.util.DeckUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/games")
@Tag(name = "Game Controller", description = "Gestión de partidas de Blackjack")
public class GameController {

    private final GameService gameService;
    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Crear nueva partida",
            description = "Inicia una nueva partida para un jugador",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Partida creada"),
                    @ApiResponse(responseCode = "400", description = "Parámetros inválidos")
            }
    )
    public Mono<Game> createGame(
            @Parameter(description = "Nombre del jugador", required = true, example = "Alice")
            @RequestParam String playerName) {
        return gameService.createGame(playerName);
    }

    @GetMapping("/deck")
    public List<Card> getShuffledDeck() {
        return DeckUtils.createShuffledDeck();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener partida",
            description = "Recupera el estado actual de una partida",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Partida encontrada"),
                    @ApiResponse(responseCode = "404", description = "Partida no encontrada")
            }
    )
    public Mono<Game> getGame(
            @Parameter(description = "ID de la partida", required = true, in = ParameterIn.PATH)
            @PathVariable String id) {
        return gameService.getGame(id);
    }

    @PostMapping("/{id}/hit")
    @Operation(
            summary = "Pedir carta (Hit)",
            description = "El jugador pide una nueva carta",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Carta añadida"),
                    @ApiResponse(responseCode = "400", description = "Partida terminada o mazo vacío"),
                    @ApiResponse(responseCode = "404", description = "Partida no encontrada")
            }
    )
    public Mono<Game> hit(
            @Parameter(description = "ID de la partida", required = true)
            @PathVariable String id) {
        return gameService.hit(id);
    }

    @PostMapping("/{id}/stand")
    @Operation(
            summary = "Plantarse (Stand)",
            description = "Finaliza el turno del jugador y ejecuta la lógica del dealer",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resultado final"),
                    @ApiResponse(responseCode = "404", description = "Partida no encontrada")
            }
    )
    public Mono<Game> stand(
            @Parameter(description = "ID de la partida", required = true)
            @PathVariable String id) {
        return gameService.stand(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Eliminar partida",
            description = "Borra una partida existente",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Partida eliminada"),
                    @ApiResponse(responseCode = "404", description = "Partida no encontrada")
            }
    )
    public Mono<Void> deleteGame(
            @Parameter(description = "ID de la partida", required = true)
            @PathVariable String id) {
        return gameService.deleteGame(id);
    }

    @GetMapping("/{id}/deck")
    @Operation(
            summary = "Ver mazo restante",
            description = "Muestra las cartas restantes en el mazo (para debug)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Info del mazo"),
                    @ApiResponse(responseCode = "404", description = "Partida no encontrada")
            }
    )
    public Mono<Map<String, Object>> getDeck(
            @Parameter(description = "ID de la partida", required = true)
            @PathVariable String id) {

        return gameService.getGame(id)
                .flatMap(game -> {
                    List<Card> deck = game.getDeck();
                    Map<String, Object> response = new LinkedHashMap<>();
                    response.put("remaining_cards", deck.size());
                    List<String> formattedCards = deck.stream()
                            .map(Card::toString)
                            .toList();
                    response.put("cards", formattedCards);
                    return Mono.just(response);
                })
                .onErrorResume(IllegalArgumentException.class, e ->
                        Mono.just(Map.of("error", e.getMessage()))
                );
    }
}

