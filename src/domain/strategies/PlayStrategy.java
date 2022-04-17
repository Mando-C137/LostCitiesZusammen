package domain.strategies;


import domain.cards.Stapel;
import domain.main.AblagePlay;

public interface PlayStrategy {

  public AblagePlay choosePlay(int remainingCards);

  public Stapel chooseStapel();

  public String getName();



}
