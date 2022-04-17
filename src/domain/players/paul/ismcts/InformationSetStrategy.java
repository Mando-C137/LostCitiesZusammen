package domain.players.paul.ismcts;

import domain.cards.Stapel;
import domain.main.AblagePlay;
import domain.main.Game;
import domain.main.WholePlay;
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
  private Game game;

  /**
   * Setter für das Spiel
   * 
   * @param myGame
   */
  public void setGame(Game myGame) {
    this.game = myGame;
  }

  /**
   * Konstruktor, die das Spiel setzt.
   * 
   * @param myGame
   */
  public InformationSetStrategy(Game myGame) {
    this.game = myGame;
  }

  @Override
  public AblagePlay choosePlay(int remainingCards) {
    Game copyGame = new Game(game);
    copyGame.replacePlayersWithSimulateStrategy();
    selectedPlay = Ismcts.ISMCTS(copyGame, copyGame.getTurn(), 50_000);
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
