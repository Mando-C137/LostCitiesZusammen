package domain.players.paul.ismcts;

import java.util.Collections;
import java.util.Random;
import domain.cards.AbstractCard;
import domain.main.Game;
import domain.main.WholePlay;
import domain.players.AiPlayer;

/**
 * Information Set - MCTS
 * 
 * @author paulh
 *
 */
public class Ismcts {


  /**
   * 
   * @param realState der aktuelle reale Zustand des Spiels
   * @param rootIndex
   * @param iterations
   * @return
   */
  public static WholePlay ISMCTS(final Game realState, final int rootIndex, final int iterations) {

    Informationset root = new Informationset(rootIndex, null, null);

    for (int i = 0; i < iterations; i++) {

      Game determinizedState = Ismcts.determinize(realState, rootIndex);

      Informationset selectedSet = root.selectBestChild(determinizedState, rootIndex);

      Informationset toSimulate = selectedSet;
      if (!selectedSet.u_v_d(determinizedState).isEmpty() && !determinizedState.getGameEnd()) {
        toSimulate = selectedSet.expand(determinizedState);
      }

      double simulationResult = Ismcts.simulateBinary(determinizedState, rootIndex);
      toSimulate.backpropagate(simulationResult);

    }

    if (root.doesNotMatter()) {
      System.out.println("does not matter");
      return root.firstWithNachziehstapel();
    }

    WholePlay finalSelection = root.finalSelection();
    root.printInfo();
    return finalSelection;

  }


  /**
   * mischt den Nachziehstapel zusammen mit der Hand des Gegners ( nicht den myPlayerInx, sondern
   * 
   * @param game wird kopiert und dann geshuffelt
   * @param myPlayer
   * @return neue game instanz die geshuffelt ist
   */
  public static Game determinize(Game game, int myPlayer) {

    Game result = new Game(game);

    result.getNachziehstapel().addAll(result.getPlayers().get(myPlayer ^ 1).getHandKarten());
    result.getPlayers().get(myPlayer ^ 1).getHandKarten().clear();

    Collections.shuffle(result.getNachziehstapel());

    Random rand = new Random();
    for (int i = 0; i < 8; i++) {

      AbstractCard cardToGiveToOpponent =
          result.getNachziehstapel().remove(rand.nextInt(result.getNachziehstapel().size()));
      result.getPlayers().get(myPlayer ^ 1).getHandKarten().add(cardToGiveToOpponent);
    }

    return result;


  }


  /**
   * Simuliert ein Spiel: Resultat entweder 1 , 0, oder 0.5
   * 
   * @param g
   * @param rootIndex
   * @return
   */
  private static double simulateBinary(Game g, int rootIndex) {

    Game toSimulate = new Game(g);
    int playerIndex = g.getTurn() ^ 1;
    int nextPlayer = playerIndex ^ 1;

    while (!toSimulate.getGameEnd()) {

      AiPlayer abs = toSimulate.getPlayers().get(toSimulate.getTurn());
      WholePlay nextPlay =
          new WholePlay(abs.play(toSimulate.getRemainingCards()), abs.chooseStapel());
      toSimulate.unCheckedPlay(nextPlay, abs);

    }

    int winner = toSimulate.calculateWinnerIndex(rootIndex);

    if (winner == rootIndex) {
      return 1;
    } else if (winner == (rootIndex ^ 1)) {
      return 0;
    } else {
      return 0.5;
    }
    //
    // if (winner == playerIndex) {
    // return (playerIndex == rootIndex) ? GitMonte.POINTS_FOR_WIN : GitMonte.POINTS_FOR_LOSS; //
    // // current
    // // player
    // // wins
    // } else if (nextPlayer == winner) {
    // return (nextPlayer == rootIndex) ? GitMonte.POINTS_FOR_WIN : GitMonte.POINTS_FOR_LOSS; //
    // // opponent
    // // // //
    // // wins
    // } else {
    // return GitMonte.POINTS_FOR_DRAW; // draw
    // }
  }

  /**
   * Simuliert ein Spiel: Resultat ist ergebnis oder -ergebnis
   * 
   * @param g
   * @param myPlayer
   * @return
   */
  private static double simulateDiff(Game g, int myPlayer) {

    Game toSimulate = new Game(g);
    int playerIndex = g.getTurn() ^ 1;
    int nextPlayer = playerIndex ^ 1;

    while (!toSimulate.getGameEnd()) {

      AiPlayer abs = toSimulate.getPlayers().get(toSimulate.getTurn());
      WholePlay nextPlay =
          new WholePlay(abs.play(toSimulate.getRemainingCards()), abs.chooseStapel());
      toSimulate.unCheckedPlay(nextPlay, abs);

    }

    int diff = toSimulate.calculateDiff(myPlayer);
    // int winner = toSimulate.calculateWinnerIndex(myPlayer);

    if (diff > 0) {
      return (playerIndex == myPlayer) ? diff : -diff; // current
                                                       // player
                                                       // wins
    } else if (diff < 0) {
      return (nextPlayer == myPlayer) ? diff : -diff; // opponent
                                                      // wins
    } else {
      return 0; // draw
    }
  }



}
