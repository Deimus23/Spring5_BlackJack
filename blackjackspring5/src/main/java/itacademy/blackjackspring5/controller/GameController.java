package itacademy.blackjackspring5.controller;

import io.swagger.v3.oas.annotations.Operation;
import itacademy.blackjackspring5.model.mongodb.Game;
import itacademy.blackjackspring5.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;
import java.util.Map;

@RestController
@RequestMapping("/games")
@Tag(name = "Game Controller", description = "Gestión de partidas de Blackjack")
public class GameController {


    private final GameService gameService;

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
            @RequestParam String playerName,
            @Parameter(description= "Apuesta del jugador", required= true , example="12" )
            @RequestParam int playerBet) {
        return gameService.createGame(playerName,playerBet);
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


    public Mono<Map<String, Object>> getDeck(
            @Parameter(description = "ID de la partida", required = true)
            @PathVariable String id) {
            return gameService.getDeck(id);
    }
}

