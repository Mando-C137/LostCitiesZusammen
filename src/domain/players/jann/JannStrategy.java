package domain.players.jann;


import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;
import domain.cards.AbstractCard;
import domain.cards.Color;
import domain.cards.NumberCard;
import domain.cards.Stapel;
import domain.cards.WettCard;
import domain.main.AblagePlay;
import domain.main.Game;
import domain.players.AiPlayer;
import domain.players.fabian.FabianISMCTSStrategy;
import domain.players.jann.game.Card;
import domain.players.jann.game.Move;
import domain.players.jann.player.ISPlayer;
import domain.strategies.PlayStrategy;
import domain.strategies.Strategies;

public class JannStrategy implements PlayStrategy {

  ISPlayer jannPlayer = new ISPlayer(2, 10000, 50, 1, true);
  int[] countCC = new int[] {-1, -1, -1, -1, -1};
  Move jannMove;
  AiPlayer information;
  int turnCount;

  public JannStrategy(AiPlayer information) {
    this.information = information;
    this.turnCount = 0;
  }

  public static void main(String[] args) {
    StringBuilder sb = new StringBuilder("");
    int[] wins = new int[] {0, 0}; // wins[0]=get(1).set
    int[] scores = new int[] {0, 0};
    // game.getPlayers().get(0).setStrategy(new InformationSetStrategy(game.getPlayers().get(0)));
    for (int i = 0; i < 40; i++) {
      Game game = Game.twoWithoutStrategies();
      game.getPlayers().get(1).setStrategy(new JannStrategy(game.getPlayers().get(1)));
      game.getPlayers().get(0).setStrategy(new FabianISMCTSStrategy(game.getPlayers().get(0)));
      game.gameFlow();
      sb.append(game.calculateScores());
      scores[0] = game.calculateScore(game.getPlayers().get(0));
      scores[1] = game.calculateScore(game.getPlayers().get(1));
      if (game.calculateWinnerIndex(0) == 0)
        wins[0]++;
      if (game.calculateWinnerIndex(1) == 1)
        wins[1]++;
    }
    System.out.println(sb + " Wins Jann " + wins[1] + "\tWins Fabian " + wins[0] + "\tScores Jann "
        + scores[1] + "\tScores Fabian " + scores[0]);
  }

  @Override
  public AblagePlay choosePlay(int remainingCards) {
    countCC = new int[] {-1, -1, -1, -1, -1};
    turnCount += 2;
    Stack<Card>[] discardPile = createDiscardPile();
    Stack<Card>[] oppExp = createEnemyExpeditions();
    Stack<Card>[] myExp = translateMap(information.getExpeditionen());
    Card[] hand = translateHand(information.getHandKarten());
    boolean turn = (information.getIndex() == 0) ? true : false;
    jannMove = jannPlayer.makeMove(hand, myExp, oppExp, discardPile, turn, turnCount);
    Card toPlace = hand[jannMove.getCardIndex()];
    AbstractCard paulCard = information.getHandKarten().get(jannMove.getCardIndex());
    AblagePlay ablagePlay =
        new AblagePlay(getStapelFromMove(jannMove, toPlace), translateCardToPaul(toPlace));
    return ablagePlay;
  }

  @Override
  public Stapel chooseStapel() {
    if (jannMove.getDrawFrom() == 0) {
      return Stapel.NACHZIEHSTAPEL;
    }
    switch (jannMove.getDrawFrom()) {
      case 1:
        return Stapel.toMiddle(Color.YELLOW);
      case 2:
        return Stapel.toMiddle(Color.BLUE);
      case 3:
        return Stapel.toMiddle(Color.WHITE);
      case 4:
        return Stapel.toMiddle(Color.GREEN);
      case 5:
        return Stapel.toMiddle(Color.RED);
      default:
        return null;
    }
  }

  @Override
  public String getName() {
    return Strategies.JANN_ISMCTS.name();
  }

  public Stapel getStapelFromMove(Move move, Card card) {
    Color color = turnColorIntoEnum(card.getColor());
    if (move.isOnExp()) {
      return Stapel.toExpedition(color);
    } else {
      return Stapel.toMiddle(color);
    }
  }

