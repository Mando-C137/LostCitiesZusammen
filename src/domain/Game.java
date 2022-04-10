/**
 * 
 */
package domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Stack;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import domain.cards.AbstractCard;
import domain.cards.Color;
import domain.cards.NoCard;
import domain.cards.NumberCard;
import domain.cards.Stapel;
import domain.cards.WettCard;
import domain.exceptions.GameException;
import domain.players.AbstractPlayer;
import domain.players.AiPlayer;
import domain.strategies.RandomStrategy;

/**
 * Die Gameinstanz, von der man das Spiel steuert und Referenzen auf alle nÃ¶tigen Objekte hat.
 *
 */
public class Game {

  /**
   * Der Nachziehstapel
   */
  private Stack<AbstractCard> nachZiehStapel;

  /**
   * Eine Map, die jede Farbe einem Stack aus Karten zuordnet.
   */
  private Map<Color, Stack<AbstractCard>> ablageStaepels;

  /**
   * Die 2 Spieler, die das Spiel spielen.
   */
  private List<AbstractPlayer> players;

  /**
   * gameEnd zeigt, ob das Spiel beendet ist.
   */
  private boolean gameEnd;

  /**
   * zeigt, an welcher Index aus der playersList als nächstes dran ist.
   */
  private int turn = 0;


  /**
   * Konstruktor: Initialisierung aller nÃ¶tigen Objekte, Generieren des Nachziehstapels und
   * Austeilen der Karten an die Spieler.
   */
  public Game() {
    this.gameEnd = false;
    initStaepel();
    generateNachziehStapel();
    initPlayers();
  }

  private void initPlayers() {

    /*
     * public FabianPlayer(LinkedList<AbstractCard> handKarten, HashMap<Color, Stack<AbstractCard>>
     * ablageStaepels, HashMap<Color, Stack<AbstractCard>> ownExpeditions, HashMap<Color,
     * Stack<AbstractCard>> enemyExpeditions)
     */

    Map<Color, Stack<AbstractCard>> fabiansExpeditions = this.generateExpeditions();
    Map<Color, Stack<AbstractCard>> randomsExpeditions = this.generateExpeditions();

    this.players = new ArrayList<AbstractPlayer>();

    AiPlayer abs = new AiPlayer(new LinkedList<AbstractCard>(), this.ablageStaepels,
        fabiansExpeditions, Collections.unmodifiableMap(randomsExpeditions));
    abs.setStrategy(new RandomStrategy(abs));

    AiPlayer other = new AiPlayer(new LinkedList<AbstractCard>(), this.ablageStaepels,
        randomsExpeditions, Collections.unmodifiableMap(fabiansExpeditions));
    other.setStrategy(new RandomStrategy(other));

    this.players.add(abs);
    this.players.add(other);
    abs.setIndex(0);
    other.setIndex(1);


    for (AbstractPlayer player : players) {
      IntStream.range(0, 8).forEach(num -> this.addCardtoPlayer(Stapel.NACHZIEHSTAPEL, player));
    }

    this.turn = 0;
  }


  private Map<Color, Stack<AbstractCard>> generateExpeditions() {

    Map<Color, Stack<AbstractCard>> result = new HashMap<Color, Stack<AbstractCard>>();

    for (Color color : Color.values()) {
      result.put(color, new Stack<AbstractCard>());
    }

    return result;


  }



  /**
   * erstellt den Stapel mit den offiziellen Karten
   */
  private void generateNachziehStapel() {

    nachZiehStapel = new Stack<AbstractCard>();

    // alle karten generieren in einer Liste
    ArrayList<AbstractCard> allCards = new ArrayList<AbstractCard>();
    for (Color c : Color.values()) {
      IntStream.range(2, 11).forEach(num -> allCards.add(new NumberCard(c, num)));
      IntStream.range(1, 4).forEach(num -> allCards.add(new WettCard(c)));

    }

    // die Karten zufaelllig auf den Stapel geben
    Random rand = new Random();
    while (!allCards.isEmpty()) {
      AbstractCard remove = allCards.remove(rand.nextInt(allCards.size()));
      this.nachZiehStapel.add(remove);

    }

  }

  /**
   * initialisiert die Staepel, auf denen die Karten abgelegt werden kÃ¶nnen.
   */
  private void initStaepel() {
    this.ablageStaepels = new HashMap<Color, Stack<AbstractCard>>();
    Stream.of(Color.values()).forEach(c -> this.ablageStaepels.put(c, new Stack<AbstractCard>()));

  }


