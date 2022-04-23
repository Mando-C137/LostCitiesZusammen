package domain.strategies;


import domain.cards.AbstractCard;
import domain.cards.Stapel;
import domain.main.AblagePlay;
import domain.players.AiPlayer;

public class RandomStrategy implements PlayStrategy {

  private AiPlayer ai;

  private Stapel lastPlay;


  public RandomStrategy(AiPlayer ai) {
    this.ai = ai;
  }

  @Override
  public AblagePlay choosePlay(int remainingCards) {

    AblagePlay result = null;

    int randomIndex = (int) (Math.random() * this.ai.getHandKarten().size());

    AbstractCard card = this.ai.getHandKarten().get(randomIndex);

    if (this.ai.getExpeditionen().get(card.getColor()).isEmpty()) {

      result = new AblagePlay(Stapel.toExpedition(card.getColor()), card);

    } else if (card.compareTo(this.ai.getExpeditionen().get(card.getColor()).peek()) >= 0) {

      result = new AblagePlay(Stapel.toExpedition(card.getColor()), card);
    } else {
      result = new AblagePlay(Stapel.toMiddle(card.getColor()), card);
    }

    lastPlay = result.getStapel();
    return result;
  }

  @Override
  public Stapel chooseStapel() {
    if (Math.random() > 0.5) {
      return Stapel.NACHZIEHSTAPEL;
    }

    int randomIndex = (int) (Math.random() * Stapel.alleZiehStapel.length);

    Stapel answer = Stapel.alleZiehStapel[randomIndex];

    /*
     * Stapel ist nachziehstapel
     */
    if (answer.getColor() == null) {
      return Stapel.NACHZIEHSTAPEL;
    }
    /*
     * Stapel ist leer oder war play
     */

    else if (this.ai.getAblagestapel(answer.getColor()).isEmpty() || this.lastPlay == answer) {
      return Stapel.NACHZIEHSTAPEL;
    }
    /*
     * stapel ist ok
     */
    else {
      return answer;
    }



  }



  @Override
  public String getName() {

    return Strategies.RANDOM.toString();
  }

}