  public Stack<Card>[] createEnemyExpeditions() {
    Stack<Card> yellow = translateStack(information.getEnemyExpeditions(Color.YELLOW));
    Stack<Card> blue = translateStack(information.getEnemyExpeditions(Color.BLUE));
    Stack<Card> white = translateStack(information.getEnemyExpeditions(Color.WHITE));
    Stack<Card> green = translateStack(information.getEnemyExpeditions(Color.GREEN));
    Stack<Card> red = translateStack(information.getEnemyExpeditions(Color.RED));
    Stack<Card>[] enemyExp = new Stack[] {yellow, blue, white, green, red};
    return enemyExp;
  }

  public Stack<Card>[] createDiscardPile() {
    Stack<Card> yellow = translateStack(information.getAblagestapel(Color.YELLOW));
    Stack<Card> blue = translateStack(information.getAblagestapel(Color.BLUE));
    Stack<Card> white = translateStack(information.getAblagestapel(Color.WHITE));
    Stack<Card> green = translateStack(information.getAblagestapel(Color.GREEN));
    Stack<Card> red = translateStack(information.getAblagestapel(Color.RED));
    Stack<Card>[] discardPile = new Stack[] {yellow, blue, white, green, red};
    return discardPile;
  }

  private Stack<Card> translateStack(List<AbstractCard> paulStack) {
    List<Card> jannList =
        paulStack.stream().map(ele -> translateCardToJann(ele)).collect(Collectors.toList());
    Stack<Card> jannStack = new Stack<Card>();
    jannStack.addAll(jannList);
    return jannStack;
  }

  private Stack<Card>[] translateMap(Map<Color, Stack<AbstractCard>> mapPaul) {
    Stack<Card>[] stackArray = new Stack[] {new Stack<Card>(), new Stack<Card>(), new Stack<Card>(),
        new Stack<Card>(), new Stack<Card>()};
    Stack<AbstractCard> gelb = mapPaul.get(Color.YELLOW);
    Stack<AbstractCard> blau = mapPaul.get(Color.BLUE);
    Stack<AbstractCard> weiss = mapPaul.get(Color.WHITE);
    Stack<AbstractCard> gruen = mapPaul.get(Color.GREEN);
    Stack<AbstractCard> rot = mapPaul.get(Color.RED);
    stackArray[0] = translateStack(gelb);
    stackArray[1] = translateStack(blau);
    stackArray[2] = translateStack(weiss);
    stackArray[3] = translateStack(gruen);
    stackArray[4] = translateStack(rot);
    return stackArray;
  }

  private Card[] translateHand(List<AbstractCard> cards) {
    Card[] hand = new Card[8];
    int i = 0;
    for (AbstractCard abstractCard : cards) {
      hand[i++] = translateCardToJann(abstractCard);
    }
    return hand;
  }

  private Card translateCardToJann(AbstractCard abstractCard) {
    if (!abstractCard.isNumber()) {
      int color = turnEnumIntoColor(abstractCard.getColor());
      return new Card(color, countCC[color]++);
    }
    return new Card(turnEnumIntoColor(abstractCard.getColor()), abstractCard.getValue());
  }

  private AbstractCard translateCardToPaul(Card card) {
    Color color = turnColorIntoEnum(card.getColor());
    if (card.isCoinCard()) {
      return new WettCard(color);
    } else {
      return new NumberCard(color, card.getValue());
    }
  }

  private Color turnColorIntoEnum(int color) {
    if (color == 0)
      return Color.YELLOW;
    if (color == 1)
      return Color.BLUE;
    if (color == 2)
      return Color.WHITE;
    if (color == 3)
      return Color.GREEN;
    if (color == 4)
      return Color.RED;
    return null;
  }

  private int turnEnumIntoColor(Color colorEnum) {
    if (colorEnum == Color.YELLOW)
      return 0;
    if (colorEnum == Color.BLUE)
      return 1;
    if (colorEnum == Color.WHITE)
      return 2;
    if (colorEnum == Color.GREEN)
      return 3;
    if (colorEnum == Color.RED)
      return 4;
    return -1;
  }
}
