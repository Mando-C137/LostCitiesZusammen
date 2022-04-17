package domain.players.fabian.domain;

import java.util.List;

public class GameState {

    private final List<Card> playerCards;

    /*
    0-4: Own Played Expedition Cards
    0: Red
    1: Green
    2: Blue
    3: White
    4: Yellow
    5-9: Opponents's Played Expedition Cards
     */
    private final List<List<Card>> field;

    /*
    0: Red
    1: Green
    2: Blue
    3: White
    4: Yellow
     */
    private final List<List<Card>> discardedCards;

    private final int cardsRemaining;
    private final int roundState;

    public GameState(List<Card> playerCards, List<List<Card>> field, List<List<Card>> discardedCards, int cardsRemaining, int roundState) {
        this.playerCards = playerCards;
        this.field = field;
        this.discardedCards = discardedCards;
        this.cardsRemaining = cardsRemaining;
        this.roundState = roundState;
    }

    public int[] calculatePoints() {
        int[] points = new int[2];

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 5; j++) {
                if (this.field.get(j + (i * 5)).size() > 0) {
                    int pointsField = -20;  // expedition cost
                    int factor = 1;

                    for (int x = 0; x < this.field.get(j + (i * 5)).size(); x++) {
                        Card c = this.field.get(j + (i * 5)).get(x);

                        if (c.getValue() == 0)
                            factor++;
                        else
                            pointsField += c.getValue();
                    }

                    points[i] += factor * pointsField;
                    if (this.field.get(j + (i * 5)).size() >= 8) {
                        points[i] += 20;    // bonus for at least 8 cards in one expedition
                    }
                }
            }
        }

        return points;
    }

    public String[] getPlayerCards() {
        String[] cards = new String[this.playerCards.size()];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = this.playerCards.get(i).toString();
        }
        return cards;
    }

    public List<Card> getPlayerCardsObject() {
        return this.playerCards;
    }

    public List<List<Card>> getField() {
        return this.field;
    }

    public List<List<Card>> getDiscardedCards() {
        return this.discardedCards;
    }

    public int getCardsRemaining() {
        return this.cardsRemaining;
    }

    public int getRoundState() {
        return this.roundState;
    }
}
