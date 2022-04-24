package domain.players.jann.player;

import domain.players.jann.game.Card;
import domain.players.jann.game.Move;
import domain.players.jann.game.Session;
import domain.players.jann.montecarlo.InformationIS;
import domain.players.jann.montecarlo.MonteCarloIS;
import java.util.ArrayList;
import java.util.Stack;

public class ISPlayer extends MemoryPlayer{

  private int time; //time given to construct mcts tree
  private int playOutStyle; //true -> use RuleBased for simulation; 0=random;1=rule;2=human
  private boolean reduceBranching; //true -> dont expand all moves
  double explorationConstant;
  int rewardStrategy; //0 =
  private ArrayList<Card> cardsOfOpp = new ArrayList<>();
  private Stack<Card>[] oldDiscardPile = new Stack[]{new Stack<Card>(), new Stack<Card>(), new Stack<Card>(), new Stack<Card>(), new Stack<Card>()};

  public ISPlayer(int playOutStyle,int time,double explorationConstant,int rewardStrategy,boolean reduceBranching){
    super();
    this.time = time;
    this.playOutStyle = playOutStyle;
    this.explorationConstant = explorationConstant;
    this.rewardStrategy = rewardStrategy;
    this.hasMemory = true;
    this.reduceBranching = reduceBranching;
  }

  @Override
  public Move makeMove(Card[] myHand,Stack<Card>[] myExp, Stack<Card>[] oppExp, Stack<Card>[] discardPile,boolean turn,int turnCounter) {
    detectOpponentsCards(discardPile,oppExp);
    InformationIS information = new InformationIS(myHand,myExp,oppExp,discardPile,turn,this.imP1,turnCounter,cardsOfOpp);
    Move m = MonteCarloIS
        .ismcts(information,time,playOutStyle,explorationConstant,rewardStrategy,reduceBranching);
    memorizeDiscardPile(discardPile,m,myHand);
    return m;
  }

  public void detectOpponentsCards(Stack<Card>[] discardPile,Stack<Card>[] oppExp){
    boolean drawFromDiscardDetected = false;
    int colorOfDraw = -1;
    for(int i = 0;i<5;i++){
      drawFromDiscardDetected = discardPile[i].size()<oldDiscardPile[i].size();
      if(drawFromDiscardDetected) {
        colorOfDraw = i;
        i=10;
      }
    }
    if(drawFromDiscardDetected){
      cardsOfOpp.add(oldDiscardPile[colorOfDraw].peek().clone());
    }
    didOpponentRemove(discardPile,oppExp); //Update known cards
  }

  public void didOpponentRemove(Stack<Card>[] discardPile,Stack<Card>[] oppExp){
    int remove = -1;
    int counter = 0;
    for(Card c : cardsOfOpp){
      int color = c.getColor();
      if(!discardPile[color].isEmpty() && c.equals(discardPile[color].peek())) remove = counter;
      if(!oppExp[color].isEmpty() && c.equals(oppExp[color].peek())) remove = counter;
      counter++;
    }
    if(remove>=0){
      cardsOfOpp.remove(remove);
    }
  }

  public void memorizeDiscardPile(Stack<Card>[] discardPile,Move move,Card[] hand){
    oldDiscardPile = new Stack[]{new Stack<Card>(), new Stack<Card>(), new Stack<Card>(), new Stack<Card>(), new Stack<Card>()};
    for(int i = 0;i<5;i++){
      for(Card c : discardPile[i]) {
        oldDiscardPile[i].add(c.clone());
      }
    }
    if(!move.isOnExp()){
      Card cloneCard = hand[move.getCardIndex()].clone();
      oldDiscardPile[cloneCard.getColor()].add(cloneCard);
    }
    if(move.getDrawFrom()!=0){
      oldDiscardPile[move.getDrawFrom()-1].pop();
    }
  }

  public void resetMemory(){
    this.oldDiscardPile = new Stack[]{new Stack<Card>(), new Stack<Card>(), new Stack<Card>(), new Stack<Card>(), new Stack<Card>()};
    this.cardsOfOpp = new ArrayList<>();
  }
  @Override
  public Move makeMove(Session session) {
    return null;
  }
}
