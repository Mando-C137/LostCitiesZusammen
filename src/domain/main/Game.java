/**
 * 
 */
package domain.main;

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
import domain.players.paul.ismcts.InformationSetStrategy;
import domain.strategies.RandomStrategy;
import domain.strategies.SimpleStrategy;

/**
 * Die Gameinstanz, von der man das Spiel steuert und Referenzen auf alle nÃ¶tigen Objekte hat.
 *
 */
public class Game {


  /**
   * zeigt an, wie viele Züge schon gemacht worden sind
   */
  private int zuege = 0;


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
  private List<AiPlayer> players;

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
  private Game() {
    this.gameEnd = false;
    initStaepel();
    generateNachziehStapel();
    initPlayers();
  }

  /**
   * Kopierkonstruktor
   * 
   * @param g
   */
  public Game(Game g) {
    this.zuege = g.zuege;
    this.gameEnd = g.gameEnd;
    this.turn = g.turn;
    this.ablageStaepels = new HashMap<Color, Stack<AbstractCard>>();
    for (Color c : Color.values()) {
      this.ablageStaepels.put(c, new Stack<AbstractCard>());
      this.ablageStaepels.get(c).addAll(g.ablageStaepels.get(c));

    }

    Map<Color, Stack<AbstractCard>> firstMap = new HashMap<Color, Stack<AbstractCard>>();

    Map<Color, Stack<AbstractCard>> secondMap = new HashMap<Color, Stack<AbstractCard>>();

    for (Color col : Color.values()) {
      firstMap.put(col, new Stack<AbstractCard>());
      secondMap.put(col, new Stack<AbstractCard>());

      firstMap.get(col).addAll(g.getPlayers().get(0).getExpeditionen().get(col));
      secondMap.get(col).addAll(g.getPlayers().get(1).getExpeditionen().get(col));

    }



    this.players = new ArrayList<AiPlayer>();
    this.players.add(new AiPlayer(new LinkedList<AbstractCard>(g.players.get(0).getHandKarten()),
        this.ablageStaepels, firstMap, Collections.unmodifiableMap(secondMap)));
    this.players.add(new AiPlayer(new LinkedList<AbstractCard>(g.players.get(1).getHandKarten()),
        this.ablageStaepels, secondMap, Collections.unmodifiableMap(firstMap)));

    // this.players.forEach(con -> con.setGame(this));

    this.players.get(0).setModel(new LinkedList<AbstractCard>(g.players.get(0).getModel()));
    this.players.get(1).setModel(new LinkedList<AbstractCard>(g.players.get(1).getModel()));


    this.nachZiehStapel = new Stack<AbstractCard>();
    this.nachZiehStapel.addAll(g.nachZiehStapel);

  }

  private void initPlayers() {

    Map<Color, Stack<AbstractCard>> fabiansExpeditions = this.generateExpeditions();
    Map<Color, Stack<AbstractCard>> randomsExpeditions = this.generateExpeditions();

    this.players = new ArrayList<AiPlayer>();

    AiPlayer abs = new AiPlayer(new LinkedList<AbstractCard>(), this.ablageStaepels,
        fabiansExpeditions, Collections.unmodifiableMap(randomsExpeditions));


    AiPlayer other = new AiPlayer(new LinkedList<AbstractCard>(), this.ablageStaepels,
        randomsExpeditions, Collections.unmodifiableMap(fabiansExpeditions));


    this.players.add(abs);
    this.players.add(other);
    abs.setIndex(0);
    other.setIndex(1);


    for (AbstractPlayer player : players) {
      IntStream.range(0, 8).forEach(num -> this.addCardtoPlayer(Stapel.NACHZIEHSTAPEL, player));
    }

    this.turn = 0;
    this.zuege = 0;
  }

  public static Game twoWithoutStrategies() {
    Game g = new Game();

    g.players.forEach(con -> con.setStrategy(new RandomStrategy(con)));


    return g;
  }


  /**
   * generiert die ExpeditionsMap, das heißt leere Expeditionen
   * 
   * @return
   */
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

    // die Karten zufaellig auf den Stapel geben
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


