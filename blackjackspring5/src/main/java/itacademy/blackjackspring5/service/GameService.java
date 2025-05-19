package itacademy.blackjackspring5.service;

import itacademy.blackjackspring5.model.mongodb.Card;
import itacademy.blackjackspring5.model.mongodb.Game;
import itacademy.blackjackspring5.model.mongodb.enums.GameStatus;
import itacademy.blackjackspring5.model.mongodb.enums.Rank;
import itacademy.blackjackspring5.model.mongodb.enums.Suit;
import itacademy.blackjackspring5.repository.mongodb.GameRepository;
import itacademy.blackjackspring5.util.DeckUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final PlayerService playerService;

    public Mono<Game> createGame(String playerName, int playerBet) {
        return playerService.createOrUpdatePlayer(playerName)
                .flatMap(player -> {
                    List<Card> deck = DeckUtils.createShuffledDeck();
                    Game game = initGame(player.getId(), playerBet, deck);

                    if (game.getPlayerScore() == 21) {
                        concludeGame(game, playerBet);
                        playerService.updatePlayerStats(player.getName(), true).subscribe();
                    }

                    return gameRepository.save(game);
                });
    }

    private Game initGame(String playerId, int bet, List<Card> deck) {
        List<Card> playerHand = drawCards(deck, 2);
        List<Card> dealerHand = Arrays.asList(drawCard(deck), hiddenCard());

        return Game.builder()
                .playerId(playerId)
                .playerBet(bet)
                .playerHand(new ArrayList<>(playerHand))
                .dealerHand(new ArrayList<>(dealerHand))
                .deck(deck)
                .playerScore(handValue(playerHand))
                .status(GameStatus.IN_PROGRES)
                .build();
    }

    public Mono<Game> hit(String gameId) {
        return gameRepository.findById(gameId)
                .flatMap(game -> {
                    if (game.getStatus() != GameStatus.IN_PROGRES)
                        return Mono.error(new IllegalStateException("La partida ya ha terminado"));

                    if (game.getDeck().isEmpty())
                        return Mono.error(new IllegalStateException("No hay cartas en el mazo"));

                    Card card = drawCard(game.getDeck());
                    game.getPlayerHand().add(card);
                    game.setPlayerScore(handValue(game.getPlayerHand()));

                    if (game.getPlayerScore() > 21)
                        concludeGame(game, game.getPlayerBet());

                    return gameRepository.save(game);
                });
    }

    public Mono<Game> stand(String gameId) {
        return gameRepository.findById(gameId)
                .flatMap(game -> {
                    revealDealerCard(game);
                    dealerPlays(game);
                    concludeGame(game, game.getPlayerBet());
                    return gameRepository.save(game);
                });
    }

    private void concludeGame(Game game, int bet) {
        int dealerScore = handValue(game.getDealerHand());
        GameStatus status = evaluateOutcome(game.getPlayerScore(), dealerScore);

        game.setStatus(status);
        game.setResult(generateResult(status, bet));

        boolean won = status == GameStatus.WIN;
        playerService.updatePlayerStats(game.getPlayerId().toString(), won).subscribe();
    }

    private String generateResult(GameStatus status, int bet) {
        return switch (status) {
            case WIN -> "Has ganado: " + (bet * 2);
            case LOSE -> "Has palmado: " + (-bet);
            case DRAW -> "Empate, te quedas con: " + bet;
            default -> "";
        };
    }

    private void revealDealerCard(Game game) {
        List<Card> dealerHand = game.getDealerHand();
        dealerHand.set(1, drawCard(game.getDeck()));
    }

    private void dealerPlays(Game game) {
        while (handValue(game.getDealerHand()) < 17 && !game.getDeck().isEmpty()) {
            game.getDealerHand().add(drawCard(game.getDeck()));
        }
    }

    private GameStatus evaluateOutcome(int playerScore, int dealerScore) {
        if (playerScore > 21) return GameStatus.LOSE;
        if (dealerScore > 21 || playerScore > dealerScore) return GameStatus.WIN;
        if (playerScore < dealerScore) return GameStatus.LOSE;
        return GameStatus.DRAW;
    }

    private int handValue(List<Card> hand) {
        int sum = 0, aces = 0;
        for (Card card : hand) {
            if (card.getRank() == Rank.HIDDEN) continue;
            sum += card.getRank().getValue();
            if (card.getRank() == Rank.ACE) aces++;
        }
        while (sum > 21 && aces-- > 0) sum -= 10;
        return sum;
    }

    private Card drawCard(List<Card> deck) {
        return deck.remove(0);
    }

    private List<Card> drawCards(List<Card> deck, int count) {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < count; i++) cards.add(drawCard(deck));
        return cards;
    }

    private Card hiddenCard() {
        return new Card(Suit.HIDDEN, Rank.HIDDEN);
    }

    public Mono<Game> getGame(String gameId) {
        return gameRepository.findById(gameId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Juego no encontrado con ID: " + gameId)));
    }

    public Mono<Map<String, Object>> getDeck(String gameId) {
        return gameRepository.findById(gameId)
                .map(game -> {
                    Map<String, Object> info = new LinkedHashMap<>();
                    info.put("remaining_cards", game.getDeck().size());
                    info.put("cards", game.getDeck().stream().map(Card::toString).toList());
                    return info;
                })
                .onErrorResume(IllegalArgumentException.class, e ->
                        Mono.just(Map.of("error", e.getMessage()))
                );
    }

    public Mono<Void> deleteGame(String gameId) {
        return gameRepository.findById(gameId)
                .flatMap(gameRepository::delete)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Juego no encontrado con ID: " + gameId)));
    }
}
