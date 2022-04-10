package domain.players;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;
import domain.PlayOption;
import domain.cards.AbstractCard;
import domain.cards.Color;
import domain.cards.Stapel;

public class RandomPlayer extends AbstractPlayer {

  public RandomPlayer(LinkedList<AbstractCard> handKarten,
      HashMap<Color, Stack<AbstractCard>> ablageStaepels,
      HashMap<Color, Stack<AbstractCard>> ownExpeditions,
      HashMap<Color, Stack<AbstractCard>> enemyExpeditions) {
    super(handKarten, ablageStaepels, ownExpeditions, enemyExpeditions, "RAND");
    // TODO Auto-generated constructor stub
  }

  @Override
  public PlayOption play(int remainingCards) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Stapel chooseStapel() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isAI() {
    // TODO Auto-generated method stub
    return false;
  }

}
