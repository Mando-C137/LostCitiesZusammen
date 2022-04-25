package domain.players.paul.ismcts;

import domain.cards.Stapel;
import domain.main.AblagePlay;
import domain.main.Game;
import domain.main.WholePlay;
import domain.players.AiPlayer;
import domain.players.paul.Utils;
import domain.strategies.PlayStrategy;
import domain.strategies.Strategies;

/**
 * Strategie, die IS_MCTS implementiert
 * 
 * @author paulh
 *
 */
public class InformationSetStrategy implements PlayStrategy {
  /**
   * der näcshte Spielzug
   */
  WholePlay selectedPlay;
  /**
   * Das Spiel, das mit der Strategie assoziiert ist.
   */
  private AiPlayer ai;



  /**
   * Konstruktor, die das Spiel setzt.
   * 
   * @param myGame
   */
  public InformationSetStrategy(AiPlayer ai) {
    this.ai = ai;
  }

  @Override
  public AblagePlay choosePlay(int remainingCards) {



    Game copyGame = Utils.determinizeGame(ai);
    copyGame.replacePlayersWithRandomStrategy();

    // System.out.println(ai.getModel());

    selectedPlay = Ismcts.ISMCTS(copyGame, copyGame.getTurn(), 10_000);
    System.out.println(copyGame.getTurn());
    return selectedPlay.getOption();
  }

  @Override
  public Stapel chooseStapel() {
    return selectedPlay.getStapel();
  }


  @Override
  public String getName() {
    return Strategies.PAUL_ISMCTS.toString();
  }



}
