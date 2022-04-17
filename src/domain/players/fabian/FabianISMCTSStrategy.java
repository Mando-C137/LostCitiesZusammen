package domain.players.fabian;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import domain.cards.AbstractCard;
import domain.cards.Color;
import domain.cards.Stapel;
import domain.main.AblagePlay;
import domain.players.AbstractPlayer;
import domain.players.AiPlayer;
import domain.players.fabian.domain.*;
import domain.strategies.PlayStrategy;
import domain.strategies.Strategies;

/**
 * AI for Lost Cities. "Foreign" domain objects will be transformed into known objects.
 *
 * @author Fabian Rajwa
 */
public class FabianISMCTSStrategy implements PlayStrategy {

  private AiPlayer player;

  /**
   * Time for MCTS iterations.
   */
  private final long MAX_TIME = 10000;

  /**
   * Amount of active additional threads.
   */
  private final int AMOUNT_THREADS = 5;

  /**
   * Needed for own domain specifications.
   */
  private final int playerNumber = 1;

  /**
   * Normally used to save known cards of the opponent. This won't be probably used within the
   * interface.
   */
  private List<Card> opponentCards;

  /**
   * This contains the first part (playing a card) and the second part (drawing a card) from a move.
   */
  private MoveSet move;

  public FabianISMCTSStrategy(AiPlayer aiPlayer) {
    this.player = aiPlayer;
  }

  /**
   * Plays the first part (playing a card) of a move.
   */
  @Override
  public AblagePlay choosePlay(int remainingCards) {
    //this.opponentCards = this.transformList(this.player.getModel());
    this.opponentCards = new ArrayList<>();
    this.move = this.createTree(this.createGameState(remainingCards));

    // transform PlayMove into AblagePlay
    PlayMove playMove = this.move.getPlayMove();
    AbstractCard card = this.player.getHandKarten().get(playMove.getCard() - 1);

    Stapel stapel = null;
    if (playMove.getTarget() == 1) {
      switch (playMove.getCardObject().getColor()) {
        case 0:
          stapel = Stapel.REDEXPEDITION;
          break;
        case 1:
          stapel = Stapel.GREENEXPEDITION;
          break;
        case 2:
          stapel = Stapel.BLUEEXPEDITION;
          break;
        case 3:
          stapel = Stapel.WHITEEXPEDITION;
          break;
        case 4:
          stapel = Stapel.YELLOWEXPEDITION;
          break;
      }
    } else {
      switch (playMove.getCardObject().getColor()) {
        case 0:
          stapel = Stapel.REDMIDDLE;
          break;
        case 1:
          stapel = Stapel.GREENMIDDLE;
          break;
        case 2:
          stapel = Stapel.BLUEMIDDLE;
          break;
        case 3:
          stapel = Stapel.WHITEMIDDLE;
          break;
        case 4:
          stapel = Stapel.YELLOWMIDDLE;
          break;
      }
    }

    return new AblagePlay(stapel, card);
  }

  /**
   * Plays the second part (drawing a card) of a move.
   */
  @Override
  public Stapel chooseStapel() {
    // transform TakeMove into Stapel
    TakeMove takeMove = this.move.getTakeMove();
    Stapel stapel = null;

    switch (takeMove.getTarget()) {
      case 0:
        stapel = Stapel.REDMIDDLE;
        break;
      case 1:
        stapel = Stapel.GREENMIDDLE;
        break;
      case 2:
        stapel = Stapel.BLUEMIDDLE;
        break;
      case 3:
        stapel = Stapel.WHITEMIDDLE;
        break;
      case 4:
        stapel = Stapel.YELLOWMIDDLE;
        break;
      case 5:
        stapel = Stapel.NACHZIEHSTAPEL;
    }

    return stapel;
  }



  /**
   * Creates a SO-ISMCTS and determines the best MoveSet.
   */
  private MoveSet createTree(GameState state) {
    List<MCTSAgent> agents = new ArrayList<>(AMOUNT_THREADS + 1);
    List<MCTSThread> threads = new ArrayList<>(AMOUNT_THREADS);

    for (int i = 0; i < AMOUNT_THREADS; i++) {
      agents.add(new MCTSAgent(state, this.playerNumber, this.opponentCards));
      threads.add(new MCTSThread(agents.get(i)));
    }
    agents.add(new MCTSAgent(state, this.playerNumber, this.opponentCards));

    long time = System.currentTimeMillis();
    for (int i = 0; i < AMOUNT_THREADS; i++) {
      threads.get(i).start();
    }
    while (System.currentTimeMillis() - time <= this.MAX_TIME) {
      agents.get(AMOUNT_THREADS).runThrough();
    }
    for (int i = 0; i < AMOUNT_THREADS; i++) {
      threads.get(i).stopRun();
    }

    Node root = agents.get(AMOUNT_THREADS).getRoot();
    for (int i = 0; i < AMOUNT_THREADS; i++) {
      while (!threads.get(i).isFinished()) {
        // wait until last iteration was finished
        try {
          Thread.sleep(10);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      root.fusionWithOther(agents.get(i).getRoot());
    }

    return root.determineMoveSet();
  }

  /**
   * Creates a GameState object with the needed information from the Game object.
   */
  private GameState createGameState(int remainingCards) {
    // Player Cards
    List<AbstractCard> playerCardsRaw = this.player.getHandKarten();
    List<Card> playerCards = new ArrayList<>(8);

    for (AbstractCard card : playerCardsRaw) {
      playerCards.add(this.transformCard(card));
    }

    // Expedition Fields
    Color[] colors = new Color[] {Color.RED, Color.GREEN, Color.BLUE, Color.WHITE, Color.YELLOW};
    List<List<Card>> field = new ArrayList<>(10);
    Map<Color, Stack<AbstractCard>> ownEpeditions = this.player.getExpeditionen();

    for (Color color : colors) {
      field.add(this.transformStack(ownEpeditions.get(color)));
    }
    for (Color color : colors) {
      field.add(this.transformList(this.player.getEnemyExpeditions(color)));
    }

    // Discard Fields
    List<List<Card>> discardedFields = new ArrayList<>(5);
    LinkedList<Stapel> discardedStapels = this.player.getDrawSet();
    for (Color color : colors) {
      discardedFields.add(this.transformList(this.player.getAblagestapel(color)));
    }

    // Return
    return new GameState(playerCards, field, discardedFields, remainingCards, 0);
  }

  /**
   * Transforms a AbstractCard object into a Card object.
   */
  private Card transformCard(AbstractCard card) {
    int color;
    if (card.getColor().equals(Color.RED)) {
      color = 0;
    } else if (card.getColor().equals(Color.GREEN)) {
      color = 1;
    } else if (card.getColor().equals(Color.BLUE)) {
      color = 2;
    } else if (card.getColor().equals(Color.WHITE)) {
      color = 3;
    } else {
      color = 4;
    }

    return new Card(color, card.getValue());
  }

  /**
   * Transforms a stack of AbstractCards into an ArrayList of Cards.
   */
  private List<Card> transformStack(Stack<AbstractCard> stack) {
    List<Card> pile = new ArrayList<>();

    for (AbstractCard card : stack) {
      pile.add(this.transformCard(card));
    }

    return pile;
  }

  /**
   * Transforms a list of AbstractCards into an ArrayList of Cards.
   */
  private List<Card> transformList(List<AbstractCard> stack) {
    List<Card> pile = new ArrayList<>();

    for (AbstractCard card : stack) {
      pile.add(this.transformCard(card));
    }

    return pile;
  }

  @Override
  public String getName() {
    return Strategies.FABIAN_ISMCTS.toString();
  }
}
