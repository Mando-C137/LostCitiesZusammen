package domain.players.paul.simple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;
import domain.cards.AbstractCard;
import domain.cards.Color;
import domain.cards.NumberCard;
import domain.cards.Stapel;
import domain.main.AblagePlay;
import domain.strategies.SimpleStrategy;

public class Evaluator {

  SimpleStrategy ai;

  public Evaluator(SimpleStrategy ai) {
    this.ai = ai;
  }

  public AblagePlay chooseAblagePlay(int remaining) {

    // System.out.println(this.ai.getPlayer().getHandKarten().toString());


    if (remaining == 1) {
      Optional<AblagePlay> opt = this.optionalHighestCardForExpedition();
      if (opt.isPresent()) {
        return opt.get();
      }
    }


    if (remaining <= 11) {
      return this.endGameDecision();
    }

    // direkte Nachfolger werden direkt gelegt
    Optional<AblagePlay> opt = optionalDirectSuccessor();
    if (opt.isPresent()) {
      // System.out.println("directMove");
      return opt.get();
    }

    // habe nächst möglichen Nachfolger
    opt = optionalNextRealSuccessor();
    if (opt.isPresent()) {
      // System.out.println("nextAvailableSuccessor");
      return opt.get();
    }

    // es kann sein, dass wir bei hinreichender Wahrscheinlichkeit eine neue Expedition starten


    opt = optionalforStartExpedition();
    if (opt.isPresent()) {
      // System.out.println("startEx");
      return opt.get();
    }


    opt = optionalForNotPerfectExpeditionPlay();
    if (opt.isPresent()) {
      // System.out.println("no perfect play");
      return opt.get();
    }

    // züge machen die auch nicht "perfekt sind"
    // opt = okayMove();
    // if (opt.isPresent()) {
    // // System.out.println("ok move");
    // return opt.get();
    // }

    if (remaining <= 9) {
      for (Color col : Color.values()) {
        if (!this.ai.getPlayer().getExpeditionen().isEmpty()) {
          Optional<AbstractCard> optio = this.optionalLowestAllowedCardToPlayOnExpedition(col);

          if (opt.isPresent()) {
            // System.out.println("endgame");
            return new AblagePlay(Stapel.toExpedition(col), optio.get());
          }
        }
      }
    }


    return ChooseBestAblageStapel();
  }



  /**
   * Methode wird aufgerufen wenn man im engame ist das heißt jetzt müssen grob schlechtere züge
   * gemacht werden.
   * 
   * @return
   */
  private AblagePlay endGameDecision() {

    Optional<AblagePlay> opt = this.optionalDirectSuccessor();
    if (this.optionalDirectSuccessor().isPresent()) {
      return opt.get();
    }

    opt = this.optionalNextRealSuccessor();
    if (opt.isPresent()) {
      return opt.get();
    }

    // Sortiere mögliche ExpeditionPlaysNachValue
    // List<AblagePlay> pl = this.ai.getPlayer().getPlaySet().stream()
    // .filter(pre -> pre.getStapel().isExpedition())
    // .filter(
    // vet -> !this.ai.getPlayer().getExpeditionen().get(vet.getCard().getColor()).isEmpty())
    // .sorted((u, v) -> u.getCard().compareTo(v.getCard())).collect(Collectors.toList());
    //
    // if (!pl.isEmpty()) {
    //
    // int index = pl.size() - this.ai.getPlayer().getRemainingCards();
    //
    // while (index < 0) {
    // index++;
    // }
    // return pl.get(index);
    //
    // }


    List<AbstractCard> goodAnswers = new ArrayList<AbstractCard>();

    for (Entry<Color, Stack<AbstractCard>> entry : this.ai.getPlayer().getExpeditionen()
        .entrySet()) {

      if (entry.getValue().isEmpty()) {
        continue;
      }

      List<AbstractCard> cardsToPlay =
          this.ai.getPlayer().getHandKarten().stream()
              .filter(card -> card.getColor() == entry.getKey()
                  && card.getValue() > entry.getValue().peek().getValue())
              .collect(Collectors.toList());

      if (cardsToPlay.isEmpty()) {
        continue;
      }

      AbstractCard potential = Collections.min(cardsToPlay);
      goodAnswers.add(potential);

    }


    if (!goodAnswers.isEmpty()) {
      AbstractCard finalanswer = Collections.max(goodAnswers);

      return new AblagePlay(Stapel.toExpedition(finalanswer.getColor()), finalanswer);
    }


    return this.ChooseBestAblageStapel();

  }



