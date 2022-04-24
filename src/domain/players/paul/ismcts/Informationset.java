package domain.players.paul.ismcts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import domain.cards.Stapel;
import domain.main.Game;
import domain.main.WholePlay;
import domain.players.AiPlayer;

/**
 * Ein Informationset ist ein Knoten für den IS_MCTS-Spielbaum
 * 
 * @author paulh
 *
 */
public class Informationset {

  /**
   * der spieler der jetzt dran ist.
   */
  private final int playerIndex;

  /**
   * Die Kinderknoten
   */
  private final List<Informationset> children;

  /**
   * Die Aktion, die zu diesem Knoten geführt hat.
   */
  private final WholePlay incomingAction;

  /**
   * Anzahl an visits
   */
  private int visitcount;

  /**
   * Anzahl an availabilties für den Knoten
   */
  private int availabiltycount;

  /**
   * Wert des Knoten
   */
  private double value;

  /**
   * Elterknoten
   */
  private final Informationset parent;

  public Informationset(int responsiblePlayer, WholePlay theAction, Informationset parent) {
    this.availabiltycount = 0;
    this.children = new ArrayList<Informationset>();
    this.incomingAction = theAction;
    this.value = 0;
    this.visitcount = 0;
    this.parent = parent;
    this.playerIndex = responsiblePlayer ^ 1;
  }



  /**
   * findet heraus, welche Kinder für die gegebene Determinierung passende plays haben.
   * 
   * @param g eine Determinisierung des Spiels
   * @return Die Kinderknoten, die eine passende Aktion für die Determisierung haben.
   */
  public List<Informationset> getCompatibleChildren(Game g) {
    List<WholePlay> ls = g.getPlayers().get(g.getTurn()).getAllActions();

    List<Informationset> theChildrenWhoAreCompatible = children.stream()
        .filter(child -> ls.contains(child.incomingAction)).collect(Collectors.toList());

    return theChildrenWhoAreCompatible;


  }



  /**
   * the actions from d for which v does not have children in the current tree. Note that c(v, d)
   * and u(v, d) are defined only for v and d such that d is a determinization of (i.e. a state
   * contained in) the information set to which v correspond
   * 
   * @return die plays auf das das oben zutrifft
   */
  public List<WholePlay> u_v_d(Game det) {

    List<WholePlay> actionsFromD = det.getPlayers().get(det.getTurn()).getAllActions();

    List<WholePlay> incomingPlays =
        this.children.stream().map(child -> child.incomingAction).collect(Collectors.toList());

    List<WholePlay> result = new ArrayList<WholePlay>();

    actionsFromD.forEach(con -> {

      if (!incomingPlays.contains(con)) {
        result.add(con);
      }

    });



    return result;

  }

  /**
   * wählt rekursiv das beste Kind gemäß UCT aus, dabei wird die determisierung immer angepasst an
   * die Aktionen der Knoten
   * 
   * @param d die aktuelle determisierung
   * @param rootIndex der Index des rootPlayers
   * @return
   */
  public Informationset selectBestChild(Game d, int rootIndex) {

    Informationset selectedChild = this;

    while (!d.getGameEnd() && selectedChild.u_v_d(d).isEmpty()) {

      List<Informationset> toEvaluate = selectedChild.getCompatibleChildren(d);

      toEvaluate.forEach(set -> set.availabiltycount++);

      selectedChild =
          Collections.max(toEvaluate, Comparator.comparing(c -> c.firstVariation(rootIndex)));

      d.unCheckedPlay(selectedChild.incomingAction, d.getPlayers().get(d.getTurn()));

    }

    return selectedChild;
  }


  /**
   * UCT-Formel für ein Informationset
   * 
   * @param rootIndex
   * @return
   */
  private double firstVariation(int rootIndex) {

    if (this.visitcount == 0) {
      return Integer.MAX_VALUE;
    }

    double value = (rootIndex == this.playerIndex) ? this.value : this.visitcount - this.value;

    return (double) (value / this.visitcount) + 0.7 * Math.sqrt(

        Math.log((double) this.availabiltycount) / (double) this.visitcount);

  }



  public Informationset expand(Game determinizedState) {

    List<WholePlay> u_v_d = this.u_v_d(determinizedState);

    int randomIdx = (int) (Math.random() * u_v_d.size());

    WholePlay theAction = u_v_d.get(randomIdx);

    AiPlayer pl = determinizedState.getPlayers().get(determinizedState.getTurn());

    determinizedState.unCheckedPlay(theAction, pl);

    Informationset newChild = new Informationset(determinizedState.getTurn(), theAction, this);

    this.children.add(newChild);

    return newChild;

  }


  /**
   * Backpropagation-phase: Erhöhen der visits und updaten des values um Simulationsresulat r
   * 
   * @param r Simulationsresultat
   */
  public void backpropagate(double r) {

    Informationset current = this;

    while (current != null) {

      current.visitcount++;
      current.value += r;

      current = current.parent;
    }

  }


  /**
   * finale Selektion der Aktion: Das Kind mit der höchsten Anzahl an visits.
   * 
   * @return
   */
  public WholePlay finalSelection() {

    Optional<Informationset> opt =
        this.children.stream().max(Comparator.comparing(c -> c.visitcount));

    if (opt.isEmpty()) {
      throw new IllegalStateException("keine Kinder bei IS-MCTS BAUM");
    }

    return opt.get().incomingAction;

  }



  public void printInfo() {

    System.out.println(this.children.size());


    this.children.stream().sorted(Comparator.comparing(c -> c.visitcount)).forEach(con -> {

      System.out.println(con.incomingAction);
      System.out.println("Wins\t\t" + con.value);
      System.out.println("VisitCount\t" + con.visitcount);

      System.out.println("Availa\t" + con.availabiltycount);

    });

  }

  public void setWinScore(int minValue) {

    if (this.value > Integer.MIN_VALUE) {
      this.value = minValue;
    }

  }


  public boolean doesNotMatter() {

    for (Informationset set : this.children) {
      if ((int) set.value != set.visitcount) {
        return false;
      }
    }

    return true;
  }



  public WholePlay firstWithNachziehstapel() {

    return this.children.stream()
        .filter(child -> child.incomingAction.getStapel() == Stapel.NACHZIEHSTAPEL)
        .map(m -> m.incomingAction).findFirst().orElse(this.children.get(0).incomingAction);

  }



}
