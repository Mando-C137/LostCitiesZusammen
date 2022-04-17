package domain.players.jann;


import domain.cards.AbstractCard;
import domain.cards.Color;
import domain.cards.NumberCard;
import domain.cards.Stapel;
import domain.cards.WettCard;
import domain.main.AblagePlay;
import domain.main.Game;
import domain.players.AiPlayer;
import domain.players.jann.game.Card;
import domain.players.jann.game.Move;
import domain.players.jann.player.ISPlayer;
import domain.strategies.PlayStrategy;
import domain.strategies.RandomStrategy;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class JannStrategy implements PlayStrategy {

ISPlayer jannPlayer = new ISPlayer(true,1000,0.7,0);
Move jannMove;
AiPlayer information;

public JannStrategy(AiPlayer information){
  this.information = information;
}

public static void main(String[] args){
  Game game;
  game = Game.twoWithoutStrategies();
  game.getPlayers().get(0).setStrategy(new JannStrategy(game.getPlayers().get(0)));
  game.getPlayers().get(1).setStrategy(new RandomStrategy(game.getPlayers().get(1)));

  game.gameFlow();
}

  @Override
  public AblagePlay choosePlay(int remainingCards) {
  Stack<Card>[] discardPile = createDiscardPile();
  Stack<Card>[] oppExp = createEnemyExpeditions();
  Stack<Card>[] myExp = translateMap(information.getExpeditionen());
  Card[] hand = translateHand(information.getHandKarten());
  boolean turn = (information.getIndex()==0)?true:false;
  jannMove = jannPlayer.makeMove(hand,myExp,oppExp,discardPile,turn);
  Card toPlace = hand[jannMove.getCardIndex()];
  AblagePlay ablagePlay = new AblagePlay(getStapelFromMove(jannMove,toPlace),translateCardToPaul(toPlace));
    return ablagePlay;
  }

  @Override
  public Stapel chooseStapel() {
    Stapel stapel;
    switch(jannMove.getDrawFrom()){
      case 0:
        stapel = Stapel.NACHZIEHSTAPEL;
        break;
      case 1:
        stapel = Stapel.toMiddle(Color.YELLOW);
        break;
      case 2:
        stapel = Stapel.toMiddle(Color.BLUE);
        break;
      case 3:
        stapel = Stapel.toMiddle(Color.WHITE);
        break;
      case 4:
        stapel = Stapel.toMiddle(Color.GREEN);
        break;
      case 5:
        stapel = Stapel.toMiddle(Color.RED);
        break;
      default: stapel = null;
    }
    return stapel;
  }

  @Override
  public String getName() {
    return null;
  }

  public Stapel getStapelFromMove(Move move,Card card){
  Color color = turnColorIntoEnum(card.getColor());
  if(move.isOnExp()){
    return Stapel.toExpedition(color);
  } else {
    return Stapel.toMiddle(color);
  }
  }

  public Stack<Card>[] createEnemyExpeditions(){
    Stack<Card> yellow = translateStack(information.getEnemyExpeditions(Color.YELLOW));
    Stack<Card> blue = translateStack(information.getEnemyExpeditions(Color.BLUE));
    Stack<Card> white = translateStack(information.getEnemyExpeditions(Color.WHITE));
    Stack<Card> green = translateStack(information.getEnemyExpeditions(Color.GREEN));
    Stack<Card> red = translateStack(information.getEnemyExpeditions(Color.RED));
    Stack<Card>[] enemyExp = new Stack[]{yellow,blue,white,green,red};
    return enemyExp;
  }

  public Stack<Card>[] createDiscardPile(){
  Stack<Card> yellow = translateStack(information.getAblagestapel(Color.YELLOW));
  Stack<Card> blue = translateStack(information.getAblagestapel(Color.BLUE));
  Stack<Card> white = translateStack(information.getAblagestapel(Color.WHITE));
  Stack<Card> green = translateStack(information.getAblagestapel(Color.GREEN));
  Stack<Card> red = translateStack(information.getAblagestapel(Color.RED));
  Stack<Card>[] discardPile = new Stack[]{yellow,blue,white,green,red};
  return discardPile;
  }

  private Stack<Card> translateStack(List<AbstractCard> paulStack){
  Stack<Card> jannStack = new Stack<Card>();
  for(AbstractCard abstractCard : paulStack){
    jannStack.add(translateCardToJann(abstractCard));
  }
  return  jannStack;
  }

  private Stack<Card>[] translateMap(Map<Color,Stack<AbstractCard>> mapPaul){
    Stack<Card>[] stackArray = new Stack[]{new Stack<Card>(), new Stack<Card>(), new Stack<Card>(), new Stack<Card>(), new Stack<Card>()};
    Stack<AbstractCard> gelb = mapPaul.get(Color.YELLOW);
    Stack<AbstractCard> blau = mapPaul.get(Color.BLUE);
    Stack<AbstractCard> weiss = mapPaul.get(Color.WHITE);
    Stack<AbstractCard> gruen = mapPaul.get(Color.GREEN);
    Stack<AbstractCard> rot = mapPaul.get(Color.RED);
    for(AbstractCard abstractCard : gelb){
      stackArray[0].add(translateCardToJann(abstractCard));
    }
    for(AbstractCard abstractCard : blau){
      stackArray[1].add(translateCardToJann(abstractCard));
    }
    for(AbstractCard abstractCard : weiss){
      stackArray[2].add(translateCardToJann(abstractCard));
    }
    for(AbstractCard abstractCard : gruen){
      stackArray[3].add(translateCardToJann(abstractCard));
    }
    for(AbstractCard abstractCard : rot){
      stackArray[4].add(translateCardToJann(abstractCard));
    }
    return stackArray;
  }

  private Card[] translateHand(List<AbstractCard> cards){
    Card[] hand = new Card[8];
    int i = 0;
    for(AbstractCard abstractCard : cards){
      hand[i++] = translateCardToJann(abstractCard);
    }
    return hand;
  }

  private Card translateCardToJann(AbstractCard abstractCard){
    return new Card(turnEnumIntoColor(abstractCard.getColor()),abstractCard.getValue());
  }

  private AbstractCard translateCardToPaul(Card card){
  Color color = turnColorIntoEnum(card.getColor());
  if(card.isCoinCard()){
    return new WettCard(color);
  } else{
    return new NumberCard(color,card.getValue());
  }
  }

  private Color turnColorIntoEnum(int color){
    if(color==0) return Color.YELLOW;
    if(color==1) return Color.BLUE;
    if(color==2) return Color.WHITE;
    if(color==3) return Color.GREEN;
    if(color==4) return Color.RED;
    return null;
  }

  private int turnEnumIntoColor(Color colorEnum){
    if(colorEnum==Color.YELLOW) return 0;
    if(colorEnum==Color.BLUE) return 1;
    if(colorEnum==Color.WHITE) return 2;
    if(colorEnum==Color.GREEN) return 3;
    if(colorEnum==Color.RED) return 4;
    return -1;
  }
}
