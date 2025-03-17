package itacademy.blackjackspring5.service;


import itacademy.blackjackspring5.model.enums.GameResult;
import itacademy.blackjackspring5.model.enums.GameStatus;
import itacademy.blackjackspring5.model.mongodb.Card;
import itacademy.blackjackspring5.model.mongodb.Game;
import itacademy.blackjackspring5.model.mongodb.enums.Rank;
import itacademy.blackjackspring5.repository.mongodb.GameRepository;
import itacademy.blackjackspring5.util.DeckUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final PlayerService playerService;

    public Mono<Game> createGame(String playerName) {
        return playerService.createOrUpdatePlayer(playerName)
                .flatMap(player -> {
                    List<Card> deck = DeckUtils.createNewShuffledDeck();
                    Game game = new Game();
                    game.setPlayerId(player.getName());

                    // Repartir cartas iniciales
                    game.setPlayerHand(drawInitialCards(deck));
                    game.setDealerHand(drawDealerInitialCards(deck));
                    game.setDeck(DeckUtils.serializeDeck(deck));
                    game.setPlayerScore(calculateHandValue(game.getPlayerHand()));

                    // Verificar blackjack natural
                    if (game.getPlayerScore() == 21) {
                        game.setStatus(GameStatus.COMPLETED);
                        game.setResult(GameResult.WIN);
                        playerService.updatePlayerStats(player.getName(), true);
                    }

                    return gameRepository.save(game);
                });
    }

    public Mono<Game> hit(String gameId) {
        return gameRepository.findById(gameId)
                .flatMap(game -> {
                    if (game.getStatus() == GameStatus.COMPLETED) {
                        return Mono.error(new IllegalStateException("La partida ya ha terminado"));
                    }

                    List<Card> deck = DeckUtils.deserializeDeck(game.getDeck());
                    if (deck.isEmpty()) {
                        return Mono.error(new IllegalStateException("No hay cartas en el mazo"));
                    }

                    Card drawnCard = deck.remove(0);
                    List<String> newHand = new ArrayList<>(game.getPlayerHand());
                    newHand.add(drawnCard.toString());

                    game.setPlayerHand(newHand);
                    game.setPlayerScore(calculateHandValue(newHand));
                    game.setDeck(DeckUtils.serializeDeck(deck));

                    if (game.getPlayerScore() > 21) {
                        endGame(game, GameResult.LOSE);
                    }

                    return gameRepository.save(game);
                });
    }

    public Mono<Game> stand(String gameId) {
        return gameRepository.findById(gameId)
                .flatMap(game -> {
                    List<Card> deck = DeckUtils.deserializeDeck(game.getDeck());
                    List<String> dealerHand = new ArrayList<>(game.getDealerHand());

                    // Revelar carta oculta
                    dealerHand.set(1, deck.remove(0).toString());

                    // Lógica del dealer
                    while (calculateHandValue(dealerHand) < 17 && !deck.isEmpty()) {
                        dealerHand.add(deck.remove(0).toString());
                    }

                    game.setDealerHand(dealerHand);
                    game.setDeck(DeckUtils.serializeDeck(deck));

                    int dealerScore = calculateHandValue(dealerHand);
                    int playerScore = game.getPlayerScore();

                    GameResult result = determineResult(playerScore, dealerScore);
                    endGame(game, result);

                    return gameRepository.save(game);
                });
    }

    private void endGame(Game game, GameResult result) {
        game.setStatus(GameStatus.COMPLETED);
        game.setResult(result);
        playerService.updatePlayerStats(game.getPlayerId(), result == GameResult.WIN)
                .subscribe(); // Ejecución en segundo plano
    }

    private List<String> drawInitialCards(List<Card> deck) {
        return List.of(
                deck.remove(0).toString(),
                deck.remove(0).toString()
        );
    }

    private List<String> drawDealerInitialCards(List<Card> deck) {
        return List.of(
                deck.remove(0).toString(),
                "HIDDEN"
        );
    }

    private int calculateHandValue(List<String> hand) {
        int total = 0;
        int aces = 0;

        for (String card : hand) {
            if (card.equals("HIDDEN")) continue;

            String rank = card.split("-")[0];
            int value = Rank.valueOf(rank).getValue();
            total += value;
            if (rank.equals("ACE")) aces++;
        }

        while (total > 21 && aces > 0) {
            total -= 10;
            aces--;
        }
        return total;
    }

    private GameResult determineResult(int playerScore, int dealerScore) {
        if (playerScore > 21) return GameResult.LOSE;
        if (dealerScore > 21) return GameResult.WIN;
        if (playerScore > dealerScore) return GameResult.WIN;
        if (playerScore < dealerScore) return GameResult.LOSE;
        return GameResult.DRAW;
    }
}