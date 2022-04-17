package domain.players.jann.player;

import domain.players.jann.game.Card;
import domain.players.jann.game.Move;
import domain.players.jann.game.Session;
import domain.players.jann.montecarlo.InformationIS;
import domain.players.jann.montecarlo.MonteCarloIS;
import java.util.Stack;

public class ISPlayer extends Player{

  private int time;
  private boolean heavyPlayout;
  double explorationConstant;
  int rewardStrategy;
  private Stack<Card> lastDiscardPile = null;

  public ISPlayer(boolean heavyPlayout,int time,double explorationConstant,int rewardStrategy){
    super();
    this.time = time;
    this.heavyPlayout = heavyPlayout;
    this.explorationConstant = explorationConstant;
    this.rewardStrategy = rewardStrategy;
  }

  @Override
  public Move makeMove(Card[] myHand,Stack<Card>[] myExp, Stack<Card>[] oppExp, Stack<Card>[] discardPile,boolean turn) {
    InformationIS information = new InformationIS(myHand,myExp,oppExp,discardPile,turn,this.imP1);
    Move m = MonteCarloIS.ismcts(information,time,heavyPlayout,explorationConstant,rewardStrategy);
    return m;
  }

  public void detectOpponentsCards(){

  }

  @Override
  public Move makeMove(Session session) {
    return null;
  }
}