  public static void main(String[] args) {
    Game game = new Game();
    game.gameFlow();
  }

  @Override
  public String toString() {


    StringBuffer res = new StringBuffer();

    res.append("-----------------------------------------\n");

    res.append("\t\t Y\tW\tB\tG\tR\n");

    for (AbstractPlayer abs : this.players) {
      res.append(abs.getName() + "\t");
      res.append(abs.expeditionenString() + "\n");
    }

    res.append("Middle\t\t");
    for (Color col : Color.values()) {
      res.append(this.peekAblageStapel(col).orElseGet(() -> new NoCard(col)));
      res.append("\t");
    }

    res.append("\n-----------------------------------------");


    return res.toString();
  }

  /**
   * liefert das oberste Element des Ablagestapels mit der spezifizierten Farbe c, falls noch nichts
   * auf dem Stapel ist das Optional leer.
   * 
   * @param c
   * @return
   */
  public Optional<AbstractCard> peekAblageStapel(Color c) {
    AbstractCard card = null;
    if (this.ablageStaepels != null && this.ablageStaepels.get(c) != null)
      if (!this.ablageStaepels.get(c).isEmpty()) {
        card = this.ablageStaepels.get(c).peek();
      }
    return Optional.ofNullable(card);
  }


  public Optional<AbstractCard> returnCard(Stapel stapel) {
    AbstractCard returnAnswer = null;
    Color c = stapel.getColor();

    if (this.ablageStaepels != null && this.ablageStaepels.get(c) != null)
      if (!this.ablageStaepels.get(stapel.getColor()).isEmpty()) {
        returnAnswer = this.ablageStaepels.get(c).pop();
      }

    return Optional.ofNullable(returnAnswer);

  }


  /**
   * entfernt die oberste Karte von dem NachZiehstapel und gibt sie zurÃ¼ck
   *
   * @return die oberste Karte von dem NachziehStapel
   */
  public Optional<AbstractCard> returnCardFromNachziehStapel() {

    AbstractCard returnAnswer = null;

    if (this.nachZiehStapel.isEmpty()) {

      System.out.println("NachziehStapel already empty");

    } else {
      returnAnswer = this.nachZiehStapel.pop();
    }

    return Optional.ofNullable(returnAnswer);



  }

  private void gameFlow() {

    int i = 0;
    int index = 0;
    for (; !this.getGameEnd(); i++) {

      // Index wechselt immer : 0 ^ 1 = 1 und 1 ^ 1= 0
      index = index ^ 1;

      AbstractPlayer top = players.get(index);
      System.out.println((top.getHandKarten()));
      PlayOption nextPlay = top.play(this.nachZiehStapel.size());
      this.makePlay(nextPlay, top);


      this.addCardtoPlayer(top.chooseStapel(), top);

      System.out.println(this);

    }

    System.out.println("Anzahl Plays = " + i);
    System.out.println(calculateScores());


  }



  public Map<Color, Stack<AbstractCard>> getPlayersExpeditions(AbstractPlayer player) {
    return Collections.unmodifiableMap(player.getExpeditionen());
  }

  public List<AbstractCard> getAblageStapel(Color c) {
    return Collections.unmodifiableList(this.ablageStaepels.get(c));
  }

  public void externalRound(AbstractPlayer p) {
    this.makePlay(p.play(this.nachZiehStapel.size()), p);
    this.addCardtoPlayer(p.chooseStapel(), p);

  }

  public void externalPlay(PlayOption opt, AbstractPlayer abs) {
    this.makePlay(opt, abs);
  }

  public void externalDraw(Stapel s, AbstractPlayer abs) {
    this.addCardtoPlayer(s, abs);
  }


  private void makePlay(PlayOption play, AbstractPlayer player) {

    if (!play.getCard().getColor().equals(play.getStapel().getColor())) {
      throw new GameException.IllegalPlayException(play.getCard(), play.getStapel());
    }

    AbstractCard cardToPlay = play.getCard();

    if (!player.getHandKarten().contains(cardToPlay)) {
      throw new GameException.DoNotOwnException(player, cardToPlay);
    }

    if (Arrays.asList(Stapel.orderedExpeditions).contains(play.getStapel())) {
      if (checkExpeditionPlay(play, player)) {
        player.getHandKarten().remove(cardToPlay);
        player.getExpeditionen().get(cardToPlay.getColor()).push(cardToPlay);
      } else {
        throw new GameException.IllegalPlayException(cardToPlay, play.getStapel());
      }
    } else {
      player.getHandKarten().remove(cardToPlay);
      this.ablageStaepels.get(cardToPlay.getColor()).push(cardToPlay);
    }



    player.setLastPlay(play.getStapel());
  }