  private Optional<AbstractCard> returnCard(Stapel stapel) {
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
  private Optional<AbstractCard> returnCardFromNachziehStapel() {

    AbstractCard returnAnswer = null;

    if (this.nachZiehStapel.isEmpty()) {

      System.out.println("NachziehStapel already empty");

    } else {
      returnAnswer = this.nachZiehStapel.pop();
    }

    return Optional.ofNullable(returnAnswer);



  }

  public void gameFlow() {

    int i = 0;
    int index = 0;
    for (; !this.getGameEnd() && this.zuege < 100; i++) {

      // Index wechselt immer : 0 ^ 1 = 1 und 1 ^ 1= 0


      AbstractPlayer top = players.get(index);
      // System.out.println((top.getHandKarten()));

      AblagePlay nextPlay = top.play(this.nachZiehStapel.size());
      // System.out.println(nextPlay);
      this.makePlay(nextPlay, top);

      Stapel chooseStapel = top.chooseStapel();
      // System.out.println(chooseStapel);
      this.addCardtoPlayer(chooseStapel, top);

      // System.out.println(this);
      index = index ^ 1;

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

  public void externalPlay(AblagePlay opt, AbstractPlayer abs) {
    this.makePlay(opt, abs);
  }

  public void externalDraw(Stapel s, AbstractPlayer abs) {
    this.addCardtoPlayer(s, abs);
  }


  private void makePlay(AblagePlay play, AbstractPlayer player) {

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


    this.players.get(player.getIndex() ^ 1).removeCardFromModel(cardToPlay);
    player.setLastPlay(play.getStapel());
  }

  private boolean checkExpeditionPlay(AblagePlay play, AbstractPlayer player) {

    if (player.getExpeditionen().get(play.getCard().getColor()).isEmpty()) {
      return true;
    } else {

      AbstractCard peek = player.getExpeditionen().get(play.getCard().getColor()).peek();

      return play.getCard().compareTo(peek) >= 0;

    }


  }

  private void addCardtoPlayer(Stapel stapel, AbstractPlayer abstractPlayer) {

    Optional<AbstractCard> abs;
    if (stapel.isMiddle()) {

      if (abstractPlayer.getLastAblage() != null) {
        if (abstractPlayer.getLastAblage().equals(stapel)) {
          System.out.println(stapel);
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

      if (stapel.isMiddle()) {
        this.players.get(abstractPlayer.getIndex() ^ 1).addCardToModel(card);
      }


    } else {
      throw new GameException("karte konnte nicht hinzugefuegt werden");
    }

    if (nachZiehStapel.isEmpty()) {
      this.gameEnd = true;
      calculateScores();
    }

    this.turn = 1 ^ this.turn;
    this.zuege++;



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

  public List<AiPlayer> getPlayers() {
    return this.players;
  }

  public Optional<AbstractCard> peekNachziehStapel() {

    if (this.nachZiehStapel.isEmpty()) {
      return Optional.ofNullable(null);
    }


    return Optional.ofNullable(this.nachZiehStapel.peek());
  }

  public boolean getGameEnd() {
    return this.nachZiehStapel.size() == 0;
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

  public AiPlayer getPlayerWithTurn() {
    return this.players.get(this.turn);
  }

  public int getTurn() {

    return this.turn;
  }

  public void unCheckedPlay(WholePlay play, AiPlayer player) {
    AblagePlay p = play.getOption();

    // Ablegen
    if (p.getStapel().isExpedition()) {
      player.getExpeditionen().get(p.getCard().getColor()).add(p.getCard());
    } else {
      this.ablageStaepels.get(p.getCard().getColor()).add(p.getCard());
    }

    if (!player.getHandKarten().remove(p.getCard())) {
      System.out.println(player.getHandKarten());
      System.out.println(play);
      throw new GameException.DoNotOwnException(player, p.getCard());
    }


    Stapel ziehStapel = play.getStapel();
    AbstractCard drawedCard = null;

    if (ziehStapel.equals(Stapel.NACHZIEHSTAPEL)) {

      player.getHandKarten().add(this.nachZiehStapel.pop());

      this.gameEnd = this.nachZiehStapel.isEmpty();
    } else {
      drawedCard = this.ablageStaepels.get(ziehStapel.getColor()).pop();

      player.getHandKarten().add(drawedCard);

      this.players.get(player.getIndex() ^ 1).addCardToModel(drawedCard);

    }


    this.turn = this.turn ^ 1;
    this.zuege++;
    player.setLastPlay(null);

    this.players.get(player.getIndex() ^ 1).removeCardFromModel(p.getCard());



  }


  public static Game determinizeGame(AiPlayer ai) {

    Game result = new Game();

    result.nachZiehStapel.clear();
    result.nachZiehStapel.addAll(allCards());

    result.players.forEach(con -> con.getHandKarten().clear());

    /*
     * ablagestapel kopieren
     */
    result.ablageStaepels.forEach((col, stack) -> {
      stack.addAll(ai.getAblagestapel(col));

      for (AbstractCard card : ai.getAblagestapel(col)) {
        result.nachZiehStapel.remove(card);
      }


    });


    /*
     * handkarten des eigenen spielers kopieren
     */
    result.players.get(ai.getIndex()).getHandKarten().addAll(ai.getHandKarten());


    for (AbstractCard card : ai.getHandKarten()) {
      result.nachZiehStapel.remove(card);
    }


    /*
     * eigene Expeditionen und die des Gegners kopieren.
     */
    for (Color col : Color.values()) {

      result.players.get(ai.getIndex()).getExpeditionen().get(col)
          .addAll(ai.getExpeditionen().get(col));

      result.players.get(ai.getIndex() ^ 1).getExpeditionen().get(col)
          .addAll(ai.getEnemyExpeditions(col));

      for (AbstractCard card : ai.getExpeditionen().get(col)) {
        result.nachZiehStapel.remove(card);
      }

      for (AbstractCard card : ai.getEnemyExpeditions(col)) {
        result.nachZiehStapel.remove(card);
      }



    }

    /*
     * Gegnermodell ausfüllen
     */
    // result.players.get(ai.getIndex() ^ 1).getHandKarten().addAll(ai.getModel());
    // // System.out.println();
    // for (AbstractCard abs : ai.getModel()) {
    //
    // result.nachZiehStapel.remove(abs);
    // }
    //
    // System.out.println(ai.getModel());
    // System.out.println();

    List<AbstractCard> enemyhand = result.players.get(ai.getIndex() ^ 1).getHandKarten();

    Random rng = new Random();
    while (enemyhand.size() < 8) {

      int index = rng.nextInt(result.nachZiehStapel.size());
      AbstractCard add = result.nachZiehStapel.remove(index);
      enemyhand.add(add);

    }

    result.turn = ai.getIndex();
    result.zuege = result.nachZiehStapel.size();

    return result;
  }


  public static List<AbstractCard> allCards() {
    ArrayList<AbstractCard> allCards = new ArrayList<AbstractCard>();
    for (Color c : Color.values()) {
      IntStream.range(2, 11).forEach(num -> allCards.add(new NumberCard(c, num)));
      IntStream.range(1, 4).forEach(num -> allCards.add(new WettCard(c)));

    }

    return allCards;
  }

  public void replacePlayersWithRandomStrategy() {
    this.players.forEach(con -> con.setStrategy(new RandomStrategy(con)));

  }

  public Stack<AbstractCard> getNachziehstapel() {

    return this.nachZiehStapel;
  }

  public int calculateWinnerIndex(int perspectiveIndex) {

    int diff = this.calculateScore(this.players.get(perspectiveIndex))
        - this.calculateScore(this.players.get(perspectiveIndex ^ 1));

    if (diff > 0) {
      return perspectiveIndex;
    } else if (diff == 0) {
      return -1;
    } else {
      return perspectiveIndex ^ 1;
    }

  }

  public int calculateDiff(int index) {


    return this.calculateScore(this.players.get(index))
        - this.calculateScore(this.players.get(index ^ 1));
  }

  public int calculateScore(AbstractPlayer p1) {
    int wholeSum = 0;
    int fact = 1;
    int singleSum = -20;
    for (Stack<AbstractCard> st : p1.getExpeditionen().values()) {

      fact = 1;
      singleSum = -20;
      if (st.size() == 0) {
        continue;
      }

      for (AbstractCard c : st) {
        if (!c.isNumber()) {
          fact++;
        } else {
          singleSum += c.getValue();
        }
      }

      singleSum *= fact;
      if (st.size() >= 8) {
        singleSum += 20;
      }
      wholeSum += singleSum;

    }

    return wholeSum;
  }

  public static void main(String[] args) {
    Game g = Game.twoWithoutStrategies();
    g.players.forEach(con -> con.setStrategy(new RandomStrategy(con)));

    g.players.get(0).setStrategy(new InformationSetStrategy(g.players.get(0)));


    while (!g.getGameEnd()) {
      AiPlayer a = g.getPlayerWithTurn();

      g.unCheckedPlay(new WholePlay(a.play(g.getRemainingCards()), a.chooseStapel()), a);

    }
  }

  public int getZuege() {
    return this.zuege;

  }

  public void replacePlayersWithSimple() {
    this.players.forEach(pl -> pl.setStrategy(new SimpleStrategy(pl)));
  }


}
