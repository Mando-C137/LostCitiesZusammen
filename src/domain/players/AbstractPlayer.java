package domain.players;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.stream.Collectors;
import domain.cards.AbstractCard;
import domain.cards.Color;
import domain.cards.Stapel;
import domain.main.AblagePlay;
import domain.main.WholePlay;

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
    this.expeditionen = ownExpeditions;
    this.enemyEx = enemyExpeditions;

    this.lastAblage = null;
  }


  /**
   * Auswahl des Spielzuges, also dem Ablegen einer Karte.
   * 
   * @return die Playoption, die der Spieler spielet.
   */
  public abstract AblagePlay play(int remainingCards);

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
   * gibt eine Liste aller möglichen Plays zurück Ein Play besteht dann aus dem Ablegen und dem
   * Ziehen
   * 
   * @return
   */
  public List<WholePlay> getAllActions(int remainingCards) {

    if (remainingCards >= 5) {
      return this.onlyGoodActions();
    }

    // new Stapel[] {Stapel.NACHZIEHSTAPEL}
    LinkedList<WholePlay> result = new LinkedList<WholePlay>();
    this.setLastPlay(null);
    for (AblagePlay p : this.getPlaySet()) {
      for (Stapel s : this.getDrawSet()) {
        if (p.getStapel() != s) {
          result.add(new WholePlay(p, s));
        }
      }
    }

    return result;
  }

  /**
   * Herausfinden aller möglichen Spielzüge die ein Spieler an einem gegebenen Zustand spielen kann.
   * kann man auch in dieser Klasse lassen. /
   * 
   * @return
   */
  public List<AblagePlay> getPlaySet() {

    List<AblagePlay> result = new LinkedList<AblagePlay>();

    this.handKarten.stream().forEach(card -> {

      /*
       * Man kann immer eine Karte in die Mitte legen
       */
      result.add(new AblagePlay(Stapel.toMiddle(card.getColor()), card));

      /*
       * Falls eine Expedition noch nicht geöffnet/gestartet wurde, kann man dort eine starten
       */
      if (this.getExpeditionen().get(card.getColor()).isEmpty()) {
        result.add(new AblagePlay(Stapel.toExpedition(card.getColor()), card));

        /*
         * Andernfalls, muss die Karte eine Wettcard sein oder man muss eine größere Nummer haben
         */
      } else {

        if (card.compareTo(this.expeditionen.get(card.getColor()).peek()) >= 0) {
          result.add(new AblagePlay(Stapel.toExpedition(card.getColor()), card));
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

  private List<WholePlay> onlyGoodActions() {


    List<WholePlay> result = new ArrayList<WholePlay>();

    List<AblagePlay> ablagen = new ArrayList<AblagePlay>();

    /*
     * gute Mittelzüge
     */
    this.handKarten.stream().filter(card -> {

      boolean keep = true;


      if (!this.expeditionen.get(card.getColor()).isEmpty()) {
        if (card.getValue() >= this.expeditionen.get(card.getColor()).peek().getValue()) {
          keep = false;
        }
      } else if (!this.enemyEx.get(card.getColor()).isEmpty()) {
        if (card.getValue() >= this.enemyEx.get(card.getColor()).peek().getValue()) {
          keep = false;
        }
      }
      return keep;

    }).forEach(card -> ablagen.add(new AblagePlay(Stapel.toMiddle(card.getColor()), card)));


    /*
     * jeweils nur den besten Expeditionenzug und joker expeditionzug
     */

    for (Entry<Color, Stack<AbstractCard>> entry : this.expeditionen.entrySet()) {
      List<AbstractCard> ls;
      if (entry.getValue().isEmpty()) {
        if (!(ls = this.handKarten.stream()
            .filter(
                card -> card.getColor() == entry.getKey() && card.isNumber() && card.getValue() < 9)
            .collect(Collectors.toList())).isEmpty()) {
          ablagen.add(new AblagePlay(Stapel.toExpedition(entry.getKey()), Collections.min(ls)));
        }
        if (!(ls = this.handKarten.stream()
            .filter(card -> card.getColor() == entry.getKey() && !card.isNumber())
            .collect(Collectors.toList())).isEmpty()) {
          ablagen.add(new AblagePlay(Stapel.toExpedition(entry.getKey()), ls.get(0)));
        }
      } else if (!entry.getValue().isEmpty()) {
        if (!(ls = this.handKarten.stream()
            .filter(card -> card.getColor() == entry.getKey()
                && entry.getValue().peek().getValue() <= card.getValue())
            .collect(Collectors.toList())).isEmpty()) {
          ablagen.add(new AblagePlay(Stapel.toExpedition(entry.getKey()), Collections.min(ls)));

        }
      }
    }


    /*
     * rausfinden welche Karten ich von den Stapel ziehen soll
     */
    List<Stapel> ziehpossibilites = new ArrayList<Stapel>();
    ziehpossibilites.add(Stapel.NACHZIEHSTAPEL);
    for (Color col : Color.values()) {

      if (!this.ablagestapels.get(col).isEmpty()) {
        AbstractCard val = this.ablagestapels.get(col).peek();
        if (!this.expeditionen.get(col).isEmpty()) {
          if (val.getValue() >= this.expeditionen.get(col).peek().getValue()) {
            ziehpossibilites.add(Stapel.toMiddle(col));
          }
        } else if (this.expeditionen.get(col).isEmpty()) {
          ziehpossibilites.add(Stapel.toMiddle(col));
        }

      }

    }

    for (Stapel zieh : ziehpossibilites) {
      for (AblagePlay opt : ablagen) {
        if (zieh != opt.getStapel()) {
          result.add(new WholePlay(opt, zieh));
        }
      }
    }

    if (result.isEmpty()) {

      result.add(new WholePlay(
          new AblagePlay(Stapel.toMiddle(handKarten.get(0).getColor()), handKarten.get(0)),
          Stapel.NACHZIEHSTAPEL));
      System.out.println("kein zug möglich gewesen");
    }


    return result;

  }



}
