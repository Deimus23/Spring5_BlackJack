package itacademy.blackjackspring5.service;

import itacademy.blackjackspring5.model.mongodb.Game;
import itacademy.blackjackspring5.model.mongodb.Card;
import itacademy.blackjackspring5.model.mongodb.enums.GameStatus;
import itacademy.blackjackspring5.model.mongodb.enums.Rank;
import itacademy.blackjackspring5.model.mongodb.enums.Suit;
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

    public Mono<Game> createGame(String playerName,int playerBet) {

        return playerService.createOrUpdatePlayer(playerName)
                .flatMap(player -> {

                    List<Card> deck = DeckUtils.createShuffledDeck();
                    Game game = new Game();
                    game.setPlayerId(player.getId());
                    game.setPlayerBet(playerBet);
                    game.setPlayerHand(drawInitialCards(deck));
                    game.setDealerHand(drawDealerInitialCards(deck));
                    game.setDeck(deck);
                    game.setPlayerScore(calculateHandValue(game.getPlayerHand()));



                    if (game.getPlayerScore() == 21) {
                        endGame(game,playerBet);
                        playerService.updatePlayerStats(player.getName(), true);
                    }

                    return gameRepository.save(game);
                });
    }

    public Mono<Game> hit(String gameId) {
        return gameRepository.findById(gameId)
                .flatMap(game -> {
                    if (game.getStatus() != GameStatus.IN_PROGRES) {
                        return Mono.error(new IllegalStateException("La partida ya ha terminado"));
                    }

                    List<Card> deck = game.getDeck();
                    if (deck.isEmpty()) {
                        return Mono.error(new IllegalStateException("No hay cartas en el mazo"));
                    }

                    Card drawnCard = deck.removeFirst();
                    List<Card> newHand = new ArrayList<>(game.getPlayerHand());
                    newHand.add(drawnCard);

                    game.setPlayerHand(newHand);
                    game.setPlayerScore(calculateHandValue(newHand));
                    game.setDeck(deck);
                   int bet= game.getPlayerBet();
//TODO:
                    if (game.getPlayerScore() > 21) {
                        endGame(game,bet);
                    }

                    return gameRepository.save(game);
                });
    }

    public Mono<Game> stand(String gameId) {
        return gameRepository.findById(gameId)
                .flatMap(game -> {
                    List<Card> deck = game.getDeck();
                    List<Card> dealerHand = new ArrayList<>(game.getDealerHand());

                    dealerHand.set(1, deck.remove(0));

                    while (calculateHandValue(dealerHand) < 17 && !deck.isEmpty()) {
                        dealerHand.add(deck.remove(0));
                    }

                    game.setDealerHand(dealerHand);
                    game.setDeck(deck);
                    int bet= game.getPlayerBet();
                    endGame(game,bet);

                    return gameRepository.save(game);
                });
    }

    private void endGame(Game game,int bet) {
        game.setStatus(determinateStatus(game.getPlayerScore(),calculateHandValue(game.getDealerHand())));
        boolean win=false;
        if(game.getStatus()==GameStatus.WIN){
            String result ="Has ganado: "+bet*2;
            game.setResult(result);
             win= true;
        } else if (game.getStatus()==GameStatus.LOSE) {
            String result="Has palmado:" +(0-bet);
            game.setResult(result);
        }else {
            String result="Empate , te quedas con :"+bet;
            game.setResult(result);
        }
        playerService.updatePlayerStats(game.getPlayerId().toString(),win)
                .subscribe();
    }

    private List<Card> drawInitialCards(List<Card> deck) {
        return List.of(deck.remove(0), deck.remove(0));
    }

    private List<Card> drawDealerInitialCards(List<Card> deck) {
        List<Card> dealerHand = new ArrayList<>();
        dealerHand.add(deck.remove(0));
        dealerHand.add(new Card(Suit.HIDDEN, Rank.HIDDEN));
        return dealerHand;
    }

    private int calculateHandValue(List<Card> hand) {
        int total = 0;
        int aces = 0;

        for (Card card : hand) {
            if (card.getRank() == Rank.HIDDEN) continue;

            int value = card.getRank().getValue();
            total += value;
            if (card.getRank() == Rank.ACE) aces++;
        }

        while (total > 21 && aces > 0) {
            total -= 10;
            aces--;
        }
        return total;
    }

    private GameStatus determinateStatus(int playerScore, int dealerScore) {
        if (playerScore > 21) return GameStatus.LOSE;
        if (dealerScore > 21) return GameStatus.WIN;
        if (playerScore > dealerScore) return GameStatus.WIN;
        if (playerScore < dealerScore) return GameStatus.LOSE;
        return GameStatus.DRAW;
    }

    public Mono<Game> getGame(String gameId) {
        return gameRepository.findById(gameId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Juego no encontrado con ID: " + gameId)));
    }

    public Mono<Void> deleteGame(String gameId) {
        return gameRepository.findById(gameId)
                .flatMap(game -> gameRepository.delete(game))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Juego no encontrado con ID: " + gameId)));
    }
}
