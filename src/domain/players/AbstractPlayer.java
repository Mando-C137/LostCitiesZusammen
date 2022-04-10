package domain.players;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import domain.PlayOption;
import domain.cards.AbstractCard;
import domain.cards.Color;
import domain.cards.Stapel;

/**
 * Ein Spieler spielt das Spiel (game). Er zieht Karten von dem Nachziehstapel und den
 * Ablagestaeplen und legt Karten an seinen Expeditionen und den Ablagestaepeln ab.
 * 
 */
public abstract class AbstractPlayer {


  protected Map<Color, Stack<AbstractCard>> expeditionen;

  protected List<AbstractCard> handKarten;

  protected Stapel lastAblage;

  protected Map<Color, Stack<AbstractCard>> ablagestapels;

  protected Map<Color, Stack<AbstractCard>> enemyEx;


  /**
   * zeigt an, welcher Index man in dem Spiel ist
   */
  private int index;

  public AbstractPlayer(List<AbstractCard> handKarten,
      Map<Color, Stack<AbstractCard>> ablageStaepels,
      Map<Color, Stack<AbstractCard>> ownExpeditions,
      Map<Color, Stack<AbstractCard>> enemyExpeditions) {
    this.handKarten = handKarten;
    this.ablagestapels = ablageStaepels;
    this.expeditionen = enemyExpeditions;
    this.enemyEx = enemyExpeditions;

    this.lastAblage = null;
  }


  /**
   * Auswahl des Spielzuges, also dem Ablegen einer Karte.
   * 
   * @return die Playoption, die der Spieler spielet.
   */
  public abstract PlayOption play(int remainingCards);

  public abstract Stapel chooseStapel();

  public abstract boolean isAI();

  public LinkedList<Stapel> getDrawSet() {

    LinkedList<Stapel> result = new LinkedList<Stapel>();

    for (Color c : Color.orderedColors) {
      if (!this.ablagestapels.get(c).isEmpty()) {
        result.add(Stapel.toMiddle(c));
      }

    }

    if (this.lastAblage != null) {
      result.remove(this.lastAblage);
    }


    result.add(Stapel.NACHZIEHSTAPEL);
    this.lastAblage = null;
    return result;
  }

  /**
   * Getter fuer die expeditionen des Spielers. Sollte eigentlich unmodifiable sein.
   * 
   * @return
   */
  public Map<Color, Stack<AbstractCard>> getExpeditionen() {
    return this.expeditionen;
  }

  /**
   * Getter fÃ¼r die Karten des Spielers, die er auf der Hand hat.
   * 
   * @return
   */
  public List<AbstractCard> getHandKarten() {
    return this.handKarten;
  }

  /**
   * Herausfinden aller möglichen Spielzüge die ein Spieler an einem gegebenen Zustand spielen kann.
   * kann man auch in dieser Klasse lassen. /
   * 
   * @return
   */
  public List<PlayOption> getPlaySet() {

    List<PlayOption> result = new LinkedList<PlayOption>();

    this.handKarten.stream().forEach(card -> {

      /*
       * Man kann immer eine Karte in die Mitte legen
       */
      result.add(new PlayOption(Stapel.toMiddle(card.getColor()), card));

      /*
       * Falls eine Expedition noch nicht geöffnet/gestartet wurde, kann man dort eine starten
       */
      if (this.getExpeditionen().get(card.getColor()).isEmpty()) {
        result.add(new PlayOption(Stapel.toExpedition(card.getColor()), card));

        /*
         * Andernfalls, muss die Karte eine Wettcard sein oder man muss eine größere Nummer haben
         */
      } else {

        if (card.compareTo(this.expeditionen.get(card.getColor()).peek()) >= 0) {
          result.add(new PlayOption(Stapel.toExpedition(card.getColor()), card));
        }

      }

    });

    // System.out.println("Anzahl meiner Karten: " + this.getHandKarten().size());
    // System.out.println("Anzahl möglicher Plays: " + result.size());
    return result;
  }



  /**
   * String representation der Expeditionen des Spielers. Eine Zeile, in der in festgelegter
   * Reihenfolge der Farben immer die oberste Karte steht.
   * 
   * @return
   */
  public String expeditionenString() {

    StringBuffer res = new StringBuffer();

    for (Color c : Color.orderedColors) {
      Stack<AbstractCard> currentStack = this.expeditionen.get(c);
      if (!currentStack.isEmpty()) {
        res.append(currentStack.peek().toString());
      }
      res.append("\t");
    }

    return res.toString();
  }

  public void setLastPlay(Stapel abs) {

    this.lastAblage = abs;
  }

  public String getName() {
    if (this.isAI()) {
      return ((AiPlayer) this).getStrategyName();
    } else {
      return "sollte nicht sein";
    }
  }

  public void setIndex(int i) {
    this.index = i;
  }

  public int getIndex() {
    return this.index;
  }

  public Stapel getLastAblage() {
    return this.lastAblage;
  }



}