  private boolean checkExpeditionPlay(PlayOption play, AbstractPlayer player) {

    /*
     * Falls die Expedition leer ist, darf man spielen
     */
    if (player.getExpeditionen().get(play.getCard().getColor()).isEmpty()) {
      return true;
      /*
       * Auf eine Wettkarte darf man auch immer spielen
       */
    } else if (!player.getExpeditionen().get(play.getCard().getColor()).peek().isNumber()) {
      return true;
    }

    else {
      /*
       * Eine Wettkarte darf nicht auf eine Nummerkarte gespielt werden
       */
      if (!play.getCard().isNumber()) {
        return false;

        /*
         * Eine Nummerkarte muss eine grÃ¶ÃŸere Zahl haben als die aktuelle Zahl.
         */
      } else {
        NumberCard newCard = (NumberCard) play.getCard();
        NumberCard peek =
            (NumberCard) player.getExpeditionen().get(play.getCard().getColor()).peek();
        return newCard.getValue() > peek.getValue();
      }


    }


  }

  private void addCardtoPlayer(Stapel stapel, AbstractPlayer abstractPlayer) {

    Optional<AbstractCard> abs;
    if (stapel.isMiddle()) {

      if (abstractPlayer.getLastAblage() != null) {
        if (abstractPlayer.getLastAblage().equals(stapel)) {
          throw new GameException.SameCardException();
        }
      }

      abs = this.returnCard(stapel);

    } else {
      abs = this.returnCardFromNachziehStapel();
    }

    if (abs.isPresent()) {

      AbstractCard card = abs.get();
      abstractPlayer.getHandKarten().add(card);


    } else {
      throw new GameException("karte konnte nicht hinzugefuegt werden");
    }

    if (nachZiehStapel.isEmpty()) {
      this.gameEnd = true;
      calculateScores();
    }

    this.turn = 1 ^ this.turn;
    abstractPlayer.setLastPlay(null);

  }


  public Stack<AbstractCard> stapelToStack(Stapel st) {

    switch (st) {
      case BLUEMIDDLE:
        return this.ablageStaepels.get(Color.BLUE);
      case GREENMIDDLE:
        return this.ablageStaepels.get(Color.GREEN);
      case REDMIDDLE:
        return this.ablageStaepels.get(Color.RED);
      case WHITEMIDDLE:
        return this.ablageStaepels.get(Color.WHITE);
      case YELLOWMIDDLE:
        return this.ablageStaepels.get(Color.YELLOW);
      case NACHZIEHSTAPEL:
        return this.nachZiehStapel;
      default:
        System.out.println("kann nicht von einer Expedition ziehen");
        return null;
    }



  }

  public List<AbstractPlayer> getPlayers() {
    return this.players;
  }

  public Optional<AbstractCard> peekNachziehStapel() {

    if (this.nachZiehStapel.isEmpty()) {
      return Optional.ofNullable(null);
    }


    return Optional.ofNullable(this.nachZiehStapel.peek());
  }

  public boolean getGameEnd() {
    return this.gameEnd;
  }

  public String calculateScores() {

    StringBuffer sb = new StringBuffer();
    int wholeSum = 0;
    int fact = 1;
    int singleSum = 0;
    for (AbstractPlayer p : this.players) {
      wholeSum = 0;
      A: for (Stack<AbstractCard> st : p.getExpeditionen().values()) {

        fact = 1;
        singleSum = -20;
        if (st.size() == 0) {
          continue A;
        }

        for (AbstractCard c : st) {
          if (!c.isNumber()) {
            fact++;
          } else {
            singleSum += ((NumberCard) c).getValue();
          }
        }

        singleSum *= fact;
        if (st.size() >= 8) {
          singleSum += 20;
        }
        wholeSum += singleSum;

      }

      sb.append(" " + p.getName() + "'s Punkte: " + wholeSum + "\n");

    }

    return sb.toString();


  }

  public int getRemainingCards() {
    return this.nachZiehStapel.size();
  }

  public AbstractPlayer getPlayerWithTurn() {
    return this.players.get(this.turn);
  }
}
