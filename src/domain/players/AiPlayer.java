package domain.players;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import domain.cards.AbstractCard;
import domain.cards.Color;
import domain.cards.Stapel;
import domain.main.AblagePlay;
import domain.strategies.PlayStrategy;


public class AiPlayer extends AbstractPlayer {

  PlayStrategy strategy;

  List<AbstractCard> enemyModel;

  private int remaining;

  public AiPlayer(List<AbstractCard> handKarten, Map<Color, Stack<AbstractCard>> ablageStaepels,
      Map<Color, Stack<AbstractCard>> ownExpeditions,
      Map<Color, Stack<AbstractCard>> enemyExpeditions) {

    super(handKarten, ablageStaepels, ownExpeditions, enemyExpeditions);
    this.enemyModel = new ArrayList<AbstractCard>();
  }



  @Override
  public Stapel chooseStapel() {
    if (strategy == null) {
      throw new RuntimeException("Strategy darf nicht null sein");
    }
    return strategy.chooseStapel();
  }

  @Override
  public boolean isAI() {
    return true;
  }

  public void setStrategy(PlayStrategy newStrat) {
    this.strategy = newStrat;
  }

  public PlayStrategy getStrategy() {
    return this.strategy;
  }

  @Override
  public AblagePlay play(int remainingCards) {

    this.remaining = remainingCards;
    if (strategy == null) {
      throw new RuntimeException("Strategy darf nicht null sein");
    }
    return strategy.choosePlay(remainingCards);

  }

  public String getStrategyName() {

    return this.strategy.getName();
  }


  public void addCardToModel(AbstractCard drawedCard) {
    this.enemyModel.add(drawedCard);
  }


  public void removeCardFromModel(AbstractCard card) {
    this.enemyModel.remove(card);
  }


  public List<AbstractCard> getAblagestapel(Color col) {
    return this.ablagestapels.get(col);
  }


  public List<AbstractCard> getModel() {
    return this.enemyModel;
  }


  public Stack<AbstractCard> getEnemyExpeditions(Color col) {

    return this.enemyEx.get(col);
  }

  public void setModel(List<AbstractCard> list) {
    this.enemyModel = list;
  }



  public int getRemainingCards() {

    return remaining;
  }


}
