package domain.players;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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

  }

  @Override
  public PlayOption play(int remainingCards) {

    List<PlayOption> ls = this.getPlaySet();

    int randomIndex = (int) (Math.random() * ls.size());

    PlayOption result = ls.get(randomIndex);
    return result;
  }

  @Override
  public Stapel chooseStapel() {

    if (Math.random() > 0.5) {
      return Stapel.NACHZIEHSTAPEL;
    }

    List<Stapel> ls = this.getDrawSet();

    int randomIndex = (int) (Math.random() * ls.size());


    return ls.get(randomIndex);
  }

  @Override
  public boolean isAI() {
    return true;
  }

}
