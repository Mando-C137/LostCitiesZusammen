package domain.strategies;

import domain.PlayOption;
import domain.cards.Stapel;

public interface PlayStrategy {

  public PlayOption choosePlay(int remainingCards);

  public Stapel chooseStapel();

  public String getName();



}
