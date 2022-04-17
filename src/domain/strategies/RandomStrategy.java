package domain.strategies;


import java.util.List;
import domain.cards.Stapel;
import domain.main.AblagePlay;
import domain.players.AiPlayer;

public class RandomStrategy implements PlayStrategy {

  private AiPlayer player;

  public RandomStrategy(AiPlayer player) {
    this.player = player;
  }

  @Override
  public AblagePlay choosePlay(int remainingCards) {

    List<AblagePlay> ls = this.player.getPlaySet();

    return ls.get((int) (Math.random() * ls.size()));
  }

  @Override
  public Stapel chooseStapel() {

    if (Math.random() > 0.5) {
      return Stapel.NACHZIEHSTAPEL;
    }

    List<Stapel> ls = this.player.getDrawSet();


    int randomIndex = (int) (Math.random() * ls.size());


    return ls.get(randomIndex);
  }

  @Override
  public String getName() {

    return Strategies.RANDOM.toString();
  }

}