  private Optional<AblagePlay> optionalHighestCardForExpedition() {
    AblagePlay p = null;

    List<AblagePlay> list = this.ai.getPlayer().getPlaySet().stream()
        .filter(
            pre -> !this.ai.getPlayer().getExpeditionen().get(pre.getCard().getColor()).isEmpty()
                && pre.getStapel().isExpedition())
        .collect(Collectors.toList());

    if (list.isEmpty()) {
      return Optional.ofNullable(p);
    }

    return list.stream().max((u, v) -> u.getCard().compareTo(v.getCard()));

  }



  //
  // private Optional<AblagePlay> okayMove() {
  //
  // List<AbstractCard> ls = new LinkedList<AbstractCard>();
  //
  // for (AbstractCard abs : this.ai.getPlayer().getPlaySet().stream()
  // .filter(p -> p.getStapel().isExpedition()).map(p -> p.getCard())
  // .collect(Collectors.toList())) {
  //
  // if (abs.isNumber()) {
  // NumberCard a = (NumberCard) abs;
  // if (this.ai.getModel().currentScoreOfExp(a.getColor()) > 20) {
  // ls.add(a);
  // }
  // }
  //
  // }
  //
  // if (ls.isEmpty()) {
  // return Optional.ofNullable(null);
  // }
  // AbstractCard res = Collections.min(ls);
  //
  // return Optional.ofNullable(new AblagePlay(Stapel.toExpedition(res.getColor()), res));
  // }

  public int currentScoreOfExpedition(Color col) {
    int summe = 0;
    for (AbstractCard abs : this.ai.getPlayer().getExpeditionen().get(col)) {
      summe += abs.getValue();
    }

    return summe;


  }


  private Optional<AblagePlay> optionalNextRealSuccessor() {

    AblagePlay p = null;

    List<AbstractCard> ls = new LinkedList<AbstractCard>();

    ai.getPlayer().getExpeditionen().forEach((c, e) -> {

      if (!e.isEmpty()) {

        AbstractCard myPeek = this.ai.getPlayer().getExpeditionen().get(c).peek();

        List<AbstractCard> temp = this.ai
            .getPlayer().getHandKarten().stream().filter(card -> card.compareTo(myPeek) >= 0
                && card.getColor().equals(c) && isNextRealSuccessor(card))
            .collect(Collectors.toList());
        ls.addAll(temp);
      }

    });

    if (!ls.isEmpty()) {
      AbstractCard card = ls.get(0);
      // System.out.println(card + " auf Expedition" + card.getColor());
      return Optional.ofNullable(new AblagePlay(Stapel.toExpedition(card.getColor()), card));
    }

    return Optional.ofNullable(p);
  }


  private boolean isNextRealSuccessor(AbstractCard card) {

    AbstractCard myPeek = this.ai.getPlayer().getExpeditionen().get(card.getColor()).peek();

    if (this.ai.getPlayer().getEnemyExpeditions(card.getColor()).isEmpty()) {
      return false;
    }
    Stack<AbstractCard> enemyEx = new Stack<AbstractCard>();
    enemyEx.addAll(this.ai.getPlayer().getEnemyExpeditions(card.getColor()));

    int counter = 1;
    int index = enemyEx.size() - 1;
    for (; index >= 0 && card.compareTo(enemyEx.get(index)) >= 0; index--) {

      if (myPeek.compareTo(enemyEx.get(index)) == counter++) {

      } else {
        return false;
      }
    }

    return card.compareTo(enemyEx.get(index < 0 ? 0 : index)) == 1;

  }


