package domain.players.fabian;

import domain.players.fabian.domain.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StateCopy {

    private final List<Card> playerCards;
    private final List<Card> opponentsCards;    // random cards

    private List<Card> pile;  // random cards

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

    private int cardsRemaining;
    private int roundState;

    private final int player;
    private final int opponent;

    /**
     * Works like a copy constructor.
     */
    public StateCopy(GameState state, int player, List<Card> knownOpponentCards) {
        this.player = player;
        this.opponent = player == 1 ? 2 : 1;

        this.playerCards = new ArrayList<>();
        this.playerCards.addAll(state.getPlayerCardsObject());
        this.field = new ArrayList<>();
        for (int i = 0; i < state.getField().size(); i++) {
            ArrayList<Card> tmp = new ArrayList<>();
            tmp.addAll(state.getField().get(i));
            this.field.add(tmp);
        }
        this.discardedCards = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ArrayList<Card> tmp = new ArrayList<>();
            tmp.addAll(state.getDiscardedCards().get(i));
            this.discardedCards.add(tmp);
        }
        this.cardsRemaining = state.getCardsRemaining();
        this.roundState = state.getRoundState();

        // randomize unknown cards
        this.opponentsCards = new ArrayList<>();
        this.opponentsCards.addAll(knownOpponentCards);
        this.createRandomCards(state);
    }

    /**
     * Executes a random move to the current state and returns it.
     */
    public MoveSet executeRandomMove() {
        // randomize a move
        List<MoveSet> possibleMoves = this.getPossibleMoves();
        int random = (int) (Math.random() * possibleMoves.size());
        MoveSet move = possibleMoves.get(random);

        this.executeMove(move);

        return move;
    }

    /**
     * Applies a move to the current state.
     */
    public void executeMove(MoveSet move) {
        if ((this.player == 1 && this.roundState == 0) ||
                (this.player == 2 && this.roundState == 2)) {
            this.applyMove(move, this.playerCards, 0);
        } else {
            this.applyMove(move, this.opponentsCards, 5);
        }
        this.roundState = this.roundState == 0 ? 2 : 0;

        // check if game is over
        if (this.cardsRemaining == 0) {
            this.roundState = 4;
        }
    }

    /**
     * Calculates and returns all possible froms within the current state.
     */
    public List<MoveSet> getPossibleMoves() {
        List<MoveSet> possibleMoves = new ArrayList<>();

        if (this.player == 1 && this.roundState == 0 || this.player == 2 && this.roundState == 2) {
            // Own Move
            this.addMoveSets(possibleMoves, this.player, this.playerCards, 0);
            this.removeBadMoves(possibleMoves, this.playerCards, 0);
            if (possibleMoves.size() == 0) {
                // only bad moves are available
                possibleMoves = new ArrayList<>();
                this.addMoveSets(possibleMoves, this.player, this.playerCards, 0);
            }
        } else if (this.player == 1 && this.roundState == 2 || this.player == 2 && this.roundState == 0) {
            // Opponent's move
            this.addMoveSets(possibleMoves, this.opponent, this.opponentsCards, 5);
            this.removeBadMoves(possibleMoves, this.opponentsCards, 5);
            if (possibleMoves.size() == 0) {
                // only bad moves are available
                possibleMoves = new ArrayList<>();
                this.addMoveSets(possibleMoves, this.opponent, this.opponentsCards, 5);
            }
        }

        return possibleMoves;
    }

    public double simulateGame() {
        while (this.roundState != 4) {
            this.executeRandomMove();
        }

        int[] finalPoints = this.calculatePoints();
        if (finalPoints[0] > finalPoints[1]) {
            return 1.0;
        } else if (finalPoints[0] < finalPoints[1]) {
            return 0;
        }

        return 0.5;
    }

    /**
     * Deletes from a list of moves all obvious bad moves.
     */
    private void removeBadMoves(List<MoveSet> possibleMoves, List<Card> currentPlayerCards, int offset) {
        Main:
        for (int i = 0; i < possibleMoves.size(); i++) {
            // inspect PlayMove for mistakes
            PlayMove playMove = possibleMoves.get(i).getPlayMove();
            int colorPlayMove = playMove.getCardObject().getColor();
            if (playMove.getTarget() == 2) {
                // don't discard useful cards
                if (this.field.get(colorPlayMove+offset).size() > 0 &&
                        this.field.get(colorPlayMove+offset).get(this.field.get(colorPlayMove+offset).size()-1).getValue() <
                        playMove.getCardObject().getValue()) {
                    possibleMoves.remove(i);
                    i--;
                    continue;
                }

                // don't give good cards to the opponent
                int opponentField = (colorPlayMove+5+offset) % 10;
                if (this.field.get(opponentField).size() > 0 && this.field.get(opponentField).get(this.field.get(opponentField).size()-1).getValue() <
                        playMove.getCardObject().getValue()) {
                    possibleMoves.remove(i);
                    i--;
                    continue;
                }
            } else {
                int sameColor = 0;
                int colorValue = 0;
                for (Card card : currentPlayerCards) {
                    if (card.getColor() == colorPlayMove) {
                        if (card.getValue() < playMove.getCardObject().getValue() &&
                                card.getValue() != 0) {
                            possibleMoves.remove(i);
                            i--;
                            continue Main;
                        }

                        sameColor++;
                        colorValue += card.getValue();
                    }
                }

                if (this.field.get(colorPlayMove+offset).size() == 0 && (sameColor <= 2 || colorValue <= 10)) {
                    // don't open expedition if there is no hope for it to be successful
                    possibleMoves.remove(i);
                    i--;
                    continue;
                }

                if (playMove.getCardObject().getValue() == 0 &&
                        this.field.get(colorPlayMove).size() == 0 && (sameColor <= 3 || colorValue <= 12)) {
                    // don't play a wager card if your hand cards are not good enough
                    possibleMoves.remove(i);
                    i--;
                    continue;
                }
            }

            // inspect TakeMove for mistakes
            TakeMove takeMove = possibleMoves.get(i).getTakeMove();
            int colorTakeMove = takeMove.getTarget();
            if (colorTakeMove != 5) {
                int sameColor = 0;
                for (Card card : currentPlayerCards) {
                    if (card.getColor() == colorPlayMove) {
                        sameColor++;
                    }
                }
                if ((this.field.get(colorTakeMove+offset).size() == 0 && sameColor <= 1) || (this.field.get(colorTakeMove+offset).size() > 0 &&
                        this.field.get(colorTakeMove+offset).get(this.field.get(colorTakeMove+offset).size()-1).getValue() >
                this.discardedCards.get(colorTakeMove).get( this.discardedCards.get(colorTakeMove).size()-1).getValue())) {
                    // don't draw unnecessary cards from discard piles (when you are not losing)
                    if (this.cardsRemaining <= 10) {
                        int points[] = this.calculatePoints();
                        if ((offset == 0 && points[0] > points[1]) || (offset == 5 && points[0] < points[1])) {
                            possibleMoves.remove(i);
                            i--;
                            continue;
                        }
                    } else {
                        possibleMoves.remove(i);
                        i--;
                        continue;
                    }
                }
            }
        }
    }

    public int[] calculatePoints() {
        int[] points = new int[2];

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 5; j++) {
                if (this.field.get(j+(i*5)).size() > 0) {
                    int pointsField = -20;  // expedition cost
                    int factor = 1;

                    for (int x = 0; x < this.field.get(j+(i*5)).size(); x++) {
                        Card c = this.field.get(j+(i*5)).get(x);

                        if (c.getValue() == 0)
                            factor++;
                        else
                            pointsField += c.getValue();
                    }

                    points[i] += factor * pointsField;
                    if (this.field.get(j+(i*5)).size() >= 8) {
                        points[i] += 20;    // bonus for at least 8 cards in one expedition
                    }
                }
            }
        }

        return points;
    }

    public int getRoundState() {
        return this.roundState;
    }

    private void createRandomCards(GameState state) {
        // create all Cards
        this.pile = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            for (int x = 2; x <= 10; x++) {
                this.pile.add(new Card(i, x));
            }
            this.pile.add(new Card(i, 0));
            this.pile.add(new Card(i, 0));
            this.pile.add(new Card(i, 0));
        }

        // removing known cards from pile
        this.removeCardsFromPile(state.getPlayerCardsObject());
        for (int i = 0; i < state.getField().size(); i++) {
            this.removeCardsFromPile(state.getField().get(i));
        }
        for (int i = 0; i < state.getDiscardedCards().size(); i++) {
            this.removeCardsFromPile(state.getDiscardedCards().get(i));
        }

        // remove already known cards
        this.removeCardsFromPile(this.opponentsCards);

        // randomize order
        Collections.shuffle(this.pile);

        // distribute first x cards to opponent
        for (int i = this.opponentsCards.size(); i < 8; i++) {
            this.opponentsCards.add(this.pile.get(0));
            this.pile.remove(0);
        }
    }

    private void removeCardsFromPile(List<Card> list) {
        for (Card c : list) {
            for (int i = 0; i < this.pile.size(); i++) {
                if (c.getColor() == this.pile.get(i).getColor()) {
                    if (c.getValue() == this.pile.get(i).getValue()) {
                        this.pile.remove(i);
                        break;
                    }
                }
            }
        }
    }

    private void addMoveSets(List<MoveSet> moveList, int currentPlayer, List<Card> currentPlayerCards, int offset) {
        for (int i = 0; i < 8; i++) {
            // all combinations of discarding moves + drawing moves
            PlayMove discardMove = new PlayMove(currentPlayer, i+1, currentPlayerCards.get(i), 2);
            int cardColor = currentPlayerCards.get(i).getColor();
            this.addTakeMoves(moveList, discardMove, currentPlayer, cardColor);

            // all combination of expedition moves + drawing moves
            if (this.field.get(cardColor+offset).size() == 0 ||
                    this.field.get(cardColor+offset).get(this.field.get(cardColor+offset).size()-1).getValue() <= currentPlayerCards.get(i).getValue()) {
                PlayMove expeditionMove = new PlayMove(currentPlayer, i+1, currentPlayerCards.get(i), 1);
                this.addTakeMoves(moveList, expeditionMove, currentPlayer, -1);
            }
        }
    }

    private void addTakeMoves(List<MoveSet> moveList, PlayMove playMove, int currentPlayer, int cardColor) {
        moveList.add(new MoveSet(playMove, new TakeMove(currentPlayer, 5)));
        for (int j = 0; j < 5; j++) {
            if (j != cardColor && this.discardedCards.get(j).size() > 0) {
                MoveSet moveSet = new MoveSet(playMove, new TakeMove(currentPlayer, j));
                moveList.add(moveSet);
            }
        }
    }

    private void applyMove(MoveSet move, List<Card> currentPlayerCards, int offset) {
        // PlayMove Sequence
        PlayMove playMove = move.getPlayMove();

        if (playMove.getTarget() == 1) {
            this.field.get(playMove.getCardObject().getColor()+offset).add(playMove.getCardObject());
        } else {
            this.discardedCards.get(playMove.getCardObject().getColor()).add(playMove.getCardObject());
        }
        currentPlayerCards.remove(playMove.getCard()-1);

        // TakeMove Sequence
        TakeMove takeMove = move.getTakeMove();

        if (takeMove.getTarget() == 5) {
            currentPlayerCards.add(this.pile.get(0));
            this.pile.remove(0);
            this.cardsRemaining--;
        } else {
            currentPlayerCards.add(this.discardedCards.get((takeMove.getTarget())).get(
                    this.discardedCards.get((takeMove.getTarget())).size()-1));
            this.discardedCards.get(takeMove.getTarget()).remove(this.discardedCards.get((takeMove.getTarget())).size()-1);
        }
    }
}