  /**
   * Wenn die KI einen direkten Nachfolger hat, dann wird sie diesen auch immer legen. Wenn sie
   * mehrere hat, dann wird sie die höchste legen.
   * 
   * @return
   */
  private Optional<AblagePlay> optionalDirectSuccessor() {

    AblagePlay p = null;

    List<AbstractCard> ls = new LinkedList<AbstractCard>();

    ai.getPlayer().getExpeditionen().forEach((c, e) -> {

      if (!e.isEmpty()) {

        List<AbstractCard> temp = this.ai.getPlayer().getHandKarten().stream()
            .filter(card -> card.getColor().equals(c)
                && (card.compareTo(e.peek()) == 1 || card.compareTo(e.peek()) == 0))
            .collect(Collectors.toList());
        if (!temp.isEmpty()) {

          AbstractCard investigate = temp.get(0);
          if (!investigate.isNumber()) {
            if (this.expectedValueOfExpedition(investigate.getColor()) >= 30
                && this.playableCards(investigate.getColor()) >= 1) {
              ls.add(investigate);
            }
          } else {
            ls.add(investigate);
          }


        }
      }

    });

    if (!ls.isEmpty()) {

      AbstractCard abs = Collections.max(ls);
      return Optional.ofNullable(new AblagePlay(Stapel.toExpedition(abs.getColor()), abs));
    }
    return Optional.ofNullable(p);

  }

  /**
   * evaluiert ob man eine expedition starten sollte und liefert falls dieser der Fall ist auch eine
   * zurück Wählt dabei den besten Start
   * 
   * @return
   */
  private Optional<AblagePlay> optionalforStartExpedition() {
    List<AblagePlay> res = new LinkedList<AblagePlay>();
    List<Double> valueArray = new LinkedList<Double>();
    this.ai.getPlayer().getExpeditionen().forEach((col, ex) -> {
      if (ex.isEmpty()) {
        double value = -1;
        if ((value = this.shouldStartExpedition(col)) != -1) {
          res.add(new AblagePlay(Stapel.toExpedition(col),
              optionalLowestAllowedCardToPlayOnExpedition(col).get()));
          valueArray.add(value);
        }
      }
    });
    if (!res.isEmpty()) {

      int highest = 0;
      for (int i = 0; i < valueArray.size(); i++) {
        if (valueArray.get(i) > valueArray.get(highest)) {
          highest = i;
        }
      }

      return Optional.ofNullable(res.get(highest));

    }

    return Optional.ofNullable(null);
  }



  /**
   * gibt für eine Expedition zurück, welche Karte die kleinste ist, die man darauf spielen kann.
   * 
   * @param col
   * @return
   */
  private Optional<AbstractCard> optionalLowestAllowedCardToPlayOnExpedition(Color col) {

    List<AbstractCard> ls = ai.getPlayer().getPlaySet().stream()
        .filter(c -> c.getStapel().isExpedition() && c.getCard().getColor() == col)
        .map(c -> c.getCard()).collect(Collectors.toList());

    if (ls.isEmpty()) {
      return Optional.ofNullable(null);
    }
    return Optional.ofNullable(Collections.min(ls));
  }


  /**
   * Auswhahl einer AblagePlay, bei der eine Karte auf einer der Ablagestapel gelegt werden muss.
   * 
   * @return
   */
  private AblagePlay ChooseBestAblageStapel() {

    List<AbstractCard> ls = new LinkedList<AbstractCard>(this.ai.getPlayer().getHandKarten());
    ls.removeAll(this.enemyWantsThoseFromMe());
    ls.removeAll(this.iNeedThoseCardsFromMe());

    AbstractCard res;
    if (!ls.isEmpty()) {
      res = ls.get(0);
    } else {
      ls = new LinkedList<AbstractCard>(this.ai.getPlayer().getHandKarten());
      ls.removeAll(this.iNeedThoseCardsFromMe());
      if (!ls.isEmpty()) {
        res = ls.get(0);
      } else {

        res = Collections.min(this.ai.getPlayer().getHandKarten());
      }

    }

    return new AblagePlay(Stapel.toMiddle(res.getColor()), res);

  }

  private List<AbstractCard> iNeedThoseCardsFromMe() {
    LinkedList<AbstractCard> res = new LinkedList<AbstractCard>();

    for (Color c : Color.values()) {
      if (!this.ai.getPlayer().getExpeditionen().get(c).isEmpty()) {
        AbstractCard top = this.ai.getPlayer().getExpeditionen().get(c).peek();
        this.ai.getPlayer().getHandKarten().stream()
            .filter(h -> h.getColor() == c && h.compareTo(top) > 0).forEach(card -> res.add(card));


      }

    }

    return res;
  }

  private List<AbstractCard> enemyWantsThoseFromMe() {


    LinkedList<AbstractCard> res = new LinkedList<AbstractCard>();

    for (Color c : Color.values()) {

      if (!this.ai.getPlayer().getEnemyExpeditions(c).isEmpty()) {
        AbstractCard top = this.ai.getPlayer().getEnemyExpeditions(c).peek();
        this.ai.getPlayer().getHandKarten().stream()
            .filter(h -> h.getColor() == c && h.compareTo(top) > 0).forEach(card -> res.add(card));
      }
    }
    return res;
  }

  public double expectedValueOfExpedition(Color c) {

    List<AbstractCard> allCardsOfColor = new ArrayList<AbstractCard>();

    for (int i = 2; i <= 10; i++) {
      allCardsOfColor.add(new NumberCard(c, i));
    }

    double e_c = 0;
    Stack<AbstractCard> stack = this.ai.getPlayer().getExpeditionen().get(c);

    for (AbstractCard alreadyThere : stack) {
      e_c += alreadyThere.getValue();
    }

    List<AbstractCard> usableHandCards;

    usableHandCards =
        this.ai.getPlayer().getHandKarten().stream()
            .filter(pre -> stack.isEmpty() ? pre.getColor() == c
                : pre.getColor() == c && pre.compareTo(stack.peek()) >= 0)
            .collect(Collectors.toList());

    for (AbstractCard inHand : usableHandCards) {
      e_c += inHand.getValue();
    }

    allCardsOfColor.removeAll(this.ai.getPlayer().getHandKarten());
    allCardsOfColor.removeAll(stack);
    allCardsOfColor.removeAll(this.ai.getPlayer().getEnemyExpeditions(c));
    allCardsOfColor.removeAll(this.ai.getEnemyModel());

    allCardsOfColor = allCardsOfColor.stream()
        .filter(param -> stack.isEmpty() ? true : param.compareTo(stack.peek()) >= 0)
        .collect(Collectors.toList());

    for (AbstractCard unknownCard : allCardsOfColor) {
      e_c +=
          (double) unknownCard.getValue() * 0.5 * ((double) this.ai.getPlayer().getRemainingCards()
              / (this.ai.getPlayer().getRemainingCards() + 8));
    }


    return e_c;

  }


  private double expectedValueOfUnstartedExpedition(Color c) {

    List<AbstractCard> allCardsOfThatColor = new ArrayList<AbstractCard>();

    for (int i = 2; i <= 10; i++) {
      allCardsOfThatColor.add(new NumberCard(c, i));
    }

    double e_c = 0;

    List<AbstractCard> cardsOfThatColorInHand = this.ai.getPlayer().getHandKarten().stream()
        .filter(pre -> pre.getColor() == c).collect(Collectors.toList());

    /*
     * wenn ich keine Karte in der Hand hab, kann ich gar nix damit anfagen.
     */
    if (cardsOfThatColorInHand.isEmpty()) {
      return Integer.MIN_VALUE;
    }

    AbstractCard minimumInHand = Collections.min(cardsOfThatColorInHand);


    for (AbstractCard abs : cardsOfThatColorInHand) {
      e_c += abs.getValue();
    }


    allCardsOfThatColor.removeAll(cardsOfThatColorInHand);
    allCardsOfThatColor.removeAll(this.ai.getPlayer().getEnemyExpeditions(c));
    allCardsOfThatColor.removeAll(this.ai.getEnemyModel());
    allCardsOfThatColor = allCardsOfThatColor.stream()
        .filter(pre -> pre.compareTo(minimumInHand) >= 0).collect(Collectors.toList());

    for (AbstractCard unknownCard : allCardsOfThatColor) {
      e_c += 0.5 * unknownCard.getValue() * ((double) this.ai.getPlayer().getRemainingCards()
          / (this.ai.getPlayer().getRemainingCards() + 8));
    }

    // System.out.println("expected value of unstarted expedition for color" + c + " : " + e_c);
    return e_c;

  }


  /**
   * Rückgabewert: der Erwartungswert einer Expedition
   * 
   * @param c
   * @return
   */
  private double shouldStartExpedition(Color c) {

    double res = -1;
    if ((res = this.expectedValueOfUnstartedExpedition(c)) >= 22 && this.playableCards(c) >= 2
        && this.ai.getPlayer().getRemainingCards() >= 10) {
      return res;
    }
    return -1;


  }

  private int playableCards(Color c) {

    Stack<AbstractCard> stack = this.ai.getPlayer().getExpeditionen().get(c);

    List<AbstractCard> playableCardsOfThatColor =
        this.ai.getPlayer().getHandKarten().stream().filter(pre -> {

          if (!pre.isNumber()) {
            return false;
          }

          if (stack.isEmpty()) {
            return pre.getColor() == c;
          } else {
            return pre.compareTo(stack.peek()) >= 0 && pre.getColor() == c;
          }

        }).collect(Collectors.toList());

    return playableCardsOfThatColor.size();


  }


  private boolean shouldMakeExpeditionPlay(AblagePlay p, Color col) {

    Stack<AbstractCard> copiedStack = new Stack<AbstractCard>();

    double valueBefore = expectedValueOfExpedition(col);

    copiedStack.addAll(this.ai.getPlayer().getExpeditionen().get(col));
    copiedStack.push(p.getCard());


    List<AbstractCard> allCardsOfColor = new ArrayList<AbstractCard>();

    for (int i = 2; i <= 10; i++) {
      allCardsOfColor.add(new NumberCard(col, i));
    }

    double newValue = 0;
    Stack<AbstractCard> stack = copiedStack;

    for (AbstractCard alreadyThere : stack) {
      newValue += alreadyThere.getValue();
    }

    List<AbstractCard> usableHandCards;

    // TODO am ende auch sehr schlechte plays machen

    usableHandCards = this.ai.getPlayer().getHandKarten().stream()
        .filter(pre -> stack.isEmpty() ? pre.getColor() == col
            : pre.getColor() == col && pre.compareTo(stack.peek()) >= 0)
        .collect(Collectors.toList());

    usableHandCards.remove(p.getCard());

    for (AbstractCard inHand : usableHandCards) {
      newValue += inHand.getValue();
    }

    allCardsOfColor.removeAll(this.ai.getPlayer().getHandKarten());
    allCardsOfColor.removeAll(stack);
    allCardsOfColor.removeAll(this.ai.getPlayer().getEnemyExpeditions(col));
    allCardsOfColor.removeAll(this.ai.getEnemyModel());

    allCardsOfColor = allCardsOfColor.stream()
        .filter(param -> stack.isEmpty() ? true : param.compareTo(stack.peek()) >= 0)
        .collect(Collectors.toList());

    for (AbstractCard unknownCard : allCardsOfColor) {

      double summand =
          0.5 * unknownCard.getValue() * ((double) this.ai.getPlayer().getRemainingCards()
              / (this.ai.getPlayer().getRemainingCards() + 8));

      newValue += summand;
    }

    if (newValue >= 25) {
      // System.out.println("newValue " + newValue);
      // System.out.println("OldValue " + valueBefore);
      // System.out.println();
      return true;
    } else {
      return false;
    }



  }


  private Optional<AblagePlay> optionalForNotPerfectExpeditionPlay() {

    List<AblagePlay> res = this.ai.getPlayer().getPlaySet().stream()
        .filter(pre -> pre.getStapel().isExpedition())
        .filter(pre -> this.shouldMakeExpeditionPlay(pre, pre.getCard().getColor())).filter(wet -> {
          if (this.ai.getPlayer().getExpeditionen().get(wet.getCard().getColor()).isEmpty()) {
            return expectedValueOfUnstartedExpedition(wet.getCard().getColor()) >= 25;
          } else {

            if (!wet.getCard().isNumber()) {
              return expectedValueOfUnstartedExpedition(wet.getCard().getColor()) >= 30;
            }

            return true;
          }

        }).collect(Collectors.toList());



    if (res.isEmpty()) {
      return Optional.ofNullable(null);
    }

    else {
      return res.stream().min((u, v) -> u.getCard().compareTo(v.getCard()));
    }



  }


}
